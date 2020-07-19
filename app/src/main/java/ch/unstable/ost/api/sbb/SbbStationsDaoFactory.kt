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
        return SbbApiFactory().createAPI(SbbApiFactory().createSslContext(context))
    }
}
