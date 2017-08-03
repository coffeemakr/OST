package ch.unstable.ost.api.search.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {

    }

    @Override
    public Date read(JsonReader in) throws IOException {
        String value = in.nextString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT);

        try {
            simpleDateFormat.parse(value);
        } catch (ParseException e) {
            simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT);
        }
    }
}
