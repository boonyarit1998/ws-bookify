package com.ws.bookify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ws.bookify.util.InstantTypeAdapter;
import com.ws.bookify.util.LocalDateTimeTypeAdapter;
import com.ws.bookify.util.LocalDateTypeAdapter;
import com.ws.bookify.util.ThrowableAddapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class GsonConfig {


    public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapterFactory(ThrowableAddapterFactory.INSTANCE)
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .setDateFormat(YYYYMMDD_HHMMSS)
                .create();
    }
}