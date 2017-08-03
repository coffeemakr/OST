package ch.unstable.ost.api.search.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.search.types.SearchCHIconClassDeserializer;
import ch.unstable.ost.utils.ParcelUtils;

public class LocationCompletion implements Location, Parcelable {


    public static final Creator<LocationCompletion> CREATOR = new Creator<LocationCompletion>() {
        @Override
        public LocationCompletion createFromParcel(Parcel in) {
            return new LocationCompletion(in);
        }

        @Override
        public LocationCompletion[] newArray(int size) {
            return new LocationCompletion[size];
        }
    };
    private final String id;
    @SerializedName("label")
    private final String name;
    @SerializedName("iconclass")
    @JsonAdapter(SearchCHIconClassDeserializer.class)
    private final StationType type;

    public LocationCompletion(String id, String name, StationType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    protected LocationCompletion(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = ParcelUtils.readEnum(StationType.values(), in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        ParcelUtils.writeEnum(dest, type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public StationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "LocationCompletion{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
