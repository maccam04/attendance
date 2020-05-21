package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.VirtualAttendanceApplication
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.User
import kotlinx.android.synthetic.main.fragment_student_request.*
import kotlinx.android.synthetic.main.layout_empty_state.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class StudentRequestFragment : Fragment(), StudentAdapter.StudentListener, Callback<String> {

    private lateinit var studentAdapter: StudentAdapter
    private var dataList : MutableList<User> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

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


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Student List"


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

        iv_empty.setImageDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_empty_room))

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

                if (snapshots?.isEmpty!!) {
                    if(inc_empty_state != null) inc_empty_state.visibility = View.VISIBLE
                    if(tv_empty_text != null) tv_empty_text.text = "No student/s found."
                    if(rv_students != null)  rv_students.visibility = View.GONE

                } else {

                    try {
                        if(inc_empty_state != null && inc_empty_state.isVisible) inc_empty_state.visibility = View.GONE
                        if(rv_students != null)    rv_students.visibility = View.VISIBLE
                    } catch (npe : NullPointerException){
                        Log.e("Empty State", npe.localizedMessage)
                    }


                }

                dataList.clear()

                for (dc in snapshots!!) {
                    val data = dc.toObject(User::class.java)
                    dataList.add(data)
                }

                studentAdapter.addStudent(dataList)

            }
    }

    override fun handleApproved(user: User, adapterPosition: Int) {

        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(arguments!!.getString("id"))
            .update("studentsList", FieldValue.arrayUnion(user.name))

        val map = HashMap<String, Int>()
        map["type"] = 1

        user.id?.let {
            FirebaseFirestore.getInstance()
                .collection("Rooms")
                .document(arguments!!.getString("id"))
                .collection("students")
                .document(it)
                .update(map as Map<String, Int>)
                .addOnSuccessListener {

                    studentAdapter.addStudent(dataList)

                    try {

                        val message = "{ \"data\": \n" +
                                "   { \"title\": \"Status Update! \", \n" +
                                "    \"content\" : \"Hi ${user.name}, your request has been approved by your professor in this ${arguments!!.getString("id")}.\",\n" +
                                "    \"imageUrl\": \"http://h5.4j.com/thumb/Ninja-Run.jpg\", \n" +
                                "    \"gameUrl\": \"https://h5.4j.com/Ninja-Run/index.php?pubid=noad\" \n" +
                                "   }, \n" +
                                "\n" +
                                " \"to\": \"${user.token}\"\n" +
                                "}"

                        val userCall = (activity?.application as VirtualAttendanceApplication).getApi().sendData(message)
                        userCall.enqueue(this)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }.addOnFailureListener {
                    Log.e("ERROR", it.localizedMessage)

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

    override fun handleDisapproved(user: User?, adapterPosition: Int) {

        val builder = android.app.AlertDialog.Builder(activity)

        // Set the alert dialog title
        builder.setTitle("Disapprove")

        // Display a message on alert dialog
        builder.setMessage("Are you sure you want to disapprove this student? ")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes"){dialog, which ->

            FirebaseFirestore.getInstance()
                .collection("Rooms")
                .document(arguments!!.getString("id"))
                .update("studentsList", FieldValue.arrayRemove(user?.name))

            user?.id?.let {
                FirebaseFirestore.getInstance()
                    .collection("Rooms")
                    .document(arguments!!.getString("id"))
                    .collection("students")
                    .document(it)
                    .delete()
                    .addOnSuccessListener {
                        makeToast("You disapproved a student!")

                        try {

                            val message = "{ \"data\": \n" +
                                    "   { \"title\": \"Status Update! \", \n" +
                                    "    \"content\" : \"Hi ${user.name}, Sorry your request to enter the section ${arguments!!.getString("id")} has been disapproved.\",\n" +
                                    "    \"imageUrl\": \"http://h5.4j.com/thumb/Ninja-Run.jpg\", \n" +
                                    "    \"gameUrl\": \"https://h5.4j.com/Ninja-Run/index.php?pubid=noad\" \n" +
                                    "   }, \n" +
                                    "\n" +
                                    " \"to\": \"${user.token}\"\n" +
                                    "}"

                            val userCall = (activity?.application as VirtualAttendanceApplication).getApi().sendData(message)
                            userCall.enqueue(this)

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    }.addOnFailureListener {
                        Log.e("ERROR", it.localizedMessage)
                        makeToast("Something went wrong. Please Try again later.")
                    }
            }
        }.setNegativeButton("No") { dialog, which ->

        }


        // Finally, make the alert dialog using builder
        val dialog: android.app.AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            else -> {
                false
            }
        }
    }


    override fun onFailure(call: Call<String>, t: Throwable) {

    }

    override fun onResponse(call: Call<String>, response: Response<String>) {

    }


}
