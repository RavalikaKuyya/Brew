package io.github.koss.brew.data.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE drink ADD COLUMN photoDeleteHash TEXT;")
        database.execSQL("ALTER TABLE drink ADD COLUMN photoId TEXT;")
        database.execSQL("ALTER TABLE drink ADD COLUMN photoLink TEXT;")
    }
}