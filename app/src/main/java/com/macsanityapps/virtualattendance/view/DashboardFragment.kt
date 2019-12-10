package com.macsanityapps.virtualattendance.view


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.macsanityapps.virtualattendance.common.makeToast
import com.macsanityapps.virtualattendance.view.buildlogic.NoteDetailInjector
import com.macsanityapps.virtualattendance.view.buildlogic.NoteViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {


    private lateinit var viewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        return inflater.inflate(com.macsanityapps.virtualattendance.R.layout.fragment_dashboard, container, false)
    }

    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.show()

        viewModel = ViewModelProvider(
            this,
            NoteDetailInjector(requireActivity().application).provideNoteViewModelFactory()
        ).get(
            NoteViewModel::class.java
        )


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(com.macsanityapps.virtualattendance.R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            com.macsanityapps.virtualattendance.R.id.action_add -> {
                val dir =  DashboardFragmentDirections.actionDashboardFragmentToUpdateProfileFragment()
                findNavController().navigate(dir)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var i = 1

        iv_add_section.setOnClickListener   {
            makeToast("No section/s available as of the moment.")
        }

        iv_leave_section.setOnClickListener {

            val dir =  DashboardFragmentDirections.actionDashboardFragmentToSectionListFragment()
            findNavController().navigate(dir)

        }
        iv_list_absence.setOnClickListener  {

            val dir =  DashboardFragmentDirections.actionDashboardFragmentToAddSectionFragment()
            findNavController().navigate(dir)
        }
        iv_list_section.setOnClickListener  {
            val dir =  DashboardFragmentDirections.actionDashboardFragmentToAbsenceListFragment()
            findNavController().navigate(dir)

        }

    }

}
