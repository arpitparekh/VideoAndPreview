package com.example.videoandpreview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoandpreview.databinding.ActivityShowVideoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*


class ShowVideoActivity : AppCompatActivity() {

    lateinit var binding : ActivityShowVideoBinding
    var position:Int = 0
    lateinit var api : API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api = APIHelper.getInstance()

        val uri : Uri = Uri.parse(intent.getStringExtra("uri"))

        intent.getStringExtra("uri")?.let { Log.i("uri", it) }  // log

        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()

        binding.videoView.setOnPreparedListener {
            binding.videoView.seekTo(0)
            if(position==0){
                binding.videoView.start()
            }else{
                binding.videoView.pause()
            }
        }

        binding.ivPlay.setOnClickListener {
            binding.videoView.start()
        }
        binding.ivPause.setOnClickListener {
            if(binding.videoView.isPlaying){
                binding.videoView.pause()
            }
        }

        binding.btnGoBack.setOnClickListener {

            val intent = Intent(this,RecordVideoActivity::class.java)
            startActivity(intent)

        }

        binding.btnSend.setOnClickListener {

            val newfile: File
            val filepath: File = Environment.getExternalStorageDirectory()
            val dir =
                File(filepath.absolutePath.toString() + "/" + "Videos" + "/")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            newfile = File(dir, "myVideo" + ".mp4")
            val fis = FileInputStream(newfile)
            val bos = ByteArrayOutputStream()
            val buf = ByteArray(1024)
            try {
                var readNum: Int
                while (fis.read(buf).also { readNum = it } != -1) {
                    bos.write(buf, 0, readNum) //no doubt here is 0
                }
            } catch (ex: IOException) {
                Log.i("exception",ex.message.toString())
            }
            val bytes: ByteArray = bos.toByteArray()

            val str = String(bytes, StandardCharsets.UTF_8)

            val call = api.sendData(str)

            call.enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Toast.makeText(this@ShowVideoActivity,"Data Send Successfully",Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ShowVideoActivity,"Data Send UnSuccessful",Toast.LENGTH_SHORT).show()
                }


            })


        }

    }


}