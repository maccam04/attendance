package com.macsanityapps.virtualattendance.view


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.macsanityapps.virtualattendance.R
import com.macsanityapps.virtualattendance.common.makeToast

import kotlinx.android.synthetic.main.fragment_add_section.*

/**
 * A simple [Fragment] subclass.
 */
class SectionListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_section_list, container, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //THIS IS IMPORTANT!!!
        rec_list_fragment.adapter = null
    }

    override fun onStart() {
        super.onStart()


    }


}
