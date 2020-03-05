package com.example.tp1

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.hardware.Sensor
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import java.io.InputStream


class CanvasActivity : Activity() {

    private lateinit var btnChooseImage: Button;
    private lateinit var canvas: Canvas;
    private var paint: Paint = Paint();
    private lateinit var bitmap: Bitmap;
    private lateinit var imageView: ImageView;
    private var firstTouch: Boolean = true;
    private lateinit var imagePath: String;

    private var oldX: Float = -1f;
    private var oldY: Float = -1f;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        imageView = findViewById(R.id.imageView)
        paint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
        paint.strokeWidth = 10f
        btnChooseImage = findViewById(R.id.btnLoad)
        imageView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent?): Boolean {
                if (firstTouch) {
                    initCanvas(v);
                }
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> startLine(v, event)
                    MotionEvent.ACTION_MOVE -> drawLine(v, event)
                    MotionEvent.ACTION_UP -> endLine(v, event)
                }
                return true // v?.onTouchEvent(event) ?: true
            }
        })
        btnChooseImage.setOnClickListener(View.OnClickListener { chooseImage() })
        imagePath = intent.getStringExtra("imagePath");
    }

    fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val exifData = data?.getData()!!
            loadPicture(exifData)
        }
    }

    private fun loadPicture(exifData: Uri) {
        val ins: InputStream? = getContentResolver()?.openInputStream(exifData);
        bitmap = BitmapFactory.decodeStream(ins).copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            canvas = Canvas(bitmap);
            firstTouch = false;
            applyBlackAndWhiteFilter()
        } else {
            System.out.println("IMG IS NULL")
        }
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.canvas_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.canvas_menu_pen -> System.out.println("Pen menu item is clicked!")
            R.id.canvas_menu_revert -> System.out.println("Revert menu item is clicked!")
        }
        return true
    }*/

    private fun drawDot(view: View, event: MotionEvent) {
        canvas.drawCircle(event.x, event.y, 5f, paint);
        view.invalidate()
    }

    private fun startLine(view: View, event: MotionEvent) {
        oldX = event.x;
        oldY = event.y;
    }

    private fun drawLine(view: View, event: MotionEvent) {
        canvas.drawLine(oldX, oldY, event.x, event.y, paint);
        oldX = event.x;
        oldY = event.y;
        view.invalidate()
    }

    private fun endLine(view: View, event: MotionEvent) {
        canvas.drawLine(oldX, oldY, event.x, event.y, paint);
        oldX = -1f
        oldY = -1f
        view.invalidate()
    }

    private fun initCanvas(view: View) {
        firstTouch = false;
        bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);
        canvas = Canvas(bitmap);
    }

    private fun applyBlackAndWhiteFilter() {
        //         imageView.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN);
        val mxA = ColorMatrix()
        mxA.setSaturation(0f);
        val mxB = ColorMatrix();
        mxB.setScale(1f, .95f, .82f, 1.0f);
        mxA.setConcat(mxB, mxA);
        val paint = Paint();
        paint.setColorFilter(ColorMatrixColorFilter(mxA));
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

}
