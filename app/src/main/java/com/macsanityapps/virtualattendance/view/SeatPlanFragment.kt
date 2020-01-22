package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.Attendance
import com.macsanityapps.virtualattendance.data.NotificationResponse
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.seatview.Seat
import com.macsanityapps.virtualattendance.seatview.SeatViewConfig
import com.macsanityapps.virtualattendance.seatview.SeatViewListener
import kotlinx.android.synthetic.main.fragment_seat_plan.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class SeatPlanFragment : Fragment(), Callback<String> {

    private var size: String? = ""
    private var data: MutableList<User> = mutableListOf()
    private var index: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_seat_plan,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Room ${arguments!!.getString("name")}"

        val rowCount = 5
        val columnCount = 10
        val rowNames: HashMap<String, String> = HashMap()

        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(arguments!!.getString("id"))
            .collection("students")
            .whereEqualTo("type", 1)
            .get()
            .addOnSuccessListener {
                data = it.toObjects(User::class.java)
                tv_student_count.text = "Number of Student/s in this section: ${data.size}"

                if (data.size != 0) {
                    size = data.size.toString()
                }
            }
            .addOnFailureListener {
                makeToast(it.localizedMessage)
            }

        val seatArray = generateSample(rowCount, columnCount, rowNames)
        seatView!!.initSeatView(seatArray, rowCount, columnCount, rowNames)
        initSeatView()

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


        btn_present.setOnClickListener {

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(data[index!!].id.toString())
                .collection("attendance")
                .document(getCurrentDate())
                .set(
                    Attendance(
                        id = "${arguments!!.getString("id")}",
                        course = "${arguments!!.getString("name")}",
                        date = getCurrentDate(),
                        userId = data[index!!].id.toString(),
                        present = true
                    )
                )
                .addOnSuccessListener {
                    makeToast("Mark as Present")

                    FirebaseFirestore.getInstance()
                        .collectionGroup("attendance")
                        .whereEqualTo("userId", data[index!!].id.toString())
                        .addSnapshotListener { snapshots, e ->

                            if (e != null) {
                                Log.w("initData", "listen:error", e)
                                return@addSnapshotListener
                            }

                            for (dc in snapshots!!) {
                                val data = dc.toObject(Attendance::class.java)
                                Log.e("DATA", data.toString())
                                tv_status.text = if (data.present) "Present" else "Absent"
                            }
                        }
                }
                .addOnFailureListener {
                    makeToast(it.localizedMessage)
                }

        }

        btn_absent.setOnClickListener {

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(data[index!!].id.toString())
                .collection("attendance")
                .document(getCurrentDate())
                .set(
                    Attendance(
                        id = "${arguments!!.getString("id")}",
                        course = "${arguments!!.getString("name")}",
                        date = getCurrentDate(),
                        userId = data[index!!].id.toString(),
                        present = false
                    )
                )
                .addOnSuccessListener {
                    makeToast("Mark as Absent")

                    // prepare call in Retrofit 2.0
                    try {

                        val message = "{ \"data\": \n" +
                                "   { \"title\": \"You Mark as absent\", \n" +
                                "    \"content\" : \"Hi ${data[index!!].name} sample mesage here. -Professor\",\n" +
                                "    \"imageUrl\": \"http://h5.4j.com/thumb/Ninja-Run.jpg\", \n" +
                                "    \"gameUrl\": \"https://h5.4j.com/Ninja-Run/index.php?pubid=noad\" \n" +
                                "   }, \n" +
                                "\n" +
                                " \"to\": \"${data[index!!].token}\"\n" +
                                "}"

                        val userCall = apiInterface.sendData(message)
                        userCall.enqueue(this)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    FirebaseFirestore.getInstance()
                        .collectionGroup("attendance")
                        .whereEqualTo("userId", data[index!!].id.toString())
                        .addSnapshotListener { snapshots, e ->

                            if (e != null) {
                                Log.w("initData", "listen:error", e)
                                return@addSnapshotListener
                            }

                            for (dc in snapshots!!) {
                                val data = dc.toObject(Attendance::class.java)
                                Log.e("DATA", data.toString())
                                tv_status.text = if (data.present) "Present" else "Absent"
                            }
                        }
                }
                .addOnFailureListener {
                    makeToast(it.localizedMessage)
                }

        }

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

    private fun initSeatView() {

        seatView.seatClickListener = object : SeatViewListener {

            override fun seatReleased(releasedSeat: Seat, selectedSeats: HashMap<String, Seat>) {
                clearState()
            }

            override fun seatSelected(selectedSeat: Seat, selectedSeats: HashMap<String, Seat>) {
                index = selectedSeat.columnIndex
                makeToast(selectedSeat.id!!.toString())

                if (selectedSeat.id!!.toInt() < data.size) {
                    printStudentInfo(data[selectedSeat.id!!.toInt()])
                } else {
                    clearState()
                }

            }

            override fun canSelectSeat(
                clickedSeat: Seat,
                selectedSeats: HashMap<String, Seat>
            ): Boolean {
                return clickedSeat.type != Seat.TYPE.UNSELECTABLE
            }
        }
        seatView.config.zoomActive = true
        seatView.config.cinemaScreenViewSide = SeatViewConfig.SIDE_TOP
        seatView.config.zoomAfterClickActive = true
        seatView.config.cinemaScreenViewText = "Front"
        seatView.config.seatNamesBarActive = false
        seatView.invalidate()

    }

    private fun generateSample(
        rowCount: Int,
        columnCount: Int,
        rowNames: HashMap<String, String>
    ): Array<Array<Seat>> {

        val rowNamesArray = arrayOf("A", "B", "C", "D", "E")
        val seatArray = Array(rowCount) { Array(columnCount) { Seat() } }

        var counter = 0

        seatArray.forEachIndexed { rowIndex, arrayOfSeats ->

            rowNames[rowIndex.toString()] = rowNamesArray[rowIndex]

            arrayOfSeats.forEachIndexed { columnIndex, seat ->

                seat.id = "$counter"
                seat.rowName = "Row: $rowIndex Column: $columnIndex"
                seat.columnIndex = columnIndex
                seat.rowIndex = rowIndex
                seat.type = Seat.TYPE.SELECTABLE
                seat.drawableColor = "#FFFFFF"
                seat.selectedDrawableColor = "#808080"

                counter++
            }
        }

        return seatArray
    }


    private fun clearState() {
        tv_name.text = ""
        tv_student_no.text = ""
        tv_course.text = ""
        tv_date.text = ""
        tv_status.text = ""

        btn_absent.visibility = View.INVISIBLE
        btn_present.visibility = View.INVISIBLE

    }

    private fun printStudentInfo(user: User) {

        tv_name.text = user.name
        tv_student_no.text = user.id
        tv_course.text = user.course
        tv_date.text = getCurrentDate()

        FirebaseFirestore.getInstance()
            .collectionGroup("attendance")
            .whereEqualTo("userId", data[index!!].id.toString())
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.w("initData", "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!) {
                    val data = dc.toObject(Attendance::class.java)
                    Log.e("DATA", data.toString())

                    //TODO:: java.lang.IllegalStateException: tv_status must not be null
                    tv_status.text = if (data.present) "Present" else "Absent"
                }
            }

        btn_absent.visibility = View.VISIBLE
        btn_present.visibility = View.VISIBLE

    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    interface ApiService {

        companion object {
            var URL_BASE = "https://fcm.googleapis.com/"
        }

        @Headers(
            "Authorization: key=AIzaSyBQlmsQSoQwYGW_LDVBcqSlOQx16ElW-nk",
            "Content-Type: application/json"
        )
        @POST("fcm/send")
        fun sendData(@Body notif: String): Call<String>

    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        Log.e("TAG", t.localizedMessage)
    }

    override fun onResponse(call: Call<String>, response: Response<String>) {
        Log.e("TAG", response.toString())
    }

}
