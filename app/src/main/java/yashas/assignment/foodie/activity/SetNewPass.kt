package yashas.assignment.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import yashas.assignment.foodie.R
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager

class SetNewPass : AppCompatActivity() {

    private lateinit var etOTP: EditText
    private lateinit var etNewPass: EditText
    private lateinit var etReNewPass: EditText
    private lateinit var btnSubmit: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_pass)

        etOTP = findViewById(R.id.etOTP)
        etNewPass = findViewById(R.id.etNewPass)
        etReNewPass = findViewById(R.id.etReNewPass)
        btnSubmit = findViewById(R.id.btnSubmit)
        sharedPreferences =
            getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)

        val queue = Volley.newRequestQueue(this@SetNewPass)
        val url = APIKeys.resetPasswordUrl

        btnSubmit.setOnClickListener {
            if (etNewPass.text.toString() == etReNewPass.text.toString()) {
                val number = sharedPreferences.getString("mobile", "9998886666")!!
                val jsonParams = JSONObject()
                jsonParams.put("otp", etOTP.text.toString())
                jsonParams.put("password", etNewPass.text.toString())
                jsonParams.put("mobile_number", number)

                if (ConnectionManager().checkConnectivity(this@SetNewPass)) {
                    val jsonRequest = object : JsonObjectRequest(
                        Method.POST,
                        url,
                        jsonParams,
                        Response.Listener {
                            val mainObject = it.getJSONObject("data")
                            val success = mainObject.getBoolean("success")
                            try {
                                if (success) {
                                    Toast.makeText(
                                        this@SetNewPass,
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@SetNewPass, Login::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@SetNewPass,
                                        "Invalid OTP",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }catch (e: JSONException){
                                Toast.makeText(
                                    this@SetNewPass,
                                    "Some Error Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@SetNewPass,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = APIKeys.auth
                            return headers
                        }
                    }
                    queue.add(jsonRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@SetNewPass)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet connection not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@SetNewPass)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            else{
                Toast.makeText(
                    this@SetNewPass,
                    "Passwords Do Not Match",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
