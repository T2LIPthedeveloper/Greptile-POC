package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.CreateConsumerRequest;
import com.mcpgateway.admin.dto.CreateSubscriptionRequest;
import com.mcpgateway.admin.dto.ConsumerResponse;
import com.mcpgateway.admin.dto.SubscriptionResponse;
import com.mcpgateway.admin.service.ConsumerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/consumers")
public class ConsumerController {

    private final ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @GetMapping
    public List<ConsumerResponse> list() {
        return consumerService.listConsumers();
    }

    @PostMapping
    public ConsumerResponse create(@Valid @RequestBody CreateConsumerRequest request) {
        return consumerService.createConsumer(request);
    }

    @PostMapping("/{consumerId}/subscriptions")
    public SubscriptionResponse subscribe(
            @PathVariable UUID consumerId,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return consumerService.createSubscription(consumerId, request);
    }
}
