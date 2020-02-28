package com.macsanityapps.virtualattendance.view


import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.Rooms
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.*
import kotlinx.android.synthetic.main.fragment_teacher_home.*
import kotlinx.android.synthetic.main.fragment_teacher_home.cl_parent
import kotlinx.android.synthetic.main.layout_empty_state.*

/**
 * A simple [Fragment] subclass.
 */
class TeacherHomeFragment : Fragment(), RoomsAdapter.RoomListener {


    private var roomsAdapter: RoomsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home, container, false)
    }


    private fun initRecyclerView() {

        val pref = activity?.getSharedPreferences("Account", 0)
        val email = pref?.getString("email", "")

        rv_rooms.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Rooms")
            .whereEqualTo("email", email)


        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if (querySnapshot?.isEmpty!!) {
                inc_empty_state.visibility = View.VISIBLE
                tv_empty_text.text = "You don't have rooms enrolled yet."
            } else {
                inc_empty_state.visibility = View.GONE
            }


        }


        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()

        roomsAdapter = RoomsAdapter(1, options, this)
        rv_rooms.adapter = roomsAdapter

        roomsAdapter!!.startListening()


        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(rv_rooms)

    }

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val notesHelper = viewHolder as RoomsAdapter.NoteViewHolder
            notesHelper.deleteItem()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.colorPrimaryDark
                    )
                )
                .addActionIcon(R.mipmap.ic_delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    override fun handleViewMap(snapshot: DocumentSnapshot) {
        val data = snapshot.toObject(Rooms::class.java)
        findNavController().navigate(TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToSeatPlanFragment(data?.id!!, data?.desc))
    }

    override fun handleEditNote(snapshot: DocumentSnapshot) {

        val data = snapshot.toObject(Rooms::class.java)
        val direction = TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToStudentRequestFragment(data?.id!!)

        findNavController().navigate(direction)


    }

    override fun handleDeleteItem(snapshot: DocumentSnapshot) {

        val builder = android.app.AlertDialog.Builder(activity)

        // Set the alert dialog title
        builder.setTitle("Delete")

        // Display a message on alert dialog
        builder.setMessage("Are you sure you want to delete this room? ")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes"){dialog, which ->
            val documentReference = snapshot.reference
            snapshot.toObject(Rooms::class.java)

            documentReference.delete()
                .addOnSuccessListener {
                    Snackbar.make(cl_parent, "Room Deleted", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(activity!!, android.R.color.black))
                        .setTextColor(ContextCompat.getColor(activity!!, android.R.color.white))
                        .show()
                }
        }.setNegativeButton("No") { dialog, which ->
            roomsAdapter?.refresh()
        }


        // Finally, make the alert dialog using builder
        val dialog: android.app.AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()



    }


}
