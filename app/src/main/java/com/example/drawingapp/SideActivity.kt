package com.example.drawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

import com.example.drawingapp.databinding.ActivitySideBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SideActivity : AppCompatActivity() {
    companion object{

        private val read_and_write=12
        private val store=10
    }
    private var count=1
     var currentPaint:ImageButton?=null
    private lateinit var binding: ActivitySideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding= ActivitySideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.drawingId.setSizeForBrush(20.toFloat())
        currentPaint=binding.colorId[2] as ImageButton
       // currentPaint!!.setImageResource(R.drawable.pallet_selectedl)
        currentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_selectedl)
        )
        binding.brush.setOnClickListener {
            totalBrush()
        }
        binding.gallery.setOnClickListener {

                checkPermission()

        }
        binding.undoButton.setOnClickListener {
            binding.drawingId.clickUndo()
        }
        binding.save.setOnClickListener {
            if( ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){

                bitmapAsync(getBitmap(binding.FrameId)).execute()

            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), read_and_write)
            }
        }


    }
    private fun checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            when {


                ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&   ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED -> {

               val intent= Intent(Intent.ACTION_PICK,
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,store)
                }

                  count==1-> {
                      count++
                      ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), read_and_write)

                  }

              shouldShowRequestPermissionRationale( android.Manifest.permission.WRITE_EXTERNAL_STORAGE)->
                                   showDialog(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, read_and_write)


                else -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), read_and_write)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK) {
            if (requestCode == store) {
                if (data!!.data != null) {
                    try{
                     binding.imageId.visibility=View.VISIBLE
                        binding.imageId.setImageURI(data!!.data)

                    }catch(e:Exception){
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun showDialog(permission: String,requestCode: Int){
        val builder= AlertDialog.Builder(this)
        builder.apply{
            setMessage("Permission to access your External Storage is required for this app")
            setTitle("Permission Required")
            setPositiveButton("OK"){ dialog, which->
                ActivityCompat.requestPermissions(this@SideActivity, arrayOf(permission),requestCode)
            }
            val dialog=builder.create()
            dialog.setCancelable(false)
            dialog.show()
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          if(requestCode == read_and_write) {
              if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  Toast.makeText(applicationContext, " permission is granted", Toast.LENGTH_SHORT).show()
              } else {
                  Toast.makeText(applicationContext, "sorry you cannot access storage files", Toast.LENGTH_SHORT).show()
              }
          }



    }
    fun paint(view:View){
        if(view!=currentPaint){
            val imageButton=view as ImageButton
            val colorTag=imageButton.tag.toString()
            binding.drawingId.selectColor(colorTag)
            imageButton.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_selectedl)
            )
            currentPaint!!.setImageDrawable(
                    ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )
            currentPaint=view



        }

    }
    private fun totalBrush(){
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.button_size)
        dialog.setTitle("BrushSize :")
        val verySmall=dialog.findViewById<ImageButton>(R.id.verySmall)
        val small=dialog.findViewById<ImageButton>(R.id.small)
        val medium=dialog.findViewById<ImageButton>(R.id.medium)
        val large=dialog.findViewById<ImageButton>(R.id.large)
        val veryLarge=dialog.findViewById<ImageButton>(R.id.veryLarge)

        verySmall.setOnClickListener {
            binding.drawingId.setSizeForBrush(5.toFloat())
            dialog.dismiss()
        }
        small.setOnClickListener {
            binding.drawingId.setSizeForBrush(10.toFloat())
            dialog.dismiss()
        }
        medium.setOnClickListener {
            binding.drawingId.setSizeForBrush(15.toFloat())
            dialog.dismiss()
        }
        large.setOnClickListener {
            binding.drawingId.setSizeForBrush(20.toFloat())
            dialog.dismiss()
        }
        veryLarge.setOnClickListener {
            binding.drawingId.setSizeForBrush(25.toFloat())
            dialog.dismiss()
        }
        dialog.show()
    }
    fun getBitmap(view:View):Bitmap{
        val returnBitmap= Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(returnBitmap)
        val bgImage=view.background
        if(bgImage!=null){
            bgImage.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }


    private inner class bitmapAsync(val bitmap:Bitmap):AsyncTask<Any,Void,String>(){
private lateinit var progressDialog:Dialog
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressBar()
        }
        override fun doInBackground(vararg params: Any?): String {
            var result =""
            if(bitmap!=null){
                try{
                    val bytes=ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val file= File(externalCacheDir!!.absoluteFile.toString()
                    + File.separator+"drawingImage_"+System.currentTimeMillis()/1000+".png")
                      val fos=FileOutputStream(file)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    result=file.absolutePath
                }catch(e:Exception){
                    result=""
                    e.printStackTrace()
                }
            }


            return result

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            stopProgressBar()
            if(!result!!.isEmpty()){
                Toast.makeText(applicationContext,"image saved successfully",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"Error in saving image",Toast.LENGTH_SHORT).show()
            }
            MediaScannerConnection.scanFile(this@SideActivity,arrayOf(result),null) { path, uri ->
                val shareIntent = Intent()
                shareIntent.action=Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                shareIntent.type="image/png"
                startActivity(
                        Intent.createChooser(
                                shareIntent,"Share to"
                        )
                )
            }
        }
        fun showProgressBar(){
            progressDialog= Dialog(this@SideActivity)
           progressDialog.setContentView(R.layout.progress_layout)
            progressDialog.show()
        }
        fun stopProgressBar(){
            progressDialog.dismiss()
        }

    }
}