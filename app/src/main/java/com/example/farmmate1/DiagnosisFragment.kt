package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

        // 진단하기 버튼 클릭 시 페이지 이동 (진단할 작물 선택)
        binding.diagnosisHistoryBtnDiagnosis.setOnClickListener{
            moveToDiagnosisSelectFragment()
        }

        // 리스트 객체 클릭 시 진단 결과 페이지로 이동..
        binding.diagnosisListLvHistory.setOnItemClickListener { parent, view, position, id ->
            moveToDiagnosisResultFragment()}

    }

    private fun moveToDiagnosisSelectFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisSelectFragment())
        transaction.commit()
    }

    private fun moveToDiagnosisResultFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisResultFragment())
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    class MainViewModel : ViewModel() {

        private val _count = MutableLiveData<Int>()
        val count : LiveData<Int> get() = _count
        init {
            _count.value = 5
        }
        fun getUpdatedCount(plusCount: Int){
            _count.value = (_count.value)?.plus(plusCount)
        }
    }
}
