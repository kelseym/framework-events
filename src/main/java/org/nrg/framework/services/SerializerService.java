package org.nrg.framework.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Service
public class SerializerService {
    public JsonNode deserializeJson(final String json) throws IOException {
        return _objectMapper.readTree(json);
    }

    public <T> T deserializeJson(final String json, final Class<T> clazz) throws IOException {
        return _objectMapper.readValue(json, clazz);
    }

    public <T> T deserializeJson(final String json, final TypeReference<T> typeRef) throws IOException {
        return _objectMapper.readValue(json, typeRef);
    }

    public <T> T deserializeJson(final String json, final JavaType type) throws IOException {
        return _objectMapper.readValue(json, type);
    }

    public JsonNode deserializeJson(final InputStream input) throws IOException {
        return _objectMapper.readTree(input);
    }

    public Map<String, String> deserializeJsonToMapOfStrings(final String json) throws IOException {
        return _objectMapper.readValue(json, MAP_STRING_STRING_TYPE_REFERENCE);
    }

    public <T> String toJson(final T instance) throws IOException {
        return _objectMapper.writeValueAsString(instance);
    }

    public JsonNode deserializeYaml(final String yaml) throws IOException {
        return _yamlObjectMapper.readTree(yaml);
    }

    public <T> T deserializeYaml(final String yaml, Class<T> clazz) throws IOException {
        return _yamlObjectMapper.readValue(yaml, clazz);
    }

    public <T> T deserializeYaml(final String yaml, final TypeReference<T> typeRef) throws IOException {
        return _yamlObjectMapper.readValue(yaml, typeRef);
    }

    public <T> T deserializeYaml(final String json, final JavaType type) throws IOException {
        return _yamlObjectMapper.readValue(json, type);
    }

    public JsonNode deserializeYaml(final InputStream input) throws IOException {
        return _yamlObjectMapper.readTree(input);
    }

    public <T> String toYaml(final T instance) throws IOException {
        return _yamlObjectMapper.writeValueAsString(instance);
    }

    public TypeFactory getTypeFactory() {
        return _objectMapper.getTypeFactory();
    }

    private final static TypeReference<HashMap<String, String>> MAP_STRING_STRING_TYPE_REFERENCE = new TypeReference<HashMap<String, String>>() {};

    @Inject
    private ObjectMapper _objectMapper;

    @Inject
    private YamlObjectMapper _yamlObjectMapper;
}
