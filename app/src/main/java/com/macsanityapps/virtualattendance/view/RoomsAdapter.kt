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


class RoomsAdapter(
    private var type: Int,
    options: FirestoreRecyclerOptions<Rooms>,
    private var roomListener: RoomListener
) :
    FirestoreRecyclerAdapter<Rooms, RoomsAdapter.NoteViewHolder>(
        options
    ) {
    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int,
        rooms: Rooms
    ) {

        when(type){

            0 -> {
                holder.btnViewStudent.text = "Apply"
                holder.btnViewSeatMap.visibility = View.GONE
            }

        }

        holder.tvRoomId.text = rooms.desc
        holder.tvRoomCode.text = rooms.id

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context)
        val view =
            layoutInflater.inflate(R.layout.view_room, parent, false)
        return NoteViewHolder(view)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRoomId: TextView = itemView.findViewById(R.id.tv_title)
        var tvRoomCode: TextView = itemView.findViewById(R.id.tv_code)
        var btnViewStudent: Button = itemView.findViewById(R.id.btn_student)
        var btnViewSeatMap: Button = itemView.findViewById(R.id.btn_seat_map)

        fun deleteItem() {
            roomListener.handleDeleteItem(snapshots.getSnapshot(adapterPosition))
        }

        init {

            btnViewStudent.setOnClickListener {
                val snapshot = snapshots.getSnapshot(adapterPosition)
                roomListener.handleEditNote(snapshot)
            }

            btnViewSeatMap.setOnClickListener {
                roomListener.handleViewMap(snapshots.getSnapshot(adapterPosition))
            }

        }
    }

    interface RoomListener {
        fun handleViewMap(snapshot: DocumentSnapshot)
        fun handleEditNote(snapshot: DocumentSnapshot)
        fun handleDeleteItem(snapshot: DocumentSnapshot)
    }


}
