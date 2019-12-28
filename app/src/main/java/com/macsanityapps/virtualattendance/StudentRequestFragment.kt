package com.macsanityapps.virtualattendance


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.view.StudentAdapter
import kotlinx.android.synthetic.main.fragment_student_request.*
import com.google.firebase.firestore.SetOptions

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Rooms




/**
 * A simple [Fragment] subclass.
 */
class StudentRequestFragment : Fragment(), StudentAdapter.StudentListener {

    private var studentAdapter: StudentAdapter? = null
    private var dataList : MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_request, container, false)
    }

    override fun onStart() {
        super.onStart()

        initRecyclerView()
    }


    private fun initRecyclerView() {

        rv_students.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val sample = FirebaseFirestore.getInstance()
            .collectionGroup("students")
            .whereEqualTo("type", 0)
            .get()
            .addOnCompleteListener {
             //   val data = it.toObjects(Rooms::class.java)

                if (it.isSuccessful) {
                    for (document in it.result!!) {

                        val data = document.toObject(User::class.java)
                        Log.e("DATA", document.id + " => " + data.toString())
                        dataList.add(data)

                    }

                } else {
                    Log.e("DATA", "Error getting documents.", it.exception)
                }

            }
            .addOnFailureListener {

                Log.e("DATA", it.localizedMessage)
            }

        val query = FirebaseFirestore.getInstance()
            .collectionGroup("Rooms")
            .whereEqualTo("type", 0)

        val options = FirestoreRecyclerOptions.Builder<Rooms>()
            .setQuery(query, Rooms::class.java)
            .build()


        studentAdapter = StudentAdapter(options, this)
        rv_students.adapter = studentAdapter

        studentAdapter!!.startListening()

    }

    override fun handleApproved(snapshot: DocumentSnapshot) {

        val data = snapshot.toObject(User::class.java)
        FirebaseFirestore.getInstance()
            .collection("room${arguments!!.getString("id")}")
            .whereEqualTo("id", data!!.id)
            .get()
            .addOnCompleteListener {

                if(it.isSuccessful){
                    for (document in it.result!!) {
                        val map = HashMap<String, Int>()
                        map["type"] = 1
                        FirebaseFirestore.getInstance()
                            .collection("room${arguments!!.getString("id")}")
                            .document(document.id)
                            .set(map, SetOptions.merge())
                            .addOnSuccessListener {
                                makeToast("Successfully approved!")
                            }.addOnFailureListener {
                                makeToast("Something went wrong. Please Try again later.")
                            }
                    }
                }
            }

    }

    override fun handleDisapproved(snapshot: DocumentSnapshot) {
        FirebaseFirestore.getInstance()
            .collection("room${arguments!!.getString("id")}")
            .whereEqualTo("id", FirebaseAuth.getInstance().uid)
            .get()
            .addOnCompleteListener {

                if(it.isSuccessful){
                    for (document in it.result!!) {
                        val map = HashMap<String, String>()
                        map["type"] = "1"
                        FirebaseFirestore.getInstance()
                            .collection("room${arguments!!.getString("id")}")
                            .document(document.id)
                            .set(map, SetOptions.merge())
                            .addOnSuccessListener {
                                makeToast("Successfully approved!")
                            }.addOnFailureListener {
                                makeToast("Something went wrong. Please Try again later.")
                            }
                    }
                }
            }
    }

}
