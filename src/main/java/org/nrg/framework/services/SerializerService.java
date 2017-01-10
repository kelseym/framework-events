/*
 * framework: org.nrg.framework.services.SerializerService
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Service
public class SerializerService {
    public static final TypeReference<ArrayList<String>>                  TYPE_REF_LIST_STRING            = new TypeReference<ArrayList<String>>() {};
    public static final TypeReference<HashMap<String, ArrayList<String>>> TYPE_REF_MAP_STRING_LIST_STRING = new TypeReference<HashMap<String, ArrayList<String>>>() {};
    public static final TypeReference<HashMap<String, Double>>            TYPE_REF_MAP_STRING_DOUBLE      = new TypeReference<HashMap<String, Double>>() {};
    public static final TypeReference<HashMap<String, String>>            TYPE_REF_MAP_STRING_STRING      = new TypeReference<HashMap<String, String>>() {};

    @Autowired
    public SerializerService(final Jackson2ObjectMapperBuilder builder) {
        _builder = builder;
    }

    public JsonNode deserializeJson(final String json) throws IOException {
        return getObjectMapper().readTree(json);
    }

    public <T> T deserializeJson(final String json, final Class<T> clazz) throws IOException {
        return getObjectMapper().readValue(json, clazz);
    }

    public <T> T deserializeJson(final String json, final TypeReference<T> typeRef) throws IOException {
        return getObjectMapper().readValue(json, typeRef);
    }

    public <T> T deserializeJson(final String json, final JavaType type) throws IOException {
        return getObjectMapper().readValue(json, type);
    }

    public JsonNode deserializeJson(final InputStream input) throws IOException {
        return getObjectMapper().readTree(input);
    }

    public Map<String, String> deserializeJsonToMapOfStrings(final String json) throws IOException {
        return getObjectMapper().readValue(json, TYPE_REF_MAP_STRING_STRING);
    }

    public <T> String toJson(final T instance) throws IOException {
        return getObjectMapper().writeValueAsString(instance);
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

    public <T> T deserializeYaml(final InputStream input, final TypeReference<T> typeRef) throws IOException {
        return _yamlObjectMapper.readValue(input, typeRef);
    }

    public <T> String toYaml(final T instance) throws IOException {
        return _yamlObjectMapper.writeValueAsString(instance);
    }

    public TypeFactory getTypeFactory() {
        return getObjectMapper().getTypeFactory();
    }

    private ObjectMapper getObjectMapper() {
        return _objectMapper == null ? _objectMapper = _builder.build() : _objectMapper;
    }

    private final Jackson2ObjectMapperBuilder _builder;

    private ObjectMapper _objectMapper;
    private YamlObjectMapper _yamlObjectMapper = new YamlObjectMapper();
}
