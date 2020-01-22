package com.macsanityapps.virtualattendance.view


import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment(), RoomsAdapter.RoomListener {

    private var roomsAdapter: RoomsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Dashboard"


        return inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //THIS IS IMPORTANT!!!
        rv_rooms.adapter = null
    }

    override fun onStart() {
        super.onStart()


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
                val dir =
                    DashboardFragmentDirections.actionDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  iv_add_section.setOnClickListener {
            withEditText()
        }

        iv_leave_section.setOnClickListener {

            val dir = DashboardFragmentDirections.actionDashboardFragmentToSectionListFragment()
            findNavController().navigate(dir)

        }
        iv_list_absence.setOnClickListener {

            val dir = DashboardFragmentDirections.actionDashboardFragmentToAddSectionFragment()
            findNavController().navigate(dir)
        }

        //absent list
        iv_list_section.setOnClickListener {
            val dir = DashboardFragmentDirections.actionDashboardFragmentToAbsenceListFragment()
            findNavController().navigate(dir)
        }*/

        initRecyclerView()

        fab_add_room_section.setOnClickListener {
            showAddRoomDialog()
        }

    }

    private fun initRecyclerView() {

        val pref = activity?.getSharedPreferences("Account", 0)
        val email = pref?.getString("emai", "")

        rv_rooms.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Rooms")
            .whereArrayContains("studentsList", email.toString())

        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()

        roomsAdapter = RoomsAdapter(0, options, this)
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

    private fun showAddRoomDialog() {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        builder.setTitle("Add Room")
        val dialogLayout = inflater.inflate(R.layout.layout_add_room, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_code)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Submit") { _, i ->

            FirebaseFirestore.getInstance()
                .collection("Rooms")
                .whereEqualTo("id", editText.text.toString())
                .get()
                .addOnSuccessListener {

                    val pref = activity?.getSharedPreferences("Account", 0)
                    val bol = pref?.getBoolean("registered", false)
                    val email = pref?.getString("emai", "")
                    val studentId = pref?.getString("studentId", "")
                    val mobileNo = pref?.getString("phoneNumber", "")
                    val name = pref?.getString("name", "")
                    val cousre = pref?.getString("course", "")

                    val user = User(studentId, name, email, mobileNo, cousre, 0, bol)

                    FirebaseFirestore.getInstance()
                        .collection("Rooms")
                        .document("${editText.text}")
                        .collection("students")
                        .document("$studentId")
                        .get()
                        .addOnSuccessListener {
                            if(it.exists()){
                                makeToast("You're already sent a request to this room!. ")
                            } else {
                                FirebaseFirestore.getInstance()
                                    .collection("Rooms")
                                    .document("${editText.text}")
                                    .collection("students")
                                    .document("$studentId")
                                    .set(user)
                                    .addOnSuccessListener {
                                        val builders = android.app.AlertDialog.Builder(activity)

                                        // Set the alert dialog title
                                        builders.setTitle("Request Sent!")

                                        // Display a message on alert dialog
                                        builders.setMessage("Please wait for your professor to accept your request!")

                                        // Set a positive button and its click listener on alert dialog
                                        builders.setPositiveButton("OK"){ _, _ ->

                                        }

                                        // Finally, make the alert dialog using builder
                                        val dialog: android.app.AlertDialog = builders.create()

                                        // Display the alert dialog on app interface
                                        dialog.show()
                                    }
                                    .addOnFailureListener {

                                    }

                            }
                        }



                    /*FirebaseFirestore.getInstance()
                        .collection("room${editText.text}")
                        .document("${editText.text}")
                        .set(user)
                        .addOnSuccessListener {
                            val builders = android.app.AlertDialog.Builder(activity)

                            // Set the alert dialog title
                            builders.setTitle("Request Sent!")

                            // Display a message on alert dialog
                            builders.setMessage("Please wait for your professor to accept your request!")

                            // Set a positive button and its click listener on alert dialog
                            builders.setPositiveButton("OK"){ _, _ ->

                            }

                            // Finally, make the alert dialog using builder
                            val dialog: android.app.AlertDialog = builders.create()

                            // Display the alert dialog on app interface
                            dialog.show()
                        }
                        .addOnFailureListener {
                            Log.d("OnFailure", it.localizedMessage!!)

                        }*/
                }.addOnFailureListener {
                    makeToast("Sorry, No room found.")
                }


        }
        builder.show()
    }

    override fun handleViewMap(snapshot: DocumentSnapshot) {
        val data = snapshot.toObject(Rooms::class.java)
        findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToStudentSeatMapFragment(data?.id!!, data?.desc))
    }

    override fun handleEditNote(snapshot: DocumentSnapshot) {

    }

    override fun handleDeleteItem(snapshot: DocumentSnapshot) {

    }

}
