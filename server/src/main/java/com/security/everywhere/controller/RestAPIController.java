package com.security.everywhere.controller;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.everywhere.configuration.GlobalPropertySource;
import com.security.everywhere.data.TempForecastAreaCode;
import com.security.everywhere.data.WeatherForecastAreaCode;
import com.security.everywhere.model.Festival;
import com.security.everywhere.model.TourImages;
import com.security.everywhere.model.Weather;
import com.security.everywhere.repository.TourImagesRepository;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.request.*;
import com.security.everywhere.response.air.AirDTO;
import com.security.everywhere.response.air.AirItem;
import com.security.everywhere.response.locationConversion.LocationConvAuthDTO;
import com.security.everywhere.response.locationConversion.LocationConvDTO;
import com.security.everywhere.response.observatory.ObservatoryDTO;
import com.security.everywhere.response.tourBasicInformation.TourItem;
import com.security.everywhere.response.tourBasicInformation.TourResponse;
import com.security.everywhere.response.tourDetailIntro.DetailIntroResponse;
import com.security.everywhere.response.tourDetailIntro.DetailIntroitem;
import com.security.everywhere.response.weatherMiddleTerm.MiddleTermWeatherResponse;
import com.security.everywhere.response.weatherShortTerm.ShortTermWeatherItem;
import com.security.everywhere.response.weatherShortTerm.ShortTermWeatherResponse;
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
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RestAPIController {

    private final FestivalRepository festivalRepository;
    private final TourImagesRepository tourImagesRepository;
    private final TempForecastAreaCode tempForecastAreaCode;
    private final WeatherForecastAreaCode weatherForecastAreaCode;
    private static Logger logger = LoggerFactory.getLogger(GlobalPropertySource.class);

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${api_service_key}")
    private String apiServiceKey;
    @Value("${consumer_key}")
    private String consumerKey;
    @Value("${consumer_secret}")
    private String consumerSecret;

    public RestAPIController(FestivalRepository festivalRepository, TempForecastAreaCode tempForecastAreaCode, WeatherForecastAreaCode weatherForecastAreaCode, TourImagesRepository tourImagesRepository) {
        this.festivalRepository = festivalRepository;
        this.tempForecastAreaCode = tempForecastAreaCode;
        this.weatherForecastAreaCode = weatherForecastAreaCode;
        this.tourImagesRepository = tourImagesRepository;
        this.mapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();

        // 모르는 property에 대해 무시하고 넘어간다. DTO의 하위 호환성 보장에 필요하다
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // ENUM 값이 존재하지 않으면 null로 설정한다. Enum 항목이 추가되어도 무시하고 넘어가게 할 때 필요하다.
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }


    @PostMapping("/festivalSearch")
    @ResponseBody
    public List<Festival> festivalSearch(@RequestBody FestivalParam requestParam) {//requestParam-ajax통해서온값
        String title = requestParam.getTitle();

        return festivalRepository.findByTitleContaining(title);
    }


    @PostMapping("/festivalInfo")
    public List<Festival> festivalInfo(@RequestBody FestivalParam requestParam) {
        int pageNo = Integer.parseInt(requestParam.getPageNo());
        int numOfRows = Integer.parseInt(requestParam.getNumOfRows());
        String eventStartDate = requestParam.getEventStartDate();

        Pageable pageElements = PageRequest.of(pageNo, numOfRows, Sort.by("eventStartDate"));

        List<Festival> festivals = new ArrayList<>();

        if("con".equals(requestParam.getCategory())) {
            festivals = festivalRepository.findAllByEventStartDateGreaterThanEqualAndEventEndDateLessThanEqualAndAddr1Containing
                    (eventStartDate, requestParam.getEventEndDate(), pageElements,requestParam.getAddress());
        }
        else if("search".equals(requestParam.getCategory())){
            festivals = festivalRepository.findByTitleContaining(requestParam.getTitle());//jpa쿼리
        }
        else if("main".equals(requestParam.getCategory())) {
            festivals = festivalRepository.findAllByEventStartDateGreaterThanEqual(eventStartDate, pageElements);
        }
        return festivals;

    }


    @PostMapping("/festivalContent")
    public Festival festivalContent(@RequestBody String contentid) {
        Festival festival;
        festival = festivalRepository.findByContentId(contentid);

        String result = festival.getOverview().replaceAll("<br>","");
        result = result.replaceAll("<br />","");
        festival.setOverview(result);

        return festival;
    }

    // 관광지의 이미지 가져오기
    @PostMapping("/festivalImages")
    public List<TourImages> festivalImages(@RequestBody String contentid) {
        return tourImagesRepository.findByContentid(contentid);
    }


    @PostMapping("/nearbyTour")
    public List<TourItem> nearbyTour(@RequestBody NearbyTourParam nearbyTourParam) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList"); /*URL*/
        urlBuilder.append("?")
                .append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey); /*공공데이터포털에서 발급받은 인증키*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nearbyTourParam.getNumOfRows(), StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nearbyTourParam.getPageNo(), StandardCharsets.UTF_8)); /*현재 페이지 번호*/
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
                .append(URLEncoder.encode(nearbyTourParam.getArrange(), StandardCharsets.UTF_8)); /*(A=제목순, B=조회순, C=수정순, D=생성일순) 대표이미지가 반드시 있는 정렬 (O=제목순, P=조회순, Q=수정일순, R=생성일순)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("contentTypeId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("12", StandardCharsets.UTF_8));       // 관광타입(관광지, 숙박 등) ID 관광지:12, 축제:15
        urlBuilder.append("&")
                .append(URLEncoder.encode("mapX", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nearbyTourParam.getMapX(), StandardCharsets.UTF_8));  // GPS X좌표(WGS84 경도 좌표)
        urlBuilder.append("&")
                .append(URLEncoder.encode("mapY", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nearbyTourParam.getMapY(), StandardCharsets.UTF_8));  // GPS Y좌표(WGS84 위도 좌표)
        urlBuilder.append("&")
                .append(URLEncoder.encode("radius", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nearbyTourParam.getRadius(), StandardCharsets.UTF_8));    // 거리 반경(단위m), Max값 20000m=20Km
        urlBuilder.append("&")
                .append(URLEncoder.encode("listYN", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("Y", StandardCharsets.UTF_8)); /*목록 구분(Y=목록, N=개수)*/
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        URL url = new URL(urlBuilder.toString());

        TourResponse tourResponse = mapper.readValue(url, TourResponse.class);

        return tourResponse.getResponse().getBody().getItems().getItem();
    }




    // 축제, 관광지의 상세정보
    @PostMapping("/detailIntro/tour")
    public DetailIntroitem tourDetailIntro(@RequestBody TourDetailIntroParam detailIntroParam) throws IOException {
        System.out.println(detailIntroParam.toString());
        StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro");
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(detailIntroParam.getNumOfRows(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(detailIntroParam.getPageNo(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("MobileOS", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("ETC", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("MobileApp", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("AppTest", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("contentId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(detailIntroParam.getContentId(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("contentTypeId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(detailIntroParam.getContentTypeId(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        URL url = new URL(urlBuilder.toString());

        DetailIntroResponse detailIntroResponse = mapper.readValue(url, DetailIntroResponse.class);

        return detailIntroResponse.getResponse().getBody().getItems().getItem();
    }


    // 대기정보
    @PostMapping("/airInfo")
    public AirItem observatoryInfo(@RequestBody ObservatoryParam requestParam) throws IOException {
        // 좌표 변환해주는 api 사용하기 전에 키를 받아야함
        StringBuilder urlBuilder = new StringBuilder("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json");
        urlBuilder.append("?")
                .append(URLEncoder.encode("consumer_key", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(consumerKey, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("consumer_secret", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(consumerSecret, StandardCharsets.UTF_8));
        URL url = new URL(urlBuilder.toString());

        LocationConvAuthDTO locationConvAuthDTO = mapper.readValue(url, LocationConvAuthDTO.class);

        // WGS84 경/위도를 TM좌표 중부원점(GRS80)으로 변환
        urlBuilder = new StringBuilder("https://sgisapi.kostat.go.kr/OpenAPI3/transformation/transcoord.json");
        urlBuilder.append("?")
                .append(URLEncoder.encode("accessToken", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(locationConvAuthDTO.getResult().getAccessToken(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("src", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("4326", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("dst", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("5181", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("posX", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(requestParam.getMapx(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("posY", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(requestParam.getMapy(), StandardCharsets.UTF_8));
        url = new URL(urlBuilder.toString());

        LocationConvDTO locationConvDTO = mapper.readValue(url, LocationConvDTO.class);

        // 좌표기준 근접 측정소 정보 가져오기
        urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?")
                .append(URLEncoder.encode("tmX", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(locationConvDTO.getResult().getPosX(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("tmY", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(locationConvDTO.getResult().getPosY(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        url = new URL(urlBuilder.toString());

        ObservatoryDTO observatoryDTO = null;

        try {
            observatoryDTO = restTemplate.getForObject(url.toURI(), ObservatoryDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // 측정소 이름으로 대기정보 가져오기
        urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
        urlBuilder.append("?")
                .append(URLEncoder.encode("stationName", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(observatoryDTO.getBody().getItems().get(0).getStationName(), StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("dataTerm", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("DAILY", StandardCharsets.UTF_8));
        urlBuilder.append("&").append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("10", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        urlBuilder.append("&")
                .append(URLEncoder.encode("ver", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1.3", StandardCharsets.UTF_8));
        url = new URL(urlBuilder.toString());

        AirDTO airDTO = null;

        try {
            airDTO = restTemplate.getForObject(url.toURI(), AirDTO.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return airDTO.getBody().getItems().get(0);
    }


    @PostMapping("/weatherInfo")
    public List<Weather> weatherInfo(@RequestBody WeatherForecastParam weatherForecastParam) throws ParseException, IOException {
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);

        String today = format.format(calendar.getTime());     // 현재 날짜
        long currentMillis = calendar.getTimeInMillis();    // 현재 시간을 초로
        Date standardDate = currentTimeFormat.parse(today+"0600");    // api가 아침 6시를 기준으로 데이터가 갱신되므로
        long standardMillis = standardDate.getTime();       // 기준 시간을 초로

        // 새벽 6시 이전이면 하루 전 데이터 가져옴
        String currentTime;
        if (standardMillis > currentMillis) {
            calendar.add(Calendar.DATE, -1);
        }
        currentTime = format.format(calendar.getTime()) + "0600";

        String addr = weatherForecastParam.getAddr();
        String regId = null;
        Map<String, String> weatherAreaCode = weatherForecastAreaCode.getAreaList();

        for (var key : weatherAreaCode.entrySet()) {
            if (addr.contains(key.getKey())) {
                regId = weatherAreaCode.get(key.getKey());
                break;
            }
        }

        // 중기예보정보 3~10일의 데이터가 들어있음
        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather");
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        urlBuilder.append("&")
                .append(URLEncoder.encode("regId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(regId, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("tmFc", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(currentTime, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("10", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        URL url = new URL(urlBuilder.toString());

        MiddleTermWeatherResponse middleTermWeatherResponse = mapper.readValue(url, MiddleTermWeatherResponse.class);

        Map<String, String> tempAreaCode = tempForecastAreaCode.getAreaList();

        for (var key : tempAreaCode.entrySet()) {
            if (addr.contains(key.getKey())) {
                regId = tempAreaCode.get(key.getKey());
                break;
            }
        }

        // 3~7일 기온의 정보가 있음
        urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature");
        urlBuilder.append("?")
                .append(URLEncoder.encode("ServiceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        urlBuilder.append("&")
                .append(URLEncoder.encode("regId", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(regId, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("tmFc", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(currentTime, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("10", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        url = new URL(urlBuilder.toString());

        WeatherTempResponse weatherTempResponse = mapper.readValue(url, WeatherTempResponse.class);

        double x = Double.parseDouble(weatherForecastParam.getMapX());
        double y = Double.parseDouble(weatherForecastParam.getMapY());
        Map<String, Object> xy = getGridxy(y, x);   // 동네예보 전용 좌표로

        standardDate = currentTimeFormat.parse(today+"0210");
        standardMillis = standardDate.getTime();

        String base_date;
        String base_time = "0210";
        // 새벽 2시 10분 이전이면 하루 전 데이터 가져옴
        if (standardMillis > currentMillis) {
            calendar.add(Calendar.DATE, -1);
            base_time = "0800";
        }
        base_date = format.format(calendar.getTime());

        String nx = String.valueOf(xy.get("x"));
        String ny = String.valueOf(xy.get("y"));
        nx = nx.substring(0, nx.length()-2);    // 문자열에서 .0 제거
        ny = ny.substring(0, ny.length()-2);

        // 3일치 기후 정보 가져오기
        urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData");
        urlBuilder.append("?")
                .append(URLEncoder.encode("serviceKey", StandardCharsets.UTF_8))
                .append("=")
                .append(apiServiceKey);
        urlBuilder.append("&")
                .append(URLEncoder.encode("base_date", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(base_date, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("base_time", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(base_time, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("nx", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(nx, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("ny", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(ny, StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("numOfRows", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("255", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("pageNo", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("1", StandardCharsets.UTF_8));
        urlBuilder.append("&")
                .append(URLEncoder.encode("_type", StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        url = new URL(urlBuilder.toString());

        ShortTermWeatherResponse shortTermWeatherResponse = mapper.readValue(url, ShortTermWeatherResponse.class);

        List<ShortTermWeatherItem> shortTermWeatherItems = shortTermWeatherResponse.getResponse().getBody().getItems().getItem();

        List<Weather> weatherList = new ArrayList<>();

        // 다음날 날짜 가져옴
        calendar = new GregorianCalendar(Locale.KOREA);
        calendar.add(Calendar.DATE, 1);
        String tomorrow = format.format(calendar.getTime());

        // 위에서 다음날로 해놨기 때문에 하루 뒤로 하고 요일 정보 가져옴
        calendar.add(Calendar.DATE, -1);
        int dayOfWeekCode = calendar.get(Calendar.DAY_OF_WEEK);

        // 형식이 달라서 1, 2일만 따로 설정
        setDay12Info(shortTermWeatherItems, weatherList, today, dayOfWeekCode);
        setDay12Info(shortTermWeatherItems, weatherList, tomorrow, dayOfWeekCode+1);

        Weather weatherInfo;

        weatherInfo = new Weather();
        weatherInfo.setMinTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMin3() );
        weatherInfo.setMaxTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMax3() );
        weatherInfo.setState( middleTermWeatherResponse.getResponse().getBody().getItems().getItem().getWf3Pm() );
        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode+2) );
        weatherList.add(weatherInfo);
        weatherInfo = new Weather();
        weatherInfo.setMinTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMin4() );
        weatherInfo.setMaxTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMax4() );
        weatherInfo.setState( middleTermWeatherResponse.getResponse().getBody().getItems().getItem().getWf4Pm() );
        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode+3) );
        weatherList.add(weatherInfo);
        weatherInfo = new Weather();
        weatherInfo.setMinTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMin5() );
        weatherInfo.setMaxTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMax5() );
        weatherInfo.setState( middleTermWeatherResponse.getResponse().getBody().getItems().getItem().getWf5Pm() );
        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode+4) );
        weatherList.add(weatherInfo);
        weatherInfo = new Weather();
        weatherInfo.setMinTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMin6() );
        weatherInfo.setMaxTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMax6() );
        weatherInfo.setState( middleTermWeatherResponse.getResponse().getBody().getItems().getItem().getWf6Pm() );
        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode+5) );
        weatherList.add(weatherInfo);
        weatherInfo = new Weather();
        weatherInfo.setMinTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMin7() );
        weatherInfo.setMaxTemp( weatherTempResponse.getResponse().getBody().getItems().getItem().getTaMax7() );
        weatherInfo.setState( middleTermWeatherResponse.getResponse().getBody().getItems().getItem().getWf7Pm() );
        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode+6) );
        weatherList.add(weatherInfo);

        return weatherList;
    }


    // 요일 설정
    private String setDayOfWeek(int dayOfWeekCode) {
        String dayOfWeek = null;

        dayOfWeekCode = dayOfWeekCode % 8;

        if (dayOfWeekCode == 0)
            dayOfWeekCode = 1;

        switch (dayOfWeekCode) {
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }

        return dayOfWeek;
    }


    // 1,2일의 날씨 정보는 3일 이후의 데이터와 형식이 달라서 따로 설정
    private void setDay12Info(List<ShortTermWeatherItem> shortTermWeatherItems, List<Weather> weatherList, String day, int dayOfWeekCode) {
        Weather weatherInfo = new Weather();

        int skyStateCode;
        String skyState = null;
        for (var item: shortTermWeatherItems) {
            if (item.getFcstDate().equals(day)) {
                if (item.getCategory().equals("SKY")) {
                    skyStateCode = Integer.parseInt(item.getFcstValue());
                    if (skyStateCode == 1)
                        skyState = "맑음";
                    if (skyStateCode == 3)
                        skyState = "구름 많음";
                    if (skyStateCode == 4)
                        skyState = "흐림";
                    if (weatherInfo.getState().isEmpty())
                        weatherInfo.setState(skyState);
                }
                if (item.getCategory().equals("TMN")) {
                    weatherInfo.setMinTemp(item.getFcstValue());
                }
                if (item.getCategory().equals("TMX")) {
                    weatherInfo.setMaxTemp(item.getFcstValue());
                }
            }
        }

        weatherInfo.setDayOfTheWeek( setDayOfWeek(dayOfWeekCode) );

        weatherList.add(weatherInfo);
    }


    // 동네예보 좌표 구하는 메소드
    private Map<String, Object> getGridxy(double orgLat, double orgLon) {

        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        // double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        Map<String, Object> map = new HashMap<>();
        map.put("lat", orgLat);
        map.put("lng", orgLat);
        double ra = Math.tan(Math.PI * 0.25 + (orgLat) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = orgLon * DEGRAD - olon;
        if (theta > Math.PI)
            theta -= 2.0 * Math.PI;
        if (theta < -Math.PI)
            theta += 2.0 * Math.PI;
        theta *= sn;

        map.put("x", Math.floor(ra * Math.sin(theta) + XO + 0.5));
        map.put("y", Math.floor(ro - ra * Math.cos(theta) + YO + 0.5));

        return map;
    }

}
