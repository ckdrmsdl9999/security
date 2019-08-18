package com.security.everywhere.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.everywhere.configuration.GlobalPropertySource;
import com.security.everywhere.data.TempForecastAreaCode;
import com.security.everywhere.data.WeatherForecastAreaCode;
import com.security.everywhere.model.Festival;
import com.security.everywhere.model.Weather;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.request.FestivalParam;
import com.security.everywhere.request.ObservatoryParam;
import com.security.everywhere.request.WeatherForecastParam;
import com.security.everywhere.response.air.AirDTO;
import com.security.everywhere.response.air.AirItem;
import com.security.everywhere.response.locationConversion.LocationConvAuthDTO;
import com.security.everywhere.response.locationConversion.LocationConvDTO;
import com.security.everywhere.response.observatory.ObservatoryDTO;
import com.security.everywhere.response.middleLandWeather.MiddleLandWeatherItem;
import com.security.everywhere.response.middleLandWeather.MiddleLandWeatherResponse;
import com.security.everywhere.response.weatherTemperature.WeatherTempItem;
import com.security.everywhere.response.weatherTemperature.WeatherTempResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
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
    private final TempForecastAreaCode tempForecastAreaCode;
    private final WeatherForecastAreaCode weatherForecastAreaCode;
    private static Logger logger = LoggerFactory.getLogger(GlobalPropertySource.class);

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${festival_key}")
    private String festivalKey;
    @Value("${consumer_key}")
    private String consumerKey;
    @Value("${consumer_secret}")
    private String consumerSecret;

    public RestAPIController(FestivalRepository festivalRepository, TempForecastAreaCode tempForecastAreaCode, WeatherForecastAreaCode weatherForecastAreaCode) {
        this.festivalRepository = festivalRepository;
        this.tempForecastAreaCode = tempForecastAreaCode;
        this.weatherForecastAreaCode = weatherForecastAreaCode;
        this.mapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }
    @PostMapping("/festivalSearch")
    @ResponseBody
    public List<Festival> festivalSearch(@RequestBody FestivalParam requestParam) {//requestParam이 어떤건지 명확해야 main json값인지아닌지
        String title=requestParam.getTitle();
        System.out.println(requestParam.getTitle()+"은 제목이야");
        List<Festival> festivals = festivalRepository.findByTitleContaining(title);//jpa쿼리
        return festivals;
    }


    @PostMapping("/festivalInfofromTitle")
    public List<Festival> festivalInfofromTitle(@RequestBody FestivalParam requestParam) {
        List<Festival> festivals = festivalRepository.findAllByTitleIsLike("%"+requestParam.getTitle()+"%");

        return festivals;
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
        StringBuilder urlBuilder = new StringBuilder("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json");
        urlBuilder.append("?" + URLEncoder.encode("consumer_key","UTF-8") + "=" + URLEncoder.encode(consumerKey, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("consumer_secret","UTF-8") + "=" + URLEncoder.encode(consumerSecret, "UTF-8"));
        URL url = new URL(urlBuilder.toString());

        LocationConvAuthDTO locationConvAuthDTO = mapper.readValue(url, LocationConvAuthDTO.class);

        // WGS84 경/위도를 TM좌표 중부원점(GRS80)으로 변환
        urlBuilder = new StringBuilder("https://sgisapi.kostat.go.kr/OpenAPI3/transformation/transcoord.json");
        urlBuilder.append("?" + URLEncoder.encode("accessToken","UTF-8") + "=" + URLEncoder.encode(locationConvAuthDTO.getResult().getAccessToken(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("src","UTF-8") + "=" + URLEncoder.encode("4326", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dst","UTF-8") + "=" + URLEncoder.encode("5181", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("posX","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapx(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("posY","UTF-8") + "=" + URLEncoder.encode(requestParam.getMapy(), "UTF-8"));
        url = new URL(urlBuilder.toString());

        LocationConvDTO locationConvDTO = mapper.readValue(url, LocationConvDTO.class);

        // 좌표기준 근접 측정소 정보 가져오기
        urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(locationConvDTO.getResult().getPosX(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(locationConvDTO.getResult().getPosY(), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" +festivalKey);
        url = new URL(urlBuilder.toString());

        ObservatoryDTO observatoryDTO = null;

        try {
            observatoryDTO = restTemplate.getForObject(url.toURI(), ObservatoryDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // 측정소 이름으로 대기정보 가져오기
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
    public WeatherTempItem weatherInfo(@RequestBody WeatherForecastParam weatherForecastParam) throws ParseException, IOException {
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
                break;
            }
        }

        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        URL url = new URL(urlBuilder.toString());

        MiddleLandWeatherResponse middleLandWeatherResponse = mapper.readValue(url, MiddleLandWeatherResponse.class);

        Map<String, String> tempAreaCode = tempForecastAreaCode.getAreaList();

        for (var key : tempAreaCode.entrySet()) {
            if (addr.contains(key.getKey().toString())) {
                regId = tempAreaCode.get(key.getKey()).toString();
                break;
            }
        }

        urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        url = new URL(urlBuilder.toString());

        WeatherTempResponse weatherTempResponse = mapper.readValue(url, WeatherTempResponse.class);


        return weatherTempResponse.getResponse().getBody().getItems().getItem();
    }

    @PostMapping("/festivalContent")
    public Festival festivalContent(@RequestBody String contentid) {
        return festivalRepository.findByContentId(contentid);
    }

}
