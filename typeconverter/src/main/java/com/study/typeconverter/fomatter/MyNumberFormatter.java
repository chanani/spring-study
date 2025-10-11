package com.study.typeconverter.fomatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    // 문자 -> 숫자
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("parse text: {}, locale={}", text, locale);
        // "1,000" -> 1000
        return NumberFormat.getInstance().parse(text);
    }

    // 객체 -> 문자
    @Override
    public String print(Number object, Locale locale) {
        log.info("print object: {}, locale={}", object, locale);
        return NumberFormat.getInstance().format(object);
    }
}
