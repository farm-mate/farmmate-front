package com.example.farmmate1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmate1.databinding.FragmentSearchBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.farmmate1.api.Disease
import com.example.farmmate1.api.SearchResponse

class SearchFragment : Fragment() {

    companion object {
        const val API_KEY: String = "20244d9c84ef1e7836356b62c321a6c729d6"
        const val SEARCH_URL: String = "http://ncpms.rda.go.kr/npmsAPI/"
    }

    private lateinit var binding: FragmentSearchBinding
    private var searchTerm: String = " "
    private var spinnerData: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var searchAdapter: SearchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        searchAdapter = SearchAdapter(mutableListOf()) { clickedItem ->
            // 클릭한 아이템의 sickKey를 가져와서 상세 페이지로 전달
            val sickKey = clickedItem.sickKey
            // 상세 페이지로 이동
            moveToSearchDetailFragment(sickKey)
        }
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val gson = GsonBuilder().setLenient().create()

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(SEARCH_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        // ApiService 인스턴스 생성
        val apiService = retrofit.create(ApiService::class.java)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 스피너에서 선택된 항목의 값을 저장
                spinnerData = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing to do
            }
        }

        binding.searchButton.setOnClickListener {
            searchTerm = binding.searchEditText.text.toString().trim()
            // 요청 보내기
            fetchSearchResults(apiService)
        }

        // 처음에 기본적인 리스트를 가져오기 위해 초기 검색을 수행합니다.
        fetchSearchResults(apiService)
    }

    private fun fetchSearchResults(apiService: ApiService) {
        // 검색어가 비어있지 않은 경우에만 요청을 보냅니다.
        if (searchTerm.isNotEmpty()) {
            val call: Call<SearchResponse> = if (spinnerData == "병 이름") {
                apiService.searchDiseases(API_KEY, "SVC01", "AA003", "", searchTerm, 20, 1)
            } else {
                apiService.searchDiseases(API_KEY, "SVC01", "AA003", searchTerm, "", 20, 1)
            }

            call.enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful) {
                        val searchResponse = response.body()
                        if (searchResponse != null) {
                            // 결과 처리
                            val results = searchResponse.service.list
                            searchAdapter.updateData(results)
                            Log.d("SearchFragment", "응답 성공: $searchResponse")
                        } else {
                            // 응답이 null일 경우 처리
                            val results = emptyList<Disease>()
                            searchAdapter.updateData(results)
                            Log.d("SearchFragment", "응답이 null입니다.")
                        }
                    } else {
                        // 요청 실패
                        Log.e("SearchFragment", "Failed to fetch plant list: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    // 네트워크 오류 등으로 요청 실패
                    Log.e("SearchFragment", "Network error: ${t.message}")
                }
            })
        }
    }

    private fun moveToSearchDetailFragment(sickKey: String) {
        val bundle = Bundle()
        bundle.putString("sickKey", sickKey)

        val searchDetailFragment = SearchDetailFragment()
        searchDetailFragment.arguments = bundle

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fl, searchDetailFragment)
            .addToBackStack(null) // 백 스택에 추가하여 뒤로가기를 위한 처리
            .commit()
    }

}

