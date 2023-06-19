package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.farmmate1.databinding.FragmentPlantAddBinding
import com.example.farmmate1.databinding.FragmentPlantInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantInfoFragment : Fragment() {

    private var _binding: FragmentPlantInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        // Retrofit 인스턴스 생성
        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        apiService.getPlant().enqueue(object : Callback<Plant> {
            override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                if (response.isSuccessful) {
                    val plant = response.body()
                    // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행
                    binding.plantInfoTvFirstdate.text = plant?.name
                    binding.plantInfoTvWaterdate.text = plant?.name
                    binding.plantInfoTvFertdate.text = plant?.name
                    binding.plantInfoTvPestdate.text = plant?.name
                } else {
                    // API 요청 실패 처리
                    Log.e("PlantInfoFragment", "Failed to fetch plant: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Plant>, t: Throwable) {
                // 통신 오류 처리
                Log.e("PlantInfoFragment", "Network error: ${t.message}")
            }
        })


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 (개별 식물 페이지로)
        binding.plantInfoBackIb.setOnClickListener {
            moveToPlantFragment()
        }

        // 식물 건강 확인하기 버튼
        binding.plantInfoBtnTest.setOnClickListener{
            moveToDiagnosisFragment()
        }

        // 다이어리 확인하기 버튼

    }

    private fun moveToPlantFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, PlantFragment())
        transaction.commit()
    }

    // 하단 바 교체해주어야
    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        // 뒤로가기 하면 이전 페이지로 갈 수 있도록.. transaction.addToBackStack(data)
        transaction.commit()
    }
}