package com.example.farmmate1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.farmmate1.databinding.FragmentDiagnosisSelectBinding
import com.example.farmmate1.databinding.FragmentPlantAddBinding


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 작물 종류 선택 (등록한 작물)
        setUpSpinnerMyplants()

        // 작물 종류 선택 (등록하지 않은 작물)
        setUpSpinnerAnyplants()

        // 뒤로 가기 버튼 클릭 후 진단 메인 페이지로 이동
        binding.diagnosisSelectBackIb.setOnClickListener{
            moveToDiagnosisFragment()
        }
    }

    private fun setUpSpinnerMyplants() {
        val myplants = resources.getStringArray(R.array.myplants)
        val adapter = ArrayAdapter(requireContext(),R.layout.spinner_item_myplant, myplants)
        val spinner = binding.diagnosisSelectSpinnerMyplant
        spinner.adapter = adapter
    }

    private fun setUpSpinnerAnyplants() {
        val anyplants = resources.getStringArray(R.array.anyplants)
        val adapter = ArrayAdapter(requireContext(),R.layout.spinner_item_anyplant, anyplants)
        val spinner = binding.diagnosisSelectSpinnerAnyplant
        spinner.adapter = adapter
    }

    private fun moveToDiagnosisFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, DiagnosisFragment())
        transaction.commit()
    }

}