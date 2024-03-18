package com.example.farmmate1

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.farmmate1.databinding.FragmentPlantInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantInfoFragment : Fragment() {

    private var _binding: FragmentPlantInfoBinding? = null
    private val binding get() = _binding!!

    private var plantUuid:String? = ""
    private var plantName:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantInfoBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantUuid = arguments?.getString("plantUuid")
        plantUuid?.let { uuid ->

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("PlantInfoFragment", "Uuid: $plantUuid")

            apiService.getPlant(plantUuid).enqueue(object : Callback<PlantGet> {
                override fun onResponse(call: Call<PlantGet>, response: Response<PlantGet>) {
                    if (response.isSuccessful) {
                        val plantget = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("PlantInfoFragment", "$plantget")

                        plantName = plantget?.plant_name
                        binding.plantInfoTvNickname.text = plantName
                        binding.plantInfoTvType.text = plantget?.plant_type
                        binding.plantInfoTvFirstdate.text = plantget?.first_planting_date
                        binding.plantInfoTvGetlocation.text = plantget?.plant_location
                        binding.plantInfoTvGetmemo.text = plantget?.memo
                        val plantImg = binding.plantInfoIvProfile

                        val imageUrl = plantget?.image_url
                        ImageLoaderTask(plantImg).execute(imageUrl)

                    } else {
                        // API 요청 실패 처리
                        Log.e("PlantInfoFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PlantGet>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("PlantInfoFragment", "Network error: ${t.message}")
                }
            })
        }?: run {
            // UUID가 null인 경우에 대한 처리
            Log.e("PlantInfoFragment", "Plant UUID is null")
        }

        // 뒤로가기 (식물 페이지로)
        binding.plantInfoBackIb.setOnClickListener {
            moveToPlantFragment()
        }

        // 식물 건강 확인하기 버튼
        binding.plantInfoBtnTest.setOnClickListener{
            moveToDiagnosisFragment()
        }

        // 다이어리 확인하기 버튼
        binding.planInfoBtnDiary.setOnClickListener {
            moveToDiaryFragment()
        }

        // 수정 버튼
        binding.plantInfoBtnEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("plantUuid", plantUuid)

            val plantEditFragment = PlantEditFragment()
            plantEditFragment.arguments = bundle

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fl, plantEditFragment)
                .commit()
        }

        // 삭제 버튼
        binding.plantInfoBtnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun moveToPlantFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, PlantFragment())
        transaction.commit()
    }

    // 하단 바 교체해주어야
    private fun moveToDiagnosisFragment() {
        val bundle = Bundle()
        bundle.putString("plantInfoName", plantName)

        val diagnosisFragment = DiagnosisFragment()
        diagnosisFragment.arguments = bundle

        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, diagnosisFragment)
        transaction.commit()
    }

    private fun moveToDiaryFragment() {
        val bundle = Bundle()
        bundle.putString("plantInfoName", plantName)

        val diaryFragment = DiaryFragment()
        diaryFragment.arguments = bundle

        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, diaryFragment)
        transaction.commit()
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("삭제 확인")
        builder.setMessage("정말로 이 항목을 삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            // 사용자가 확인을 눌렀을 때의 동작 수행
            deletePlant()
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            // 사용자가 취소를 눌렀을 때의 동작 수행 (아무것도 안 함)
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deletePlant(){
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.deletePlant(plantUuid)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                    moveToPlantFragment()
                    // 식물 등록이 성공하면 화면을 초기화하거나 다른 작업을 수행할 수 있음
                } else {
                    Toast.makeText(requireContext(), "식물 삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}