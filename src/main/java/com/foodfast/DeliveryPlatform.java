package com.foodfast;

import com.foodfast.business.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DeliveryPlatform
{
    private final Map<String, Order> orders;

    public DeliveryPlatform()
    {
        this.orders = new HashMap<>();
    }

    public Map<String, Order> getOrders()
    {
        return orders;
    }

    public void placeOrder(Order order)
    {
        if (order == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }
        if (order.getId() == null || order.getId().isEmpty()) {
            throw new IllegalArgumentException("La commande doit avoir un ID");
        }
        orders.put(order.getId(), order);
    }

    public Optional<Order> findOrderById(String orderId)
    {
        if (orderId == null || orderId.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(orderId));
    }
}
