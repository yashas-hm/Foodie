package yashas.assignment.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_faq.view.*
import yashas.assignment.foodie.R
import yashas.assignment.foodie.model.FAQ

class FAQRecyclerAdapter(val context: Context, val questionList: ArrayList<FAQ>): RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>(){
    class FAQViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int){
        val faq = questionList[position]
        holder.txtQuestion.text = faq.question
        holder.txtAnswer.text = faq.answer
    }
}