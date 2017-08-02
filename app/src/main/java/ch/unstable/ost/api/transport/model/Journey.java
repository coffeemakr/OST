package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

import ch.unstable.ost.utils.ParcelUtils;

public class Journey implements Parcelable {
    public static final Creator<Journey> CREATOR = new Creator<Journey>() {
        @Override
        public Journey createFromParcel(Parcel in) {
            return new Journey(in);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };
    private final String name;
    private final String category;
    private final String categoryCode;
    private final String number;
    private final String operator;
    private final String to;
    private final Capacity capacity;
    private final Checkpoint[] passList;

    Journey(String name, String category, String categoryCode, int number, String operator, String to, Capacity capacity, Checkpoint[] passList) {
        this(name, category, categoryCode, "" + number, operator, to, capacity, passList);
    }


    Journey(String name, String category, String categoryCode, String number, String operator, String to, Capacity capacity, Checkpoint[] passList) {
        this.name = name;
        this.category = category;
        this.categoryCode = categoryCode;
        this.number = number;
        this.operator = operator;
        this.to = to;
        this.capacity = capacity;
        this.passList = passList;
    }

    protected Journey(Parcel in) {
        name = in.readString();
        category = in.readString();
        categoryCode = in.readString();
        number = in.readString();
        operator = in.readString();
        to = in.readString();
        capacity = ParcelUtils.readNullableParcelable(in, Capacity.CREATOR);
        passList = in.createTypedArray(Checkpoint.CREATOR);
    }

    private static boolean equals(String first, String second) {
        return first != null ? !first.equals(second) : second != null;
    }

    public Checkpoint[] getPassList() {
        return passList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(categoryCode);
        dest.writeString(number);
        dest.writeString(operator);
        dest.writeString(to);
        ParcelUtils.writeNullableParcelable(dest, capacity, flags);
        dest.writeTypedArray(passList, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getNumber() {
        return number;
    }

    public String getOperator() {
        return operator;
    }

    public String getTo() {
        return to;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journey journey = (Journey) o;
        return Objects.equal(name, journey.name) &&
                Objects.equal(category, journey.category) &&
                Objects.equal(categoryCode, journey.categoryCode) &&
                Objects.equal(number, journey.number) &&
                Objects.equal(operator, journey.operator) &&
                Objects.equal(to, journey.to) &&
                Objects.equal(capacity, journey.capacity) &&
                Objects.equal(passList, journey.passList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, category, categoryCode, number, operator, to, capacity, passList);
    }
}
