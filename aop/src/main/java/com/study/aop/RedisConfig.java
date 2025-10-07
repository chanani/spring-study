package com.study.aop;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // Redis 서버 설정
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);

        // Lettuce Client 설정 - 재연결 및 타임아웃 설정
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(3))  // 명령 타임아웃 3초
                .clientOptions(ClientOptions.builder()
                        .socketOptions(SocketOptions.builder()
                                .connectTimeout(Duration.ofSeconds(3))  // 연결 타임아웃 3초
                                .build())
                        .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(3)))  // 타임아웃 활성화
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)  // 연결 끊김 시 명령 거부
                        .autoReconnect(true)  // 자동 재연결 활성화
                        .build())
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }
}
