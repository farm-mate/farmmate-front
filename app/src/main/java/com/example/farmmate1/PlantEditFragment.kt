package com.example.farmmate1

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
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
import com.example.farmmate1.databinding.FragmentPlantEditBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PlantEditFragment : Fragment() {

    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var _binding: FragmentPlantEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var plantUuid: String
    private lateinit var plant: PlantPost
    private var plantImg: MultipartBody.Part? = null
    private var imageData: ByteArray? = null

    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantEditBinding.inflate(inflater, container, false)
        val view = binding.root

        plantUuid = arguments?.getString("plantUuid") ?: ""
        plantUuid?.let { uuid ->

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("PlantEditFragment", "Uuid: $plantUuid")

            apiService.getPlant(plantUuid).enqueue(object : Callback<PlantGet> {
                override fun onResponse(call: Call<PlantGet>, response: Response<PlantGet>) {
                    if (response.isSuccessful) {
                        val plantget = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("PlantEditFragment", "$plantget")

                        binding.plantEditEtName.text = plantget?.plant_name.toEditable()
                        binding.plantEditTvType.text = plantget?.plant_type
                        selectedDate = plantget?.first_planting_date?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                        }
                        binding.plantEditTvSelectdate.text = plantget?.first_planting_date
                        binding.plantEditEtLocation.text = plantget?.plant_location.toEditable()
                        binding.plantEditEtMemo.text = plantget?.memo.toEditable()
                        val plant_Img = binding.plantEditIbImage

                        val imageUrl = plantget?.image_url
                        ImageLoaderTask(plant_Img).execute(imageUrl)

                    } else {
                        // API 요청 실패 처리
                        Log.e("PlantEditFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PlantGet>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("PlantEditFragment", "Network error: ${t.message}")
                }
            })
        }?: run {
            // UUID가 null인 경우에 대한 처리
            Log.e("PlantEditFragment", "Plant UUID is null")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 (개별 식물 페이지로)
        binding.plantEditBackIb.setOnClickListener {
            moveToPlantInfoFragment()
        }

        // 날짜 선택 달력 띄우기
        binding.plantEditTvSelectdate.setOnClickListener{
            selectStartPlantDate()
        }

        binding.plantEditIbImage.setOnClickListener {
            openGallery()
        }

        // 수정 버튼
        binding.plantEditBtnEnroll.setOnClickListener {
            editPlant()
        }
    }

    private fun moveToPlantInfoFragment() {
        val bundle = Bundle()
        bundle.putString("plantUuid", plantUuid)

        val plantInfoFragment = PlantInfoFragment()
        plantInfoFragment.arguments = bundle

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, plantInfoFragment)
            .commit()
    }

    private fun selectStartPlantDate() {
        val cal = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            selectedDate = cal.time // 선택한 날짜를 저장

            binding.plantEditTvSelectdate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
        }
        DatePickerDialog(
            requireContext(),
            datePickerListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                        // Plant 객체에 이미지 데이터 설정
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()
                        imageData = bytes

                        // 이미지 파일의 MIME 타입 가져오기
                        val contentType = requireActivity().contentResolver.getType(uri)
                        //파일명
                        val fileName = getFileNameFromUri(uri)
                        binding.plantEditTvUploadFileinfo.text = "파일 선택됨: $fileName"

                        // 이미지 파일을 MultipartBody.Part로 변환
                        val requestBody = RequestBody.create(MediaType.parse(contentType), bytes)
                        val multipart = MultipartBody.Part.createFormData("plantImg", fileName, requestBody)
                        plantImg = multipart
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

    private fun editPlant() {
        val plantType = binding.plantEditTvType.text.toString()
        val plantName = binding.plantEditEtName.text.toString()
        val plantLocation = binding.plantEditEtLocation.text.toString()
        val memo = binding.plantEditEtMemo.text.toString()
        val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)

        val selectedDateFormatted = selectedDate?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: ""

        Log.d("editplant","$plantType, $plantName, $plantLocation, $memo, $deviceId, $selectedDateFormatted")

        val requestBodyMap = hashMapOf<String, RequestBody>()
        requestBodyMap["plantType"] = RequestBody.create(MediaType.parse("text/plain"), plantType)
        requestBodyMap["plantName"] = RequestBody.create(MediaType.parse("text/plain"), plantName)
        requestBodyMap["firstPlantingDate"] = RequestBody.create(MediaType.parse("text/plain"), selectedDateFormatted)
        requestBodyMap["plantLocation"] = RequestBody.create(MediaType.parse("text/plain"), plantLocation)
        requestBodyMap["memo"] = RequestBody.create(MediaType.parse("text/plain"), memo)
        requestBodyMap["deviceId"] = RequestBody.create(MediaType.parse("text/plain"), deviceId)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        plant = PlantPost(plantType, plantName, selectedDateFormatted, plantLocation, memo, deviceId, plantImg)

        val call = apiService.editPlant(plantUuid, requestBodyMap, plant.plantImg)
        call.enqueue(object : Callback<PlantPost> {
            override fun onResponse(call: Call<PlantPost>, response: Response<PlantPost>) {
                if (response.isSuccessful) {
                    val editPlant = response.body()
                    Toast.makeText(requireContext(), "수정 완료", Toast.LENGTH_SHORT).show()
                    moveToPlantInfoFragment()
                } else {
                    Toast.makeText(requireContext(), "식물 수정 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlantPost>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}