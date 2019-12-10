package com.wiseassblog.jetpacknotesmvvmkotlin.note.notelist.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.macsanityapps.virtualattendance.data.RoomSectionDatabase
import com.macsanityapps.virtualattendance.data.implementations.SectionRepoImpl
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository


class NoteListInjector(application:Application): AndroidViewModel(application) {
    private fun getNoteRepository(): ISectionRepository {
        FirebaseApp.initializeApp(getApplication())
        return SectionRepoImpl(
            local = RoomSectionDatabase.getInstance(getApplication()).roomNoteDao()
        )
    }

    fun provideNoteListViewModelFactory(): NoteListViewModelFactory =
        NoteListViewModelFactory(
            getNoteRepository()
        )
}