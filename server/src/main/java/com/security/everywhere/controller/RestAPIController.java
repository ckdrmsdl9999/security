package com.security.everywhere.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.everywhere.data.TempForecastAreaCode;
import com.security.everywhere.data.WeatherForecastAreaCode;
import com.security.everywhere.model.Festival;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.request.FestivalParam;
import com.security.everywhere.request.ObservatoryParam;
import com.security.everywhere.request.WeatherForecastParam;
import com.security.everywhere.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RestAPIController {

    private final FestivalRepository festivalRepository;
    private TempForecastAreaCode tempForecastAreaCode;
    private WeatherForecastAreaCode weatherForecastAreaCode;

    @Value("${festival_key}")
    private String festivalKey;

    public RestAPIController(FestivalRepository festivalRepository, TempForecastAreaCode tempForecastAreaCode, WeatherForecastAreaCode weatherForecastAreaCode) {
        this.festivalRepository = festivalRepository;
        this.tempForecastAreaCode = tempForecastAreaCode;
        this.weatherForecastAreaCode = weatherForecastAreaCode;
    }

    @PostMapping("/festivalInfo")
    public List<Festival> festivalInfo(@RequestBody FestivalParam requestParam) {
        int pageNo = Integer.parseInt(requestParam.getPageNo());
        int numOfRows = Integer.parseInt(requestParam.getNumOfRows());
        String eventStartDate = requestParam.getEventStartDate();

        Pageable pageElements = PageRequest.of(pageNo, numOfRows, Sort.by("eventStartDate"));

        List<Festival> festivals = festivalRepository.findAllByEventStartDateGreaterThanEqual(eventStartDate, pageElements);

        return festivals;
    }

    @PostMapping("/airInfo")
    public AirItem observatoryInfo(@RequestBody ObservatoryParam requestParam) throws IOException {

        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapx(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapy(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        URL url = new URL(urlBuilder.toString());

        RestTemplate restTemplate = new RestTemplate();
        ObservatoryDTO observatoryDTO = null;

        try {
            observatoryDTO = restTemplate.getForObject(url.toURI(), ObservatoryDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
        urlBuilder.append("?" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(observatoryDTO.getBody().getItems().get(0).getStationName(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8"));
        url = new URL(urlBuilder.toString());

        AirDTO airDTO = null;

        try {
            airDTO = restTemplate.getForObject(url.toURI(), AirDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return airDTO.getBody().getItems().get(0);
    }

    @PostMapping("/weatherForecast")
    public WeatherForecastItem weatherInfo(@RequestBody WeatherForecastParam weatherForecastParam) throws ParseException, IOException {
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);

        String now = format.format(calendar.getTime());     // 현재 날짜
        long currentMillis = calendar.getTimeInMillis();    // 현재 시간을 초로
        Date standardDate = currentTimeFormat.parse(now+"0600");    // api가 아침 6시를 기준으로 데이터가 갱신되므로
        Long standardMillis = standardDate.getTime();       // 기준 시간을 초로

        String currentTime;
        if (standardMillis > currentMillis) {
            calendar.add(Calendar.DATE, -1);
        }
        currentTime = format.format(calendar.getTime()) + "0600";

        String addr = weatherForecastParam.getAddr();
        String regId = null;
        Map<String, String> weatherAreaCode = weatherForecastAreaCode.getAreaList();

        for (var key : weatherAreaCode.entrySet()) {
            if (addr.contains(key.getKey().toString())) {
                regId = weatherAreaCode.get(key.getKey()).toString();
                System.out.println(key.getKey().toString());
                System.out.println(regId);
                System.out.println(currentTime);
                break;
            }
        }

        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        URL url = new URL(urlBuilder.toString());

        ObjectMapper mapper = new ObjectMapper();
        WeatherForecastResponse weatherForecastResponse = mapper.readValue(url, WeatherForecastResponse.class);


//        return "성공";
        return weatherForecastResponse.getResponse().getBody().getItems().getItem();
    }

}