package com.example.farmmate1

import android.os.Bundle
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiagnosisFragment : Fragment() {

    private var _binding: FragmentDiagnosisBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

//    var HistoryList = arrayListOf<History>(
//        History(R.drawable.number_one, "2023-06-01", "정상"),
//        History(R.drawable.number_two, "2023-06-01", "정상"),
//        History(R.drawable.number_three, "2023-06-01", "정상"),
//        History(R.drawable.number_four, "2023-06-01", "정상"),
//        History(R.drawable.number_five, "2023-06-01", "정상"),
//        History(R.drawable.number_six, "2023-06-01", "정상"),
//        History(R.drawable.number_seven, "2023-06-01", "정상"),
//        History(R.drawable.number_eight, "2023-06-01", "정상"),
//        History(R.drawable.number_nine, "2023-06-01", "정상"),
//        History(R.drawable.number_ten, "2023-06-01", "정상")
//    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // 데이터 요청
        apiService.getHistoryList().enqueue(object : Callback<List<History>> {
            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                if (response.isSuccessful) {
                    val historyList = response.body() as? ArrayList<History>
                    if (historyList != null) {
                        val adapter = HistoryAdapter(requireContext(), historyList)
                        binding.diagnosisListLvHistory.adapter = adapter
                    }
                } else {
                    // API 요청 실패 처리
                    Log.e("DiagnosisFragment", "Failed to fetch plant list: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                // 통신 오류 처리
                Log.e("DiagnosisFragment", "Network error: ${t.message}")
            }
        })

        // history 어댑터 연결
//        val Adapter = HistoryAdapter(requireContext(),HistoryList)
//        binding.diagnosisListLvHistory.adapter = Adapter

        // 진단하기 버튼 클릭 시 알림창 (진단할 작물 선택)
        binding.diagnosisBtnDiagnosis.setOnClickListener{
            //moveToDiagnosisSelectFragment()
            //moveToDiagnosisCameraFragment()
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

    private fun moveToDiagnosisSelectFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisSelectFragment())
        transaction.commit()
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
