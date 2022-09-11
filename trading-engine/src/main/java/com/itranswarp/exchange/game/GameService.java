package com.itranswarp.exchange.game;

import com.itranswarp.exchange.assets.AssetService;
import com.itranswarp.exchange.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameService {
    final AssetService assetService;

    final OrderService orderService;

    public GameService(@Autowired AssetService assetService, @Autowired OrderService orderService) {
        this.assetService = assetService;
        this.orderService = orderService;
    }



}
