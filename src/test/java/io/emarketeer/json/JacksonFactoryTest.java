package io.emarketeer.json;

import io.emarketeer.json.jackson.JacksonAdapter;
import io.emarketeer.json.rules.DependenciesInjectionRunner;
import io.emarketeer.json.rules.LibraryPathSets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DependenciesInjectionRunner.class)
@LibraryPathSets("jackson-deps")
public class JacksonFactoryTest {

    private final Map<String, String> map = new LinkedHashMap<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testToJson() {
        final String result = JsonAdapterFactory.get().toJson(map);
        assertThat(result).isEqualTo("{}");
        assertThat(JsonAdapterFactory.__resolved.get()).isNotNull();
        assertThat(JsonAdapterFactory.__resolved.get()).isInstanceOf(JacksonAdapter.class);
    }

    @Test
    public void testFromJson() {
        final Map<String, String> result = JsonAdapterFactory.get().fromJson("{\"a\":\"1\"}", map.getClass());
        assertThat(result).isNotEmpty().extracting("a").contains("1");
        assertThat(JsonAdapterFactory.__resolved.get()).isNotNull();
        assertThat(JsonAdapterFactory.__resolved.get()).isInstanceOf(JacksonAdapter.class);
    }

    @Test
    public void testFromJsonToList() {
        final List<Map<String, String>> result = JsonAdapterFactory.get().fromJsonToList("[{\"a\":\"1\"}]");
        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0)).isNotEmpty().extracting("a").contains("1");
        assertThat(JsonAdapterFactory.__resolved.get()).isNotNull();
        assertThat(JsonAdapterFactory.__resolved.get()).isInstanceOf(JacksonAdapter.class);
    }
}
