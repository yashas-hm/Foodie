package yashas.assignment.foodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import yashas.assignment.foodie.R

class SplashScreen : AppCompatActivity() {

    lateinit var startAct: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            startAct = Intent(this@SplashScreen, Login::class.java)
            startActivity(startAct)
            finish()
        }, 1500)
    }
}
