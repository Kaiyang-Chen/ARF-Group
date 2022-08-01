package cn.edu.sjtu.arf.kotlin.uploadhelper
import android.view.Menu.FIRST
import android.view.Menu.NONE
import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem

import android.view.View

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.startActivity

import androidx.lifecycle.ViewModel
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ActivityArBinding.inflate
import cn.edu.sjtu.arf.databinding.ActivityPostpicBinding
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import cn.edu.sjtu.arf.kotlin.uploadhelper.picstore.postpic
//import cn.edu.sjtu.arf.kotlin.databinding.ActivityPostBinding

class PostViewState: ViewModel() {
    var enableSend = true
    var imageUri: Uri? = null

    var videoUri: Uri? = null
    var videoIcon = android.R.drawable.presence_video_online
}

class postpicActivity : AppCompatActivity() {
    var here = prodstore.str


    private lateinit var view: ActivityPostpicBinding
    private var enableSend = true
    private val viewState: PostViewState by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        view = ActivityPostpicBinding.inflate(layoutInflater)
        setContentView(view.root)

        //setContentView(view.root)
        //view.videoButton.setImageResource(viewState.videoIcon)
        viewState.imageUri?.let { view.previewImage.display(it) }
        //println(prodstore.str)
        //println(prodstore.str)

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    toast("${it.key} access denied")
                    finish()
                }
            }
        }.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE))

        val cropIntent = initCropIntent()
        val forPickedResult =
            registerForActivityResult(ActivityResultContracts.GetContent(), fun(uri: Uri?) {
                uri?.let {

                        val inStream = contentResolver.openInputStream(it) ?: return
                        viewState.imageUri = mediaStoreAlloc("image/jpeg")
                        viewState.imageUri?.let {
                            val outStream = contentResolver.openOutputStream(it) ?: return
                            val buffer = ByteArray(8192)
                            var read: Int
                            while (inStream.read(buffer).also { read = it } != -1) {
                                outStream.write(buffer, 0, read)
                            }
                            outStream.flush()
                            outStream.close()
                            inStream.close()
                        }

                    doCrop(cropIntent)
            } ?: run { Log.d("Pick media", "failed") }
            })

        findViewById<ImageButton>(R.id.albumButton).setOnClickListener {
            forPickedResult.launch("*/*")
        }
        forCropResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data.let {
                    viewState.imageUri?.run {
                        if (!toString().contains("ORIGINAL")) {
                            // delete uncropped photo taken for posting
                            contentResolver.delete(this, null, null)
                        }
                    }
                    viewState.imageUri = it
                    viewState.imageUri?.let { view.previewImage.display(it) }
                }
            } else {
                Log.d("Crop", result.resultCode.toString())
            }
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            toast("Device has no camera!")
            return
        }

        val Takepicture_contract =
            registerForActivityResult(ActivityResultContracts.TakePicture()){success ->
                if (success) {
                    doCrop(cropIntent)
                } else {
                    Log.d("TakePicture", "failed")
                }
            }
        findViewById<ImageButton>(R.id.cameraButton).setOnClickListener {
            viewState.imageUri = mediaStoreAlloc("image/jpeg")
            Takepicture_contract.launch(viewState.imageUri)
        }

        val CaptureVideo_contract =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()){
                viewState.videoIcon = android.R.drawable.presence_video_busy
                //view.videoButton.setImageResource(viewState.videoIcon)
            }

        /*view.videoButton.setOnClickListener {
            viewState.videoUri = mediaStoreAlloc("video/mp4")
            CaptureVideo_contract.launch(viewState.videoUri)
        }*/


        initListener()
    }
    private fun initCropIntent(): Intent? {
        // Is there any published Activity on device to do image cropping?
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val listofCroppers = packageManager.queryIntentActivities(intent, 0)
        // No image cropping Activity published
        if (listofCroppers.size == 0) {
            toast("Device does not support image cropping")
            return null
        }

        intent.component = ComponentName(
            listofCroppers[0].activityInfo.packageName,
            listofCroppers[0].activityInfo.name)

        // create a square crop box:
        intent.putExtra("outputX", 500)
            .putExtra("outputY", 500)
            .putExtra("aspectX", 1)
            .putExtra("aspectY", 1)
            // enable zoom and crop
            .putExtra("scale", true)
            .putExtra("crop", true)
            .putExtra("return-data", true)

        return intent
    }

    private fun doCrop(intent: Intent?) {
        intent ?: run {
            viewState.imageUri?.let { view.previewImage.display(it) }
            return
        }

        viewState.imageUri?.let {
            intent.data = it
            forCropResult.launch(intent)
        }
    }

    private fun mediaStoreAlloc(mediaType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, mediaType)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        return contentResolver.insert(
            if (mediaType.contains("video"))
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
    }

    fun initListener() {
        var Login_main = findViewById<Button>(R.id.publish_pic)
        //var init_str = prodstore.str.split(":")[1].split("}")[0]
        //println(init_str)
        Login_main.setOnClickListener {


            postpic(
                applicationContext, prodstore.str, "title",viewState.imageUri, viewState.videoUri
            ){
                println(prodstore.str)
            }
                startActivity(Intent(this, postpicActivityv::class.java))

        }
    }



}