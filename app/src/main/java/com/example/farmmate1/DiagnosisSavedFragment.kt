package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.farmmate1.databinding.FragmentDiagnosisSavedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DiagnosisSavedFragment : Fragment() {

    private var _binding: FragmentDiagnosisSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var plantDiseaseUuid: String

    private var symptom: String? = ""
    private var cause: String? = ""
    private var treatment: String? = ""
    private var selectedButtonId: Int = R.id.diagnosis_result_btn_symptom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisSavedBinding.inflate(inflater, container, false)
        val view = binding.root

        plantDiseaseUuid = arguments?.getString("plantDiseaseUuid") ?: ""
        plantDiseaseUuid?.let { uuid ->

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("DiagnosisSavedFragment", "Uuid: $plantDiseaseUuid")

            apiService.getResult(plantDiseaseUuid).enqueue(object : Callback<History> {
                override fun onResponse(call: Call<History>, response: Response<History>) {
                    if (response.isSuccessful) {
                        val historyget = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("DiagnosisSavedFragment", "$historyget")

                        binding.diagnosisSavedTvName.text = historyget?.disease?.diseaseName
                        binding.diagnosisSavedTvDetail.text = historyget?.disease?.diseaseSymptom

                        // 사진
                        val plantImg = binding.diagnosisSavedIvPhoto

                        val imageUrl = historyget?.disease?.plantImg
                        ImageLoaderTask(plantImg).execute(imageUrl)

                        symptom = historyget?.disease?.diseaseSymptom
                        cause = historyget?.disease?.diseaseCause
                        treatment = historyget?.disease?.diseaseTreatment
                        val plantType = historyget?.disease?.plantName

                        if (historyget?.disease?.diseaseName == "정상") {
                            binding.diagnosisSavedBtnSymptom.visibility = View.GONE
                            binding.diagnosisSavedBtnCause.visibility = View.GONE
                            binding.diagnosisSavedBtnCure.visibility = View.GONE
                            binding.diagnosisSavedTvDetail.text = "$plantType 진단 결과 정상 작물로 예측됩니다."
                        }
                    } else {
                        // API 요청 실패 처리
                        Log.e("DiagnosisSavedFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<History>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("DiagnosisSavedFragment", "Network error: ${t.message}")
                }
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectButton(binding.diagnosisSavedBtnSymptom)

        // 증상
        binding.diagnosisSavedBtnSymptom.setOnClickListener {
            selectButton(it)
            binding.diagnosisSavedTvDetail.text = symptom
        }

        // 원인
        binding.diagnosisSavedBtnCause.setOnClickListener {
            selectButton(it)
            binding.diagnosisSavedTvDetail.text = cause
        }

        // 치료법
        binding.diagnosisSavedBtnCure.setOnClickListener {
            selectButton(it)
            binding.diagnosisSavedTvDetail.text = treatment
        }


        // back button
        binding.diagnosisSavedBackIb.setOnClickListener {
            moveToDiagnosisFragment()
        }
    }

    private fun selectButton(button: View) {
        // 이전에 선택된 버튼의 isSelected 속성을 false로 설정하여 선택 해제
        binding.diagnosisSavedBtnSymptom.isSelected = false
        binding.diagnosisSavedBtnCause.isSelected = false
        binding.diagnosisSavedBtnCure.isSelected = false

        // 현재 선택된 버튼의 isSelected 속성을 true로 설정하여 선택
        button.isSelected = true

        // 선택된 버튼의 ID를 추적
        selectedButtonId = button.id
    }

    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        transaction.commit()
    }

}