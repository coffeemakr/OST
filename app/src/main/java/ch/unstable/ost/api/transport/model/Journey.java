package ch.unstable.ost.api.transport.model;


import android.os.Parcel;
import android.os.Parcelable;

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

    Journey(String name, String category, String categoryCode, int number, String operator, String to, Capacity capacity) {
        this(name, category, categoryCode, "" + number, operator, to, capacity);
    }

    Journey(String name, String category, String categoryCode, String number, String operator, String to, Capacity capacity) {
        this.name = name;
        this.category = category;
        this.categoryCode = categoryCode;
        this.number = number;
        this.operator = operator;
        this.to = to;
        this.capacity = capacity;
    }

    protected Journey(Parcel in) {
        name = in.readString();
        category = in.readString();
        categoryCode = in.readString();
        number = in.readString();
        operator = in.readString();
        to = in.readString();
        capacity = ParcelUtils.readNullableParcelable(in, Capacity.CREATOR);
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

    private static boolean equals(String first, String second) {
        return first != null ? !first.equals(second) : second != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journey journey = (Journey) o;

        if (!equals(number, journey.number)) return false;
        if (!equals(name, journey.name)) return false;
        if (!equals(category, journey.category)) return false;
        if (categoryCode != null ? !categoryCode.equals(journey.categoryCode) : journey.categoryCode != null)
            return false;
        if (operator != null ? !operator.equals(journey.operator) : journey.operator != null)
            return false;
        if (to != null ? !to.equals(journey.to) : journey.to != null) return false;
        return capacity != null ? capacity.equals(journey.capacity) : journey.capacity == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (categoryCode != null ? categoryCode.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
        return result;
    }
}
