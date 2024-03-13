package com.example.farmmate1

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.farmmate1.databinding.FragmentDiaryInfoBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DiaryInfoFragment : Fragment() {

    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var _binding: FragmentDiaryInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var diaryUuid: String
    //private lateinit var diary: DiaryPost
    private var image: MultipartBody.Part? = null
    private var imageData: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        diaryUuid = arguments?.getString("diaryUuid") ?: ""
        diaryUuid?.let { uuid ->

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("DiaryInfoFragment", "Uuid: $diaryUuid")

            apiService.getDiary(diaryUuid).enqueue(object : Callback<DiaryGet> {
                override fun onResponse(call: Call<DiaryGet>, response: Response<DiaryGet>) {
                    if (response.isSuccessful) {
                        val diaryget = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("PlantEditFragment", "$diaryget")

                        binding.diaryInfoTvType.text = diaryget?.plant?.plant_name
                        binding.diaryInfoEtWeather.text = diaryget?.plant_weather.toEditable()
                        binding.diaryInfoEtTemperature.text = diaryget?.temperature.toEditable()
                        binding.diaryInfoEtHumidity.text = diaryget?.humidity.toEditable()
                        binding.diaryInfoCbWater.isChecked = diaryget?.water_flag?: false
                        binding.diaryInfoCbFert.isChecked = diaryget?.fertilizer_flag?:false
                        binding.diaryInfoEtFert.text = diaryget?.fertilizer_name?.toEditable()
                        binding.diaryInfoEtFertuse.text = diaryget?.fertilizer_usage?.toEditable()
                        binding.diaryInfoCbPes.isChecked = diaryget?.pesticide_flag?:false
                        binding.diaryInfoEtPes.text = diaryget?.pesticide_name?.toEditable()
                        binding.diaryInfoEtPesuse.text = diaryget?.pesticide_usage?.toEditable()
                        binding.diaryInfoEtMemo.text = diaryget?.memo.toEditable()
                        val diaryImg = binding.diaryInfoIbImage
                        val diary_Img = binding.diaryInfoIvProfile

                        val imageUrl = diaryget?.image_url
                        ImageLoaderTask(diaryImg).execute(imageUrl)
                        ImageLoaderTask(diary_Img).execute(imageUrl)

                    } else {
                        // API 요청 실패 처리
                        Log.e("DiaryEditFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DiaryGet>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("DiaryEditFragment", "Network error: ${t.message}")
                }
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 (일지 메인 페이지로)
        binding.diaryInfoBackIb.setOnClickListener {
            moveToDiaryFragment()
        }

        binding.diaryInfoIbImage.setOnClickListener {
            openGallery()
        }

        // 수정 버튼
        binding.diaryInfoBtnEnroll.setOnClickListener {
            editDiary()
        }

        // 삭제 버튼
        binding.diaryInfoBtnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun moveToDiaryFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiaryFragment())
        transaction.commit()
    }

    private fun openGallery() {
        if (checkStoragePermission()) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY)
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_FROM_GALLERY -> {
                    data?.data?.let { uri ->
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()
                        imageData = bytes

                        // 이미지 파일의 MIME 타입 가져오기
                        val contentType = requireActivity().contentResolver.getType(uri)
                        //파일명
                        val fileName = getFileNameFromUri(uri)
                        binding.diaryInfoTvUploadFileinfo.text = "파일 선택됨: $fileName"

                        // 이미지 파일을 MultipartBody.Part로 변환
                        val requestBody = RequestBody.create(MediaType.parse(contentType), bytes)
                        val multipart = MultipartBody.Part.createFormData("diaryImg", fileName, requestBody)
                        image = multipart
                    }
                }
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex >= 0) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }

    private fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), string)
    }

    private fun editDiary() {
        val plantName = binding.diaryInfoTvType.text.toString()
        val plantWeather = binding.diaryInfoEtWeather.text.toString()
        val temperature = binding.diaryInfoEtTemperature.text.toString()
        val humidity = binding.diaryInfoEtHumidity.text.toString()
        val waterFlag = binding.diaryInfoCbWater.isChecked.toString()
        val fertilizeFlag = binding.diaryInfoCbFert.isChecked.toString()
        val fertilizeName = binding.diaryInfoEtFert.text.toString()
        val fertilizeUsage = binding.diaryInfoEtFertuse.text.toString()
        val pesticideFlag = binding.diaryInfoCbPes.isChecked.toString()
        val pesticideName = binding.diaryInfoEtPes.text.toString()
        val pesticideUsage = binding.diaryInfoEtPesuse.text.toString()
        val memo = binding.diaryInfoEtMemo.text.toString()
        val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)

        //Log.d("editplant","$plantType, $plantName, $plantLocation, $memo, $deviceId, $selectedDateFormatted")

        val requestBodyMap = hashMapOf<String, RequestBody>()
        //requestBodyMap["plantUuid"] = createPartFromString(plantUuid)
        //requestBodyMap["diaryDate"] = createPartFromString(diaryDate)
        requestBodyMap["plantName"] = createPartFromString(plantName)
        requestBodyMap["plantWeather"] = createPartFromString(plantWeather)
        requestBodyMap["temperature"] = createPartFromString(temperature)
        requestBodyMap["humidity"] = createPartFromString(humidity)
        requestBodyMap["waterFlag"] = createPartFromString(waterFlag)
        requestBodyMap["fertilizeFlag"] = createPartFromString(fertilizeFlag)
        requestBodyMap["fertilizeName"] = createPartFromString(fertilizeName)
        requestBodyMap["fertilizeUsage"] = createPartFromString(fertilizeUsage)
        requestBodyMap["pesticideFlag"] = createPartFromString(pesticideFlag)
        requestBodyMap["pesticideName"] = createPartFromString(pesticideName)
        requestBodyMap["pesticideUsage"] = createPartFromString(pesticideUsage)
        requestBodyMap["memo"] = createPartFromString(memo)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        //diary = DiaryPost(plantName, plantUuid, diaryDate, plantWeather, temperature, humidity, waterFlag, fertilizeFlag, fertilizeName, )

        val imagePart = image

        val call = apiService.editDiary(diaryUuid, requestBodyMap, imagePart)
        call.enqueue(object : Callback<DiaryPost> {
            override fun onResponse(call: Call<DiaryPost>, response: Response<DiaryPost>) {
                if (response.isSuccessful) {
                    val editPlant = response.body()
                    Toast.makeText(requireContext(), "수정 완료", Toast.LENGTH_SHORT).show()
                    moveToDiaryFragment()
                } else {
                    Toast.makeText(requireContext(), "식물 수정 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DiaryPost>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("삭제 확인")
        builder.setMessage("정말로 이 항목을 삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            // 사용자가 확인을 눌렀을 때의 동작 수행
            deleteDiary()
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            // 사용자가 취소를 눌렀을 때의 동작 수행 (아무것도 안 함)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteDiary(){
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.deleteDiary(diaryUuid)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                    moveToDiaryFragment()
                } else {
                    Toast.makeText(requireContext(), "식물 삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}