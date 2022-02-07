package com.geekbrains.spring.web.recommendation.services;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekbrains.spring.web.api.core.OrderDto;
import com.geekbrains.spring.web.api.core.OrderItemDto;
import com.geekbrains.spring.web.api.core.ProductDto;
import com.geekbrains.spring.web.recommendation.integrations.CartServiceIntegration;
import com.geekbrains.spring.web.recommendation.integrations.OrderServiceIntegration;
import com.geekbrains.spring.web.recommendation.integrations.ProductServiceIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {
    private final OrderServiceIntegration orderServiceIntegration;
    private final ProductServiceIntegration productServiceIntegration;
    private final CartServiceIntegration cartServiceIntegration;

    public List<ProductDto> getRecommendationFiveProducts() {
        
        List list = orderServiceIntegration.getOrderByTimePeriod(
                LocalDateTime.now().minus(30, ChronoUnit.DAYS),
                LocalDateTime.now());

        List<OrderDto> orderDtoList = new ArrayList<>();

        //мапим в OrderDto, без этого не работает
        for (Object order : list) {
            ObjectMapper objectMapper = new ObjectMapper();
            OrderDto orderDto = objectMapper.convertValue(order, OrderDto.class);
            orderDtoList.add(orderDto);
        }

        //вытаскиваем все item-ы в один список
        List<OrderItemDto> orderItemDtoList = orderDtoList.stream()
                .map(OrderDto::getItems)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        //отбираем только уникальные значения
        Set<Long> productIdSet = orderItemDtoList.stream()
                .map(OrderItemDto::getProductId)
                .collect(Collectors.toSet());

        //Суммируем кол-во по каждому id
        Map<Long, Integer> productMap = new HashMap();
        for (Long productId : productIdSet) {
            int productCount = 0;
            for (OrderItemDto orderItem : orderItemDtoList) {
                if (Objects.equals(orderItem.getProductId(), productId)) {
                    productCount = productCount + orderItem.getQuantity();
                }
            }
            productMap.put(productId, productCount);
        }

        //Сортируем по частоте и берем первые 5
        return productMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5).map(i -> productServiceIntegration.getProductById(i.getKey())).collect(Collectors.toList());
    }

    public List<ProductDto> getAddedToCartFiveProducts() {
        List<ProductDto> productDtoList = new ArrayList<>();
        log.info("Создали productDtoList");

        //Мапим в productDto
        List list = productServiceIntegration.getAllProducts();
        log.info("Получили list " +list.toString());
        for (Object product : list) {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.convertValue(product, ProductDto.class);
            productDtoList.add(productDto);
        }
        log.info("Отмапили list в productDtoList");

        Map<Long,Integer> productMap = new HashMap<>();
        for (ProductDto product:productDtoList) {
            Integer counter = cartServiceIntegration.getCounterByProductId(product.getId());
            if (counter>0){
                productMap.put(product.getId(),counter);
            }
        }
        log.info("Заполнили мапу productMap "+productMap);

        //Сортируем по частоте и берем первые 5
        return productMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5).map(i -> productServiceIntegration.getProductById(i.getKey())).collect(Collectors.toList());

    }
}