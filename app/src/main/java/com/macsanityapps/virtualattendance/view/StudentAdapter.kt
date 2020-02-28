package com.macsanityapps.virtualattendance.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.User

class StudentAdapter(var context: Context, var studentListener: StudentListener) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var options : MutableList<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.view_student, parent, false)
        return StudentViewHolder(view)
    }

    fun addStudent( data : MutableList<User>){
        options.clear()
        options.addAll(data)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.tvRoomId.text = options[position].name
    }


    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvRoomId: TextView = itemView.findViewById(R.id.tv_student_name)
        var btnApproved: Button = itemView.findViewById(R.id.btn_approve)
        var btnDisapproved: Button = itemView.findViewById(R.id.btn_disapproved)

        init {

            btnApproved.setOnClickListener {
                studentListener.handleApproved(options[adapterPosition], adapterPosition)
            }

            btnDisapproved.setOnClickListener {

                studentListener.handleDisapproved(options[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }


    interface StudentListener {

        fun handleApproved(user: User, adapterPosition: Int)
        fun handleDisapproved(user: User?, adapterPosition: Int)

    }
}
