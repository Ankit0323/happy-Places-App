package com.example.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.happyplaces.databinding.ActivityHappyPlacesBinding
import com.example.happyplaces.room.happyPlacesData
import com.example.happyplaces.room.happyPlacesViewModel
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class happyPlacesActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: happyPlacesViewModel
    private  var placeOptions: PlaceOptions?=null
    var calendar =Calendar.getInstance()
    private var saveImageToInternal:Uri?=null
    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0
    private lateinit var fusedLocation:FusedLocationProviderClient
    private var editedPlaceData:happyPlacesData?=null
    private lateinit var datePicker:DatePickerDialog.OnDateSetListener
private lateinit var binding:ActivityHappyPlacesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("EXTRA_PLACE_DETAILS")){
            editedPlaceData=intent.getParcelableExtra("EXTRA_PLACE_DETAILS")as happyPlacesData
        }

        viewModel= ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)).get(happyPlacesViewModel::class.java)


        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        fusedLocation= LocationServices.getFusedLocationProviderClient(this)


        datePicker=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }
       // updateDate()

        if(editedPlaceData!=null){
            supportActionBar!!.title="Edit_Happy_Place"
            binding.title.setText(editedPlaceData!!.title)
            binding.decription.setText(editedPlaceData!!.description)
            binding.date.setText(editedPlaceData!!.date)
            binding.location.setText(editedPlaceData!!.location)
            mLatitude=editedPlaceData!!.latitude
            mLongitude=editedPlaceData!!.longitude
            saveImageToInternal= Uri.parse(
                    editedPlaceData!!.image
            )
            binding.imageCapture.setImageURI(saveImageToInternal)
            binding.save.setText("UPDATE")


        }
        binding.date.setOnClickListener(this)
        binding.addImage.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.location.setOnClickListener(this)
        binding.currLocationBtn.setOnClickListener(this)

    }
    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.date -> DatePickerDialog(this@happyPlacesActivity,
                    datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
            R.id.addImage -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Select Action")
                val items = arrayOf("Select photo from Gallery", "Capture photo from Camera")
                dialog.setItems(items) { pictureDialog, which ->
                    when (which) {
                        0 -> selectImageFromGallery()
                        1 -> selectImageFromCamera()
                    }
                }
                dialog.show()

            }
            R.id.save -> {
                when {
                    binding.title.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter the Title", Toast.LENGTH_SHORT).show()
                    }
                    binding.decription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter the Description", Toast.LENGTH_SHORT).show()
                    }
                    binding.location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter the Location", Toast.LENGTH_SHORT).show()
                    }
                    binding.date.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select the date", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternal == null -> {
                        Toast.makeText(this, "Please select/capture image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // setResult(Activity.RESULT_OK)
                        //
                        if (editedPlaceData == null) {

                            viewModel.insertData(
                                    happyPlacesData(
                                            binding.title.text.toString(),
                                            binding.decription.text.toString(),
                                            binding.date.text.toString(),
                                            binding.location.text.toString(),
                                            saveImageToInternal.toString(),
                                            mLatitude, mLongitude
                                    )
                            )

                            finish()
                        } else {

                            val id = editedPlaceData!!.id
                            val data = happyPlacesData(
                                    binding.title.text.toString(),
                                    binding.decription.text.toString(),
                                    binding.date.text.toString(),
                                    binding.location.text.toString(),
                                    saveImageToInternal.toString(),
                                    mLatitude, mLongitude
                            )
                            data.id = id
                            viewModel.updateData(data)

                            setResult(Activity.RESULT_OK)
                            finish()

                        }
                    }
                }
            }
            R.id.location -> {


                PlaceAutocomplete.clearRecentHistory(applicationContext)
                val intent = PlaceAutocomplete.IntentBuilder()
                        .accessToken(getString(R.string.access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#FFFFFFFF"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(this)

                startActivityForResult(intent, PLACES_CODE)

            }
            R.id.currLocationBtn -> {

                if (!isLocationEnabled()) {

                    Toast.makeText(applicationContext,
                            "your Location Provider is turned Off. Please turn On", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                } else {
                    Dexter.withContext(this)
                            .withPermissions(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,


                                    ).withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                    if (report!!.areAllPermissionsGranted()) {
                                      currLocationRequest()
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                                    showRationale()
                                    token?.cancelPermissionRequest()
                                }
                            }).onSameThread().check()

                }
            }
        }

    }
    fun updateDate(){
        val format="dd.MM.yyyy"
        val sdf=SimpleDateFormat(format, Locale.getDefault())
        binding.date.setText(sdf.format(calendar.time).toString())
    }
    fun selectImageFromGallery() {
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(intent, GALLERY)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                      showRationale()
                        token?.cancelPermissionRequest()
                    }
                }).onSameThread().check()
    }
    fun selectImageFromCamera() {
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA


                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                        showRationale()
                        token?.cancelPermissionRequest()
                    }
                }).onSameThread().check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            if(requestCode== GALLERY){
                if(data!=null){
                    try {
                        val contentUri = data.data
                        val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        saveImageToInternal= saveImageToInternalStorage(imageBitmap)
                        Log.e("savedImage", "path:: $saveImageToInternal")
                        binding.imageCapture.setImageBitmap(imageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@happyPlacesActivity,
                                "Failed to load Image", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap
                 saveImageToInternal= saveImageToInternalStorage(thumbnail)
                Log.e("savedImage", "path:: $saveImageToInternal")
                binding.imageCapture.setImageBitmap(thumbnail)
            }
            else if ( requestCode == PLACES_CODE) {
                binding.location.setText("")
                val feature = PlaceAutocomplete.getPlace(data)
                val location=PlaceAutocomplete.getPlace(data).center()
                mLatitude=location!!.latitude()
                mLongitude=location.longitude()
                binding.location.setText(feature.text())
                Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show()
            }

        }
    }

   fun showRationale(){
        AlertDialog.Builder(this)
                .setMessage("You denied the permission for this Feature." +
                        "It can can be enabled under Settings")
                .setPositiveButton("GO TO SETTINGS"){
                    _,_->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

                        val uri = Uri.fromParts("package", packageName, null)

                        intent.data = uri
                        startActivity(intent)

                    }catch (e: ActivityNotFoundException){
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("CANCEL"){
                    Dialog,_->
                    Dialog.dismiss()
                }.show()
    }

    private fun currLocationRequest(){
        var locationRequest=LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=1000
        locationRequest.numUpdates=1
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }
        fusedLocation.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper())
    }
    private val mLocationCallback=object:LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location =locationResult!!.lastLocation
            mLatitude=lastLocation.latitude
            mLongitude=lastLocation.longitude
            var currentTask=getCurrentAddress(applicationContext,mLatitude,mLongitude)
            currentTask.setAddressListener(object:getCurrentAddress.AddressListener{
                override fun onAddressFound(address: String?) {
                 binding.location.setText(address)
                }

                override fun onError() {
                   Toast.makeText(applicationContext,"error...",Toast.LENGTH_SHORT).show()
                }
            })
            currentTask.getAddress()

        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file= File(file, "${UUID.randomUUID()}.jpg")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
    companion object{
        private  const val GALLERY=1
        private  const val CAMERA=2
        private const val IMAGE_DIRECTORY ="Happy Places Images"
        private const val PLACES_CODE=3

    }
}


