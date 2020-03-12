package com.example.tp1

import android.R.attr.src
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.tp1.dataclasses.Sticker
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import java.io.File
import java.io.InputStream


class CanvasActivity : Activity(), SensorEventListener {

    private var last_x: Float = 0f
    private var last_y: Float = 0f
    private var last_z: Float = 0f
    private var lastUpdate: Long = 0
    private lateinit var btnChooseImage: ImageView;
    private lateinit var btnChooseColor: ImageView;
    private lateinit var btnNextFilter: ImageView;
    private lateinit var canvas: Canvas;
    private var paint: Paint = Paint();
    private lateinit var bitmap: Bitmap;
    private lateinit var imageView: ImageView;
    private var firstTouch: Boolean = true;
    private var imagePath: String? = null;
    private var originalImage: Bitmap? = null;
    private var originalFilterImage: Bitmap? = null;
    private var busy: Boolean = false;

    private var oldX: Float = -1f;
    private var oldY: Float = -1f;
    private lateinit  var s: Sticker;

    enum class FilterType {
        NORMAL,
        BLACK_AND_WHITE,
        INVERTED
    }
    private var currentFilter: FilterType = FilterType.NORMAL

    private lateinit var powerMenu: PowerMenu;

    private val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem> = object : OnMenuItemClickListener<PowerMenuItem> {
        override fun onItemClick(position: Int, item: PowerMenuItem) {
            Toast.makeText(baseContext, item.title, Toast.LENGTH_SHORT).show()
            powerMenu.selectedPosition = position // change selected item
            powerMenu.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        imageView = findViewById(R.id.imageView)
        paint.setColor(Color.BLACK)
        paint.strokeWidth = 10f
        btnChooseImage = findViewById(R.id.btnLoad)
        btnChooseColor = findViewById(R.id.btnColor)
        btnNextFilter = findViewById(R.id.btnNextFilter)
        println("================= > On passe dans mon code")
        imageView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent?): Boolean {
                if (firstTouch) {
                    initCanvas(v);
                }
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> if (false) startLine(v, event);
                    MotionEvent.ACTION_MOVE ->{
                      if (false) { // TODO
                          drawLine(v, event)
                      } else {
                         // println(s)
                         // println("x= "+ event.x + " y = " + event.y)
                          if (s.left < event.getX() && event.getX() < s.left + s.right
                              && s.top < event.getY() && event.getY() < s.top + s.bottom) {
                              println("repositionnement")
                              s.left = event.getX() - 128;
                              s.top  = event.getY() - 128;
                              s.bottom = s.top + 256f;
                              s.right = s.left + 256f;
                              drawSticker(s)
                          }
                      }
                    }
                    MotionEvent.ACTION_UP -> if (false) endLine(v, event)
                }
                return true // v?.onTouchEvent(event) ?: true
            }
        })
        btnChooseImage.setOnClickListener(View.OnClickListener { chooseImage() })
        btnChooseColor.setOnClickListener(View.OnClickListener { chooseColor() })
        btnNextFilter.setOnClickListener(View.OnClickListener { nextFilter() })
        s = Sticker(R.drawable.emoji_happy, 0f, 0f, 256f, 256f)
        powerMenu = PowerMenu.Builder(this)
            .addItemList(listOf(PowerMenuItem("Novel"), PowerMenuItem("Poetry"), PowerMenuItem("Art"))) // list has "Novel", "Poerty", "Art"
            .addItem(PowerMenuItem("Journals", false)) // add an item.
            .addItem(PowerMenuItem("Travel", false)) // aad an item list.
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()
        try {
            imagePath = intent.getStringExtra("imagePath");
            loadPicture(Uri.fromFile(File(imagePath)))
        } catch (e: Exception) {
            // hack
        }
        var mSensorMgr = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    private fun chooseColor() {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("ok") { dialog, selectedColor, allColors -> }
            .setOnColorSelectedListener { selectedColor -> System.out.println("ok" + selectedColor); paint.setColor(selectedColor) }
            // .setNegativeButton("cancel") { dialog, which -> paint.setColor(Color.BLACK) }
            .build()
            .show()
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
        originalImage = BitmapFactory.decodeStream(ins);
        originalFilterImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap != null) {
            redrawImageView()
            drawSticker(s)
        } else {
            System.out.println("IMG IS NULL")
        }
    }

    private fun redrawImageView() {
        imageView.setImageBitmap(bitmap);
        canvas = Canvas(bitmap);
        firstTouch = false;
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

    private fun drawSticker(st: Sticker) {
        bitmap = originalFilterImage!!.copy(Bitmap.Config.ARGB_8888, true);
        imageView.setImageBitmap(bitmap!!);
        canvas = Canvas(bitmap!!);
        // draw current sticker
        var paint0 = Paint();
        paint.isAntiAlias = false;
        paint.isFilterBitmap = true;
        paint.isDither = true;
        var sticker = BitmapFactory.decodeResource(resources, st.drawableId);
        canvas.drawBitmap(sticker, null, RectF(st.left, st.top, st.right, st.bottom), paint0)
        imageView.invalidate()
    }

    private fun applyInvertedFilter() {
        // image size
        // image size
        val width: Int = originalImage!!.getWidth()
        val height: Int = originalImage!!.getHeight()
        // create output bitmap
        // create output bitmap
        val bmOut = Bitmap.createBitmap(originalImage!!.width, originalImage!!.height, originalImage!!.getConfig())
        // color information
        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int
        // scan through all pixels
        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) { // get pixel color
                pixel = originalImage!!.getPixel(x, y)
                // get color on each channel
                A = Color.alpha(pixel)
                R = Color.red(pixel)
                G = Color.green(pixel)
                B = Color.blue(pixel)
                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, 255 - R, 255 - G, 255 - B))
            }
        }
        originalFilterImage = bmOut.copy(Bitmap.Config.ARGB_8888, true)
        drawSticker(s)
    }

    private fun applyBlackAndWhiteFilter() {
        val bwBitmap = Bitmap.createBitmap(originalImage!!.width, originalImage!!.height, Bitmap.Config.ARGB_8888)
        val hsv = FloatArray(3)
        for (col in 0 until originalImage!!.width) {
            for (row in 0 until originalImage!!.height) {
                Color.colorToHSV(originalImage!!.getPixel(col, row), hsv)
                if (hsv[2] > 0.5f) {
                    bwBitmap.setPixel(col, row, -0x1)
                } else {
                    bwBitmap.setPixel(col, row, -0x1000000)
                }
            }
        }
        originalFilterImage = bwBitmap.copy(Bitmap.Config.ARGB_8888, true)
        drawSticker(s)
    }

    private fun applyNormalFilter() {
        // todo
        originalFilterImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        drawSticker(s)
    }

    private fun nextFilter() {
        // busy = true
        when (currentFilter) {
            FilterType.NORMAL -> {
                applyBlackAndWhiteFilter()
                currentFilter = FilterType.BLACK_AND_WHITE
            }
            FilterType.BLACK_AND_WHITE -> {
                applyInvertedFilter()
                currentFilter = FilterType.INVERTED
            }
            FilterType.INVERTED -> {
                applyNormalFilter()
                currentFilter = FilterType.NORMAL
            }
        }
        // busy = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!busy) {
            busy = true
            val curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            // println(curTime)
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime
                val x = event!!.values[SensorManager.DATA_X]
                val y = event!!.values[SensorManager.DATA_Y]
                val z = event!!.values[SensorManager.DATA_Z]
                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > 7000) {
                    nextFilter()
                }
                last_x = x
                last_y = y
                last_z = z
            }
            busy = false
        }
    }

}
