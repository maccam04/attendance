package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.HeatDataEntry
import com.anychart.charts.HeatMap
import com.anychart.enums.SelectionMode
import com.anychart.graphics.vector.SolidFill
import com.google.firebase.firestore.FirebaseFirestore
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.Attendance
import com.macsanityapps.virtualattendance.data.ChildData
import com.macsanityapps.virtualattendance.data.ParentData
import kotlinx.android.synthetic.main.fragment_absence_list.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class AbsenceListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_absence_list, container, false)
    }

    override fun onStart() {
        super.onStart()

      //  (activity as AppCompatActivity).supportActionBar!!.show()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}
