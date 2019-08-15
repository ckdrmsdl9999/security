package com.security.everywhere.Scheduler;

import com.security.everywhere.repository.FestivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CronTable {

    @Autowired
    private final FestivalRepository festivalRepository;

    @Value("${festival_key}")
    private String festivalKey;

    public CronTable(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

//        // 매일 21시 30분 0초에 실행
//    @Scheduled(cron = "0 30 21 * * *")
//    public void dayJob() {
//
//    }
//
//    // 매월 1일 0시 0분 0초에 실행
//    @Scheduled(cron = "0 0 0 1 * *")
//    public void monthJob() {
//
//    }
//
//
//    //서버 시작하고 10초후에 실행 후 30분마다 실행
//    @Scheduled(initialDelay = 10000, fixedDelay = 1800000)
//    public void Job() throws IOException {
//        //축제 정보 가져와서 디비에 저장 *현재 실행 시키면 안됨*
//        GetFestivalInfo getFestivalInfo = new GetFestivalInfo(festivalRepository, festivalKey);
//        getFestivalInfo.run();
//    }

}
