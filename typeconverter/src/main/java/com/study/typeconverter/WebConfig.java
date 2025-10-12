package com.study.typeconverter;

import com.study.typeconverter.converter.IntegerToStringConverter;
import com.study.typeconverter.converter.IpPortToStringConverter;
import com.study.typeconverter.converter.StringToIntegerConverter;
import com.study.typeconverter.converter.StringToIpPortConverter;
import com.study.typeconverter.fomatter.MyNumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 컨버터를 추가하면 추가한 컨버터가 기본 컨버터보다 높은 우선순위를 가진다.
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 우선 순위 때문에 주석처리
        // registry.addConverter(new StringToIntegerConverter());
        // registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());

        // 추가
        registry.addFormatter(new MyNumberFormatter());
    }
}
