package com.example.farmmate1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

//    var PlantList = arrayListOf<Plant>(
//        Plant("","딸기",  "재배지1", "메모1", "2023-05-19"),
//        Plant("","고추",  "재배지2", "메모2", "2023-05-20"),
//        Plant("","토마토",  "재배지3", "메모3", "2023-05-21"),
//        Plant("","포도",  "재배지4", "메모4", "2023-05-22"),
//        Plant("","파프리카",  "재배지5", "메모5", "2023-05-23"),
//        Plant("","딸기5",  "재배지9", "메모9", "2023-05-27")
//    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        // SharedPreferences에서 디바이스 ID 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val deviceId: String = sharedPreferences.getString(DEVICE_ID_KEY, "") ?: ""

        // 데이터 요청
        apiService.getPlantList(deviceId).enqueue(object : Callback<List<PlantGet>> {
            override fun onResponse(call: Call<List<PlantGet>>, response: Response<List<PlantGet>>) {
                val plantList = response.body() as? ArrayList<PlantGet>
                if (plantList != null) {
                    Log.d("PlantFragment", "$plantList")
                    val adapter = PlantAdapter(requireContext(),plantList)
                    binding.plantListLvPlants.adapter = adapter
                    //Log.d("view","$plant[0]")
                } else {
                    // API 요청 실패 처리
                    Log.e("PlantFragment", "Failed to fetch plant list: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PlantGet>>, t: Throwable) {
                // 통신 오류 처리
                Log.e("PlantFragment", "Network error: ${t.message}")
            }
        })

//        val Adapter = PlantAdapter(requireContext(),PlantList)
//        binding.plantListLvPlants.adapter = Adapter

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

}