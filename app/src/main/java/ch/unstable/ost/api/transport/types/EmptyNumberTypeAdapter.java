package ch.unstable.ost.api.transport.types;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Type adapter to allow empty strings for numbers
 */
public class EmptyNumberTypeAdapter extends TypeAdapter<Number> {

    @Override
    public void write(JsonWriter out, Number value) throws IOException {
        out.value(value);
    }

    @Override
    public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.STRING) {
            String value = in.nextString();
            if (value.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
        try {
            return in.nextInt();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }
}
