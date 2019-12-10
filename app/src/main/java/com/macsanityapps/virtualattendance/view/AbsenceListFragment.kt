package com.macsanityapps.virtualattendance.view


import android.os.Bundle
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
import com.macsanityapps.virtualattendance.R
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

        (activity as AppCompatActivity).supportActionBar!!.show()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val riskMap = AnyChart.heatMap()

        riskMap.stroke("1 #fff")
        riskMap.hovered()
            .stroke("6 #fff")
            .fill(SolidFill("#545f69", 1.0))
            .labels("{ fontColor: '#fff' }")

        riskMap.interactivity().selectionMode(SelectionMode.NONE)

        riskMap.title().enabled(true)
        riskMap.title()
            .text("Number of Absents")
            .padding(0.0, 0.0, 20.0, 0.0)

        riskMap.labels().enabled(true)
        riskMap.labels()
            .minFontSize(14.0)
            .format(
                "function() {\n" +
                        "      var namesList = [\"X\"];\n" +
                        "      return namesList[this.heat];\n" +
                        "    }"
            )

        riskMap.yAxis(0).stroke(null)
        riskMap.yAxis(0).labels().padding(0.0, 15.0, 0.0, 0.0)
        riskMap.yAxis(0).ticks(false)
        riskMap.xAxis(0).stroke(null)
        riskMap.xAxis(0).ticks(false)

        riskMap.tooltip().title().useHtml(true)
        riskMap.tooltip()
            .useHtml(true)
            .titleFormat(
                "function() {\n" +
                        "      var namesList = [\"X\"];\n" +
                        "      return '<b>' + namesList[this.heat] + '</b> ';\n" +
                        "    }"
            )
            .format(
                "function () {\n" +
                        "       return '<span style=\"color: #CECECE\">Date: </span>' + this.x + '<br/>' +\n" +
                        "           '<span style=\"color: #CECECE\">Subject: </span>' + this.y;\n" +
                        "   }"
            )

        val data = ArrayList<CustomHeatDataEntry>()
        data.add(CustomHeatDataEntry("10/29", "IT-008", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("10/31", "IT-009", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("11/04", "IT-010", 0, "#90caf9"))

        riskMap.data(data as List<DataEntry>?)

        any_chart_view.setChart(riskMap)

    }

    private inner class CustomHeatDataEntry internal constructor(
        x: String,
        y: String,
        heat: Int?,
        fill: String
    ) : HeatDataEntry(x, y, heat) {
        init {
            setValue("fill", fill)
        }

    }

}
