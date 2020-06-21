package com.cyclone.diary.Model

import com.cyclone.diary.Presenter.EventInterface
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class EventModel : EventInterface {
    override fun addEvent(realm: Realm, event: Event): Boolean {
        return try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(event)
            realm.commitTransaction()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun editEvent(realm: Realm, event: Event): Boolean {
        return try {
            realm.beginTransaction()
            realm.copyToRealm(event)
            realm.commitTransaction()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteEvent(realm: Realm, event_id: Int): Boolean {
        return try {
            realm.beginTransaction()
            realm.where(Event::class.java).equalTo("id", event_id).findFirst()?.deleteFromRealm()
            realm.commitTransaction()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getEvent(realm: Realm, event_id: Int): Event {
            return realm.where(Event::class.java).equalTo("id", event_id).findFirst()!!
    }

    fun getEvents(realm: Realm): RealmResults<Event> {
        return realm.where(Event::class.java).findAll()
    }

    fun getLastEvent(realm: Realm): Event {
        return realm.where(Event::class.java).findAll().last()!!
    }
}