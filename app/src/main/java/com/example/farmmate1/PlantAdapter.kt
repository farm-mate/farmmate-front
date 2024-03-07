package com.example.farmmate1

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class PlantAdapter (val context: Context, val PlantList: ArrayList<PlantGet>) : BaseAdapter()
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

        val plant = PlantList[position]

        plantName.text = plant.plant_name
        plantType.text = plant.plant_type
        firstPlantingDate.text = plant.first_planting_date
        // 북마크

        val imageUrl = plant.image_url
        ImageLoaderTask(plantImg).execute(imageUrl)

        return view
    }


}