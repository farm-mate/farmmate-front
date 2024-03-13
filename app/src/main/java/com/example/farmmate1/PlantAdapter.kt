package com.example.farmmate1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantAdapter (val context: Context, val PlantList: ArrayList<PlantGet>, val clickListener: PlantItemClickListener) : BaseAdapter()
{
    override fun getCount(): Int {
        return PlantList.size
    }

    override fun getItem(position: Int): Any {
        return PlantList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item_plant, null)
        val plantName = view.findViewById<TextView>(R.id.list_item_name)
        val plantType = view.findViewById<TextView>(R.id.list_item_type)
        val firstPlantingDate = view.findViewById<TextView>(R.id.list_item_startdate)
        val plantImg = view.findViewById<ImageView>(R.id.list_item_profile)
        val bookmarkButton = view.findViewById<ImageButton>(R.id.list_item_bookmark)

        val plant = PlantList[position]

        plantName.text = plant.plant_name
        plantType.text = plant.plant_type
        firstPlantingDate.text = plant.first_planting_date
        // 북마크

        val imageUrl = plant.image_url
        ImageLoaderTask(plantImg).execute(imageUrl)

        Log.d("bookmark", "${plant.plant_name}, ${plant.bookmark?.bookmark_uuid}")
        var isBookmarked = false
        // 북마크 상태에 따라 이미지 설정
        if (plant.bookmark != null) {
            isBookmarked = true
            bookmarkButton.setImageResource(R.drawable.star_filled)
        } else {
            isBookmarked = false
            bookmarkButton.setImageResource(R.drawable.star)
        }

        bookmarkButton.setOnClickListener {
            // 북마크 상태 토글
            //plant.bookmark = !plant.bookmark
            // 북마크 상태에 따라 이미지 변경
            if (isBookmarked) {
                bookmarkButton.setImageResource(R.drawable.star)
            } else {
                bookmarkButton.setImageResource(R.drawable.star_filled)
            }

            //post 요청

            sendBookmarkToServer(plant.plant_uuid)
        }

        plantImg.setOnClickListener {
            val selectedPlantUuid = PlantList.get(position).plant_uuid
//            selectedPlantUuid?.let { plantUuid ->
//                (context as? PlantFragment)?.onItemClick(plantUuid)
//            }

            clickListener.onItemClick(selectedPlantUuid)
        }



//        plantImg.setOnClickListener {
//            // 이미지뷰 클릭 시 동작 수행
//            // 클릭된 아이템의 plant_uuid를 사용하여 원하는 동작을 수행하면 됩니다.
//            val selectedPlantUuid = PlantList?.get(position)?.plant_uuid
//            Log.d("plant 선택---", "$selectedPlantUuid")
//            val bundle = Bundle()
//            selectedPlantUuid?.let {
//                bundle.putString("plantUuid", it)
//            }
//
//            val plantInfoFragment = PlantInfoFragment()
//            plantInfoFragment.arguments = bundle
//
//            parentFragmentManager
//                .beginTransaction()
//                .replace(R.id.main_fl, plantInfoFragment)
//                .commit()
//        }
        return view
    }

    interface PlantItemClickListener {
        fun onItemClick(plantUuid: String)
    }

    private fun sendBookmarkToServer(plantUuid: String) {

        val retrofit = RetrofitClient.instance
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.postBookmark(plantUuid)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("BookMark", "BookMarkPosted")
                } else {
                    Log.e("c", "Failed to fetch plant: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BookMark", "Network error: ${t.message}")
            }
        })
    }
}

