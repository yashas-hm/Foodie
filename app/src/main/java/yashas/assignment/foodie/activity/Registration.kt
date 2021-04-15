package yashas.assignment.foodie.activity

import android.app.AlertDialog
import android.content.Intent
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

class Registration : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etMail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfPassword: EditText
    private lateinit var btnRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etName = findViewById(R.id.etName)
        etMail = findViewById(R.id.etMail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfPassword = findViewById(R.id.etConfPassword)
        btnRegister = findViewById(R.id.btnRegister)



        val queue = Volley.newRequestQueue(this@Registration)
        val url = APIKeys.registrationUrl

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val mail = etMail.text.toString()
            val number = etPhoneNumber.text.toString()
            val address = etAddress.text.toString()
            val password = etPassword.text.toString()
            val confPass = etConfPassword.text.toString()

            if (password == confPass) {
                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", number)
                jsonParams.put("password", password)
                jsonParams.put("address", address)
                jsonParams.put("email", mail)
                if (ConnectionManager().checkConnectivity(this@Registration)) {
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
                                        this@Registration,
                                        "Registered Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@Registration, Login::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@Registration,
                                        mainObject.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    etName.text.clear()
                                    etMail.text.clear()
                                    etPhoneNumber.text.clear()
                                    etAddress.text.clear()
                                    etPassword.text.clear()
                                    etConfPassword.text.clear()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@Registration,
                                    "Some Error Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@Registration,
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
                    val dialog = AlertDialog.Builder(this@Registration)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet connection not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@Registration)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            else {
                Toast.makeText(
                    this@Registration,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
                etPassword.text.clear()
                etConfPassword.text.clear()
            }
        }
    }
}
