package com.macsanityapps.virtualattendance


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
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.*
import com.macsanityapps.virtualattendance.seatview.Seat
import com.macsanityapps.virtualattendance.seatview.SeatViewConfig
import com.macsanityapps.virtualattendance.seatview.SeatViewListener
import com.macsanityapps.virtualattendance.view.AttendanceAdapter
import com.macsanityapps.virtualattendance.view.RoomsAdapter
import kotlinx.android.synthetic.main.fragment_seat_plan.*
import kotlinx.android.synthetic.main.fragment_seat_plan.seatView
import kotlinx.android.synthetic.main.fragment_student_seat_map.*
import kotlinx.android.synthetic.main.fragment_teacher_dashboard.*
import kotlinx.coroutines.*
import java.sql.Time
import java.util.HashMap
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class StudentSeatMapFragment : Fragment() {

    private val rowCount = 5
    private val columnCount = 10
    private val rowNames: HashMap<String, String> = HashMap()

    private var data: MutableList<User> = mutableListOf()
    private var parentData : MutableList<ParentData> = mutableListOf()

    private var index : Int? = 0

    private lateinit var attendanceAdapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_seat_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Room ${arguments!!.getString("name")}"

        val pref = activity?.getSharedPreferences("Account", 0)
        val email = pref?.getString("emai", "")


        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(arguments!!.getString("id"))
            .collection("students")
            .whereEqualTo("type", 1)
            .get().addOnSuccessListener {

                val userData = it.toObjects(User::class.java)
                data.addAll(userData)
                index  = data.indexOfFirst { it.email == email }
                Log.i("TAG", "Data Comes")

            }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                Log.i("TAG", "this will be called after 3 seconds")

            }
        }

        val seatArray = generatePreLoadData(rowCount, columnCount, rowNames)
        seatView!!.initSeatView(seatArray, rowCount, columnCount, rowNames)
        initSeatView()

        generateAttendance()
    }

    private fun initSeatView(){

        seatView.seatClickListener = object : SeatViewListener {

            override fun seatReleased(releasedSeat: Seat, selectedSeats: HashMap<String, Seat>) { }

            override fun seatSelected(selectedSeat: Seat, selectedSeats: HashMap<String, Seat>) {
                makeToast(selectedSeat.id.toString())
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

    private fun generatePreLoadData(
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

                if(counter == index!!){
                    seat.drawableColor = "#808080"
                } else {
                    seat.drawableColor = "#FFFFFF"
                }

                counter++
            }
        }

        return seatArray
    }

    private fun generateAttendance(){

        rv_attendance.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val pref = activity?.getSharedPreferences("Account", 0)
        val id = pref?.getString("studentId", "")


       val query =  FirebaseFirestore.getInstance()
            .collection("Users")
            .document(id!!)
            .collection("attendance")
            .whereEqualTo("present", false)

        val options = FirestoreRecyclerOptions.Builder<AttendanceStatus>()
            .setQuery(query, AttendanceStatus::class.java)
            .build()


        Log.e("DATA", options.snapshots.toString())

        attendanceAdapter = AttendanceAdapter(options)
        rv_attendance.adapter = attendanceAdapter

        attendanceAdapter.startListening()

    }

}
