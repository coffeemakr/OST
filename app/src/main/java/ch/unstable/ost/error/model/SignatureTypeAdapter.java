package ch.unstable.ost.error.model;

import android.content.pm.Signature;
import android.util.Base64;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SignatureTypeAdapter extends com.google.gson.TypeAdapter<Signature> {
    @Override
    public void write(JsonWriter out, Signature value) throws IOException {
        String signatureString = Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP);
        out.value(signatureString);
    }

    @Override
    public Signature read(JsonReader in) throws IOException {
        byte[] bytes = Base64.decode(in.nextString(), Base64.NO_WRAP);
        return new Signature(bytes);
    }
}
