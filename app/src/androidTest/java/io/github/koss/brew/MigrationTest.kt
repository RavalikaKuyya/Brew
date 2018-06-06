package io.github.koss.brew

import android.support.test.runner.AndroidJUnit4
import org.junit.runner.RunWith
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.support.test.InstrumentationRegistry
import android.arch.persistence.room.testing.MigrationTestHelper
import io.github.koss.brew.data.local.BrewDatabase
import org.junit.Rule
import io.github.koss.brew.data.local.MIGRATION_1_2
import org.junit.Test
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @Rule
    @JvmField
    var migrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            BrewDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrationFrom1To2_migrates() {
        migrationTestHelper.runMigrationsAndValidate(DB_NAME, 2, true,
                MIGRATION_1_2)
    }

    companion object {
        const val DB_NAME = "brew-database"
    }
}