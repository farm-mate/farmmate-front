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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    private var selectedPlantUuid: String = ""
    private var plantList: ArrayList<PlantGet>? = null

    private var _binding: FragmentDiaryAddBinding? = null
    private val binding get() = _binding!!      // !! -> non-null assertion

    private var imageData: ByteArray? = null
    private var image: MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryAddBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    private var checkedItems = mutableSetOf<String>() // 체크된 항목 기록

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스피너에 사용자 작물 목록 설정
        loadUserCropsToSpinner()

        // 스피너에서 항목을 선택할 때마다 선택한 작물의 plant_uuid를 저장
        binding.diaryAddSpinnerSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택한 작물의 plant_uuid 저장
                selectedPlantUuid = (binding.diaryAddSpinnerSelect.selectedItem as? Pair<String, String>)?.second ?: ""
                Log.d("DiaryAdd-- plant_uuid", "$selectedPlantUuid")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택하지 않았을 때, 첫 번째 항목을 기본값으로 설정
                selectedPlantUuid = (binding.diaryAddSpinnerSelect.getItemAtPosition(0) as? Pair<String, String>)?.second ?: ""

            }
        }

        // Post 요청할 때 selectedPlantUuid 사용
        // 예: saveDiaryEntry(selectedPlantUuid, ...)

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

            if (validateTemperature(temperatureText) && validateHumidity(humidityText)) {
                postDiary()
            }
        }

        //back button
        binding.diaryAddBackIb.setOnClickListener {
            moveToDiaryFragment()
        }
    }

    // 스피너에 사용자 작물 목록 설정
    private fun loadUserCropsToSpinner() {
        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // SharedPreferences에서 디바이스 ID 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId: String = sharedPreferences.getString(DEVICE_ID_KEY, "") ?: ""

        apiService.getPlantList(deviceId).enqueue(object : Callback<List<PlantGet>> {
            override fun onResponse(call: Call<List<PlantGet>>, response: Response<List<PlantGet>>) {
                val plantList = response.body() as? ArrayList<PlantGet>
                if (plantList != null) {
                    // 작물 이름과 plant_uuid를 매핑하여 리스트에 저장
                    val plantData = plantList.map { (it.plant_name ?: "Unknown") to it.plant_uuid }
                    val spinnerAdapter = object : ArrayAdapter<Pair<String, String>>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        plantData
                    ) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            val plantName = getItem(position)?.first
                            val textView = view.findViewById<TextView>(android.R.id.text1)
                            textView.text = plantName
                            return view
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            val plantName = getItem(position)?.first
                            val textView = view.findViewById<TextView>(android.R.id.text1)
                            textView.text = plantName
                            return view
                        }
                    }
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.diaryAddSpinnerSelect.adapter = spinnerAdapter
                } else {
                    Log.e("DiaryAddFragment", "Failed to fetch user crops: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                Log.e("DiaryAddFragment", "Network error: ${t.message}")
            }
        })
    }

    private fun postDiary() {

        Log.d("DiaryAdd-- Post plant_uuid","$selectedPlantUuid")
        val diaryDate = binding.diaryAddTvDate.text.toString()
        val plantUuid = selectedPlantUuid
        val plantWeather = binding.diaryAddEtWeather.text.toString()
        val temperature = binding.diaryAddEtTemperature.text.toString()
        val humidity = binding.diaryAddEtHumidity.text.toString()
        val waterFlag = binding.diaryAddCbWater.isChecked.toString()
        val fertilizeFlag = binding.diaryAddCbFert.isChecked.toString()
        val fertilizeName = binding.diaryAddEtFert.text.toString()
        val fertilizeUsage = binding.diaryAddEtFertuse.text.toString()
        val pesticideFlag = binding.diaryAddCbPes.isChecked.toString()
        val pesticideName = binding.diaryAddEtPes.text.toString()
        val pesticideUsage = binding.diaryAddEtPesuse.text.toString()
        val memo = binding.diaryAddEtMemo.text.toString()

        val requestBodyMap = hashMapOf<String, RequestBody>()
        requestBodyMap["plantUuid"] = createPartFromString(plantUuid)
        requestBodyMap["diaryDate"] = createPartFromString(diaryDate)
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

        // 이미지 데이터를 MultipartBody.Part로 변환
        val imagePart = image

        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        plantUuid?.let {
            apiService.postDiary(requestBodyMap, imagePart).enqueue(object : Callback<DiaryPost> {
                override fun onResponse(call: Call<DiaryPost>, response: Response<DiaryPost>) {
                    if (response.isSuccessful) {
                        Log.d("전송성공", "전송성공")
                        moveToDiaryFragment()
                    } else {
                        // 전송 실패
                        Log.d("전송실패", "전송실패")
                    }
                }

                override fun onFailure(call: Call<DiaryPost>, t: Throwable) {
                    // 전송 실패
                    Log.d("전송실패who", "전송실who패")
                }
            })
        } ?: Log.d("전송실패", "플랜트 UUID가 null입니다.")
    }

    private fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), string)
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
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()
                        imageData = bytes

                        // 이미지 파일의 MIME 타입 가져오기
                        val contentType = requireActivity().contentResolver.getType(uri)
                        //파일명
                        val fileName = getFileNameFromUri(uri)
                        binding.diaryAddTvUploadFileinfo.text = "파일 선택됨: $fileName"

                        // 이미지 파일을 MultipartBody.Part로 변환
                        val requestBody = RequestBody.create(MediaType.parse(contentType), bytes)
                        val multipart = MultipartBody.Part.createFormData("image", fileName, requestBody)
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

    private fun moveToDiaryFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiaryFragment())
        transaction.commit()
    }



//    fun getCheckedItems(): Set<String> {
//        return checkedItems
//    }
//
//    fun getSelectedDate(): CalendarDay? {
//        return (requireArguments().getSerializable(ARG_DATE) as? CalendarDay)
//    }

}