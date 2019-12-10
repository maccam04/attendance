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
import com.macsanityapps.virtualattendance.sections.NoteListAdapter
import com.macsanityapps.virtualattendance.sections.NoteListEvent
import com.macsanityapps.virtualattendance.sections.SectionListViewModel
import com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic.NoteListInjector
import kotlinx.android.synthetic.main.fragment_add_section.*

/**
 * A simple [Fragment] subclass.
 */
class SectionListFragment : Fragment() {

    private lateinit var viewModel: SectionListViewModel
    private lateinit var adapter: NoteListAdapter


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
        viewModel = ViewModelProvider(
            this,
            NoteListInjector(requireActivity().application).provideNoteListViewModelFactory()
        ).get(
            SectionListViewModel::class.java
        )

        setUpAdapter()
        observeViewModel()

        viewModel.handleEvent(
            NoteListEvent.OnStartSection
        )
    }

    private fun setUpAdapter() {
        adapter = NoteListAdapter()
        adapter.event.observe(
            viewLifecycleOwner,
            Observer {
                viewModel.handleEvent(it)
            }
        )

        rec_list_fragment.adapter = adapter

    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.noteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                adapter.submitList(noteList)

            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer {

                val builder = AlertDialog.Builder(activity)

                // Set the alert dialog title
                builder.setTitle("${it.contents}")

                // Display a message on alert dialog
                builder.setMessage("Leaving Request Sent!")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK"){dialog, which ->

                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()

                // startNoteDetailWithArgs(noteId)
            }
        )
    }

/*    private fun startNoteDetailWithArgs(noteId: String) = findNavController().navigate(
     //   NoteListViewDirections.actionNoteListViewToNoteDetailView(noteId)
    )*/


    private fun showErrorState(errorMessage: String?) = makeToast(errorMessage!!)



}
