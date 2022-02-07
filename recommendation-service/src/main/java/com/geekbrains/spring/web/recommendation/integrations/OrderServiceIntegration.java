package com.geekbrains.spring.web.recommendation.integrations;

import java.util.List;

import java.time.LocalDateTime;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.geekbrains.spring.web.api.core.OrderDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderServiceIntegration {
    private final WebClient coreServiceWebClient;

    public List getOrderByTimePeriod(LocalDateTime startDate, LocalDateTime finishDate) {
        List orderDtoList =  coreServiceWebClient.get()
                .uri("/api/v1/orders/between_date")
                .header("startDate", String.valueOf(startDate))
                .header("finishDate", String.valueOf(finishDate))
                .retrieve()
                .bodyToMono(List.class)
                .block();
        return orderDtoList;
    }
}
