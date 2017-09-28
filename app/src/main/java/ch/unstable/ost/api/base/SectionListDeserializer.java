package ch.unstable.ost.api.base;

import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ch.unstable.ost.api.model.Section;


public interface SectionListDeserializer extends JsonDeserializer<List<Section>> {
    Type type = TypeToken.getParameterized(List.class, Section.class).getType();
}
