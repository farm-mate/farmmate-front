package com.example.farmmate1

import android.os.Bundle
import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.farmmate1.databinding.FragmentPlantAddBinding
import java.util.*


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