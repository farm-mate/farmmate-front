package com.example.farmmate1

import android.os.Bundle
import android.util.Log
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
import kotlin.math.log
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.provider.Settings.Secure
import androidx.core.content.ContextCompat
import android.util.Base64



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

    private lateinit var plant: Plant

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
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            getContent.launch(intent)
            openGallery()
        }

        plant = Plant("", "", "", "", "", null) // 초기화

        // 등록하기 버튼 클릭 후 나의 식물 페이지로 이동, 새로운 아이템 추가됨
        binding.plantAddBtnEnroll.setOnClickListener{

            //val profile = R.drawable.strawberry // 프로필 이미지
            val plantType = binding.plantAddSpinnerSelect.selectedItem.toString()
            //val nickname = binding.plantAddEtName.text.toString() // 사용자가 입력한 식물 닉네임
            val firstPlantingDate = binding.plantAddTvSelectdate.text.toString() // 사용자가 선택한 시작일
            val plantLocation = binding.plantAddEtLocation.text.toString() // 사용자가 선택한 재배지
            val memo = binding.plantAddEtMemo.text.toString() // 사용자가 입력한 메모
            val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
            val plantImageData = if (imageData != null) imageData else byteArrayOf()


            plant = Plant(deviceId, plantType, plantLocation, memo, selectedDate?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            } ?: "", plantImageData)


            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)


            // 데이터 요청
            apiService.postPlant(plant).enqueue(object : Callback<Plant> {
                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                    if (response.isSuccessful) {
                        val postPlant = response.body()
                        //Log.d("PlantAddFragment", "Plant created: ${postPlant}")
                        Log.d("check--- plant", "deviceid: ${plant.deviceId}, plantType: ${plant.plantType}, plantLocation: ${plant.plantLocation}, plantMemo: ${plant.memo}, firstPlantingDate: ${plant.firstPlantingDate}")
                        // 콜백 완료 후에 프래그먼트 이동
                        moveToPlantFragment()
                    } else {
                        Log.e("PlantAddFragment", "Failed to create plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Plant>, t: Throwable) {
                    Log.e("PlantAddFragment", "Network error: ${t.message}")
                }
            })
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
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate = cal.time // 선택한 날짜를 저장

            val dateTimeString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            binding.plantAddTvSelectdate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            plant.firstPlantingDate = dateTimeString // Plant 객체에 저장
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

                        val fileName = getFileNameFromUri(uri)
                        binding.plantAddTvUploadFileinfo.text = "파일 선택됨: $fileName"
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

//    private fun selectLocation() {
//        TODO("Not yet implemented")
//    }
//
//    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == android.app.Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val selectedImageUri = data?.data
//            // 선택한 이미지 처리
//            if (selectedImageUri != null) {
//                val fileName = getFileInfo(selectedImageUri)
//                val textView = binding.plantAddTvUploadFileinfo
//                textView.text = "파일 선택됨: $fileName"
//            }
//        }
//    }
//
//    private fun getFileInfo(uri: Uri): String {
//        var fileInfo = ""
//        val contentResolver = requireContext().contentResolver
//        val cursor = contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
//                if (columnIndex != -1) {
//                    fileInfo = it.getString(columnIndex) ?: ""
//                } else {
//                    fileInfo = "열이 없음"
//                }
//            } else {
//                fileInfo = "데이터 없음"
//            }
//        }
//        return fileInfo
//    }

}