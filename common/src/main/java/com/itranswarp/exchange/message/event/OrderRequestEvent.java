package com.itranswarp.exchange.message.event;

import java.math.BigDecimal;

import com.itranswarp.exchange.enums.AssetEnum;
import com.itranswarp.exchange.enums.Direction;

public class OrderRequestEvent extends AbstractEvent {

    public Long userId;

    public AssetEnum asset;

    public Direction direction;

    public BigDecimal price;

    public BigDecimal quantity;

    @Override
    public String toString() {
        return "OrderRequestEvent{" +
                "userId=" + userId +
                ", asset=" + asset +
                ", direction=" + direction +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
