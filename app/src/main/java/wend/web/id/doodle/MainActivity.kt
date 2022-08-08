package wend.web.id.doodle

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.Image
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var ibBrush: ImageButton? = null
    private var ibGallery: ImageButton? = null
    private var ibUndo: ImageButton? = null
    private var ibRedo: ImageButton? = null
    private var ibSave: ImageButton? = null
    private var ibClear: ImageButton? = null

    private var selectedIbColor: ImageButton? = null
    private var ibPink: ImageButton? = null
    private var ibOrange: ImageButton? = null
    private var ibSkin: ImageButton? = null
    private var ibPurple: ImageButton? = null
    private var ibIndigo: ImageButton? = null
    private var ibRed: ImageButton? = null
    private var ibGreen: ImageButton? = null
    private var ibBlue: ImageButton? = null
    private var ibYellow: ImageButton? = null
    private var ibGray: ImageButton? = null
    private var ibBlack: ImageButton? = null
    private var ibWhite: ImageButton? = null
    private var customProgressDialog: Dialog? = null

    // open gallery and set ivBackground to selected image
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val ivBackground = findViewById<ImageView>(R.id.iv_background)
                ivBackground.setImageURI(result.data?.data)
            }
        }

    private fun clearIvBackground() {
        val ivBackground = findViewById<ImageView>(R.id.iv_background)
        ivBackground.setImageURI(null)
    }

    // read application permission
    private val getPermissions: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Read external storage permission is granted",
                            Toast.LENGTH_SHORT
                        )
                    }
                    if (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Write external storage permission is granted",
                            Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Read external storage permission is denied",
                            Toast.LENGTH_SHORT
                        )
                    }
                    if (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Write external storage permission is denied",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.dv)
        drawingView?.setSizeForBrush(20.toFloat())

        ibBrush = findViewById(R.id.ib_brush)
        ibBrush?.setOnClickListener {
            showBrushSizeDialog()
        }

        ibUndo = findViewById(R.id.ib_undo)
        ibUndo?.setOnClickListener {
            drawingView?.onClickUndo()
        }

        ibRedo = findViewById(R.id.ib_redo)
        ibRedo?.setOnClickListener {
            drawingView?.onClickRedo()
        }

        ibGallery = findViewById(R.id.ib_gallery)
        ibGallery?.setOnClickListener {
            if (isReadStorageAllowed()) { // open image gallery if read storage is allowed
                val pickIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(pickIntent)
            } else {
                requestStoragePermission()
            }
        }

        ibClear = findViewById(R.id.ib_clear)
        ibClear?.setOnClickListener {
            clearIvBackground()
        }

        ibSave = findViewById(R.id.ib_save)
        ibSave?.setOnClickListener {
            if (drawingView!!.isEmpty()){
                Toast.makeText(this,"No doodle found", Toast.LENGTH_SHORT).show()
            }else{
                if (isWriteStorageAllowed()) {
                    showProgressDialog()
                    lifecycleScope.launch {
                        val flDrawingView: FrameLayout = findViewById(R.id.fl_container)
                        saveBitmapFile(getBitmapFromView(flDrawingView))
                    }
                } else {
                    requestStoragePermission()
                }
            }
        }

        ibPink = findViewById(R.id.ib_pink)
        ibOrange = findViewById(R.id.ib_orange)
        ibSkin = findViewById(R.id.ib_skin)
        ibPurple = findViewById(R.id.ib_purple)
        ibIndigo = findViewById(R.id.ib_indigo)
        ibRed = findViewById(R.id.ib_red)
        ibGreen = findViewById(R.id.ib_green)
        ibBlue = findViewById(R.id.ib_blue)
        ibYellow = findViewById(R.id.ib_yellow)
        ibGray = findViewById(R.id.ib_gray)
        ibBlack = findViewById(R.id.ib_black)
        ibWhite = findViewById(R.id.ib_white)

        // default selected black
        selectedIbColor = ibBlack
        selectedIbColor!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        ibPink?.setOnClickListener {
            clickIbColor(ibPink!!)
        }
        ibOrange?.setOnClickListener {
            clickIbColor(ibOrange!!)
        }
        ibSkin?.setOnClickListener {
            clickIbColor(ibSkin!!)
        }
        ibPurple?.setOnClickListener {
            clickIbColor(ibPurple!!)
        }
        ibIndigo?.setOnClickListener {
            clickIbColor(ibIndigo!!)
        }
        ibRed?.setOnClickListener {
            clickIbColor(ibRed!!)
        }
        ibGreen?.setOnClickListener {
            clickIbColor(ibGreen!!)
        }
        ibBlue?.setOnClickListener {
            clickIbColor(ibBlue!!)
        }
        ibYellow?.setOnClickListener {
            clickIbColor(ibYellow!!)
        }
        ibGray?.setOnClickListener {
            clickIbColor(ibGray!!)
        }
        ibBlack?.setOnClickListener {
            clickIbColor(ibBlack!!)
        }
        ibWhite?.setOnClickListener {
            clickIbColor(ibWhite!!)
        }
    }

    private fun showBrushSizeDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size")
        val btnExtraSmall: ImageButton = brushDialog.findViewById(R.id.ib_extra_small_brush)
        val btnSmall: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        val btnMedium: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        val btnLarge: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)

        btnExtraSmall.setOnClickListener {
            drawingView?.setSizeForBrush(5.toFloat())
            brushDialog.dismiss()
        }

        btnSmall.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        btnMedium.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        btnLarge.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    private fun isReadStorageAllowed(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun isWriteStorageAllowed(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // request read permission
    private fun requestStoragePermission() {
        // if permission already denied
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) || (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        ) {
            showRationaleDialog(
                "Doodle App",
                "Need to access your external storage\nPermission is denied"
            )
        } else { // if permission already granted
            getPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    private fun clickIbColor(ib: ImageButton) {
        if (ib != selectedIbColor) { // only change if it is not already selected
            drawingView?.setColor(ib.tag.toString())
            ib.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
            selectedIbColor!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            selectedIbColor = ib
        }
    }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    // try to bitmap to png file
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val file =
                        File(externalCacheDir?.absoluteFile.toString() + File.separator + "DoodleApp_" + System.currentTimeMillis() / 1000 + ".png")
                    val fileOut = FileOutputStream(file)
                    fileOut.write(bytes.toByteArray())
                    fileOut.close()

                    result = file.absolutePath

                    // display toast can only happen on UI thread
                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File saved to $result",
                                Toast.LENGTH_SHORT
                            ).show()

                            share(FileProvider.getUriForFile(baseContext, "wend.web.id.doodle.fileprovider",file)) // share the saved image
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Save file failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                } finally {
                    runOnUiThread{
                        dismissProgressDialog()
                    }
                }
            }
        }
        return result
    }

    // show loading / progress
    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)
        // set the custom dialog screen to a layout resource
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.show()
    }

    // dismiss loading / progress
    private fun dismissProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    // function to share image result
    private fun share(uri: Uri) {
//        MediaScannerConnection.scanFile(this, arrayOf(result), null) {
//            _, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
//    }

}