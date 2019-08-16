package com.security.everywhere.controller;


import com.security.everywhere.model.Festival;
import com.security.everywhere.repository.FestivalRepository;
import com.security.everywhere.request.FestivalParam;
import com.security.everywhere.request.ObservatoryParam;
import com.security.everywhere.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired
    private final FestivalRepository festivalRepository;

    @Value("${festival_key}")
    private String festivalKey;

    public RestAPIController(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @PostMapping("/festivalInfo")
    public List<Festival> festivalInfo(FestivalParam requestParam) {
        int pageNo = Integer.parseInt(requestParam.getPageNo());
        int numOfRows = Integer.parseInt(requestParam.getNumOfRows());
        String eventStartDate = requestParam.getEventStartDate();

        Pageable pageElements = PageRequest.of(pageNo, numOfRows, Sort.by("eventStartDate"));

        List<Festival> festivals = festivalRepository.findAllByEventStartDateGreaterThanEqual(eventStartDate, pageElements);

        return festivals;
    }

    @PostMapping("/airInfo")
    public AirItem observatoryInfo(ObservatoryParam requestParam) throws IOException {

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

}
