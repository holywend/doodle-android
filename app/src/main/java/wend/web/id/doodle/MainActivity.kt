package wend.web.id.doodle

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var ibBrush: ImageButton? = null

    private var selectedIbColor: ImageButton? = null
    private var ibPink: ImageButton? = null
    private var ibCyan: ImageButton? = null
    private var ibTeal: ImageButton? = null
    private var ibPurple: ImageButton? = null
    private var ibIndigo: ImageButton? = null
    private var ibRed: ImageButton? = null
    private var ibGreen: ImageButton? = null
    private var ibBlue: ImageButton? = null
    private var ibYellow: ImageButton? = null
    private var ibGray: ImageButton? = null
    private var ibBlack: ImageButton? = null
    private var ibWhite: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.dv)
        drawingView?.setSizeForBrush(20.toFloat())

        ibBrush = findViewById(R.id.ib_brush)
        ibBrush?.setOnClickListener {
            showBrushSizeDialog()
        }

        ibPink = findViewById(R.id.ib_pink)
        ibCyan = findViewById(R.id.ib_cyan)
        ibTeal = findViewById(R.id.ib_teal)
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
        ibCyan?.setOnClickListener {
            clickIbColor(ibCyan!!)
        }
        ibTeal?.setOnClickListener {
            clickIbColor(ibTeal!!)
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
        val btnSmall: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        val btnMedium: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        val btnLarge: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)

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

}