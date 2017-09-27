/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lavidatec.template.entity;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author Mikey
 */

@Data
public class UserJson {
    private Map<String, String> nickname = new HashMap<>();
    private Map<String, Boolean> concern = new HashMap<>();
}
