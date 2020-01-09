package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.User
import kotlinx.android.synthetic.main.fragment_student_request.*


/**
 * A simple [Fragment] subclass.
 */
class StudentRequestFragment : Fragment(), StudentAdapter.StudentListener {

    private lateinit var studentAdapter: StudentAdapter
    private var dataList : MutableList<User> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentAdapter = StudentAdapter(activity!!, this)

        with(rv_students){
            hasFixedSize()
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = studentAdapter
        }
    }

    override fun onStart() {
        super.onStart()

        initData()

    }


    private fun initData() {

        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(arguments!!.getString("id"))
            .collection("students")
            .whereEqualTo("type", 0)
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.w("initData", "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!) {
                    val data = dc.toObject(User::class.java)

                    dataList.clear()
                    dataList.add(data)
                }

                studentAdapter.addStudent(dataList)

            }

       /* FirebaseFirestore.getInstance()
            .collectionGroup("students")
            .whereEqualTo("type", 0)
            .get()
            .addOnCompleteListener {
             //   val data = it.toObjects(Rooms::class.java)

                if (it.isSuccessful) {
                    for (document in it.result!!) {

                        val data = document.toObject(User::class.java)

                        dataList.clear()
                        dataList.add(data)

                    }

                    studentAdapter.addStudent(dataList)

                } else {
                    Log.e("DATA", "Error getting documents.", it.exception)
                }

            }
            .addOnFailureListener {

                Log.e("DATA", it.localizedMessage)
            }*/
    }

    override fun handleApproved(id: String?, adapterPosition: Int) {

        val map = HashMap<String, Int>()
        map["type"] = 1

        id?.let {
            FirebaseFirestore.getInstance()
                .collection("Rooms")
                .document(arguments!!.getString("id"))
                .collection("students")
                .document(it)
                .update(map as Map<String, Int>)
                .addOnSuccessListener {
                    makeToast("Successfully approved!")
                    dataList.removeAt(adapterPosition)
                    studentAdapter.addStudent(dataList)
                }.addOnFailureListener {
                    Log.e("ERROR", it.localizedMessage)
                    makeToast("Something went wrong. Please Try again later.")
                }
        }

        /*FirebaseFirestore.getInstance()
            .collectionGroup("students")
            .whereEqualTo("id", id)
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
            }*/

    }

    override fun handleDisapproved(id: String?, adapterPosition: Int) {

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
                                dataList.removeAt(adapterPosition)
                                studentAdapter.addStudent(dataList)
                                makeToast("Successfully approved!")
                            }.addOnFailureListener {
                                makeToast("Something went wrong. Please Try again later.")
                            }
                    }
                }
            }
    }

}
