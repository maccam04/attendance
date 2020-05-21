package com.macsanityapps.virtualattendance


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
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

    private var data: MutableList<Rooms> = mutableListOf()
    private var parentData: MutableList<ParentData> = mutableListOf()

    private var index: Int? = null

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
        val name = pref?.getString("name", "")


        FirebaseFirestore.getInstance()
            .collection("Rooms")
            .document(arguments!!.getString("id"))
            .get().addOnSuccessListener { it ->

                val rooms = it.toObject(Rooms::class.java)
                val sortedList = rooms!!.studentsList!!.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { text -> text })

                index = sortedList.indexOfFirst { e -> e == name }

                activity!!.runOnUiThread {
                    val seatArray = generatePreLoadData(index!!, rowCount, columnCount, rowNames)
                    seatView!!.initSeatView(seatArray, rowCount, columnCount, rowNames)
                    initSeatView()


                    generateAttendance()
                }
            }
    }

    private fun initSeatView() {

        seatView.seatClickListener = object : SeatViewListener {

            override fun seatReleased(releasedSeat: Seat, selectedSeats: HashMap<String, Seat>) {}

            override fun seatSelected(selectedSeat: Seat, selectedSeats: HashMap<String, Seat>) {

            }

            override fun canSelectSeat(
                clickedSeat: Seat,
                selectedSeats: HashMap<String, Seat>
            ): Boolean {
                return clickedSeat.type != Seat.TYPE.UNSELECTABLE
            }
        }

        seatView.config.zoomActive = false
        seatView.config.cinemaScreenViewSide = SeatViewConfig.SIDE_TOP
        seatView.config.zoomAfterClickActive = false
        seatView.config.cinemaScreenViewText = "Front"
        seatView.config.seatNamesBarActive = false
        seatView.invalidate()

    }

    private fun generatePreLoadData(
        index: Int,
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
                seat.type = Seat.TYPE.UNSELECTABLE

                if (counter == index) {
                    seat.drawableColor = "#808080"
                } else {
                    seat.drawableColor = "#FFFFFF"
                }

                counter++
            }
        }

        return seatArray
    }

    private fun generateAttendance() {

        rv_attendance.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val pref = activity?.getSharedPreferences("Account", 0)
        val id = pref?.getString("studentId", "")

        val query = FirebaseFirestore.getInstance()
            .collection("Users")
            .document(id!!)
            .collection("attendance")
            .whereEqualTo("present", false)

        val options = FirestoreRecyclerOptions.Builder<AttendanceStatus>()
            .setQuery(query, AttendanceStatus::class.java)
            .build()


        attendanceAdapter = AttendanceAdapter(options)
        rv_attendance.adapter = attendanceAdapter

        attendanceAdapter.startListening()

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

}
