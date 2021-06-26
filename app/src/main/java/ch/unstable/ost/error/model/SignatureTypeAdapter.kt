package ch.unstable.ost.error.model

import android.content.pm.Signature
import android.util.Base64
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class SignatureTypeAdapter : TypeAdapter<Signature>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Signature) {
        val signatureString = Base64.encodeToString(value.toByteArray(), Base64.NO_WRAP)
        out.value(signatureString)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Signature {
        val bytes = Base64.decode(`in`.nextString(), Base64.NO_WRAP)
        return Signature(bytes)
    }
}