package com.example.farmmate1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmate1.databinding.FragmentSearchDetailBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SearchDetailFragment : Fragment() {

    private var _binding: FragmentSearchDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sickKey: String

    companion object {
        const val API_KEY: String = "20244d9c84ef1e7836356b62c321a6c729d6"
        const val SEARCH_URL: String = "http://ncpms.rda.go.kr/npmsAPI/"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val gson = GsonBuilder().setLenient().create()

        sickKey = arguments?.getString("sickKey") ?: ""
        sickKey?.let { Key ->

            // Retrofit 인스턴스 생성
            val retrofit = Retrofit.Builder()
                .baseUrl(SearchFragment.SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val apiService = retrofit.create(ApiService::class.java)

            Log.d("SearchDetailFragment", "Key: $sickKey")

            apiService.getDetail(API_KEY, "SVC05", sickKey).enqueue(object : Callback<SearchDetail> {
                override fun onResponse(call: Call<SearchDetail>, response: Response<SearchDetail>) {
                    if (response.isSuccessful) {
                        val searchDetail = response.body()
                        // 데이터를 받아온 후에 해당 데이터를 View에 설정해주는 작업을 수행

                        Log.d("SearchDetailFragment", "$searchDetail")

                        binding.searchDetailCropNameTv.text = searchDetail?.service?.cropName
                        binding.searchDetailSickNameKorTv.text = searchDetail?.service?.sickNameKor
                        val sickNameEng = searchDetail?.service?.sickNameEng ?: ""
                        val sickNameChn = searchDetail?.service?.sickNameChn ?: ""
                        binding.searchDetailSickNameEngTv.text = "$sickNameEng / $sickNameChn"

                        val imageContainer = binding.searchDetailLayoutImage // 이미지를 담을 컨테이너

                        if (searchDetail?.service?.imageList?.isNotEmpty() == true) {
                            val firstImage = searchDetail.service.imageList[0]
                            ImageLoaderTask(binding.searchDetailImage).execute(firstImage.image)
                        } else {
                            // 이미지 리스트가 비어있는 경우 이미지 컨테이너를 숨김
                            imageContainer.visibility = View.GONE
                        }

                        searchDetail?.service?.imageList?.size?.let { imageSize ->
                            for (i in 1 until imageSize) {
                                val image = searchDetail?.service?.imageList?.get(i)

                                // 동적으로 ImageView 생성
                                val imageView = ImageView(requireContext()).apply {
                                    layoutParams = ViewGroup.MarginLayoutParams(
                                        90.dpToPx(requireContext()), // width 설정
                                        90.dpToPx(requireContext()) // height 설정
                                    ).apply {
                                        marginStart = 4.dpToPx(requireContext()) // marginStart 설정
                                    }
                                    setImageResource(R.drawable.ic_launcher_foreground)
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                }

                                // 이미지를 비동기적으로 로드하고 설정
                                ImageLoaderTask(imageView).execute(image?.image)

                                // 생성한 ImageView를 이미지 컨테이너에 추가
                                imageContainer.addView(imageView)
                            }
                        }

                        binding.searchDetailContentSymptom.text = searchDetail?.service?.symptoms?.replace("<br/>", " ")

                        if (searchDetail?.service?.virusList?.isNotEmpty() == true) {
                            val firstVirus = searchDetail.service.virusList[0]
                            binding.searchDetailContentVirus.text = firstVirus.virusName
                        } else {
                            binding.searchDetailTitleVirus.visibility = View.GONE
                            binding.searchDetailContentVirus.visibility = View.GONE
                        }

                        binding.searchDetailContentCondition.text = searchDetail?.service?.developmentCondition?.replace("<br/>", " ")
                        binding.searchDetailContentPrevent.text = searchDetail?.service?.preventionMethod?.replace("<br/>", " ")

                    } else {
                        // API 요청 실패 처리
                        Log.e("SearchDetailFragment", "Failed to fetch plant: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<SearchDetail>, t: Throwable) {
                    // 통신 오류 처리
                    Log.e("SearchDetailFragment", "Network error: ${t.message}")
                }
            })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchDetailBackIb.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }


}