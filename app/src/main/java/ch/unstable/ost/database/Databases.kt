package ch.unstable.ost.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.common.base.Preconditions
import java.io.FileOutputStream
import java.io.IOException

object Databases {
    @JvmField
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `favorite_connections` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `connection` TEXT NOT NULL, `creation_date` INTEGER NOT NULL)")
            database.execSQL("CREATE  INDEX `index_favorite_connections_creation_date` ON `favorite_connections` (`creation_date`)")
        }
    }
    private const val FORCE_OVERRIDE = true
    private var cacheDatabase: CacheDatabase? = null
    @JvmStatic
    @Synchronized
    fun getCacheDatabase(context: Context): CacheDatabase {
        Preconditions.checkNotNull(context, "context is null")
        var cacheDatabase = this.cacheDatabase
        if (cacheDatabase == null) {
            val databaseName = "cache.db"
            cacheDatabase = Room.databaseBuilder(context.applicationContext, CacheDatabase::class.java, databaseName)
                    .addMigrations(MIGRATION_1_2)
                    .build()
            this.cacheDatabase = cacheDatabase
            return cacheDatabase
        }
        return cacheDatabase
    }

    fun copyDBFromAssets(context: Context, database: String) {
        try {
            val databaseFile = context.applicationContext.getDatabasePath(database)
            val parentDir = databaseFile.parentFile
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw IOException("Couldn't create directory: $parentDir")
                }
            }
            if (FORCE_OVERRIDE || !databaseFile.exists()) {
                context.assets.open(database).use { inputStream ->
                    FileOutputStream(databaseFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}