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

class ForgotPassword : AppCompatActivity() {

    private lateinit var etPhoneNumber: EditText
    private lateinit var etMail: EditText
    private lateinit var btnSendOTP: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etMail = findViewById(R.id.etMail)
        btnSendOTP = findViewById(R.id.btnSendOTP)
        sharedPreferences =
            getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)

        val queue = Volley.newRequestQueue(this@ForgotPassword)
        val url = APIKeys.forgotPasswordUrl

        btnSendOTP.setOnClickListener {
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etPhoneNumber.text.toString())
            jsonParams.put("email", etMail.text.toString())

            if (ConnectionManager().checkConnectivity(this@ForgotPassword)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        val mainObject = it.getJSONObject("data")
                        val success = mainObject.getBoolean("success")
                        val firstTry = mainObject.getBoolean("first_try")
                        try {
                            if (success && firstTry) {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "OTP sent to mail",
                                    Toast.LENGTH_SHORT
                                ).show()
                                sharedPreferences.edit().putString("mobile", etPhoneNumber.text.toString()).apply()
                                startActivity(Intent(this@ForgotPassword, SetNewPass::class.java))
                                finish()
                            }
                            else if (success && !firstTry) {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "OTP already sent. Check Mail",
                                    Toast.LENGTH_SHORT
                                ).show()
                                sharedPreferences.edit().putString("mobile", etPhoneNumber.text.toString()).apply()
                                startActivity(Intent(this@ForgotPassword, SetNewPass::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "Invalid Credentials",
                                    Toast.LENGTH_SHORT
                                ).show()
                                etMail.text.clear()
                                etPhoneNumber.text.clear()
                            }
                        }catch (e: JSONException){
                            Toast.makeText(
                                this@ForgotPassword,
                                "Some error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPassword,
                            "Some error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
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
            else {
                val dialog = AlertDialog.Builder(this@ForgotPassword)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ForgotPassword)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}
