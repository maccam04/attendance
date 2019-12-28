package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.data.User
import com.macsanityapps.virtualattendance.seatview.Seat
import com.macsanityapps.virtualattendance.seatview.SeatViewConfig
import com.macsanityapps.virtualattendance.seatview.SeatViewListener
import kotlinx.android.synthetic.main.fragment_seat_plan.*
import java.util.*




/**
 * A simple [Fragment] subclass.
 */
class SeatPlanFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var size: String? = ""
    private var data : MutableList<User> = mutableListOf()
    private var index : Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seat_plan, container, false)
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
            .collection("room${arguments!!.getString("id")}")
            .get()
            .addOnSuccessListener {
                data = it.toObjects(User::class.java)
                tv_student_count.text = "Number of Student/s in this section: ${data.size}"

                if(data.size != 0 ) {
                    size = data.size.toString()
                }
            }
            .addOnFailureListener {
                makeToast(it.localizedMessage)
            }

        val seatArray = generateSample(data, rowCount, columnCount, rowNames)
        seatView!!.initSeatView(seatArray, rowCount, columnCount, rowNames)
        initSeatView()


        spinner.onItemSelectedListener = this

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                requireActivity().onBackPressedDispatcher.addCallback(this) {
                    findNavController().navigate(R.id.teacherDashboardFragment)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initSeatView() {

        seatView.seatClickListener = object : SeatViewListener {

            override fun seatReleased(releasedSeat: Seat, selectedSeats: HashMap<String, Seat>) {
                clearState()
            }

            override fun seatSelected(selectedSeat: Seat, selectedSeats: HashMap<String, Seat>) {
                index = selectedSeat.columnIndex

                if(selectedSeat.columnIndex < data.size){
                    printStudentInfo(data[selectedSeat.columnIndex])
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
        seatView.config.cinemaScreenViewSide = SeatViewConfig.SIDE_BOTTOM
        seatView.config.zoomAfterClickActive = true
        seatView.config.cinemaScreenViewText = "Front"
        seatView.config.seatNamesBarActive = false
        seatView.invalidate()

    }

    private fun generateSample(
        data: MutableList<User>,
        rowCount: Int,
        columnCount: Int,
        rowNames: HashMap<String, String>
    ): Array<Array<Seat>> {
        val rowNamesArray = arrayOf("A", "B", "C", "D", "E")

        val seatArray = Array(rowCount) { Array(columnCount) { Seat() } }

        seatArray.forEachIndexed { rowIndex, arrayOfSeats ->
            rowNames[rowIndex.toString()] = rowNamesArray[rowIndex]

            arrayOfSeats.forEachIndexed { columnIndex, seat ->

                seat.id = (rowIndex.toString() + "_" + columnIndex.toString())
                seat.rowName = "Row: $rowIndex Column: $columnIndex"
                seat.columnIndex = columnIndex
                seat.rowIndex = rowIndex
                seat.type = Seat.TYPE.SELECTABLE
                seat.drawableColor = "#FFFFFF"
                seat.selectedDrawableColor = "#808080"

            }
        }

        return seatArray
    }


    private fun clearState() {
        tv_name.text = ""
        tv_student_no.text = ""
        tv_course.text = ""
        tv_date.text = ""
    }

    private fun printStudentInfo(user: User){
        tv_name.text = user.name
        tv_student_no.text= user.id
        tv_course.text = user.course

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()

//        makeToast(data.get(index = index!!).name!! + " Attendance updated.")
       /* FirebaseFirestore.getInstance()
            .collection("room${arguments!!.getString("id")}")
            .whereEqualTo("id", data[index!!].id)
            .update("status", item)
            .addOnSuccessListener {
                makeToast("Attendance Updated Successfully.")
            }
            .addOnFailureListener {
                print(it.localizedMessage)
            }*/
    }

}
