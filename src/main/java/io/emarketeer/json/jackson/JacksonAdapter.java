package io.emarketeer.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.emarketeer.json.JsonAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.emarketeer.json.JsonAdapterException;

import java.io.IOException;
import java.util.List;

public class JacksonAdapter implements JsonAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (final JsonProcessingException e) {
            throw new JsonAdapterException("Unable to writeValueAsString", e);
        }
    }

    @Override
    public <T> T fromJson(final String input, final Class<T> clazz) {
        try {
            return objectMapper.readValue(input, clazz);
        } catch (final IOException e) {
            throw new JsonAdapterException("Failed to readValue", e);
        }
    }

    @Override
    public <T> List<T> fromJsonToList(final String input) {
        try {
            return objectMapper.readValue(input, new TypeReference<List<T>>(){});
        } catch (IOException e) {
            throw new JsonAdapterException("Failed to readValue", e);
        }
    }
}
