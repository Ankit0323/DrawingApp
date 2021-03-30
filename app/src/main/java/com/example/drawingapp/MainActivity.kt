package com.example.drawingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        var topAnim=AnimationUtils.loadAnimation(this,R.anim.top_animation)
        var midAnim=AnimationUtils.loadAnimation(this,R.anim.mid_animation)
        var bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_animation)
   drawingWord.setAnimation(topAnim)
        drawImage.setAnimation(midAnim)
        appWord.setAnimation(bottomAnim)

        Handler().postDelayed(Runnable {
            finish()
            val intent=Intent(this,SideActivity::class.java)
            startActivity(intent)
        },2500)




    }
}




