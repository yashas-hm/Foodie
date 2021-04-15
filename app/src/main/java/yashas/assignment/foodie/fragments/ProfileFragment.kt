package yashas.assignment.foodie.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text
import yashas.assignment.foodie.R


class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtNumber: TextView
    lateinit var txtMail : TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val context = activity
        txtName = view.findViewById(R.id.txtName)
        txtMail = view.findViewById(R.id.txtMail)
        txtAddress = view.findViewById(R.id.txtAddress)
        txtNumber = view.findViewById(R.id.txtNumber)
        sharedPreferences = context!!.getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)

        txtName.text = sharedPreferences.getString("name", "name")
        txtMail.text = sharedPreferences.getString("email", "email")
        txtNumber.text = sharedPreferences.getString("mobile_number", "mobile_number")
        txtAddress.text = sharedPreferences.getString("address", "address")

        return view
    }

}
