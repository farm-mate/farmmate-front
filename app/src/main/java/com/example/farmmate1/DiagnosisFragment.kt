package com.example.farmmate1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AlertDialog
import com.example.farmmate1.databinding.FragmentDiagnosisBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DiagnosisFragment : Fragment() {

    private var _binding: FragmentDiagnosisBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: CalendarDay? = null
    private var selectedPlantName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisBinding.inflate(inflater, container, false)
        val view = binding.root


        // 상단바 get
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // SharedPreferences에서 디바이스 ID 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId: String = sharedPreferences.getString(DEVICE_ID_KEY, "") ?: ""

        apiService.getPlantList(deviceId).enqueue(object : Callback<List<PlantGet>> {
            override fun onResponse(call: Call<List<PlantGet>>, response: Response<List<PlantGet>>) {
                val plantList = response.body() as? ArrayList<PlantGet>
                if (plantList != null) {

                    setButtonTextForLinearLayout(plantList)
                    // 동적으로 버튼 추가
                    for (plant in plantList) {
                    }

                    for (i in 0 until 5) {
                        val button = binding.diagnosisLinearlayout.getChildAt(i) as? Button
                        button?.setOnClickListener {
                            // 클릭된 버튼을 강조하기 위해 색상 변경
                            onButtonClicked(button)
                            selectedPlantName = button?.text.toString()
                            //fetchDiaryListFromServer()
                        }
                    }

                    // 동적으로 생성된 버튼에 클릭 이벤트 처리기 추가
                    for (i in 5 until binding.diagnosisLinearlayout.childCount) {
                        val button = binding.diagnosisLinearlayout.getChildAt(i) as? Button
                        button?.setOnClickListener {
                            // 클릭된 버튼을 강조하기 위해 색상 변경
                            onButtonClicked(button)
                            selectedPlantName = button?.text.toString()
                            //fetchDiaryListFromServer()
                        }
                    }

                    // 맨 처음 버튼을 선택 상태로 지정
                    val firstButton = binding.diagnosisLinearlayout.getChildAt(0) as? Button
                    firstButton?.isSelected = true
                    selectedPlantName = firstButton?.text.toString()
                    //fetchDiaryListFromServer()

                } else {
                    // API 요청 실패 처리
                    Log.e("DiagnosisFragment", "Failed to fetch plant list: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                // 통신 오류 처리
                Log.e("DiaryFragment", "Network error: ${t.message}")
            }
        })

        return view
    }

    var HistoryList = arrayListOf<History>(
        History(R.drawable.number_one, "2023-06-01", "정상"),
        History(R.drawable.number_two, "2023-06-11", "상추균핵병"),
        History(R.drawable.number_three, "2023-07-07", "정상"),
        History(R.drawable.number_four, "2023-07-19", "정상"),
        History(R.drawable.number_five, "2023-08-03", "상추균핵병"),
        History(R.drawable.number_six, "2023-08-26", "정상"),
        History(R.drawable.number_seven, "2023-09-14", "정상"),
        History(R.drawable.number_eight, "2023-09-30", "상추노균병"),
        History(R.drawable.number_nine, "2023-10-01", "상추노균병"),
        History(R.drawable.number_ten, "2023-10-06", "정상")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // 데이터 요청
//        apiService.getHistoryList().enqueue(object : Callback<List<History>> {
//            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
//                if (response.isSuccessful) {
//                    val historyList = response.body() as? ArrayList<History>
//                    if (historyList != null) {
//                        val adapter = HistoryAdapter(requireContext(), historyList)
//                        binding.diagnosisListLvHistory.adapter = adapter
//                    }
//                } else {
//                    // API 요청 실패 처리
//                    Log.e("DiagnosisFragment", "Failed to fetch plant list: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<History>>, t: Throwable) {
//                // 통신 오류 처리
//                Log.e("DiagnosisFragment", "Network error: ${t.message}")
//            }
//        })

        // history 어댑터 연결
        val Adapter = HistoryAdapter(requireContext(),HistoryList)
        binding.diagnosisListLvHistory.adapter = Adapter

        // 진단하기 버튼 클릭 시 알림창 (진단할 작물 선택)
        binding.diagnosisBtnDiagnosis.setOnClickListener{
            showCropSelectionDialog()
        }

        // 리스트 객체 클릭 시 진단 결과 페이지로 이동..
        binding.diagnosisListLvHistory.setOnItemClickListener { parent, view, position, id ->
            moveToDiagnosisResultFragment()}

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    // LinearLayout에 버튼을 추가하는 함수
    fun setButtonTextForLinearLayout(plantList: List<PlantGet>) {
        for (i in 0 until plantList.size) {
            val plantName = plantList[i].plant_name
            if (i < 5) {
                // 처음 다섯 개의 버튼에 텍스트만 할당
                val button = binding.diagnosisLinearlayout.getChildAt(i) as? Button
                button?.text = plantName ?: ""
            } else {
                // 여섯 번째 요소부터 동적으로 버튼을 추가하며 텍스트 할당
                val button = Button(requireContext())
                button.text = plantName ?: ""
                val layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.button_width),
                    resources.getDimensionPixelSize(R.dimen.button_height)
                )
                val marginHorizontalPx = resources.getDimensionPixelSize(R.dimen.button_margin_horizontal)
                layoutParams.setMargins(marginHorizontalPx, 0, marginHorizontalPx, 0) // 좌우 마진과 상단 마진을 dimens.xml에서 가져온 값으로 설정
                button.layoutParams = layoutParams
                button.setTextColor(Color.BLACK) // 텍스트 색상 설정
                button.setBackgroundResource(R.drawable.rounded_btn)
                button.gravity = Gravity.CENTER

                button.setPadding(0, -3, 0, 0)
                layoutParams.weight = 1f

                button.setOnClickListener {
                    // 버튼이 클릭되었을 때 수행할 작업 추가
                }
                binding.diagnosisLinearlayout.addView(button)
            }
        }
    }

    // 모든 버튼을 클릭했을 때 호출되는 메서드
    fun onButtonClicked(clickedButton: Button) {
        // 모든 버튼을 탐색하면서 클릭된 버튼인지 확인하고 상태를 변경합니다.
        for (i in 0 until binding.diagnosisLinearlayout.childCount) {
            val button = binding.diagnosisLinearlayout.getChildAt(i) as? Button
            button?.isSelected = (button == clickedButton)
        }
    }

    private fun moveToDiagnosisCameraFragment(selectedCrop: String?) {
        val bundle = Bundle().apply {
            putString("selectedCrop", selectedCrop) // 선택한 작물을 번들에 저장
        }

        val fragment = DiagnosisCameraFragment().apply {
            arguments = bundle // 번들을 프래그먼트에 전달
        }

        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, fragment)
        transaction.commit()
    }

    private fun moveToDiagnosisResultFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisResultFragment())
        transaction.commit()
    }

    private var selectedCrop: String? = null // 선택한 작물을 저장할 변수

    private fun showCropSelectionDialog() {
        val crops = arrayOf("가지", "고추", "단호박", "딸기", "상추", "수박", "애호박", "오이", "쥬키니호박", "참외", "토마토", "포도")
        val checkedItem = -1

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("작물 종류를 선택하세요")
            .setSingleChoiceItems(crops, checkedItem) { dialog, which ->
                // 라디오 버튼을 클릭한 경우의 동작 설정
                selectedCrop = crops[which] // 선택한 작물 저장
            }
            .setPositiveButton("다음") { dialog, which ->
                // "다음" 버튼을 클릭한 경우의 동작 설정
                if (selectedCrop != null) {
                    moveToDiagnosisCameraFragment(selectedCrop!!)
                    dialog.dismiss() // 다이얼로그 닫기
                } else {
                    Toast.makeText(requireContext(), "진단할 작물을 선택하세요", Toast.LENGTH_SHORT).show()
                    // 왜 알림창이 닫힐까 ^^
                }
            }
            .setNegativeButton("취소") { dialog, which ->
                // "취소" 버튼을 클릭한 경우의 동작 설정
                dialog.dismiss() // 다이얼로그 닫기
            }
            .show() // 다이얼로그 표시
    }

//    class MainViewModel : ViewModel() {
//
//        private val _count = MutableLiveData<Int>()
//        val count : LiveData<Int> get() = _count
//        init {
//            _count.value = 5
//        }
//        fun getUpdatedCount(plusCount: Int){
//            _count.value = (_count.value)?.plus(plusCount)
//        }
//    }
}
