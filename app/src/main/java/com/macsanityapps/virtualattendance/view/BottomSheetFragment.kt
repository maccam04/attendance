package com.macsanityapps.virtualattendance.view


import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.data.User

/**
 * A simple [Fragment] subclass.
 */
class BottomSheetFragment : BottomSheetDialogFragment() {

    private var studentAdapter: StudentAdapter? = null

    companion object {
        fun newInstance(roomId: String) = BottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString("id", roomId)
            }
        }
    }

    private lateinit var dialog: BottomSheetDialog
    private lateinit var behavior: BottomSheetBehavior<View>


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        /* dialog.setOnShowListener {
             val d = it as BottomSheetDialog
             val sheet = d.findViewById<View>(android.support.design.R.id.design_bottom_sheet)
             behavior = BottomSheetBehavior.from(sheet)
             behavior.isHideable = false
             behavior.state = BottomSheetBehavior.STATE_COLLAPSED
         }*/
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }


    private fun initRecyclerView() {



    }



}
