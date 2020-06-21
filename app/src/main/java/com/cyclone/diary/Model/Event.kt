package com.cyclone.diary.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Event(
    @PrimaryKey open var id: Int = 0,
    open var title: String = "",
//    open var time: Long = Calendar.getInstance(Locale.getDefault()).time.time,
//    open var date: Int = Calendar.getInstance(Locale.getDefault()).get(Calendar.DATE),
    open var time: String = "",
    open var date: String = "",
    open var description: String = ""
) : RealmObject()