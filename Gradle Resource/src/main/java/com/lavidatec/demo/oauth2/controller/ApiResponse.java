/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lavidatec.demo.oauth2.controller;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.json.JSONObject;
import org.json.JSONArray;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
/**
 *
 * @author Mikey
 */
@Data
public class ApiResponse {
    
    public HttpStatus status;
    public String message;
    public JsonArray payload;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public LocalDateTime timestamp;
    public String debugMessage;
//    private List<ApiSubError> subErrors;

    private ApiResponse() {
        timestamp = LocalDateTime.now();
    }

    ApiResponse(HttpStatus status) {
        this();
        this.status = status;
    }

    ApiResponse(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }
    
    ApiResponse(HttpStatus status, String message, JsonArray payload) {
        this();
        this.status = status;
        this.message = message;
        if(payload != null)
            this.payload = payload;
        else
            this.payload = new JsonArray();
    }
}
