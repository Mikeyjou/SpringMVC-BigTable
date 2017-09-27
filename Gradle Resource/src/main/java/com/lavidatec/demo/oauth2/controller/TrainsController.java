/*
 * UsersAction
 * Author : Mikey-2017/09/11
 * 
 */
package com.lavidatec.demo.oauth2.controller;

import com.lavidatec.template.entity.TrainsModel;
import com.lavidatec.template.service.ITrainService;
import com.lavidatec.template.service.TrainServiceImpl;
import com.lavidatec.template.vo.TrainsVo;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Arrays;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@EnableWebMvc
@RequestMapping("/Train")
public class TrainsController {
    
    private ITrainService trainService = new TrainServiceImpl();
  
    //新增火車資訊
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addTrain(@RequestParam("no") String no, 
                          @RequestParam("type") String type,
                          @RequestParam("date") String date,
                          @RequestParam("ticketsLimit") String ticketsLimit)
            throws Exception{
        if(StringUtils.isNotBlank(no) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(date) && StringUtils.isNotBlank(ticketsLimit)){
            TrainsVo trainsVo = new TrainsVo();
            trainsVo.setNo(no);
            if(trainService.trainFind(trainsVo).isPresent())
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"This train is duplicate",null), HttpStatus.BAD_REQUEST);
            TrainsModel trainModel = new TrainsModel();
            trainModel.setNo(no);
            trainModel.setDate(date);
            trainModel.setType(type);
            trainModel.setTicketsLimit(Integer.parseInt(ticketsLimit));
            trainService.trainPersist(Optional.of(trainModel));
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",null), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST,"Miss some parameters",null), HttpStatus.BAD_REQUEST);
        }
    }
    
    //依照條件搜尋火車資訊
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> searchTrain(Principal user,
                            @RequestParam(value = "no", required = false) String searchNo, 
                            @RequestParam(value = "type", required = false) String searchType,
                            @RequestParam(value = "date", required = false) String searchDate) 
            throws Exception{
        TrainsVo trainVo = new TrainsVo();
        if(StringUtils.isNotBlank(searchNo))
            trainVo.setNo(searchNo);
        if(StringUtils.isNotBlank(searchType))
            trainVo.setType(searchType);
        if(StringUtils.isNotBlank(searchDate))
            trainVo.setDate(searchDate);
        System.out.println(trainVo);
        Optional<List<TrainsModel>> trainFindList = trainService.trainFindList(trainVo);
        System.out.println(trainFindList.get());
        
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Success",new JSONArray(trainFindList.get())), HttpStatus.OK);
    }
}
