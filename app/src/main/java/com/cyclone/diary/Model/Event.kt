package com.cyclone.diary.Model

import io.realm.RealmObject
import io.realm.Realm.*
import io.realm.annotations.PrimaryKey
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalTime
import java.util.*

open class Event(
    @PrimaryKey var id: Int = 0,
    var title: String = "",
    var starttime: Date = Date.from(Instant.now()),
    var endtime: Date = Date.from(Instant.now()),
    var description: String = ""
) : RealmObject()