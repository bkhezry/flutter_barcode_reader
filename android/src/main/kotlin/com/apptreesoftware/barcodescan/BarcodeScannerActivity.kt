package com.apptreesoftware.barcodescan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.yourcompany.barcodescan.R
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarcodeScannerActivity : Activity(), ZXingScannerView.ResultHandler {
  companion object {
    val REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100
    val TOGGLE_FLASH = 200
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_barcode_scanner)
    scannerView.setAutoFocus(true)
    scannerView.setAspectTolerance(0.5f)
  }

//  override fun onCreateOptionsMenu(menu: Menu): Boolean {
//    if (scannerView.flash) {
//      val item = menu.add(0,
//          TOGGLE_FLASH, 0, "Flash Off")
//      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//    } else {
//      val item = menu.add(0,
//          TOGGLE_FLASH, 0, "Flash On")
//      item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//    }
//    return super.onCreateOptionsMenu(menu)
//  }
//
//  override fun onOptionsItemSelected(item: MenuItem): Boolean {
//    if (item.itemId == TOGGLE_FLASH) {
//      scannerView.flash = !scannerView.flash
//      this.invalidateOptionsMenu()
//      return true
//    }
//    return super.onOptionsItemSelected(item)
//  }

  override fun onResume() {
    super.onResume()
    scannerView.setResultHandler(this)
    if (!requestCameraAccessIfNecessary()) {
      scannerView.startCamera()
    }
  }

  override fun onPause() {
    super.onPause()
    scannerView.stopCamera()
  }

  override fun handleResult(result: Result?) {
    val intent = Intent()
    intent.putExtra("SCAN_RESULT", result.toString())
    setResult(RESULT_OK, intent)
    finish()
  }

  private fun finishWithError(errorCode: String) {
    val intent = Intent()
    intent.putExtra("ERROR_CODE", errorCode)
    setResult(RESULT_CANCELED, intent)
    finish()
  }

  private fun requestCameraAccessIfNecessary(): Boolean {
    val array = arrayOf(Manifest.permission.CAMERA)
    if (ContextCompat
            .checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this, array,
          REQUEST_TAKE_PHOTO_CAMERA_PERMISSION)
      return true
    }
    return false
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      REQUEST_TAKE_PHOTO_CAMERA_PERMISSION -> {
        if (PermissionUtil.verifyPermissions(grantResults)) {
          scannerView.startCamera()
        } else {
          finishWithError("PERMISSION_NOT_GRANTED")
        }
      }
      else -> {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      }
    }
  }
}

object PermissionUtil {
  fun verifyPermissions(grantResults: IntArray): Boolean {
    if (grantResults.isEmpty()) {
      return false
    }
    for (result in grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false
      }
    }
    return true
  }
}
