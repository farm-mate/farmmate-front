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
        searchAdapter = SearchAdapter(mutableListOf()) // 변경된 부분
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


//    private fun searchDiseases(searchTerm: String) {
//        Log.d("SearchFragment", "검색어: $searchTerm")
//        // TODO: Retrofit을 사용하여 서버로 요청을 보내고 응답을 처리하는 코드 작성
//        // 여기서는 더미 데이터를 사용하여 결과를 표시합니다.
//        val dummyResults = listOf(
//            SearchResultItem("가지", "질병1", "Disease1", "thumb1", 1),
//            SearchResultItem("사과", "질병2", "Disease2", "thumb2", 2),
//            // 여기에 더 많은 결과 추가 가능
//        )
//
//        val filteredResults = dummyResults.filter { item ->
//            item.cropName.contains(searchTerm) || item.sickNameKor.contains(searchTerm) || item.sickNameEng.contains(searchTerm)
//        }
//
//        // 어댑터의 데이터를 업데이트
//        searchAdapter.updateData(filteredResults)
//    }
}

