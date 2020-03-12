package com.example.tp1

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.example.tp1.dataclasses.Sticker
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.File
import java.io.InputStream
import android.graphics.Bitmap
import android.R.attr.y
import android.R.attr.x
import android.widget.Toast
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class CanvasActivity : Activity() {

    private lateinit var btnChooseImage: ImageView;
    private lateinit var btnChooseColor: ImageView;
    private lateinit var canvas: Canvas;
    private var paint: Paint = Paint();
    private lateinit var bitmap: Bitmap;
    private lateinit var imageView: ImageView;
    private var firstTouch: Boolean = true;
    private var imagePath: String? = null;
    private var originalImage: Bitmap? = null;

    private var oldX: Float = -1f;
    private var oldY: Float = -1f;
    private var stickers: MutableList<Sticker> = ArrayList();

    private lateinit var btnEmojiHappy: ImageButton;
    private lateinit var btnEmojiNerd: ImageButton;
    private lateinit var btnEmojiLove: ImageButton;
    private lateinit var btnEmojiDead: ImageButton;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        imageView = findViewById(R.id.imageView)
        paint.setColor(Color.BLACK)
        paint.strokeWidth = 10f
        btnChooseImage = findViewById(R.id.btnLoad)
        btnChooseColor = findViewById(R.id.btnColor)
        btnEmojiHappy = findViewById(R.id.btnHappy);
        btnEmojiHappy.setOnClickListener { v -> onBtnEmojiClick() }
        btnEmojiLove = findViewById(R.id.btnLove);
        btnEmojiLove.setOnClickListener { v -> onBtnEmojiLoveClick() }
        btnEmojiNerd = findViewById(R.id.btnNerd);
        btnEmojiNerd.setOnClickListener { v -> onBtnEmojiNerdClick() }
        btnEmojiDead = findViewById(R.id.btnDead);
        btnEmojiDead.setOnClickListener { v -> onBtnEmojiDeadClick() }

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
                         drawStickers(event);
                      }
                    }
                    MotionEvent.ACTION_UP -> if (false) endLine(v, event)
                }
                return true // v?.onTouchEvent(event) ?: true
            }
        })
        btnChooseImage.setOnClickListener(View.OnClickListener { chooseImage() })
        btnChooseColor.setOnClickListener(View.OnClickListener { chooseColor() })
        imagePath = intent.getStringExtra("imagePath");
        loadPicture(Uri.fromFile(File(imagePath)))
    }

    fun drawStickers(event: MotionEvent?) {
        if (originalImage != null) {
            bitmap = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
            imageView.setImageBitmap(bitmap!!);
            canvas = Canvas(bitmap!!);
            firstTouch = false;

        }

        stickers.forEach{ sticker ->
            if (event != null) {
                if (sticker.left < event.getX() && event.getX() < sticker.left + sticker.right
                    && sticker.top < event.getY() && event.getY() < sticker.top + sticker.bottom) {
                    sticker.left = event.getX() - 128;
                    sticker.top  = event.getY() - 128;
                    sticker.bottom = sticker.top + 256f;
                    sticker.right = sticker.left + 256f;
                }
            }
            drawSticker(sticker)
        }
    }

    fun onBtnEmojiNerdClick() {
        var addedSticker = Sticker(R.drawable.emoji_nerd, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiLoveClick() {
        var addedSticker = Sticker(R.drawable.emoji_love, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiDeadClick() {
        var addedSticker = Sticker(R.drawable.emoji_dead, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiClick() {
        var addedSticker = Sticker(R.drawable.emoji_happy, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
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
        bitmap = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);

        if (bitmap != null) {
            redrawImageView()
            applyBlackAndWhiteFilter()
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
        // redraw scene

        // draw current sticker
        var paint0 = Paint();
        paint.isAntiAlias = false;
        paint.isFilterBitmap = true;
        paint.isDither = true;
        for (sticker in stickers) {
            var sticker = BitmapFactory.decodeResource(resources, st.drawableId);
            canvas.drawBitmap(sticker, null, RectF(st.left, st.top, st.right, st.bottom), paint0)
        }
        imageView.invalidate()
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
