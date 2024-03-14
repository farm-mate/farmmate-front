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

    private var selectedButtonId: Int = R.id.diagnosis_result_btn_symptom

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diagnosisResult = arguments?.getParcelable<DiagnosisResult>("diagnosisResult")

        var diseaseName: String? = ""
        var symptom: String? = ""
        var cause: String? = ""
        var treatment: String? = ""
        var plantType: String? = ""

        if (diagnosisResult != null) {

            // 결과
            diseaseName = diagnosisResult.diseaseName
            binding.diagnosisResultTvName.text = diseaseName
            // 설명
            //binding.diagnosisResultTvDescribe.text = diagnosisResult.
            // 처음에는 증상
            binding.diagnosisResultTvDetail.text = diagnosisResult.diseaseSymptom

            plantType = diagnosisResult.plantName
            symptom = diagnosisResult.diseaseSymptom
            cause = diagnosisResult.diseaseCause
            treatment = diagnosisResult.diseaseTreatment

            if (diseaseName == "정상") {
                binding.diagnosisResultBtnSymptom.visibility = View.GONE
                binding.diagnosisResultBtnCause.visibility = View.GONE
                binding.diagnosisResultBtnCure.visibility = View.GONE
                binding.diagnosisResultTvDetail.text = "정상입니다."
            }

        } else {
            Log.e("DiagnosisResultFragment", "DiagnosisResult object is null")
        }

        selectButton(binding.diagnosisResultBtnSymptom)

        // 증상
        binding.diagnosisResultBtnSymptom.setOnClickListener {
            selectButton(it)
            binding.diagnosisResultTvDetail.text = symptom
        }

        // 원인
        binding.diagnosisResultBtnCause.setOnClickListener {
            selectButton(it)
            binding.diagnosisResultTvDetail.text = cause
        }

        // 치료법
        binding.diagnosisResultBtnCure.setOnClickListener {
            selectButton(it)
            binding.diagnosisResultTvDetail.text = treatment
        }


        // back button
        binding.diagnosisResultBackIb.setOnClickListener {
            moveToDiagnosisFragment()
        }

        // 재측정
        binding.diagnosisResultBtnRecheck.setOnClickListener {
            // TODO: plantName 전달
            moveToDiagnosisCameraFragment()
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

    // 버튼을 선택 상태로 변경하는 함수
    private fun selectButton(button: View) {
        // 이전에 선택된 버튼의 isSelected 속성을 false로 설정하여 선택 해제
        binding.diagnosisResultBtnSymptom.isSelected = false
        binding.diagnosisResultBtnCause.isSelected = false
        binding.diagnosisResultBtnCure.isSelected = false

        // 현재 선택된 버튼의 isSelected 속성을 true로 설정하여 선택
        button.isSelected = true

        // 선택된 버튼의 ID를 추적
        selectedButtonId = button.id
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