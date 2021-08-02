package com.example.volleymultipartexample

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var uploadImageBtn:Button
    lateinit var image:ImageView

    val URL = "http://admin.avrn.in/upload-profile"
    val uid = "SCRr7vjTEbaFSZldmqjol6upBiq2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = findViewById(R.id.imageUploadIV)
        uploadImageBtn = findViewById(R.id.uploadImageBtn)

        uploadImageBtn.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
            openGallery.type = "image/*"
            startActivityForResult(openGallery, 1)
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        val volleyMultipartRequest =
            object : VollyMultiplePartRequestJ(
                Method.POST, URL,
                {
                    try {
                        val obj = JSONObject(String(it.data))
                        Toast.makeText(
                            applicationContext,
                            obj.getString("status"),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("GotResponse", "uploadBitmap: ${it.statusCode}")
                        Log.d("GotResponse", "uploadBitmap: ${it.data}")
                        Log.d("GotResponse", "uploadBitmap: ${it}")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    Log.e("GotError", "" + it.message)
                }) {

                override fun getParams(): HashMap<String?, String?> {
                    val params = HashMap<String?, String?>()
                    params["uid"] = uid
                    return params
                }

                override fun getByteData(): HashMap<String?, DataPart?> {
                    val params = HashMap<String?, DataPart?>()
                    val imageName = System.currentTimeMillis()
                    params["user_profile"] =
                        DataPart("$imageName.png", getFileDataFromDrawable(bitmap)!!)
                    Log.d("getByteData", "getByteData: $params")
                    return params
                }
            }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && data!=null){
            val uri = data.data

            val openInputStream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(openInputStream)
            uploadBitmap(bitmap)
            image.setImageBitmap(bitmap)
        }
    }
}