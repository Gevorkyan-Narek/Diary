package com.cyclone.diary.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.sql.Timestamp
import java.util.*

open class Event(
    @PrimaryKey open var id: Int = 0,
    open var title: String = "",
    open var time: String = "",
    open var endtime: String = "",
    open var date: String = "",
    open var description: String = ""
) : RealmObject()