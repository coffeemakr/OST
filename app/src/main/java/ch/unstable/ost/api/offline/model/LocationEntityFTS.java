package ch.unstable.ost.api.offline.model;

import android.arch.persistence.room.PrimaryKey;

/**
 * Dummy to make room library happy
 */
@Entity(tableName = "fts_stations")
public class LocationEntityFTS {

    @PrimaryKey
    private int docid;

    private int fts_stations;

    public int getDocid() {
        return docid;
    }

    public void setDocid(int docid) {
        this.docid = docid;
    }

    public int getFts_stations() {
        return fts_stations;
    }

    public void setFts_stations(int fts_stations) {
        this.fts_stations = fts_stations;
    }
}
