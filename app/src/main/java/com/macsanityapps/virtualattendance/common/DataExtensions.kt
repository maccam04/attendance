package com.macsanityapps.virtualattendance.common

import android.text.Editable
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.macsanityapps.virtualattendance.data.FirebaseNote
import com.macsanityapps.virtualattendance.data.RoomSection
import com.macsanityapps.virtualattendance.data.Section
import com.macsanityapps.virtualattendance.data.User
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

//Wraps Firebase/GMS calls
internal suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

internal val FirebaseUser.toUser: UserStudent
    get() = UserStudent(
        uid = this.uid,
        name = this.displayName ?: ""
    )

internal val FirebaseNote.toSection: Section
    get() = Section(
        this.creationDate ?: "",
        this.contents ?: "",
        this.upVotes ?: 0,
        this.imageUrl ?: "",
        false,
        UserStudent(this.creator ?: "")
    )

internal val Section.toFirebaseNote: FirebaseNote
    get() = FirebaseNote(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        this.safeGetUid
    )

internal val RoomSection.toNote: Section
    get() = Section(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        false,
        UserStudent(this.creatorId)
    )

internal val Section.toRoomNote: RoomSection
    get() = RoomSection(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        this.safeGetUid
    )

internal fun List<RoomSection>.toNoteListFromRoomNote(): List<Section> = this.flatMap {
    listOf(it.toNote)
}

internal fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

internal val Section.safeGetUid: String
    get() = this.creator?.uid ?: ""

