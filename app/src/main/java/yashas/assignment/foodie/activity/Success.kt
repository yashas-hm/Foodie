package yashas.assignment.foodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import yashas.assignment.foodie.R

class Success : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        Handler().postDelayed({
            val startAct = Intent(this@Success, MainActivity::class.java)
            startActivity(startAct)
            finish()
        }, 1500)
    }
}
