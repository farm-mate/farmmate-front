package com.example.farmmate1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookmarkAdapter(private val context: Context, private val bookmarkedPlants: List<PlantGet>) : BaseAdapter() {

    override fun getCount(): Int {
        return bookmarkedPlants.size
    }

    override fun getItem(position: Int): Any {
        return bookmarkedPlants[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_bookmark, parent, false)

        val plantNameTextView: TextView = view.findViewById(R.id.bookmark_list_item_name)
        val plantTypeTextView: TextView = view.findViewById(R.id.bookmark_list_item_type)
        val firstPlantingDateTextView: TextView = view.findViewById(R.id.bookmark_list_item_startdate)
        val bookmarkImg = view.findViewById<ImageView>(R.id.bookmark_list_item_profile)

        val plant = bookmarkedPlants[position]
        plantNameTextView.text = plant.plant_name
        plantTypeTextView.text = plant.plant_type
        firstPlantingDateTextView.text = plant.first_planting_date

        val imageUrl = plant.image_url
        ImageLoaderTask(bookmarkImg).execute(imageUrl)

        view.setOnClickListener {
            // 해당 항목의 plant_uuid 가져오기
            val plantUuid = plant.plant_uuid

            // 다음 프래그먼트로 전환하기 위해 번들에 데이터 추가
            val bundle = Bundle()
            bundle.putString("plantUuid", plantUuid)

            val plantInfoFragment = PlantInfoFragment()
            plantInfoFragment.arguments = bundle
//
            // 다음 프래그먼트로 이동하고 번들에 담긴 데이터 전달
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_fl, plantInfoFragment)
            transaction.addToBackStack(null)
            transaction.commit()

//        parentFragmentManager
//            requireActivity().supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.main_fl, plantInfoFragment)
//                .commit()

//            val transaction = parentFragmentManager
//                .beginTransaction()
//                .replace(R.id.main_fl, diaryAddFragment)
//            transaction.commit()
        }

        return view
    }
}
