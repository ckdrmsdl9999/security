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
import com.security.everywhere.response.middleTermWeather.MiddleTermWeatherResponse;
import com.security.everywhere.response.shortTermWeather.ShortTermWeatherItem;
import com.security.everywhere.response.shortTermWeather.ShortTermWeatherResponse;
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
    public List<Festival> festivalSearch(@RequestBody FestivalParam requestParam) {//requestParam-ajax통해서온값
        String title=requestParam.getTitle();
       // System.out.println(requestParam.getTitle()+"은 제목이야");
        List<Festival> festivals = festivalRepository.findByTitleContaining(title);//jpa쿼리
        return festivals;
    }


    @PostMapping("/festivalInfofromTitle")
    public List<Festival> festivalInfoFromTitle(@RequestBody FestivalParam requestParam) {
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

    @PostMapping("/festivalContent")
    public Festival festivalContent(@RequestBody String contentid) {
        return festivalRepository.findByContentId(contentid);
    }

    @PostMapping("/airInfo")
    public AirItem observatoryInfo(@RequestBody ObservatoryParam requestParam) throws IOException {
        // 좌표 변환해주는 api 사용하기 전에 키를 받아야함
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
    public List<Weather> weatherInfo(@RequestBody WeatherForecastParam weatherForecastParam) throws ParseException, IOException {
        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA);

        String today = format.format(calendar.getTime());     // 현재 날짜
        long currentMillis = calendar.getTimeInMillis();    // 현재 시간을 초로
        Date standardDate = currentTimeFormat.parse(today+"0600");    // api가 아침 6시를 기준으로 데이터가 갱신되므로
        Long standardMillis = standardDate.getTime();       // 기준 시간을 초로

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
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
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
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
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
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + festivalKey);
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("255", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
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
    public String setDayOfWeek(int dayOfWeekCode) {
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
    public void setDay12Info (List<ShortTermWeatherItem> shortTermWeatherItems, List<Weather> weatherList, String day, int dayOfWeekCode) {
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
    public Map<String, Object> getGridxy(double orgLat, double orgLon) {

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
