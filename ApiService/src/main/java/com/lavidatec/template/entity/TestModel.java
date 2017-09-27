/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lavidatec.template.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;

/**
 *
 * @author Mikey
 */

@Data
@Entity
@Table(name = "Unit_Test")
public class TestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @GeneratedValue(generator = "nativeGenerator")
//  @GenericGenerator(name = "nativeGenerator", strategy = "sequence")
    @Column(name = "p_key", unique = true, nullable = false,
            precision = 20, scale = 0)
    private Long pKey;
    
    
    @Column(name="size_in_cm")
    @ColumnTransformer(
        forColumn = "size_in_cm",
        read = "size_in_cm / 2.54E0",
        write = "? * 2.54E0" )
    private Long no;
}
