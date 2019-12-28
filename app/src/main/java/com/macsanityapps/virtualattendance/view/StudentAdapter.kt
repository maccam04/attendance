package com.macsanityapps.virtualattendance.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User

class StudentAdapter(
    options: FirestoreRecyclerOptions<Rooms>,
     var studentListener: StudentListener
) :
    FirestoreRecyclerAdapter<Rooms, StudentAdapter.StudentViewHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.view_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StudentViewHolder,
        position: Int,
        model: Rooms
    ) {
        holder.tvRoomId.text = model.students!![position].name
    }


    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvRoomId: TextView = itemView.findViewById(R.id.tv_student_name)
        var btnApproved: Button = itemView.findViewById(R.id.btn_approve)
        var btnDisapproved: Button = itemView.findViewById(R.id.btn_disapproved)

        init {

            btnApproved.setOnClickListener {
                studentListener.handleApproved(snapshots.getSnapshot(adapterPosition))
            }

            btnDisapproved.setOnClickListener {

                studentListener.handleDisapproved(snapshots.getSnapshot(adapterPosition))
            }
        }
    }

    interface StudentListener {

        fun handleApproved(snapshot: DocumentSnapshot)
        fun handleDisapproved(snapshot: DocumentSnapshot)
    }

}
