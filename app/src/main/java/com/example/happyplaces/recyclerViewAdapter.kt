package com.example.happyplaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.room.happyPlacesData


class recyclerViewAdapter(val context: Context,val listener:click ):RecyclerView.Adapter<recyclerViewAdapter.viewModel>() {
    var list=ArrayList<happyPlacesData>()
    inner class viewModel(view: View):RecyclerView.ViewHolder(view){
        var image=view.findViewById<ImageView>(R.id.rImage)
        var title=view.findViewById<TextView>(R.id.rTitle)
        var desc=view.findViewById<TextView>(R.id.rDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewModel {
        val myView=LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
        var view=viewModel(myView)
        myView.setOnClickListener {
            listener.itemClick(list[view.adapterPosition])

        }
        return view

    }

    override fun onBindViewHolder(holder: viewModel, position: Int) {
         val myList=list[position]
        holder.image.setImageURI(Uri.parse(myList.image))
        holder.title.setText(myList.title)
        holder.desc.setText(myList.description)
    }

    override fun getItemCount(): Int {
       return list.size
    }
    fun updateList(myList:List<happyPlacesData>){
        list.clear()
        list.addAll(myList)
        notifyDataSetChanged()
    }

    fun editItems(activity:Activity,position :Int,requestCode:Int){
        val intent= Intent(context,happyPlacesActivity::class.java)
        intent.putExtra("EXTRA_PLACE_DETAILS",list[position])
       activity.startActivityForResult(intent,requestCode)

        notifyItemChanged(position)
    }
    interface click{
        fun itemClick(happyPlacesData: happyPlacesData)
    }
}