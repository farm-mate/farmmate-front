package com.example.farmmate1

import android.os.Bundle
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.farmmate1.databinding.FragmentPlantAddBinding
import java.util.*
import java.text.SimpleDateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class PlantAddFragment : Fragment() {

    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var _binding: FragmentPlantAddBinding? = null
    private val binding get() = _binding!!

    private var imageData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantAddBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private lateinit var plant: PlantPost
    private var plantImg: MultipartBody.Part? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 작물 종류 선택
        setUpSpinnerPlants()

        // 뒤로 가기 버튼 클릭 후 나의 식물 페이지로 이동
        binding.plantAddBackIb.setOnClickListener{
            moveToPlantFragment()
        }

        // 날짜 선택 달력 띄우기
        binding.plantAddTvSelectdate.setOnClickListener{
            selectStartPlantDate()
        }

        binding.plantAddLocationIb.setOnClickListener {
            //selectLocation()
        }

        binding.plantAddIbImage.setOnClickListener {
            openGallery()
        }

        // 등록하기 버튼 클릭 후 나의 식물 페이지로 이동, 새로운 아이템 추가됨
        binding.plantAddBtnEnroll.setOnClickListener{
            enrollPlant()
        }

    }

    private fun setUpSpinnerPlants() {
        val plants = resources.getStringArray(R.array.plants)
        val adapter = ArrayAdapter(requireContext(),R.layout.spinner_item_plant, plants)
        val spinner = binding.plantAddSpinnerSelect
        spinner.adapter = adapter
    }

    private fun moveToPlantFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, PlantFragment())
        transaction.commit()
    }

    private var selectedDate: Date? = null

    private fun selectStartPlantDate() {
        val cal = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            selectedDate = cal.time // 선택한 날짜를 저장

            binding.plantAddTvSelectdate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
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
                        binding.plantAddTvUploadFileinfo.text = "파일 선택됨: $fileName"

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

    private fun enrollPlant() {
        val plantType = binding.plantAddSpinnerSelect.selectedItem.toString()
        val plantName = binding.plantAddEtName.text.toString()
        val plantLocation = binding.plantAddEtLocation.text.toString()
        val memo = binding.plantAddEtMemo.text.toString()
        val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)

        val selectedDateFormatted = selectedDate?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: ""

        val requestBodyMap = hashMapOf<String, RequestBody>()
        requestBodyMap["plantType"] = RequestBody.create(MediaType.parse("text/plain"), plantType)
        requestBodyMap["plantName"] = RequestBody.create(MediaType.parse("text/plain"), plantName)
        requestBodyMap["firstPlantingDate"] = RequestBody.create(MediaType.parse("text/plain"), selectedDateFormatted)
        requestBodyMap["plantLocation"] = RequestBody.create(MediaType.parse("text/plain"), plantLocation)
        requestBodyMap["memo"] = RequestBody.create(MediaType.parse("text/plain"), memo)
        requestBodyMap["deviceId"] = RequestBody.create(MediaType.parse("text/plain"), deviceId)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        plant = PlantPost(deviceId, plantName, plantType, plantLocation, memo, selectedDateFormatted, plantImg)

        val call = apiService.postPlant(requestBodyMap, plant.plantImg)
        call.enqueue(object : Callback<PlantPost> {
            override fun onResponse(call: Call<PlantPost>, response: Response<PlantPost>) {
                if (response.isSuccessful) {
                    val postPlant = response.body()
                    Toast.makeText(requireContext(), "식물 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    moveToPlantFragment()
                    // 식물 등록이 성공하면 화면을 초기화하거나 다른 작업을 수행할 수 있음
                } else {
                    Toast.makeText(requireContext(), "식물 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlantPost>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun selectLocation() {
//        TODO("Not yet implemented")
//    }

}