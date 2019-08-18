package com.security.everywhere.Scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.security.everywhere.model.Festival;
import com.security.everywhere.model.FestivalImages;
import com.security.everywhere.repository.FestivalImagesRepository;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.response.festival.FestivalItem;
import com.security.everywhere.response.festival.FestivalResponse;
import com.security.everywhere.response.festivalCommonInformation.FestivalComInfoItem;
import com.security.everywhere.response.festivalCommonInformation.FestivalComInfoResponse;
import com.security.everywhere.response.festivalImages.FestivalImagesItem;
import com.security.everywhere.response.festivalImages.FestivalImagesResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetFestivalInfo {

    private final FestivalRepository festivalRepository;
    private final FestivalImagesRepository festivalImagesRepository;
    private String festivalKey;
    private StringBuilder urlBuilder;
    private URL url;
    private ObjectMapper mapper;

    public GetFestivalInfo(FestivalRepository festivalRepository, FestivalImagesRepository festivalImagesRepository, String festivalKey) {
        this.festivalRepository = festivalRepository;
        this.festivalImagesRepository = festivalImagesRepository;
        this.festivalKey = festivalKey;
        this.mapper = new ObjectMapper();
    }

    public void run() throws IOException {
        urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival"); /*URL*/
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
                .append(URLEncoder.encode("20190101", StandardCharsets.UTF_8)); /*행사 시작일(형식:YYYYMMDD)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("eventEndDate", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("", StandardCharsets.UTF_8)); /*행사 종료일(형식:YYYYMMDD)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8)); /*행사 종료일(형식:YYYYMMDD)*/
        url = new URL(urlBuilder.toString());

        FestivalResponse responseResult = mapper.readValue(url, FestivalResponse.class);

        List<FestivalItem> festivals = responseResult.getResponse().getBody().getItems().getItem();


        List<FestivalImagesItem> festivalImagesItems;
        FestivalComInfoItem festivalComInfoItem;
        for (FestivalItem item: festivals) {
            // 홈페이지와 개요 정보 가져옴
            festivalComInfoItem = getFestivalComInfo(item);

            // 축제 정보 DB에 저장
            festivalRepository.save(new Festival(item
                    , festivalComInfoItem.getHomepage()
                    , festivalComInfoItem.getOverview()));

            // 여러장의 이미지를 가져와 DB에 저장
            festivalImagesItems = getImages(item);
            for (FestivalImagesItem imagesItem: festivalImagesItems) {
                festivalImagesRepository.save(new FestivalImages(imagesItem));
            }
        }
    }

    private FestivalComInfoItem getFestivalComInfo(FestivalItem festivalItem) throws IOException {
        urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon"); /*URL*/
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(festivalKey); /*공공데이터포털에서 발급받은 인증키*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("10", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
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
                .append(URLEncoder.encode("contentId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(festivalItem.getContentid(), StandardCharsets.UTF_8));    // 콘텐츠 ID
        urlBuilder.append("&")
                .append(URLEncoder.encode("contentTypeId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("15", StandardCharsets.UTF_8));    // 관광타입(관광지, 숙박 등) ID
        urlBuilder.append("&")
                .append(URLEncoder.encode("defaultYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8));    // 기본정보 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("firstImageYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("N", StandardCharsets.UTF_8));    // 원본, 썸네일 대표이미지 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("areacodeYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("N", StandardCharsets.UTF_8));    // 지역코드, 시군구코드 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("catcodeYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("N", StandardCharsets.UTF_8));    // 대,중,소분류코드 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("addrinfoYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("N", StandardCharsets.UTF_8));    // 주소, 상세주소 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("mapinfoYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("N", StandardCharsets.UTF_8));    // 좌표 X,Y 조회여부
        urlBuilder.append("&")
                .append(URLEncoder.encode("overviewYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8));    // 콘텐츠 개요 조회여부
        url = new URL(urlBuilder.toString());

        FestivalComInfoResponse responseResult = mapper.readValue(url, FestivalComInfoResponse.class);

        return responseResult.getResponse().getBody().getItems().getItem();
    }

    private List<FestivalImagesItem> getImages(FestivalItem festivalItem) throws IOException {
        urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage"); /*URL*/
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(festivalKey); /*공공데이터포털에서 발급받은 인증키*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("10", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
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
                .append(URLEncoder.encode("contentId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(festivalItem.getContentid(), StandardCharsets.UTF_8));    // 콘텐츠 ID
        urlBuilder.append("&")
                .append(URLEncoder.encode("imageYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8));    // Y=콘텐츠 이미지 조회, N='음식점'타입의 음식메뉴 이미지
        urlBuilder.append("&")
                .append(URLEncoder.encode("subImageYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8));    // Y=원본,썸네일 이미지 조회 N=Null
        url = new URL(urlBuilder.toString());

        FestivalImagesResponse responseResult = mapper.readValue(url, FestivalImagesResponse.class);

        return responseResult.getResponse().getBody().getItems().getItem();
    }
}
