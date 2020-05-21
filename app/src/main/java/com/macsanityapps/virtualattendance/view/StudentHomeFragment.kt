package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast

import com.macsanityapps.virtualattendance.data.Rooms
import kotlinx.android.synthetic.main.fragment_student_home.*
import kotlinx.android.synthetic.main.layout_empty_state.*


/**
 * A simple [Fragment] subclass.
 */
class StudentHomeFragment : Fragment(), RoomsAdapter.RoomListener {


    private var roomsAdapter: RoomsAdapter? = null

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment FirstFragment.
     */
    fun newInstance(): StudentHomeFragment {
        return StudentHomeFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initRecyclerView() {

        val pref = activity?.getSharedPreferences("Account", 0)
        val name = pref?.getString("name", "")

        rv_rooms.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Rooms")
            .whereArrayContains("studentsList", name.toString())

        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            try {
                if (querySnapshot?.isEmpty!!) {
                    iv_empty.setImageResource(R.drawable.ic_empty_room)
                    inc_empty_state.visibility = View.VISIBLE
                    tv_empty_text.text = "You don't have rooms enrolled yet."
                } else {

                    inc_empty_state.visibility = View.GONE
                }
            } catch(e : Exception){

            }
        }

        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()

        roomsAdapter = RoomsAdapter(0, options, this)
        rv_rooms.adapter = roomsAdapter

        roomsAdapter!!.startListening()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        //THIS IS IMPORTANT!!!
        rv_rooms.adapter = null
    }

    override fun handleEditNote(snapshot: DocumentSnapshot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleDeleteItem(snapshot: DocumentSnapshot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleViewMap(snapshot: DocumentSnapshot) {
        val data = snapshot.toObject(Rooms::class.java)
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToStudentSeatMapFragment(
                data?.id!!,
                data.desc
            )
        )
    }
}
