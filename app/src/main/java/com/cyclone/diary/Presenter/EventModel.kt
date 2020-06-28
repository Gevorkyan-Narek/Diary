package com.cyclone.diary.Presenter

import android.content.Context
import com.cyclone.diary.Model.Event
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.lang.Exception

class EventModel {

    companion object {
        private lateinit var realm: Realm

        fun realmInit(context: Context) {
            Realm.init(context)
            realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        }

        fun addEvent(event: Event): Boolean {
            return try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(event)
                realm.commitTransaction()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun editEvent(event: Event): Boolean {
            return try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(event)
                realm.commitTransaction()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun deleteEvent(event_id: Int): Boolean {
            return try {
                realm.beginTransaction()
                realm.where(Event::class.java).equalTo("id", event_id).findFirst()
                    ?.deleteFromRealm()
                realm.commitTransaction()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun getEvent(event_id: Int): Event {
            return realm.where(Event::class.java).equalTo("id", event_id).findFirst()!!
        }

        fun getEvents(): RealmResults<Event> {
            return realm.where(Event::class.java).findAll()
        }

        fun getLastEvent(): Event {
            return realm.where(Event::class.java).findAll().last()!!
        }
    }
}