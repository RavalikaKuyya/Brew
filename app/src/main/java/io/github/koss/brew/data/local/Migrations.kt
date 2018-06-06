package io.github.koss.brew.data.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE drink ADD COLUMN remoteId TEXT;")
        database.execSQL("ALTER TABLE drink ADD COLUMN timestamp INTEGER;")
        database.execSQL("ALTER TABLE drink ADD COLUMN description TEXT;")
    }
}