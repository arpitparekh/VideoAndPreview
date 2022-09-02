package com.example.videoandpreview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videoandpreview.databinding.ActivityRecordVideoBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream


class RecordVideoActivity : AppCompatActivity() {

    lateinit var binding : ActivityRecordVideoBinding

    lateinit var launcher : ActivityResultLauncher<Intent>

    lateinit var launcher1: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)



        launcher1 = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        checkStoragePermission()

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {

                val uri = it.data?.data

                val intent = Intent(this,ShowVideoActivity::class.java)
                intent.putExtra("uri",uri.toString())
                startActivity(intent)

                try {
                    val newfile: File
                    val videoAsset =
                        it.data?.data?.let { it1 -> contentResolver.openAssetFileDescriptor(it1, "r") }
                    val `in`: FileInputStream = videoAsset!!.createInputStream()
                    val filepath: File = Environment.getExternalStorageDirectory()
                    val dir =
                        File(filepath.absolutePath.toString() + "/" + "Videos" + "/")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    newfile = File(dir, "myVideo" + ".mp4")
                    if (newfile.exists()) newfile.delete()
                    val out: OutputStream = FileOutputStream(newfile)


                    val buf = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }

                    `in`.close()
                    out.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            })

        binding.btnRecordVideo.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,120)
            launcher.launch(intent)

        }


    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

            // permission below Android R   // complete
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    101
                )
            }
        } else {

            // permission above android R   // if otherwise
            if (!Environment.isExternalStorageManager()) {

                // use intent to open settings and check permission from it for particular package
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))

//                startActivityForResult(intent, 2296);   // start activity for result is deprecated
                launcher1.launch(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}