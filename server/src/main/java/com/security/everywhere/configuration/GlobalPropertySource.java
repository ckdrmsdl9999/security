/*
* 설정파일 읽어오기
* */

package com.security.everywhere.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({  // 여러개의 경로 지정해서 여러개의 설정파일 읽기 가능
        @PropertySource(value = "file:etc/application.properties"
                , ignoreResourceNotFound = true)
})
public class GlobalPropertySource {
}
