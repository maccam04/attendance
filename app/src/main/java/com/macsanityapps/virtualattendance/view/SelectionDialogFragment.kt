package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.SelectionDialogListener
import kotlinx.android.synthetic.main.fragment_selection_dialog.*


/**
 * A simple [Fragment] subclass.
 */
class SelectionDialogFragment : DialogFragment() {


    private lateinit var selectionDialogListener: SelectionDialogListener

    fun newInstance(): SelectionDialogFragment {
        return SelectionDialogFragment()
    }

  /*  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity?.layoutInflater?.inflate(R.layout.fragment_selection_dialog, null, false)

        val alertDialog = AlertDialog.Builder(activity!!)
        //restore the background_color and layout_gravity that Android strips
        alertDialog.setView(view)
        return alertDialog.create()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selection_dialog, container, false)
    }

    fun setSelectionDialogListener(selectionDialogListener: SelectionDialogListener) {
        this.selectionDialogListener = selectionDialogListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_student.setOnClickListener {
            selectionDialogListener.onSelectStudent()
            dialog?.dismiss()
        }

        btn_teacher.setOnClickListener {
            selectionDialogListener.onSelectTeacher()
            dialog?.dismiss()
        }

    }
}
