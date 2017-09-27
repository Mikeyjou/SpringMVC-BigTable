/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lavidatec.template.entity;

import java.io.IOException;
import javax.persistence.AttributeConverter;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Suhmian
 */
public class JpaConverterJson implements AttributeConverter<Object, String> {

  private final static ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Object meta) {
    try {
        System.out.println(meta);
        System.out.println("Convert 2 object" + objectMapper.writeValueAsString(meta));
      return objectMapper.writeValueAsString(meta);
    } catch (JsonProcessingException ex) {
      return null;
      // or throw an error
    } catch (IOException ex){
        return null;
    }
  }

  @Override
  public Object convertToEntityAttribute(String dbData) {
    try {
        System.out.println("Convert 2 json" + objectMapper.readValue(dbData, Object.class));
      return objectMapper.readValue(dbData, Object.class);
    } catch (IOException ex) {
      // logger.error("Unexpected IOEx decoding json from database: " + dbData);
      return null;
    }
  }

}