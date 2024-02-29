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
import com.example.farmmate1.databinding.FragmentPlantBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantFragment : Fragment() {

    private var _binding: FragmentPlantBinding? = null
    private val binding get() = _binding!!

    // 데이터 보존
    companion object {
        fun newInstance(count : Int): PlantFragment{
            val args = Bundle()
            args.putInt("number", count)
            val fragment = PlantFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    var PlantList = arrayListOf<Plant>(
        Plant("","딸기", "재배지1", "메모1", "2023-05-19"),
        Plant("","고추", "재배지2", "메모2", "2023-05-20"),
//        Plant("토마토", "재배지3", "메모3", "2023-05-21"),
//        Plant("포도", "재배지4", "메모4", "2023-05-22"),
//        Plant("파프리카", "재배지5", "메모5", "2023-05-23"),
//        Plant("딸기2", "재배지6", "메모6", "2023-05-24"),
//        Plant("딸기3", "재배지7", "메모7", "2023-05-25"),
//        Plant("딸기4", "재배지8", "메모8", "2023-05-26"),
        Plant("","딸기5", "재배지9", "메모9", "2023-05-27")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val retrofit = RetrofitClient.instance
//        val apiService = retrofit.create(ApiService::class.java)
//
//        // 데이터 요청
//        apiService.getPlantList().enqueue(object : Callback<List<Plant>> {
//            override fun onResponse(call: Call<List<Plant>>, response: Response<List<Plant>>) {
//                if (response.isSuccessful) {
//                    val plantList = response.body() as? ArrayList<Plant>
//                    if (plantList != null) {
//                        val adapter = PlantAdapter(requireContext(), plantList)
//                        binding.plantListLvPlants.adapter = adapter
//                    }
//                } else {
//                    // API 요청 실패 처리
//                    Log.e("PlantFragment", "Failed to fetch plant list: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<Plant>>, t: Throwable) {
//                // 통신 오류 처리
//                Log.e("PlantFragment", "Network error: ${t.message}")
//            }
//        })

        val Adapter = PlantAdapter(requireContext(),PlantList)
        binding.plantListLvPlants.adapter = Adapter

        // 리스트 객체 클릭 시 식물 정보 페이지로 이동
        // 각 객체에 따른 식물정보(페이지)를 보여줄 수 있도록 수정
        binding.plantListLvPlants.setOnItemClickListener { parent, view, position, id ->
            Log.d("test", "event listener clicked . .")
            requireActivity().runOnUiThread {
                val transaction = parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fl, PlantInfoFragment())
                transaction.commit()
            }
        }

        // 식물 추가 버튼 클릭 후 식물 추가 페이지로 이동
        binding.plantAddBtn.setOnClickListener{
            moveToAddPlantFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 메모리 누수를 방지하기 위해 Fragment View에 대한 참조를 제거하여 가비지 컬렉터가 수거
        _binding = null
    }

    private fun moveToAddPlantFragment() {
        val transaction = parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, PlantAddFragment())
        transaction.commit()
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