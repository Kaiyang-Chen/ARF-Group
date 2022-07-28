/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.sjtu.arf.kotlin.ar

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.common.helpers.*
import com.google.ar.core.*
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*


/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3D model.
 */
class HelloArActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "HelloArActivity"
  }
  private lateinit var arFragment: ArFragment

  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper


  val instantPlacementSettings = InstantPlacementSettings()
  val depthSettings = DepthSettings()
  private val GLTF_ASSET =
    "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ar)
    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
    arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
      val anchor = hitResult.createAnchor()
      placeObject(arFragment, anchor, Uri.parse("saucepan.sfb"))
    }
    btn_back.setOnClickListener {
      onBackPressed()
    }

  }
  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }
  private fun placeObject(arFragment: ArFragment, anchor: Anchor, uri: Uri) {
    ModelRenderable.builder()
      .setSource(arFragment.context, uri)
      .build()
      .thenAccept({ modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable) })
      .exceptionally { throwable ->
        Toast.makeText(arFragment.context, "Error:" + throwable.message, Toast.LENGTH_LONG)
          .show()
        null
      }
/*    ModelRenderable.builder()
      .setSource(
        this, RenderableSource.builder().setSource(
          this,
          Uri.parse(GLTF_ASSET),
          RenderableSource.SourceType.GLTF2
        )
          .setScale(0.5f) // Scale the original model to 50%.
          .setRecenterMode(RenderableSource.RecenterMode.ROOT)
          .build()
      )
      .setRegistryId(GLTF_ASSET)
      .build()
      .thenAccept { modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable)
      }
      .exceptionally { throwable: Throwable? ->
        val toast = Toast.makeText(
          this, "Unable to load renderable " +
                  GLTF_ASSET, Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
        null
      }*/

  }
  private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {
    val anchorNode = AnchorNode(anchor)
    val node = TransformableNode(arFragment.transformationSystem)
    node.renderable = renderable
    node.setParent(anchorNode)
    arFragment.arSceneView.scene.addChild(anchorNode)
    node.select()
  }
  // Configure the session, using Lighting Estimation, and Depth mode.
  fun configureSession(session: Session) {
    session.configure(
      session.config.apply {
        lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

        // Depth API is used if it is configured in Hello AR's settings.
        depthMode =
          if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            Config.DepthMode.AUTOMATIC
          } else {
            Config.DepthMode.DISABLED
          }

        // Instant Placement is used if it is configured in Hello AR's settings.
        instantPlacementMode =
          if (instantPlacementSettings.isInstantPlacementEnabled) {
            InstantPlacementMode.LOCAL_Y_UP
          } else {
            InstantPlacementMode.DISABLED
          }
      }
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    results: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, results)
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
        .show()
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this)
      }
      finish()
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }
}
