package io.emarketeer.json;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Map;

public class JsonFactoryTest {

    private final Map<String, String> map = Collections.emptyMap();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSingleInvoke() {
        thrown.expect(JsonAdapterException.class);
        final String result = JsonAdapterFactory.get().toJson(map);
    }

}