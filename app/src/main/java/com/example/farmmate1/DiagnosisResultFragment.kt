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
import com.example.farmmate1.databinding.FragmentDiagnosisResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiagnosisResultFragment : Fragment() {
    private var _binding: FragmentDiagnosisResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisResultBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

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
//                    Log.e("DiagnosisResultFragment", "Failed to fetch plant list: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<History>>, t: Throwable) {
//                // 통신 오류 처리
//                Log.e("DiagnosisResultFragment", "Network error: ${t.message}")
//            }
//        })

        // back button
        binding.diagnosisResultBackIb.setOnClickListener {
            moveToDiagnosisFragment()
        }

        binding.diagnosisResultBtnRecheck.setOnClickListener {
            moveToDiagnosisCameraFragment()
        }

        binding.diagnosisResultBtnCause.setOnClickListener {
            binding.diagnosisResultTvDetail.text = "자낭각의 형태로 병든 식물체의 잔재에서 겨울을 지내고 1차 전염원이 되며, 시설재배에서는 분생포자가 공기전염 되어 계속해서 발생한다. 본 병은 일반적으로 15～28℃에서 많이 발생되며, 32℃이상의 고온에서는 병 발생이 억제된다. 노지포장에서는 억제재배 참외에서 심하게 발생한다. 특히 일조가 부족하고, 밤,낮의 온도차가 심하며, 다비재배를 할 때 병 발생이 많아진다."
        }

        binding.diagnosisResultBtnCure.setOnClickListener {
            binding.diagnosisResultTvDetail.text = "- 수확 후 병든 잔재물을 제거, 불태운다.\n" +
                    "- 질소질 비료의 편용을 피하고, 균형시비를 한다.\n" +
                    "- 하우스내에서는 기온의 일교차를 줄여준다.\n" +
                    "- 밀식을 피하고, 통풍이 잘되게 한다.\n" +
                    "- 발병 초기에 등록약제를 살포하여 초기의 병원균 밀도를 줄여준다."
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        transaction.commit()
    }

    private fun moveToDiagnosisCameraFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisCameraFragment())
        transaction.commit()
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