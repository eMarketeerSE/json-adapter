package io.emarketeer.json;

import java.util.List;

public interface JsonAdapter {

    String toJson(final Object data);

    <T> T fromJson(final String input, final Class<T> clazz);

    <T> List<T> fromJsonToList(final String input);
}
