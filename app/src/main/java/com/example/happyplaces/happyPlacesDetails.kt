package com.example.happyplaces

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.databinding.ActivityHappyPlacesDetailsBinding
import com.example.happyplaces.room.happyPlacesData
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style


class happyPlacesDetails : AppCompatActivity() {
    private lateinit var binding: ActivityHappyPlacesDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHappyPlacesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var placesData:happyPlacesData?=null
        if(intent.hasExtra("FROM_MAIN_TO_DETAILS")){
            placesData=intent.getParcelableExtra("FROM_MAIN_TO_DETAILS") as happyPlacesData
        }
        if(placesData!=null){
            setSupportActionBar(binding.toolBar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title=placesData.title
            binding.toolBar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        binding.dImage.setImageURI(Uri.parse(placesData!!.image))
        binding.dDesc.setText(placesData!!.description)
        binding.dLocation.setText(placesData!!.location)
        binding.btn.setOnClickListener {
            val intent= Intent(this, MapActivity::class.java)
            intent.putExtra("FROM_DETAILS_TO_MAP", placesData)
            startActivity(intent)
        }

    }




}