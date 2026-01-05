package com.foodfast;

import com.foodfast.business.Customer;
import com.foodfast.business.Dish;
import com.foodfast.business.DishSize;
import com.foodfast.business.Order;
import com.foodfast.business.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryPlatformTest {

    private DeliveryPlatform platform;
    private Order order1;
    private Order order2;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Initialisation de la plateforme de livraison
        platform = new DeliveryPlatform();

        // Initialisation d'un client
        customer = new Customer()
                .setId("CUST001")
                .setName("Jean Dupont")
                .setAddress("123 Rue de Paris, 75001");

        // Initialisation de la première commande
        order1 = new Order()
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now())
                .setStatus(OrderStatus.PENDING);
        order1.setDishes(new HashMap<>());
        order1.addDish(new Dish()
                .setName("Pizza Margherita")
                .setPrice(new BigDecimal("12.50"))
                .setSize(DishSize.MEDIUM));

        // Initialisation de la deuxième commande
        order2 = new Order()
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now())
                .setStatus(OrderStatus.PENDING);
        order2.setDishes(new HashMap<>());
        order2.addDish(new Dish()
                .setName("Pâtes Carbonara")
                .setPrice(new BigDecimal("14.00"))
                .setSize(DishSize.LARGE));
    }

    // ==================== Tests pour placeOrder ====================

    @Test
    void testPlaceOrderSuccess() {
        platform.placeOrder(order1);

        assertTrue(platform.getOrders().containsKey(order1.getId()));
        assertEquals(1, platform.getOrders().size());
    }

    @Test
    void testPlaceMultipleOrders() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        assertEquals(2, platform.getOrders().size());
        assertTrue(platform.getOrders().containsKey(order1.getId()));
        assertTrue(platform.getOrders().containsKey(order2.getId()));
    }

    @Test
    void testPlaceOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            platform.placeOrder(null);
        });
    }

    @Test
    void testPlaceOrderWithNullId() {
        Order orderWithNullId = new Order();
        orderWithNullId.setId(null);
        orderWithNullId.setDishes(new HashMap<>());

        assertThrows(IllegalArgumentException.class, () -> {
            platform.placeOrder(orderWithNullId);
        });
    }

    @Test
    void testPlaceOrderWithEmptyId() {
        Order orderWithEmptyId = new Order();
        orderWithEmptyId.setId("");
        orderWithEmptyId.setDishes(new HashMap<>());

        assertThrows(IllegalArgumentException.class, () -> {
            platform.placeOrder(orderWithEmptyId);
        });
    }

    @Test
    void testPlaceOrderStoresCorrectOrder() {
        platform.placeOrder(order1);

        Order storedOrder = platform.getOrders().get(order1.getId());
        assertEquals(order1.getId(), storedOrder.getId());
        assertEquals(customer, storedOrder.getCustomer());
    }

    @Test
    void testPlaceOrderReplacesPreviousOrderWithSameId() {
        Order order1FirstVersion = new Order()
                .setId("SAME_ID")
                .setStatus(OrderStatus.PENDING)
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now());
        order1FirstVersion.setDishes(new HashMap<>());

        Order order1SecondVersion = new Order()
                .setId("SAME_ID")
                .setStatus(OrderStatus.IN_PREPARATION)
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now());
        order1SecondVersion.setDishes(new HashMap<>());

        platform.placeOrder(order1FirstVersion);
        assertEquals(OrderStatus.PENDING, platform.getOrders().get("SAME_ID").getStatus());

        platform.placeOrder(order1SecondVersion);
        assertEquals(OrderStatus.IN_PREPARATION, platform.getOrders().get("SAME_ID").getStatus());
        assertEquals(1, platform.getOrders().size());
    }

    // ==================== Tests pour findOrderById ====================

    @Test
    void testFindOrderByIdSuccess() {
        platform.placeOrder(order1);

        Optional<Order> foundOrder = platform.findOrderById(order1.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(order1.getId(), foundOrder.get().getId());
        assertEquals(customer, foundOrder.get().getCustomer());
    }

    @Test
    void testFindOrderByIdNotFound() {
        platform.placeOrder(order1);

        Optional<Order> foundOrder = platform.findOrderById("NON_EXISTENT_ID");

        assertTrue(foundOrder.isEmpty());
    }

    @Test
    void testFindOrderByIdWithNullId() {
        Optional<Order> foundOrder = platform.findOrderById(null);

        assertTrue(foundOrder.isEmpty());
    }

    @Test
    void testFindOrderByIdWithEmptyString() {
        Optional<Order> foundOrder = platform.findOrderById("");

        assertTrue(foundOrder.isEmpty());
    }

    @Test
    void testFindOrderByIdMultipleOrders() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        Optional<Order> foundOrder1 = platform.findOrderById(order1.getId());
        Optional<Order> foundOrder2 = platform.findOrderById(order2.getId());

        assertTrue(foundOrder1.isPresent());
        assertTrue(foundOrder2.isPresent());
        assertNotEquals(foundOrder1.get().getId(), foundOrder2.get().getId());
    }

    @Test
    void testFindOrderByIdReturnsCorrectOrder() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        Optional<Order> foundOrder = platform.findOrderById(order2.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(order2.getId(), foundOrder.get().getId());
        assertEquals("Pâtes Carbonara", foundOrder.get().getDishes().keySet().stream().findFirst().get().getName());
    }

    // ==================== Tests généraux ====================

    @Test
    void testDeliveryPlatformInitiallyEmpty() {
        DeliveryPlatform newPlatform = new DeliveryPlatform();

        assertTrue(newPlatform.getOrders().isEmpty());
        assertEquals(0, newPlatform.getOrders().size());
    }

    @Test
    void testGetOrdersReturnsCorrectSize() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        assertEquals(2, platform.getOrders().size());
    }

    @Test
    void testCompleteWorkflow() {
        // Placer une commande
        platform.placeOrder(order1);
        assertTrue(platform.findOrderById(order1.getId()).isPresent());

        // Mettre à jour le statut de la commande
        order1.setStatus(OrderStatus.IN_PREPARATION);
        Optional<Order> updatedOrder = platform.findOrderById(order1.getId());

        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.IN_PREPARATION, updatedOrder.get().getStatus());

        // Ajouter une autre commande
        platform.placeOrder(order2);
        assertEquals(2, platform.getOrders().size());

        // Vérifier que les deux commandes existent
        assertTrue(platform.findOrderById(order1.getId()).isPresent());
        assertTrue(platform.findOrderById(order2.getId()).isPresent());
    }

}
