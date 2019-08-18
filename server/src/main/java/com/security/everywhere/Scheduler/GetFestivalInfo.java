package com.security.everywhere.Scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.security.everywhere.model.Festival;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.response.festival.FestivalItem;
import com.security.everywhere.response.festival.FestivalResponse;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetFestivalInfo {

    private final FestivalRepository festivalRepository;
    private String festivalKey;

    public GetFestivalInfo(FestivalRepository festivalRepository, String festivalKey) {
        this.festivalRepository = festivalRepository;
        this.festivalKey = festivalKey;
    }

    public void run() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival"); /*URL*/
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(festivalKey); /*공공데이터포털에서 발급받은 인증키*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1811", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1", StandardCharsets.UTF_8)); /*현재 페이지 번호*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("MobileOS", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("ETC", StandardCharsets.UTF_8)); /*IOS (아이폰), AND (안드로이드), WIN (원도우폰),ETC*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("MobileApp", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("AppTest", StandardCharsets.UTF_8)); /*서비스명=어플명*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("arrange", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("A", StandardCharsets.UTF_8)); /*(A=제목순, B=조회순, C=수정순, D=생성일순) 대표이미지가 반드시 있는 정렬 (O=제목순, P=조회순, Q=수정일순, R=생성일순)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("listYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8)); /*목록 구분(Y=목록, N=개수)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("areaCode", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("", StandardCharsets.UTF_8)); /*지역코드*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("sigunguCode", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("", StandardCharsets.UTF_8)); /*시군구코드(areaCode 필수)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("eventStartDate", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("20170901", StandardCharsets.UTF_8)); /*행사 시작일(형식:YYYYMMDD)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("eventEndDate", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("", StandardCharsets.UTF_8)); /*행사 종료일(형식:YYYYMMDD)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*행사 종료일(형식:YYYYMMDD)*/
        URL url = new URL(urlBuilder.toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        FestivalResponse responseResult = mapper.readValue(url, FestivalResponse.class);

        List<FestivalItem> festivals = responseResult.getResponse().getBody().getItems().getItem();

        for (FestivalItem item: festivals) {
            festivalRepository.save(new Festival(item));
        }
    }
}
