package com.macsanityapps.virtualattendance.data.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.macsanityapps.virtualattendance.common.*
import com.macsanityapps.virtualattendance.common.awaitTaskResult
import com.macsanityapps.virtualattendance.common.toSection
import com.macsanityapps.virtualattendance.data.FirebaseNote
import com.macsanityapps.virtualattendance.data.Section
import com.macsanityapps.virtualattendance.data.SectionDao
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository


private const val COLLECTION_NAME = "sections"

/**
 * If this wasn't a demo project, I would apply more abstraction to this repository (i.e. local and remote would be
 * separate interfaces which this class would depend on). I wanted to keep it the back end simple since this app is
 * a demo on MVVM, which is a front end architecture pattern.
 */
class SectionRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val local: SectionDao
) : ISectionRepository {

    override suspend fun getOwnNotes(): ResultWrapper<Exception, List<Section>> {
        val user = getActiveUser()
        return if (user != null) getRemoteOwnSection(user)
        else getLocalNotes()
    }

    override suspend fun getNoteById(noteId: String): ResultWrapper<Exception, Section> {
        val user = getActiveUser()
        return if (user != null) getRemoteNote(noteId, user)
        else getLocalNote(noteId)
    }

    override suspend fun deleteNote(note: Section): ResultWrapper<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) deleteRemoteNote(note.copy(creator = user))
        else deleteLocalNote(note)
    }

    override suspend fun updateNote(note: Section): ResultWrapper<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) updateRemoteNote(note.copy(creator = user))
        else updateLocalNote(note)
    }

    override suspend fun getNotes(): ResultWrapper<Exception, List<Section>> {
        val user = getActiveUser()
        return if (user != null) getRemoteNotes(user)
        else getLocalNotes()
    }

    /**
     * if currentUser != null, return true
     */
    private fun getActiveUser(): UserStudent? {
        return firebaseAuth.currentUser?.toUser
    }


    private fun resultToNoteList(result: QuerySnapshot?): ResultWrapper<Exception, List<Section>> {
        val noteList = mutableListOf<Section>()

        result?.forEach { documentSnapshot ->
            noteList.add(documentSnapshot.toObject(FirebaseNote::class.java).toSection)
        }

        return ResultWrapper.build {
            noteList
        }
    }


    /* Remote Datasource */

    private suspend fun getRemoteNotes(user: UserStudent): ResultWrapper<Exception, List<Section>> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user.uid)
                    .get()
            )

            resultToNoteList(task)
        } catch (exception: Exception) {
            ResultWrapper.build { throw exception }
        }
    }

    private suspend fun getRemoteOwnSection(user: UserStudent): ResultWrapper<Exception, List<Section>> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user.uid)
                    .get()
            )

            resultToNoteList(task)
        } catch (exception: Exception) {
            ResultWrapper.build { throw exception }
        }
    }


    private suspend fun getRemoteNote(creationDate: String, user: UserStudent): ResultWrapper<Exception, Section> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .document(creationDate + user.uid)
                    .get()
            )

            ResultWrapper.build {
                //Task<DocumentSnapshot!>
                task.toObject(FirebaseNote::class.java)?.toSection ?: throw Exception()
            }
        } catch (exception: Exception) {
            ResultWrapper.build { throw exception }
        }
    }

    private suspend fun deleteRemoteNote(note: Section): ResultWrapper<Exception, Unit> = ResultWrapper.build {
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.creationDate + note.creator!!.uid)
                .delete()
        )
    }

    /**
     * Notes are stored with the following composite document name:
     * note.creationDate + note.creator.uid
     * The reason for this, is that if I just used the creationDate, hypothetically two users
     * creating a note at the same time, would have duplicate entries in the cloud database :(
     */
    private suspend fun updateRemoteNote(note: Section): ResultWrapper<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.creationDate + note.creator!!.uid)
                    .set(note.toFirebaseNote)
            )

            ResultWrapper.build { Unit }

        } catch (exception: Exception) {
            ResultWrapper.build { throw exception }
        }
    }

    /* Local Datasource */
    private suspend fun getLocalNotes(): ResultWrapper<Exception, List<Section>> = ResultWrapper.build {
        local.getSections().toNoteListFromRoomNote()
    }

    private suspend fun getLocalNote(id: String): ResultWrapper<Exception, Section> = ResultWrapper.build {
        local.getSectioneById(id).toNote
    }

    private suspend fun deleteLocalNote(note: Section): ResultWrapper<Exception, Unit> = ResultWrapper.build {
        local.deleteSection(note.toRoomNote)
        Unit
    }

    private suspend fun updateLocalNote(note: Section): ResultWrapper<Exception, Unit> = ResultWrapper.build {
        local.insertOrUpdateSection(note.toRoomNote)
        Unit
    }



}