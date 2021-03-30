package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View



class drawingView(context: Context, attribute:AttributeSet) : View(context,attribute)
{

    private  var mPath :customPath?=null
    private  var mCanvasBitmap : Bitmap?=null
    private  var mCanvasPaint :Paint?=null
    private  var mDrawPaint :Paint?=null
    private var canvas: Canvas?=null
    private  var color = Color.BLACK
    private  var mbrushSize :Float=0.toFloat()

    private val showPath=ArrayList<customPath>()
    private var undo=ArrayList<customPath>()
    init{
        mPath=customPath(color,mbrushSize)
        mDrawPaint=Paint()
        mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        //mbrushSize=20.toFloat()
        mCanvasPaint=Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        //to make drawing stay
        for(path in showPath){
            mDrawPaint!!.strokeWidth = path.brushSize
            mDrawPaint!!.color =path.color
            canvas!!.drawPath(path,mDrawPaint!!)
        }
        if (!mPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mPath!!.brushSize
            mDrawPaint!!.color = mPath!!.color
            canvas!!.drawPath(mPath!!, mDrawPaint!!)

        }
    }
    fun clickUndo(){
        if(showPath.size>0) {
            undo.add(showPath.removeAt(showPath.size-1))
            invalidate()
        }
    }
  fun setSizeForBrush(size:Float){
      mbrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,size,resources.displayMetrics)
      mDrawPaint!!.strokeWidth=mbrushSize
  }

       override  fun onTouchEvent(event: MotionEvent?): Boolean {
             val touchX=event?.x
             val touchY=event?.y
             when(event?.action){
                 MotionEvent.ACTION_DOWN->{
                     mPath!!.brushSize=mbrushSize
                     mPath!!.color=color

                     if (touchX != null) {
                         if (touchY != null) {
                             mPath!!.moveTo(touchX,touchY)
                         }
                     }
                 }


                 MotionEvent.ACTION_MOVE->{
                     if (touchX != null) {
                         if (touchY != null) {
                             mPath!!.lineTo(touchX,touchY)
                         }
                     }

                 }


                 MotionEvent.ACTION_UP->{
                     showPath.add(mPath!!)
                     mPath=customPath(color,mbrushSize)

                 }
                 else->return false
             }
             invalidate()
            return true
        }
    fun selectColor(newColor: String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color

    }


    internal inner class customPath(var color:Int,var brushSize:Float): Path() {

    }
}