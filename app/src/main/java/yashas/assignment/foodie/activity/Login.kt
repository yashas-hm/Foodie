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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import yashas.assignment.foodie.R
import yashas.assignment.foodie.util.APIKeys
import yashas.assignment.foodie.util.ConnectionManager

class Login : AppCompatActivity() {

    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtForgotPass: TextView
    private lateinit var txtRegister: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPass = findViewById(R.id.txtForgotPass)
        txtRegister = findViewById(R.id.txtRegister)
        sharedPreferences =
            getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)



        val intent = Intent(this@Login, MainActivity::class.java)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(intent)
            finish()
        }

        val queue = Volley.newRequestQueue(this@Login)
        val url = APIKeys.loginUrl

        btnLogin.setOnClickListener {

            val phoneNumber = etPhoneNumber.text.toString()
            val password = etPassword.text.toString()
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", phoneNumber)
            jsonParams.put("password", password)

            if (ConnectionManager().checkConnectivity(this@Login)) {
                val jsonRequest = object : JsonObjectRequest(
                    POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        val mainObject = it.getJSONObject("data")
                        val success = mainObject.getBoolean("success")
                        try {
                            if (success) {
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                val data = mainObject.getJSONObject("data")
                                val userId = data.getString("user_id").toString()
                                val name = data.getString("name").toString()
                                val email = data.getString("email").toString()
                                val mobileNumber = data.getString("mobile_number").toString()
                                val address = data.getString("address").toString()
                                println(userId)
                                sharedPreferences.edit().putString("user_id", userId).apply()
                                sharedPreferences.edit().putString("name", name).apply()
                                sharedPreferences.edit().putString("email", email).apply()
                                sharedPreferences.edit().putString("mobile_number", mobileNumber).apply()
                                sharedPreferences.edit().putString("address", address).apply()
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@Login,
                                    "Wrong credentials enter again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                etPhoneNumber.text.clear()
                                etPassword.text.clear()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@Login,
                                "Some Unexpected Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                            etPhoneNumber.text.clear()
                            etPassword.text.clear()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@Login,
                            "Some Unexpected Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                        etPhoneNumber.text.clear()
                        etPassword.text.clear()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = APIKeys.auth
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }
            else{
                val dialog = AlertDialog.Builder(this@Login)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@Login)
                }
                dialog.create()
                dialog.show()
            }
        }

        txtForgotPass.setOnClickListener {
            startActivity(Intent(this@Login, ForgotPassword::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this@Login, Registration::class.java))
        }
    }
}
