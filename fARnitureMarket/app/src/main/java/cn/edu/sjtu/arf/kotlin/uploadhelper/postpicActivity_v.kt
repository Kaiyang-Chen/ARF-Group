package cn.edu.sjtu.arf.kotlin.uploadhelper
import android.view.Menu.FIRST
import android.view.Menu.NONE
import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import cn.edu.sjtu.arf.App
import cn.edu.sjtu.arf.Constants.serverUrl
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.databinding.ActivityArBinding.inflate
import cn.edu.sjtu.arf.databinding.ActivityPostpicBinding
import cn.edu.sjtu.arf.databinding.ActivityPostvideoBinding
import cn.edu.sjtu.arf.kotlin.loginhelper.loginstore
import cn.edu.sjtu.arf.kotlin.mehelper.Meinfo
import cn.edu.sjtu.arf.kotlin.uploadhelper.picstore.postpic
import cn.edu.sjtu.arf.kotlin.uploadhelper.prodstore.copystr
import cn.edu.sjtu.arf.kotlin.uploadhelper.videostore.postvideo
import com.google.ar.core.dependencies.e
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import cn.edu.sjtu.arf.kotlin.databinding.ActivityPostBinding

class PostViewStatev: ViewModel() {
    var enableSend = true
    var imageUri: Uri? = null
    var videoUri: Uri? = null
    var videoIcon = android.R.drawable.presence_video_online
}

class postpicActivityv : AppCompatActivity() {
    var vhere = prodstore.str

    private val client = OkHttpClient()
    private val serverUrl = "https://101.132.97.115/"
    private lateinit var view: ActivityPostvideoBinding
    private var enableSend = true
    private val viewState: PostViewStatev by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        view = ActivityPostvideoBinding.inflate(layoutInflater)

        setContentView(view.root)
        //view.videoButtonVideo.setImageResource(viewState.videoIcon)
        viewState.imageUri?.let { view.previewImageVideo.display(it) }

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

        val cropIntent = initCropIntentv()
        val forPickedResult =
            registerForActivityResult(ActivityResultContracts.GetContent(), fun(uri: Uri?) {
                uri?.let {
                    if (it.toString().contains("video")) {
                        viewState.videoUri = it}
                    else {
                        val inStream = contentResolver.openInputStream(it) ?: return
                        viewState.imageUri = mediaStoreAllocv("image/jpeg")
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
                    }
            } ?: run { Log.d("Pick media", "failed") }
            })

        findViewById<ImageButton>(R.id.albumButton_video).setOnClickListener {
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
                    viewState.imageUri?.let { view.previewImageVideo.display(it) }
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
                    doCropv(cropIntent)
                } else {
                    Log.d("TakePicture", "failed")
                }
            }
        findViewById<ImageButton>(R.id.cameraButton_video).setOnClickListener {
            viewState.imageUri = mediaStoreAllocv("image/jpeg")
            Takepicture_contract.launch(viewState.imageUri)
        }
        val CaptureVideo_contract =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()){
                viewState.videoIcon = android.R.drawable.presence_video_busy
                //view.videoButtonVideo.setImageResource(viewState.videoIcon)
            }

        /*view.videoButtonVideo.setOnClickListener {
            viewState.videoUri = mediaStoreAllocv("video/mp4")
            CaptureVideo_contract.launch(viewState.videoUri)
        }*/

        initListenerv()
        genear()
    }
    private fun initCropIntentv(): Intent? {
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

    private fun doCropv(intent: Intent?) {
        intent ?: run {
            viewState.imageUri?.let { view.previewImageVideo.display(it) }
            return
        }

        viewState.imageUri?.let {
            intent.data = it
            forCropResult.launch(intent)
        }
    }

    private fun mediaStoreAllocv(mediaType: String): Uri? {
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

    fun initListenerv() {
        var Login_main = findViewById<Button>(R.id.publish_video)
        //var init_str = prodstore.str.split(":")[1].split("}")[0]
        println(prodstore.copystr)
        println(prodstore.copystr)
        Login_main.setOnClickListener {
            postvideo(
                applicationContext, prodstore.copystr, "title",viewState.imageUri, viewState.videoUri
            ){ msg ->
                runOnUiThread {
                    toast("Video posted")
                }
                //finish()
            }
        }
    }

    fun genear(){
        var ge = findViewById<Button>(R.id.getar)
        //var init_str = prodstore.str.split(":")[1].split("}")[0]
        //println(init_str)
        ge.setOnClickListener {
            enterar(applicationContext, prodstore.copystr, "title")
        }
    }

    fun enterar(context: Context, uid: String?, name: String?){

        var jsonObj = mapOf(
            "UID" to uid,
            "name" to name,
        )

        println(jsonObj)
        var jsonobj = JSONObject(jsonObj)
        val req = okhttp3.Request.Builder()
            .addHeader("Cookie", App.loginHeader?.get("Cookie")?:"")
            .url(serverUrl + "generate_ar/")
            .post(
                RequestBody.create(
                    "application/json".toMediaType(),
                    jsonobj.toString()
                )
            )
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Toast.makeText(this@postpicActivityv,
                        "It is generating now!", Toast.LENGTH_SHORT).show()
                })
                Log.e("load", " failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(this@postpicActivityv,
                            "It is generating now!", Toast.LENGTH_SHORT).show()
                    })
                    Log.e("load", " successfully")
                    val responseReceived =
                        try{
                            JSONObject(response.body?.string() ?: "")
                        } catch (e: JSONException){
                            JSONObject()
                        }
                    println(responseReceived)
                }
            }
        })
    }


}