package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository
import com.macsanityapps.virtualattendance.sections.SectionListViewModel
import kotlinx.coroutines.Dispatchers

class NoteListViewModelFactory(
    private val noteRepo: ISectionRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return SectionListViewModel(noteRepo, Dispatchers.Main) as T
    }

}