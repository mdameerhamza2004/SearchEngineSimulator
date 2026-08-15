package com.searchengine.simulator.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Converter
public class JsonListConverter implements AttributeConverter<List<Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Object> attribute) {
        try {
            return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List to JSON", e);
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, new TypeReference<>() {}) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading List from JSON", e);
        }
    }
}
