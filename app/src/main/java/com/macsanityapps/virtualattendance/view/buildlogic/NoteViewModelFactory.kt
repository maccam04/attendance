package com.macsanityapps.virtualattendance.view.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository

import kotlinx.coroutines.Dispatchers

class NoteViewModelFactory(
    private val noteRepo: ISectionRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return NoteViewModel(noteRepo, Dispatchers.Main) as T
    }

}