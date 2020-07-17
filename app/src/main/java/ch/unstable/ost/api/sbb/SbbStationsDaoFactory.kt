package ch.unstable.ost.api.sbb

import android.content.Context
import androidx.annotation.Keep
import ch.unstable.lib.sbb.SbbApiFactory
import ch.unstable.ost.api.StationsDAO
import ch.unstable.ost.preference.StationDaoLoader


@Keep
@Suppress("unused")
class SbbStationsDaoFactory : StationDaoLoader.StationDAOFactory {
    override fun getStationsDAO(context: Context): StationsDAO {
        val api = SbbApiFactory().createAPI(SbbApiFactory().createSslContext(context))
        return SbbStationDao(api)
    }
}
