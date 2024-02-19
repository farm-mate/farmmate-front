package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.farmmate1.databinding.FragmentDiagnosisSelectBinding
import com.example.farmmate1.databinding.FragmentPlantAddBinding
import android.widget.Spinner
import android.widget.AdapterView



class DiagnosisSelectFragment : Fragment() {

    private var _binding: FragmentDiagnosisSelectBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisSelectBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private lateinit var spinnerMyplant: Spinner
    private lateinit var spinnerAnyplant: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // 데이터 요청 (spinner myplant 에 들어갈 내 식물 목록만 받아오면 됨)
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

        // 작물 종류 선택 (등록한 작물)
        spinnerMyplant = binding.diagnosisSelectSpinnerMyplant
        val myplants = resources.getStringArray(R.array.myplants)
        val myplantsadapter = ArrayAdapter(requireContext(),R.layout.spinner_item_myplant, myplants)
        spinnerMyplant.adapter = myplantsadapter

        // 작물 종류 선택 (등록하지 않은 작물)
        spinnerAnyplant = binding.diagnosisSelectSpinnerAnyplant
        val anyplants = resources.getStringArray(R.array.anyplants)
        val anyplantsadapter = ArrayAdapter(requireContext(),R.layout.spinner_item_anyplant, anyplants)
        spinnerAnyplant.adapter = anyplantsadapter

        spinnerMyplant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent === spinnerMyplant) {

                    Log.d("TAG", "---초기화전 my onItemSelected: ${position}")

                    // Any 스피너 초기화
                    spinnerAnyplant.setSelection(0)

                    // 선택한 항목을 My 스피너에 표시
                    spinnerMyplant.setSelection(position)
                    Log.d("TAG", "---my onItemSelected: ${position}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spinnerAnyplant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent === spinnerAnyplant) {
                    Log.d("TAG", "---초기화전 onItemSelected: ${position}")

                    // My 스피너 초기화
                    spinnerMyplant.setSelection(0)


                    spinnerAnyplant.setSelection(position)
                    Log.d("TAG", "---any onItemSelected: ${position}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // 뒤로 가기 버튼 클릭 후 진단 메인 페이지로 이동
        binding.diagnosisSelectBackIb.setOnClickListener{
            moveToDiagnosisFragment()
        }
    }

    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        transaction.commit()
    }

}