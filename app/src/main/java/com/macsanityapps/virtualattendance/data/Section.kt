package com.macsanityapps.virtualattendance.data

import com.macsanityapps.virtualattendance.common.UserStudent

data class Section(val creationDate:String,
                   val contents:String,
                   val upVotes: Int,
                   val imageUrl: String,
                   val isEnroll : Boolean,
                   val creator: UserStudent?)

