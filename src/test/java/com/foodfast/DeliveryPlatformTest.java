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

    // ==================== Tests pour findOrdersByCustomer ====================

    @Test
    void testFindOrdersByCustomerSuccess() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var ordersOfCustomer = platform.findOrdersByCustomer(customer);

        assertEquals(2, ordersOfCustomer.size());
        assertTrue(ordersOfCustomer.contains(order1));
        assertTrue(ordersOfCustomer.contains(order2));
    }

    @Test
    void testFindOrdersByCustomerWithNullCustomer() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var ordersOfNullCustomer = platform.findOrdersByCustomer(null);

        assertTrue(ordersOfNullCustomer.isEmpty());
    }

    @Test
    void testFindOrdersByCustomerNoOrders() {
        Customer otherCustomer = new Customer()
                .setId("CUST002")
                .setName("Marie Leblanc")
                .setAddress("456 Avenue des Champs");

        var ordersOfOtherCustomer = platform.findOrdersByCustomer(otherCustomer);

        assertTrue(ordersOfOtherCustomer.isEmpty());
    }

    @Test
    void testFindOrdersByCustomerMultipleCustomers() {
        Customer customer2 = new Customer()
                .setId("CUST002")
                .setName("Marie Leblanc")
                .setAddress("456 Avenue des Champs");

        Order order3 = new Order()
                .setCustomer(customer2)
                .setOrderDate(LocalDateTime.now())
                .setStatus(OrderStatus.PENDING);
        order3.setDishes(new HashMap<>());
        order3.addDish(new Dish()
                .setName("Burger")
                .setPrice(new BigDecimal("9.50"))
                .setSize(DishSize.MEDIUM));

        platform.placeOrder(order1);
        platform.placeOrder(order2);
        platform.placeOrder(order3);

        var ordersOfCustomer1 = platform.findOrdersByCustomer(customer);
        var ordersOfCustomer2 = platform.findOrdersByCustomer(customer2);

        assertEquals(2, ordersOfCustomer1.size());
        assertEquals(1, ordersOfCustomer2.size());
        assertTrue(ordersOfCustomer1.contains(order1));
        assertTrue(ordersOfCustomer1.contains(order2));
        assertTrue(ordersOfCustomer2.contains(order3));
        assertFalse(ordersOfCustomer1.contains(order3));
    }

    @Test
    void testFindOrdersByCustomerWithoutCustomerSet() {
        Order orderWithoutCustomer = new Order()
                .setOrderDate(LocalDateTime.now())
                .setStatus(OrderStatus.PENDING);
        orderWithoutCustomer.setDishes(new HashMap<>());

        platform.placeOrder(order1);
        platform.placeOrder(orderWithoutCustomer);

        var ordersOfCustomer = platform.findOrdersByCustomer(customer);

        assertEquals(1, ordersOfCustomer.size());
        assertTrue(ordersOfCustomer.contains(order1));
        assertFalse(ordersOfCustomer.contains(orderWithoutCustomer));
    }

    // ==================== Tests pour findOrdersByStatus ====================

    @Test
    void testFindOrdersByStatusSuccess() {
        order1.setStatus(OrderStatus.PENDING);
        order2.setStatus(OrderStatus.PENDING);

        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var pendingOrders = platform.findOrdersByStatus(OrderStatus.PENDING);

        assertEquals(2, pendingOrders.size());
        assertTrue(pendingOrders.contains(order1));
        assertTrue(pendingOrders.contains(order2));
    }

    @Test
    void testFindOrdersByStatusWithNullStatus() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var ordersWithNullStatus = platform.findOrdersByStatus(null);

        assertTrue(ordersWithNullStatus.isEmpty());
    }

    @Test
    void testFindOrdersByStatusNoOrders() {
        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var completedOrders = platform.findOrdersByStatus(OrderStatus.COMPLETED);

        assertTrue(completedOrders.isEmpty());
    }

    @Test
    void testFindOrdersByStatusMultipleStatuses() {
        order1.setStatus(OrderStatus.PENDING);
        order2.setStatus(OrderStatus.IN_PREPARATION);

        Order order3 = new Order()
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now())
                .setStatus(OrderStatus.COMPLETED);
        order3.setDishes(new HashMap<>());
        order3.addDish(new Dish()
                .setName("Salade")
                .setPrice(new BigDecimal("8.50"))
                .setSize(DishSize.SMALL));

        platform.placeOrder(order1);
        platform.placeOrder(order2);
        platform.placeOrder(order3);

        var pendingOrders = platform.findOrdersByStatus(OrderStatus.PENDING);
        var inPrepOrders = platform.findOrdersByStatus(OrderStatus.IN_PREPARATION);
        var completedOrders = platform.findOrdersByStatus(OrderStatus.COMPLETED);

        assertEquals(1, pendingOrders.size());
        assertEquals(1, inPrepOrders.size());
        assertEquals(1, completedOrders.size());
        assertTrue(pendingOrders.contains(order1));
        assertTrue(inPrepOrders.contains(order2));
        assertTrue(completedOrders.contains(order3));
    }

    @Test
    void testFindOrdersByStatusAllOrders() {
        order1.setStatus(OrderStatus.IN_PREPARATION);
        order2.setStatus(OrderStatus.IN_PREPARATION);

        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var inPrepOrders = platform.findOrdersByStatus(OrderStatus.IN_PREPARATION);

        assertEquals(2, inPrepOrders.size());
        assertTrue(inPrepOrders.contains(order1));
        assertTrue(inPrepOrders.contains(order2));
    }

    @Test
    void testFindOrdersByStatusDoesNotReturnOtherStatuses() {
        order1.setStatus(OrderStatus.PENDING);
        order2.setStatus(OrderStatus.COMPLETED);

        platform.placeOrder(order1);
        platform.placeOrder(order2);

        var pendingOrders = platform.findOrdersByStatus(OrderStatus.PENDING);

        assertEquals(1, pendingOrders.size());
        assertTrue(pendingOrders.contains(order1));
        assertFalse(pendingOrders.contains(order2));
    }

    @Test
    void testPlaceOrderWithSuccessfulPreparation() {
        // Essayer plusieurs fois pour augmenter les chances d'une préparation réussie
        Order testOrder = new Order()
                .setCustomer(customer)
                .setOrderDate(LocalDateTime.now());
        testOrder.setDishes(new HashMap<>());

        boolean successfulPreparation = false;
        for (int i = 0; i < 20; i++) {
            Order attemptOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            attemptOrder.setDishes(new HashMap<>());

            platform.placeOrder(attemptOrder);

            if (attemptOrder.getStatus() == OrderStatus.IN_PREPARATION) {
                successfulPreparation = true;
                assertEquals(OrderStatus.IN_PREPARATION, attemptOrder.getStatus());
                assertTrue(platform.getOrders().containsKey(attemptOrder.getId()));
                break;
            }
        }

        assertTrue(successfulPreparation, "Aucune préparation réussie après 20 tentatives");
    }

    @Test
    void testPlaceOrderWithFailedPreparation() {
        // Essayer plusieurs fois pour augmenter les chances d'une préparation échouée
        boolean failedPreparation = false;
        for (int i = 0; i < 100; i++) {
            Order testOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            testOrder.setDishes(new HashMap<>());

            platform.placeOrder(testOrder);

            if (testOrder.getStatus() == OrderStatus.CANCELLED) {
                failedPreparation = true;
                assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
                assertTrue(platform.getOrders().containsKey(testOrder.getId()));
                break;
            }
        }

        assertTrue(failedPreparation, "Aucune préparation échouée après 100 tentatives (20% attendu)");
    }

    @Test
    void testPlaceOrderStorageAfterPreparationFailure() {
        // Essayer plusieurs fois pour augmenter les chances d'une préparation échouée
        for (int i = 0; i < 100; i++) {
            Order testOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            testOrder.setDishes(new HashMap<>());

            platform.placeOrder(testOrder);

            if (testOrder.getStatus() == OrderStatus.CANCELLED) {
                // Vérifier que la commande est stockée
                Optional<Order> retrievedOrder = platform.findOrderById(testOrder.getId());
                assertTrue(retrievedOrder.isPresent());
                assertEquals(OrderStatus.CANCELLED, retrievedOrder.get().getStatus());
                break;
            }
        }
    }

    @Test
    void testPlaceOrderStorageAfterSuccessfulPreparation() {
        // Essayer plusieurs fois pour augmenter les chances d'une préparation réussie
        for (int i = 0; i < 20; i++) {
            Order testOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            testOrder.setDishes(new HashMap<>());

            platform.placeOrder(testOrder);

            if (testOrder.getStatus() == OrderStatus.IN_PREPARATION) {
                // Vérifier que la commande est stockée avec le bon statut
                Optional<Order> retrievedOrder = platform.findOrderById(testOrder.getId());
                assertTrue(retrievedOrder.isPresent());
                assertEquals(OrderStatus.IN_PREPARATION, retrievedOrder.get().getStatus());
                break;
            }
        }
    }

    @Test
    void testFindCancelledOrdersAfterPreparationFailure() {
        // Placer plusieurs commandes pour augmenter les chances d'en avoir au moins une échouée
        for (int i = 0; i < 50; i++) {
            Order testOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            testOrder.setDishes(new HashMap<>());
            platform.placeOrder(testOrder);
        }

        var cancelledOrders = platform.findOrdersByStatus(OrderStatus.CANCELLED);

        // Vérifier qu'il y a au moins une commande annulée (statiquement improbable d'avoir 0 sur 50)
        assertTrue(cancelledOrders.size() > 0, "Aucune commande annulée trouvée après 50 tentatives");
    }

    @Test
    void testFindInPreparationOrdersAfterSuccessfulPreparation() {
        // Placer plusieurs commandes pour augmenter les chances d'en avoir au moins une réussie
        for (int i = 0; i < 20; i++) {
            Order testOrder = new Order()
                    .setCustomer(customer)
                    .setOrderDate(LocalDateTime.now());
            testOrder.setDishes(new HashMap<>());
            platform.placeOrder(testOrder);
        }

        var inPrepOrders = platform.findOrdersByStatus(OrderStatus.IN_PREPARATION);

        // Vérifier qu'il y a au moins une commande en préparation
        assertTrue(inPrepOrders.size() > 0, "Aucune commande en préparation trouvée après 20 tentatives");
    }

}
