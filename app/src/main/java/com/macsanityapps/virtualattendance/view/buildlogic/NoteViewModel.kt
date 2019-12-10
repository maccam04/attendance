package com.macsanityapps.virtualattendance.view.buildlogic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.macsanityapps.virtualattendance.common.BaseViewModel
import com.macsanityapps.virtualattendance.common.GET_NOTE_ERROR
import com.macsanityapps.virtualattendance.common.ResultWrapper
import com.macsanityapps.virtualattendance.data.Section
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository
import com.macsanityapps.virtualattendance.view.NoteDetailEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class NoteViewModel(
    val noteRepo: ISectionRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteDetailEvent>(uiContext) {

    private val noteState = MutableLiveData<Section>()
    val note: LiveData<Section> get() = noteState

    private val deletedState = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> get() = deletedState

    private val updatedState = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean> get() = updatedState

    override fun handleEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.OnStart -> getNote(event.noteId)
            is NoteDetailEvent.OnDeleteClick -> onDelete()
            is NoteDetailEvent.OnDoneClick -> updateNote(event.contents)
        }
    }

    private fun onDelete() = launch {
        val deleteResult = noteRepo.deleteNote(note.value!!)

        when (deleteResult) {
            is ResultWrapper.Value -> deletedState.value = true
            is ResultWrapper.Error -> deletedState.value = false
        }
    }


    private fun updateNote(contents: String) = launch {
        val updateResult = noteRepo.updateNote(
            Section(getCalendarTime(), contents, 0, "rocket_loop", false,null)
        )

        when (updateResult) {
            is ResultWrapper.Value -> updatedState.value = true
            is ResultWrapper.Error -> updatedState.value = false
        }
    }

    private fun getNote(noteId: String) = launch {
        if (noteId == "") newNote()
        else {
            val noteResult = noteRepo.getNoteById(noteId)

            when (noteResult) {
                is ResultWrapper.Value -> noteState.value = noteResult.value
                is ResultWrapper.Error -> errorState.value = GET_NOTE_ERROR
            }
        }
    }

    private fun newNote() {
        noteState.value =
            Section(getCalendarTime(), "", 0, "rocket_loop", false,null)
    }


    private fun getCalendarTime(): String {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val format = SimpleDateFormat("d MMM yyyy HH:mm:ss Z")
        format.timeZone = cal.timeZone
        return format.format(cal.time)
    }
}