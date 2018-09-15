package io.emarketeer.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.emarketeer.json.JsonAdapter;

import java.util.List;

public class GsonAdapter implements JsonAdapter {

    private Gson gson = new GsonBuilder()
            .create();

    @Override
    public String toJson(Object data) {
        return gson.toJson(data);
    }

    @Override
    public <T> T fromJson(final String input, final Class<T> clazz) {
        return gson.fromJson(input, clazz);
    }

    @Override
    public <T> List<T> fromJsonToList(final String input) {
        return gson.fromJson(input, new TypeToken<List<T>>(){}.getType());
    }

}
