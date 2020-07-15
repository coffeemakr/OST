package ch.unstable.ost.api.sbb

import android.content.Context
import ch.unstable.lib.sbb.SbbApiFactory
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.preference.StationDaoLoader


class SbbStationsDaoFactory : StationDaoLoader.StationDAOFactory {
    override fun getStationsDAO(context: Context): StationsDAO {
        val api = SbbApiFactory().createAPI(context)
        return SbbStationDao(api)
    }
}
