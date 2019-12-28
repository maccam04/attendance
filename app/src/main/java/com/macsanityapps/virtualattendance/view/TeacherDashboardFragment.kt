package com.macsanityapps.virtualattendance.view


import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.login.LoginFragmentDirections
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TeacherDashboardFragment : Fragment(), RoomsAdapter.RoomListener {


    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private var roomsAdapter: RoomsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_teacher_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"

    }
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.show()

        fab_add_room.setOnClickListener {

            val builder = AlertDialog.Builder(activity!!)
            val inflater = layoutInflater
            builder.setTitle("Create Room")
            val dialogLayout = inflater.inflate(R.layout.view_generate_room, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.et_room_name)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Submit") { dialogInterface, i ->
                createRoom(editText.text.toString())
            }
            builder.show()


        }

        initRecyclerView()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add -> {

                val dir = TeacherDashboardFragmentDirections.actionTeacherDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                true

            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createRoom(text: String) {

        val randomString = (1..6)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        val pref = activity?.getSharedPreferences("Account", 0)
        val studentId = pref?.getString("studentId", "")

        val rooms = Rooms(randomString, "", studentId!!, text)

        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(randomString)
            .set(rooms)
            .addOnSuccessListener {
                makeToast("Room created successfully.")
            }
            .addOnFailureListener {
                Log.d("OnFailure", it.localizedMessage!!)

            }

    }

    private fun initRecyclerView() {

        val pref = activity?.getSharedPreferences("Account", 0)
        val studentId = pref?.getString("studentId", "")

        rv_rooms.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Rooms")
            .whereEqualTo("profId", studentId)

        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()

        roomsAdapter = RoomsAdapter(options, this)
        rv_rooms.adapter = roomsAdapter

        roomsAdapter!!.startListening()
/*

        if(roomsAdapter!!.itemCount > 0) {
            tv_empty.visibility = View.GONE
            rv_rooms.visibility = View.VISIBLE
        } else {
            tv_empty.visibility = View.VISIBLE
            rv_rooms.visibility = View.GONE
        }
*/

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

        val documentReference = snapshot.reference
        val rooms = snapshot.toObject(Rooms::class.java)

        documentReference.delete()
            .addOnSuccessListener {
                Snackbar.make(cl_parent, "Room Deleted", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(activity!!, android.R.color.black))
                    .setTextColor(ContextCompat.getColor(activity!!, android.R.color.white))
                    .show()
            }

    }

}
