package com.example.farmmate1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.farmmate1.data.*
import com.example.farmmate1.databinding.FragmentDiaryAddBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import com.prolificinteractive.materialcalendarview.*

class DiaryAddFragment : Fragment() {

    // 날짜 데이터 전달받아 인스턴스 생성
    companion object {
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: Calendar): DiaryAddFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            val fragment = DiaryAddFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var _binding: FragmentDiaryAddBinding? = null
    private val binding get() = _binding!!      // !! -> non-null assertion

    private var imageData: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryAddBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

//    private lateinit var spinner: Spinner
//    private lateinit var saveButton: Button
//    private lateinit var imageView: ImageView
//
//    private lateinit var diaryDataListener: DiaryDataListener
//    private lateinit var WriteDiaryTvDate: TextView
//    private lateinit var data: String

    private var checkedItems = mutableSetOf<String>() // 체크된 항목 기록

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스피너에 사용자 작물 목록 설정
        loadUserCropsToSpinner()

        // 일지 작성 페이지 상단에 기록하는 날짜 표시
        val selectedDate = arguments?.getSerializable(ARG_DATE) as? Calendar
        selectedDate?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(it.time)
            binding.diaryAddTvDate.text = formattedDate
        } ?: run {
            binding.diaryAddTvDate.text = "에러 발생" // 날짜가 없을 경우에 대한 처리 (빈 문자열로 표시)
        }

        binding.diaryAddIbImage.setOnClickListener {
            openGallery()
        }

        binding.diaryAddBtnEnroll.setOnClickListener {
            val selectedPlant = binding.diaryAddSpinnerSelect.selectedItem.toString()
            val temperatureText = binding.diaryAddEtTemperature.text.toString()
            val humidityText = binding.diaryAddEtHumidity.text.toString()
            val diaryImageData = if (imageData != null) imageData else byteArrayOf()

            if (validateTemperature(temperatureText) && validateHumidity(humidityText)) {
                postDiary(selectedPlant, diaryImageData)
            }
        }

        //back button
        val backButton: ImageButton = binding.diaryAddBackIb
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    // 스피너에 사용자 작물 목록 설정
    private fun loadUserCropsToSpinner() {
        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // 데이터 요청
        apiService.getUserCrops().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val userCrops = response.body() // 서버에서 받은 사용자 작물 목록
                    userCrops?.let {
                        // 스피너에 사용자 작물 목록 설정
                        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.diaryAddSpinnerSelect.adapter = spinnerAdapter
                    }
                } else {
                    Log.e("DiaryAddFragment", "Failed to fetch user crops: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("DiaryAddFragment", "Network error: ${t.message}")
            }
        })
    }

    private fun postDiary(selectedPlant: String, diaryImageData: ByteArray?) {

        // Diary 객체 생성
        val diary = Diary(
            plantName = selectedPlant,
            diaryDate = binding.diaryAddTvDate.text.toString(),
            plantWeather = binding.diaryAddEtWeather.toString(),
            temperature = binding.diaryAddEtTemperature.text.toString().toInt(),
            humidity = binding.diaryAddEtHumidity.text.toString().toInt(),
            waterFlag = binding.diaryAddCbWater.isChecked,
            fertilizeFlag = binding.diaryAddCbFert.isChecked,
            fertilizeName = binding.diaryAddEtFert.text.toString(),
            fertilizeUsage = binding.diaryAddEtFertuse.text.toString(),
            pesticideFlag = binding.diaryAddCbPes.isChecked,
            pesticideName = binding.diaryAddEtPes.text.toString(),
            pesticideUsage = binding.diaryAddEtPesuse.text.toString(),
            memo = binding.diaryAddEtMemo.text.toString(),
            imageData = diaryImageData
        )

        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        apiService.postDiary(diary).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("전송성공","전송성공")
                } else {
                    // 전송 실패
                    Log.d("전송실패","전송실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 전송 실패
                Log.d("전송실패who","전송실who패")
            }
        })
    }

    private fun validateTemperature(temperatureText: String): Boolean {
        if (temperatureText.isEmpty()) {
            // 온도가 비어있는 경우
            Toast.makeText(requireContext(), "온도를 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        val temperature = temperatureText.toIntOrNull()
        if (temperature == null) {
            // 온도가 숫자로 변환할 수 없는 경우
            Toast.makeText(requireContext(), "온도는 숫자로 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (temperature < -40 || temperature > 40) {
            // 온도가 범위를 벗어난 경우
            Toast.makeText(requireContext(), "온도는 -40도에서 50도 사이로 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun validateHumidity(humidityText: String): Boolean {
        if (humidityText.isEmpty()) {
            // 습도가 비어있는 경우
            Toast.makeText(requireContext(), "습도를 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        val humidity = humidityText.toIntOrNull()
        if (humidity == null) {
            // 습도가 숫자로 변환할 수 없는 경우
            Toast.makeText(requireContext(), "습도는 숫자로 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (humidity < 50 || humidity > 100) {
            // 습도가 범위를 벗어난 경우
            Toast.makeText(requireContext(), "습도는 50에서 100 사이로 입력하세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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
                        binding.diaryAddTvUploadFileinfo.text = "파일 선택됨: $fileName"
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



//    fun getCheckedItems(): Set<String> {
//        return checkedItems
//    }
//
//    fun getSelectedDate(): CalendarDay? {
//        return (requireArguments().getSerializable(ARG_DATE) as? CalendarDay)
//    }

}