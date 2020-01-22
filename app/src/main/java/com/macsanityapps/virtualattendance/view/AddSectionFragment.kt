package com.macsanityapps.virtualattendance.view


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User
import kotlinx.android.synthetic.main.fragment_add_section.*

/**
 * A simple [List of Section] subclass.
 */
class AddSectionFragment : Fragment(), RoomsAdapter.RoomListener {


    private var roomsAdapter: RoomsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_section, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //THIS IS IMPORTANT!!!
        rec_list_fragment.adapter = null
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.show()

        initRecyclerView()
    }

    private fun initRecyclerView() {

        rec_list_fragment.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Rooms")


        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()

        roomsAdapter = RoomsAdapter(0, options, this)
        rec_list_fragment.adapter = roomsAdapter

        roomsAdapter!!.startListening()


    }


    override fun handleEditNote(snapshot: DocumentSnapshot) {

        val data = snapshot.toObject(Rooms::class.java)

        val pref = activity?.getSharedPreferences("Account", 0)
        val prefs = activity?.getSharedPreferences("Token", 0)
        val bol = pref?.getBoolean("registered", false)
        val email = pref?.getString("emai", "")
        val studentId = pref?.getString("studentId", "")
        val mobileNo = pref?.getString("phoneNumber", "")
        val name = pref?.getString("name", "")
        val cousre = pref?.getString("course", "")
        val token = prefs?.getString("token", "")

        val user = User(studentId, name, email, mobileNo, cousre, 0, true, "Present", token!!)


        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(data!!.id)
            .collection("students")
            .document(studentId!!)
            .get()
            .addOnSuccessListener {

                if(it.exists()){
                    makeToast("You're already sent a request to this room!. ")
                } else {

                    FirebaseFirestore.getInstance()
                        .collection("Rooms")
                        .document(data!!.id)
                        .collection("students")
                        .document(studentId!!)
                        .set(user)
                        .addOnSuccessListener {
                            val builder = AlertDialog.Builder(activity)

                            // Set the alert dialog title
                            builder.setTitle("Request Sent!")

                            // Display a message on alert dialog
                            builder.setMessage("Please wait for your professor to accept your request!")

                            // Set a positive button and its click listener on alert dialog
                            builder.setPositiveButton("OK"){ _, _ -> }

                            // Finally, make the alert dialog using builder
                            val dialog: AlertDialog = builder.create()

                            // Display the alert dialog on app interface
                            dialog.show()

                        }.addOnFailureListener {
                            Log.e("ERROR", it.localizedMessage)
                            makeToast("Something went wrong. Please Try again later.")
                        }
                }
            }


/*
        FirebaseFirestore.getInstance()
            .collection("room${data!!.id}")
            .add(user)
            .addOnSuccessListener {
                val builder = AlertDialog.Builder(activity)

                // Set the alert dialog title
                builder.setTitle("Request Sent!")

                // Display a message on alert dialog
                builder.setMessage("Please wait for your professor to accept your request!")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK"){ _, _ ->

                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
            .addOnFailureListener {
                Log.d("OnFailure", it.localizedMessage!!)

            }*/
    }

    override fun handleViewMap(snapshot: DocumentSnapshot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun handleDeleteItem(snapshot: DocumentSnapshot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
