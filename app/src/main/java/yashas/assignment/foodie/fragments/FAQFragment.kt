package yashas.assignment.foodie.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import yashas.assignment.foodie.R
import yashas.assignment.foodie.adapter.FAQRecyclerAdapter
import yashas.assignment.foodie.model.FAQ

class FAQFragment : Fragment() {

    lateinit var recyclerFAQ: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val questionList = arrayListOf<FAQ>(
        FAQ("Q1. How do I place an order?","A1. As of now, orders can only be placed through the Foodie mobile app. Start by downloading the app for Android. Create an account and choose your items."),
        FAQ("Q2. How do I know my order is confirmed?", "A2. We will send you a order confirmation email once we receive your order."),
        FAQ("Q3. How will be my orders delivered?","A3. Orders are delivered directly by the Packaging Assist suppliers. Different items in an order could be fulfilled by different suppliers. We will share the contact details and amount payable for all deliveries via email and SMS."),
        FAQ("Q4. Why is my location is not serviceable?","A4. We are currently live only in a few cities. We will be expanding soon."),
        FAQ("Q5.  When I have to pay?","A5. You will have to pay at the time of delivery as per the payment details we send to you via email."),
        FAQ("Q6. Will I get an invoice?","A6. Yes. You will get your invoice from our supplier at the time of delivery.")
    )
    lateinit var recyclerAdapter: FAQRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)

        recyclerFAQ = view.findViewById(R.id.recyclerFAQ)
        layoutManager = LinearLayoutManager(activity)

        recyclerAdapter = FAQRecyclerAdapter(activity as Context, questionList)
        recyclerFAQ.adapter = recyclerAdapter
        recyclerFAQ.layoutManager = layoutManager
        return view
    }
}
