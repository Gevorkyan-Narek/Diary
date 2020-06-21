package com.cyclone.diary.Presenter

import com.cyclone.diary.Model.Event
import io.realm.Realm

interface EventInterface {
    fun addEvent(realm: Realm, event: Event): Boolean
    fun editEvent(realm: Realm, event: Event): Boolean
    fun deleteEvent(realm: Realm, event_id: Int): Boolean
    fun getEvent(realm: Realm, event_id: Int): Event?
}