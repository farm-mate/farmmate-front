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
import java.text.SimpleDateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


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

        plant = Plant("", "", "", "") // 초기화

        // 등록하기 버튼 클릭 후 나의 식물 페이지로 이동, 새로운 아이템 추가됨
        binding.plantAddBtnEnroll.setOnClickListener{

            //val profile = R.drawable.strawberry // 프로필 이미지
            val plantType = binding.plantAddSpinnerSelect.selectedItem.toString()
            //val nickname = binding.plantAddEtName.text.toString() // 사용자가 입력한 식물 닉네임
            val firstPlantingDate = binding.plantAddTvSelectdate.text.toString() // 사용자가 선택한 시작일
            val plantLocation = binding.plantAddEtLocation.text.toString() // 사용자가 선택한 재배지
            val memo = binding.plantAddEtMemo.text.toString() // 사용자가 입력한 메모

            plant = Plant(plantType, plantLocation, memo, selectedDate?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            } ?: "")


            // Retrofit 인스턴스 생성
            val retrofit = RetrofitClient.instance
            val apiService = retrofit.create(ApiService::class.java)

            // 데이터 요청
            apiService.postPlant(plant).enqueue(object : Callback<Plant> {
                override fun onResponse(call: Call<Plant>, response: Response<Plant>) {
                    if (response.isSuccessful) {
                        val postPlant = response.body()
                        Log.d("PlantAddFragment", "Plant created: $postPlant")
                        Log.d("check--- plant", "plantType: " + plant.plantType + "\n" +"plantLocation: " + plant.plantLocation + "\n" + "plantMemo: " +  plant.memo + "\n" +"firstPlantingDate: "  + plant.firstPlantingDate);


                        // 콜백 완료 후에 프래그먼트 이동
                        moveToPlantFragment()
                    } else {
                        Log.e("PlantAddFragment", "Failed to create plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Plant>, t: Throwable) {
                    Log.e("PlantAddFragment", "Network error: ${t.message}")
                }
            })
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

    private var selectedDate: Date? = null

    private fun selectStartPlantDate() {
        val cal = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate = cal.time // 선택한 날짜를 저장

            val dateTimeString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            binding.plantAddTvSelectdate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            plant.firstPlantingDate = dateTimeString // Plant 객체에 저장
        }
        DatePickerDialog(
            requireContext(),
            datePickerListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun selectLocation() {
        TODO("Not yet implemented")
    }

}