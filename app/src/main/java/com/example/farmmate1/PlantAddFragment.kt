package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.farmmate1.databinding.FragmentPlantAddBinding
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


class PlantAddFragment : Fragment() {

    private var _binding: FragmentPlantAddBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantAddBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private lateinit var plant: Plant

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 작물 종류 선택
        setUpSpinnerPlants()

        // 뒤로 가기 버튼 클릭 후 나의 식물 페이지로 이동
        binding.plantAddBackIb.setOnClickListener{
            moveToPlantFragment()
        }

        // 날짜 선택 달력 띄우기
        binding.plantAddTvSelectdate.setOnClickListener{
            selectStartPlantDate()
        }

        binding.plantAddLocationIb.setOnClickListener {
            selectLocation()
        }

        // 등록하기 버튼 클릭 후 나의 식물 페이지로 이동, 새로운 아이템 추가됨
        binding.plantAddBtnEnroll.setOnClickListener{

            //val profile = R.drawable.strawberry // 프로필 이미지
            val plantType = binding.plantAddSpinnerSelect.selectedItem.toString()
            //val nickname = binding.plantAddEtName.text.toString() // 사용자가 입력한 식물 닉네임
            val firstPlantingDate = binding.plantAddTvSelectdate.text.toString() // 사용자가 선택한 시작일
            val plantLocation = binding.plantAddEtLocation.text.toString() // 사용자가 선택한 재배지
            val memo = binding.plantAddEtMemo.text.toString() // 사용자가 입력한 메모
            //val favorite = R.drawable.star_filled // 즐겨찾기 이미지


            plant = Plant(plantType, plantLocation, memo, firstPlantingDate)
            Log.d("check--- plant", "plantType: " + plant.plantType + "\n" +"plantLocation: " + plant.plantLocation + "\n" + "plantMemo: " +  plant.memo + "\n" +"firstPlantingDate: "  + plant.firstPlantingDate);
            //plantData = PlantData(name, plantLocation, memo, firstPlantingDate)

            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            // 데이터 요청
            apiService.postPlant(plant).enqueue(object : Callback<Plant> {
                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                    if (response.isSuccessful) {
                        val postPlant = response.body()

                        // 생성된 식물에 대한 처리를 진행할 수 있습니다.
                        Log.d("PlantAddFragment", "Plant created: $postPlant")
                        moveToPlantFragment()
                    } else {
                        // API 요청 실패 처리
                        Log.e("PlantAddFragment", "Failed to create plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Plant>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("PlantAddFragment", "Network error: ${t.message}")
                }
            })

            //get 요청
//            apiService.postPlant(plant).enqueue(object : Callback<Plant> {
//                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
//                    if (response.isSuccessful) {
//                        val createdPlant = response.body()
//                        // 생성된 식물에 대한 처리를 진행할 수 있습니다.
//                        Log.d("PlantAddFragment", "Plant created: $createdPlant")
//                        moveToPlantFragment()
//                    } else {
//                        // API 요청 실패 처리
//                        Log.e("PlantAddFragment", "Failed to create plant: ${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<Plant>, t: Throwable) {
//                    // 통신 오류 처리
//                    Log.e("PlantAddFragment", "Network error: ${t.message}")
//                }
//            })
        }
    }

    private fun setUpSpinnerPlants() {
        val plants = resources.getStringArray(R.array.plants)
        val adapter = ArrayAdapter(requireContext(),R.layout.spinner_item_plant, plants)
        val spinner = binding.plantAddSpinnerSelect
        spinner.adapter = adapter
    }

    private fun moveToPlantFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, PlantFragment())
        transaction.commit()
    }

    private fun selectStartPlantDate() {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.plantAddTvSelectdate.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
        }
        DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun selectLocation() {
        TODO("Not yet implemented")
    }

}