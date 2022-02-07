package com.geekbrains.spring.web.recommendation.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.geekbrains.spring.web.api.core.ProductDto;
import com.geekbrains.spring.web.recommendation.services.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsController {
    private final StatisticService statisticService;

    @GetMapping("/purchased")
    public List<ProductDto> getFrequentlyPurchasedProducts() {
        log.info("Вызвали метод getFrequentlyPurchasedProducts");
        return statisticService.getRecommendationFiveProducts();
    }

    @GetMapping("/folded")
    public List<ProductDto> getFrequentlyFoldedToCartProducts() {
        log.info("Вызвали метод getFrequentlyFoldedToCartProducts");
        return statisticService.getAddedToCartFiveProducts();
    }
}
