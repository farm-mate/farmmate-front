package com.example.farmmate1


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.farmmate1.databinding.FragmentPlantEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PlantEditFragment : Fragment() {

    private var _binding: FragmentPlantEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var plantUuid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantEditBinding.inflate(inflater, container, false)
        val view = binding.root

        plantUuid = arguments?.getString("plantUuid") ?: ""
        plantUuid?.let { uuid ->

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("PlantEditFragment", "Uuid: $plantUuid")

            apiService.getPlant(plantUuid).enqueue(object : Callback<PlantGet> {
                override fun onResponse(call: Call<PlantGet>, response: Response<PlantGet>) {
                    if (response.isSuccessful) {
                        val plantget = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("PlantEditFragment", "$plantget")

                        binding.plantEditEtName.text = plantget?.plant_name.toEditable()
                        binding.plantEditTvType.text = plantget?.plant_type
                        binding.plantEditTvSelectdate.text = plantget?.first_planting_date
                        binding.plantEditEtLocation.text = plantget?.plant_location.toEditable()
                        binding.plantEditEtMemo.text = plantget?.memo.toEditable()
                        val plantImg = binding.plantEditIbImage

                        val imageUrl = plantget?.image_url
                        ImageLoaderTask(plantImg).execute(imageUrl)

                    } else {
                        // API 요청 실패 처리
                        Log.e("PlantEditFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PlantGet>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("PlantEditFragment", "Network error: ${t.message}")
                }
            })
        }?: run {
            // UUID가 null인 경우에 대한 처리
            Log.e("PlantEditFragment", "Plant UUID is null")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 (개별 식물 페이지로)
        binding.plantEditBackIb.setOnClickListener {
            moveToPlantInfoFragment()
        }

        // 날짜 선택 달력 띄우기
        binding.plantEditTvSelectdate.setOnClickListener{
            selectStartPlantDate()
        }

        // 수정 버튼
        binding.plantEditBtnEnroll.setOnClickListener {
            // PUT 요청
        }
    }

    private fun moveToPlantInfoFragment() {
        val bundle = Bundle()
        bundle.putString("plantUuid", plantUuid)

        val plantInfoFragment = PlantInfoFragment()
        plantInfoFragment.arguments = bundle

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, plantInfoFragment)
            .commit()
    }

    private var selectedDate: Date? = null

    private fun selectStartPlantDate() {
        val cal = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
            selectedDate = cal.time // 선택한 날짜를 저장

            binding.plantEditTvSelectdate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
        }
        DatePickerDialog(
            requireContext(),
            datePickerListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}