package ch.unstable.ost.database

import androidx.room.TypeConverter
import ch.unstable.ost.api.model.Connection
import ch.unstable.ost.api.model.ConnectionQuery
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ConnectionConverters {
    companion object {
        @JvmStatic
        @TypeConverter
        fun connectionArrayToString(connection: Connection): String {
            val gson = Gson()
            return gson.toJson(connection)
        }

        @JvmStatic
        @TypeConverter
        fun connectionStringToArray(connectionString: String): Connection {
            val gson = Gson()
            return gson.fromJson(connectionString, Connection::class.java)
        }

        @JvmStatic
        @TypeConverter
        fun connectionQueryToString(connection: ConnectionQuery): String {
            val gson = Gson()
            return gson.toJson(connection)
        }

        @JvmStatic
        @TypeConverter
        fun readConnectionQuery(connectionString: String): ConnectionQuery {
            val gson = Gson()
            return gson.fromJson(connectionString, ConnectionQuery::class.java)
        }

        @JvmStatic
        @TypeConverter
        fun dateToLong(date: Date?): Long {
            return if (date == null) 0 else date.time / 1000L
        }

        @JvmStatic
        @TypeConverter
        fun longToDate(date: Long): Date? {
            return if (date == 0L) null else Date(date * 1000L)
        }

        @JvmStatic
        @TypeConverter
        fun stringArrayToCSV(values: Array<String>): String {
            return Gson().toJson(values)
        }

        @JvmStatic
        @TypeConverter
        fun csvToStringArray(value: String): Array<String> {
            return Gson().fromJson(value, Array<String>::class.java)
        }

        @TypeConverter
        fun stringListToCSV(values: List<String>): String {
            return Gson().toJson(values)
        }

        @TypeConverter
        fun csvToStringList(value: String): List<String> {
            return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
        }
    }
}