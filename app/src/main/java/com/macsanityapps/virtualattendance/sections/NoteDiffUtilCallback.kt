package com.macsanityapps.virtualattendance.sections


import androidx.recyclerview.widget.DiffUtil
import com.macsanityapps.virtualattendance.data.Section

class NoteDiffUtilCallback : DiffUtil.ItemCallback<Section>(){
    override fun areItemsTheSame(oldItem: Section, newItem: Section): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }

    override fun areContentsTheSame(oldItem: Section, newItem: Section): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }
}