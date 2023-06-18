package com.example.farmmate1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class PlantAdapter (val context: Context, val PlantList: ArrayList<Plant>) : BaseAdapter()
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
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item_plant,null)
        val profile = view.findViewById<ImageView>(R.id.list_item_profile)
        val name = view.findViewById<TextView>(R.id.list_item_name)
        val startdate = view.findViewById<TextView>(R.id.list_item_startdate)
        val favorite = view.findViewById<ImageView>(R.id.plant_info_back_ib)

        val plant = PlantList[position]

        profile.setImageResource(plant.profile)
        name.text = plant.name
        startdate.text = plant.startdate
        favorite.setImageResource(plant.favorite)

        return view
    }

}