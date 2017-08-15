package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class Checkpoint {
    @Nullable
    private final String platform;
    private final Location location;


    Checkpoint(@Nullable String platform, Location location) {
        this.platform = Strings.emptyToNull(platform);
        this.location = checkNotNull(location, "location");
    }

    Checkpoint(Parcel in) {
        platform = in.readString();
        location = ParcelUtils.readParcelable(in, Location.CREATOR);
    }

    public Location getLocation() {
        return location;
    }

    @NonNull
    public abstract Date getDisplayDate();

    @Nullable
    public String getPlatform() {
        return platform;
    }

    @CallSuper
    protected void writeToParcel(Parcel dest, int flags) {
        dest.writeString(platform);
        ParcelUtils.writeParcelable(dest, location, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Checkpoint)) return false;
        Checkpoint that = (Checkpoint) o;
        return Objects.equal(platform, that.platform) &&
                Objects.equal(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(platform, location);
    }
}
