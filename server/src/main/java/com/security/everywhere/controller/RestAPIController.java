package com.security.everywhere.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.security.everywhere.request.AirParam;
import com.security.everywhere.request.FestivalParam;
import com.security.everywhere.request.ObservatoryParam;
import com.security.everywhere.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestAPIController {

    @Value("${festival_key}")
    private String festivalKey;

    @ResponseBody
    @PostMapping("/festivalInfo")
    public List<FestivalItem> festivalInfo(FestivalParam requestParam) throws IOException {

        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey); /*공공데이터포털에서 발급받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(requestParam.getNumOfRows(), "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(requestParam.getPageNo(), "UTF-8")); /*현재 페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("MobileOS","UTF-8") + "=" + URLEncoder.encode(requestParam.getMobileOS(), "UTF-8")); /*IOS (아이폰), AND (안드로이드), WIN (원도우폰),ETC*/
        urlBuilder.append("&" + URLEncoder.encode("MobileApp","UTF-8") + "=" + URLEncoder.encode(requestParam.getMobileApp(), "UTF-8")); /*서비스명=어플명*/
        urlBuilder.append("&" + URLEncoder.encode("arrange","UTF-8") + "=" + URLEncoder.encode(requestParam.getArrange(), "UTF-8")); /*(A=제목순, B=조회순, C=수정순, D=생성일순) 대표이미지가 반드시 있는 정렬 (O=제목순, P=조회순, Q=수정일순, R=생성일순)*/
        urlBuilder.append("&" + URLEncoder.encode("listYN","UTF-8") + "=" + URLEncoder.encode(requestParam.getListYN(), "UTF-8")); /*목록 구분(Y=목록, N=개수)*/
        urlBuilder.append("&" + URLEncoder.encode("areaCode","UTF-8") + "=" + URLEncoder.encode(requestParam.getAreaCode(), "UTF-8")); /*지역코드*/
        urlBuilder.append("&" + URLEncoder.encode("sigunguCode","UTF-8") + "=" + URLEncoder.encode(requestParam.getSigunguCode(), "UTF-8")); /*시군구코드(areaCode 필수)*/
        urlBuilder.append("&" + URLEncoder.encode("eventStartDate","UTF-8") + "=" + URLEncoder.encode(requestParam.getEventStartDate(), "UTF-8")); /*행사 시작일(형식:YYYYMMDD)*/
        urlBuilder.append("&" + URLEncoder.encode("eventEndDate","UTF-8") + "=" + URLEncoder.encode(requestParam.getEventEndDate(), "UTF-8")); /*행사 종료일(형식:YYYYMMDD)*/
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*행사 종료일(형식:YYYYMMDD)*/
        URL url = new URL(urlBuilder.toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        Festival responseResult = mapper.readValue(url, Festival.class);

//        RestTemplate restTemplate = new RestTemplate();
//        FestivalDTO responseResult = null;
//
//        try {
//            responseResult = restTemplate.getForObject(url.toURI(), FestivalDTO.class);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

        System.out.println("--------------------------------------------");
        System.out.println(responseResult.getResponse().getHeader().toString());
        System.out.println(responseResult.getResponse().getBody().toString());
        System.out.println("--------------------------------------------");

        return responseResult.getResponse().getBody().getItems().getItem();
    }

    @PostMapping("/observatoryInfo")
    public List<ObservatoryItem> observatoryInfo(ObservatoryParam requestParam) throws IOException {

        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapx(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapy(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        URL url = new URL(urlBuilder.toString());

        RestTemplate restTemplate = new RestTemplate();
        ObservatoryDTO responseResult = null;

        try {
            responseResult = restTemplate.getForObject(url.toURI(), ObservatoryDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println("--------------------------------------------");
        System.out.println(responseResult.getHeader().toString());
        System.out.println(responseResult.getBody().toString());
        System.out.println("--------------------------------------------");

        return responseResult.getBody().getItems();
    }

    @PostMapping("/airInfo")
    public List<AirItem> airInfo(AirParam requestParam) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
        urlBuilder.append("?" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(requestParam.getStationName(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode(requestParam.getDataTerm(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(requestParam.getPageNo(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(requestParam.getNumOfRows(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode(requestParam.getVer(), "UTF-8"));
        URL url = new URL(urlBuilder.toString());

        RestTemplate restTemplate = new RestTemplate();
        AirDTO responseResult = null;

        try {
            responseResult = restTemplate.getForObject(url.toURI(), AirDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println("--------------------------------------------");
        System.out.println(responseResult.getHeader().toString());
        System.out.println(responseResult.getBody().toString());
        System.out.println("--------------------------------------------");

        return responseResult.getBody().getItems();
    }
}
