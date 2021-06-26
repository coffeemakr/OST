package ch.unstable.ost.preference

import android.content.Context
import android.preference.PreferenceManager
import ch.unstable.ost.R
import ch.unstable.ost.api.StationsDAO

object StationDaoLoader {
    fun createStationDAO(context: Context): StationsDAO {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val className = sharedPreferences.getString(PreferenceKeys.KEY_STATIONS_DAO, context.getString(R.string.prefs_station_dao_factory_default))!!
        return try {
            val clazz = Class.forName(className)
            val daoFactory = clazz.getDeclaredConstructor().newInstance() as StationDAOFactory
            daoFactory.getStationsDAO(context)
        } catch (e: Exception) {
            throw IllegalStateException("Couldn't get factory for StationsDAO $className", e)
        }
    }

    interface StationDAOFactory {
        fun getStationsDAO(context: Context): StationsDAO
    }
}