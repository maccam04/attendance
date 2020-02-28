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
import com.google.firebase.iid.FirebaseInstanceId
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.VirtualAttendanceApplication
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.ApiService
import com.macsanityapps.virtualattendance.data.Rooms
import com.macsanityapps.virtualattendance.data.User
import kotlinx.android.synthetic.main.fragment_add_section.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * A simple [List of Section] subclass.
 */
class AddSectionFragment : Fragment(), RoomsAdapter.RoomListener, Callback<String> {


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

    //    (activity as AppCompatActivity).supportActionBar!!.show()

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
        val email = pref?.getString("email", "")
        val studentId = pref?.getString("studentId", "")
        val mobileNo = pref?.getString("phoneNumber", "")
        val name = pref?.getString("name", "")
        val cousre = pref?.getString("course", "")

        val token = prefs?.getString("token", "")

        val user = User(studentId, name, email, mobileNo, cousre, 0, true, "Present", FirebaseInstanceId.getInstance().token)
        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(data!!.id)
            .get()
            .addOnSuccessListener {

                val rooms = it.toObject(Rooms::class.java)

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
                                    builder.setPositiveButton("OK"){ _, _ ->

                                        val interceptor = HttpLoggingInterceptor()
                                        interceptor.level = HttpLoggingInterceptor.Level.BODY
                                        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

                                        val retrofit = Retrofit.Builder()
                                            .baseUrl(ApiService.URL_BASE)
                                            .addConverterFactory(ScalarsConverterFactory.create())
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(client)
                                            .build()

                                        val apiInterface = retrofit.create(ApiService::class.java!!)


                                        // prepare call in Retrofit 2.0
                                        try {

                                            val message = "{ \"data\": \n" +
                                                    "   { \"title\": \"New student apply. \", \n" +
                                                    "    \"content\" : \"Hi professor, ${name} applying to your room\",\n" +
                                                    "    \"imageUrl\": \"http://h5.4j.com/thumb/Ninja-Run.jpg\", \n" +
                                                    "    \"gameUrl\": \"https://h5.4j.com/Ninja-Run/index.php?pubid=noad\" \n" +
                                                    "   }, \n" +
                                                    "\n" +
                                                    " \"to\": \"${rooms?.token}\"\n" +
                                                    "}"

                                            val userCall = apiInterface.sendData(message)
                                            userCall.enqueue(this)

                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }

                                    }

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

    override fun onFailure(call: Call<String>, t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
