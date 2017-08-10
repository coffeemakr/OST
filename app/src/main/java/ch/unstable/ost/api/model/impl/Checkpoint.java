package ch.unstable.ost.api.model.impl;

import android.arch.persistence.room.Database;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;

import com.google.common.base.Strings;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

abstract class Checkpoint {
    @Nullable
    private final String platform;
    private final Location location;


    Checkpoint(@Nullable String platform, Location location) {
        this.platform = Strings.emptyToNull(platform);
        this.location = requireNonNull(location, "location");
    }

    Checkpoint(Parcel in) {
        platform = in.readString();
        location = ParcelUtils.readParcelable(in, Location.CREATOR);
    }

    public Location getLocation() {
        return location;
    }

    @NonNull
    abstract public Date getDisplayDate();

    @Nullable
    public String getPlatform() {
        return platform;
    }

    @CallSuper
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(platform);
        ParcelUtils.writeParcelable(dest, location, flags);
    }
}
