package ch.unstable.ost.test;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import ch.unstable.ost.api.model.ArrivalCheckpoint;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Location;
import ch.unstable.ost.api.model.PassingCheckpoint;
import ch.unstable.ost.api.model.Route;

public class TestHelper {
    public static <T extends Parcelable> T writeAndRead(T parcelable, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    @NonNull
    public static PassingCheckpoint[] generatePassingCheckpoints(int number) {
        PassingCheckpoint[] checkpoints = new PassingCheckpoint[number];
        for(int i = 0; i < number; ++i) {
            Date arrival = new Date(number);
            Date departure = new Date(number);
            Location location = generateLocation(number);
            checkpoints[i] = new PassingCheckpoint(arrival, departure, location, "" + number);
        }
        return checkpoints;
    }

    @NonNull
    public static Route generateRandomRoute(int number) {
        String shortName = "short " + number;
        String longName = "long" + number;
        PassingCheckpoint[] stops = generatePassingCheckpoints(25);
        return new Route(shortName, longName, stops);
    }

    @NonNull
    public static DepartureCheckpoint generateDepartureCheckpoint(int number) {
        Date departure = new Date(number);
        String platform = String.valueOf(number);
        Location location = generateLocation(number);
        return new DepartureCheckpoint(departure, platform, location);
    }

    @NonNull
    public static Location generateLocation(int number) {
        return new Location("" + number, Location.StationType.TRAIN, "" + number);
    }

    @NonNull
    public static ArrivalCheckpoint generateArrivalCheckpoint(int number) {
        Date arrival = new Date(number);
        String platform = String.valueOf(number);
        Location location = generateLocation(number);
        return new ArrivalCheckpoint(arrival, platform, location);
    }
}
