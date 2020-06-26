package com.cyclone.diary.Presenter

import io.realm.RealmConfiguration

class RealmUtility {
    companion object {
        private val SCHEMA_V_PREV = 3
        private val SCHEMA_V_NOW = 4
        fun getSchemaVNow(): Int = SCHEMA_V_NOW
        fun getDefaultConfig(): RealmConfiguration =
            RealmConfiguration.Builder().schemaVersion(SCHEMA_V_NOW.toLong())
                .deleteRealmIfMigrationNeeded().build()
    }
}