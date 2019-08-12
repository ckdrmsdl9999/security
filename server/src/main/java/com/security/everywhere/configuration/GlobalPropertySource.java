/*
* 설정파일 읽어오기
* */

package com.security.everywhere.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({  // 여러개의 경로 지정해서 여러개의 설정파일 읽기 가능
        @PropertySource(value = "file:etc/application.properties"
                , ignoreResourceNotFound = true)
})
@Getter // 변수마다 getter를 순서대로 만들어줌 (lombok에서 제공)
public class GlobalPropertySource {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

//    @Override
//    public void run(ApplicationArguments args) throws Exception {   // 잘 읽었는지 테스트 잘 되면 지워도 됨 단 위 implements ApplicationRunner도 지워야함
//        System.out.println(driverClassName);
//        System.out.println(url);
//        System.out.println(username);
//        System.out.println(password);
//    }
}
