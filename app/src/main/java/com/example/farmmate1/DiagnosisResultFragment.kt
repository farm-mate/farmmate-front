package com.example.farmmate1

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.farmmate1.databinding.FragmentDiagnosisBinding
import com.example.farmmate1.databinding.FragmentDiagnosisResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DiagnosisResultFragment : Fragment() {
    private var _binding: FragmentDiagnosisResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisResultBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private var selectedButtonId: Int = R.id.diagnosis_result_btn_symptom
    private var plantList: ArrayList<PlantGet>? = null
    private var plantData: List<Pair<String, String>> = emptyList()
    private var diseaseUuid: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val diagnosisResult = arguments?.getParcelable<DiagnosisResult>("diagnosisResult")

        var diseaseName: String? = ""
        var symptom: String? = ""
        var cause: String? = ""
        var treatment: String? = ""
        var plantType: String? = ""

        if (diagnosisResult != null) {
            diseaseUuid = diagnosisResult.diseaseUuid

            // 사진
            val plantImg = binding.diagnosisResultIvPhoto

            val imageUrl = diagnosisResult?.plantImg
            ImageLoaderTask(plantImg).execute(imageUrl)

            // 결과
            diseaseName = diagnosisResult.diseaseName
            binding.diagnosisResultTvName.text = diseaseName
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
                binding.diagnosisResultTvDetail.text = "$plantType 진단 결과 정상 작물로 예측됩니다."
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

        // plantList GET
        getPlantList()

        // 저장
        binding.diagnosisResultBtnSave.setOnClickListener {
            showPlantSelectionDialog()
        }

        // 재측정
        binding.diagnosisResultBtnRecheck.setOnClickListener {
            moveToDiagnosisCameraFragment(plantType)
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

    private fun moveToDiagnosisCameraFragment(plantType: String?) {
        val transaction = parentFragmentManager.beginTransaction()
        val fragment = DiagnosisCameraFragment()

        // 번들에 데이터 추가
        val bundle = Bundle()
        bundle.putString("selectedCrop", plantType)
        fragment.arguments = bundle

        transaction.replace(R.id.main_fl, fragment)
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

    private fun getPlantList(){
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
                    plantData = plantList.map { (it.plant_name ?: "Unknown") to it.plant_uuid }
                } else {
                    Log.e("DiaryResultFragment", "Failed to fetch user crops: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                Log.e("DiaryResultFragment", "Network error: ${t.message}")
            }
        })
    }

    private fun showPlantSelectionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("작물 선택")

        val plantNames = plantData.map { it.first }.toTypedArray()
        var selectedPlantIndex = 0 // 초기 선택된 항목의 인덱스를 추적

        // 라디오 버튼으로 작물 목록 표시
        builder.setSingleChoiceItems(plantNames, selectedPlantIndex) { dialog, which ->
            selectedPlantIndex = which // 선택한 항목의 인덱스 업데이트
        }

        // 다음 버튼
        builder.setPositiveButton("다음") { dialog, _ ->
            val selectedPlantUuid = plantData[selectedPlantIndex].second
            val request = DiagnosisSaveRequest(selectedPlantUuid, diseaseUuid)
            Log.d("DiagnosisResultFragment", "$selectedPlantUuid, $diseaseUuid")
            postDiagnosisSave(request)

            dialog.dismiss()
        }

        // 취소 버튼
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun postDiagnosisSave(request: DiagnosisSaveRequest) {
        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // POST 요청 보내기
        apiService.postDiagnosisSave(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 요청이 성공적으로 처리된 경우의 처리 로직
                    Log.d("DiagnosisResultFragment", "Diagnosis saved successfully.")
                    moveToDiagnosisFragment()
                } else {
                    // 요청이 실패한 경우의 처리 로직
                    Log.e("DiagnosisResultFragment", "Failed to save diagnosis: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 오류 또는 예외 발생 시의 처리 로직
                Log.e("DiagnosisResultFragment", "Network error: ${t.message}")
            }
        })
    }


}