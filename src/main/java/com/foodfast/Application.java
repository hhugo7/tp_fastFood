package com.foodfast;

import com.foodfast.business.Customer;
import com.foodfast.business.Dish;
import com.foodfast.business.DishSize;
import com.foodfast.business.Order;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe principale de l'application FoodFast.
 * Démontre l'utilisation thread-safe de la plateforme DeliveryPlatform
 * avec un ExecutorService simulant plusieurs restaurants qui passent des commandes en parallèle.
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("Bienvenue chez FoodFast !");
        System.out.println("=".repeat(60));

        // Créer une instance thread-safe de DeliveryPlatform
        DeliveryPlatform deliveryPlatform = new DeliveryPlatform();

        // Créer un ExecutorService avec 5 threads pour simuler 5 restaurants
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Nombre de commandes à passer par restaurant
        int ordersPerRestaurant = 3;
        int numberOfRestaurants = 5;

        System.out.println("\nLancement de " + numberOfRestaurants + " restaurants (threads) passant " +
                ordersPerRestaurant + " commandes chacun...\n");

        // Soumettre des tâches à chaque thread
        for (int restaurantId = 1; restaurantId <= numberOfRestaurants; restaurantId++) {
            final int restId = restaurantId;
            executorService.submit(() -> simulateRestaurantOrders(deliveryPlatform, restId, ordersPerRestaurant));
        }

        // Arrêter l'ExecutorService et attendre la fin de toutes les tâches
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Afficher le résumé final
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Résumé final :");
        System.out.println("Total des commandes : " + deliveryPlatform.getOrders().size());
        System.out.println("Commandes en attente : " + deliveryPlatform.findOrdersByStatus(
                com.foodfast.business.OrderStatus.PENDING).size());
        System.out.println("Commandes en préparation : " + deliveryPlatform.findOrdersByStatus(
                com.foodfast.business.OrderStatus.IN_PREPARATION).size());
        System.out.println("Commandes complétées : " + deliveryPlatform.findOrdersByStatus(
                com.foodfast.business.OrderStatus.COMPLETED).size());
        System.out.println("Commandes annulées : " + deliveryPlatform.findOrdersByStatus(
                com.foodfast.business.OrderStatus.CANCELLED).size());
    }

    /**
     * Simule un restaurant qui passe plusieurs commandes sur la plateforme.
     * Cette méthode est exécutée dans un thread séparé via ExecutorService.
     *
     * @param deliveryPlatform la plateforme de livraison (thread-safe)
     * @param restaurantId l'identifiant du restaurant
     * @param numberOfOrders le nombre de commandes à passer
     */
    private static void simulateRestaurantOrders(DeliveryPlatform deliveryPlatform, int restaurantId, int numberOfOrders) {
        for (int orderIndex = 1; orderIndex <= numberOfOrders; orderIndex++) {
            try {
                // Créer un client
                Customer customer = new Customer()
                        .setId("CUSTOMER_REST" + restaurantId + "_ORD" + orderIndex)
                        .setName("Client Restaurant " + restaurantId + " - Commande " + orderIndex)
                        .setAddress("Adresse Client " + restaurantId + "-" + orderIndex);

                // Créer un plat
                Dish dish = new Dish()
                        .setName("Plat " + orderIndex + " du Restaurant " + restaurantId)
                        .setPrice(new java.math.BigDecimal("12.99"))
                        .setSize(DishSize.MEDIUM);

                // Créer une commande
                Order order = new Order()
                        .setId("ORDER_REST" + restaurantId + "_ORD" + orderIndex)
                        .setCustomer(customer)
                        .setDishes(new HashMap<>());
                order.addDish(dish);

                // Placer la commande de manière thread-safe
                System.out.println("[Restaurant " + restaurantId + "] Passage de la commande " + orderIndex +
                        " (ID: " + order.getId() + ") par " + Thread.currentThread().getName());

                deliveryPlatform.placeOrder(order);

                System.out.println("[Restaurant " + restaurantId + "] Commande " + orderIndex +
                        " placée avec le statut : " + order.getStatus());

                // Petit délai pour simuler du traitement
                Thread.sleep(100 + (restaurantId * 50));

            } catch (IllegalArgumentException | InterruptedException e) {
                System.err.println("[Restaurant " + restaurantId + "] Erreur lors du placement de la commande " +
                        orderIndex + " : " + e.getMessage());
            }
        }
        System.out.println("[Restaurant " + restaurantId + "] Toutes les commandes ont été placées !");
    }
}