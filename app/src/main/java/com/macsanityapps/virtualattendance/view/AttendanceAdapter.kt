package com.macsanityapps.virtualattendance.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.Attendance
import com.macsanityapps.virtualattendance.data.AttendanceStatus

class AttendanceAdapter(options: FirestoreRecyclerOptions<AttendanceStatus>):  FirestoreRecyclerAdapter<AttendanceStatus, AttendanceAdapter.AttendanceViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.parent_view, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AttendanceViewHolder,
        position: Int,
        model: AttendanceStatus
    ) {
        holder.tvDate.text = model.date
    }

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate: TextView = itemView.findViewById(R.id.tv_date)

    }
}