package com.study.typeconverter.converter;

import com.study.typeconverter.type.IpPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.*;

public class ConversionServiceTest {

    @Test
    void conversionService(){
        // 등록
        DefaultConversionService service = new DefaultConversionService();
        service.addConverter(new StringToIntegerConverter());
        service.addConverter(new IntegerToStringConverter());
        service.addConverter(new StringToIpPortConverter());
        service.addConverter(new IpPortToStringConverter());

        // 사용
        assertThat(service.convert("10", Integer.class)).isEqualTo(10);
        assertThat(service.convert(10, String.class)).isEqualTo("10");
        IpPort ipPort = service.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        String ipPortString = service.convert(new IpPort("127.0.0.1", 8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");
    }
}
