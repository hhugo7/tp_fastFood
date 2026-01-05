package com.foodfast;

import com.foodfast.business.Customer;
import com.foodfast.business.Order;
import com.foodfast.business.OrderStatus;
import com.foodfast.business.Restaurant;
import com.foodfast.database.DatabaseConfig;
import com.foodfast.exceptions.OrderPreparationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Plateforme de gestion des commandes et livraisons de FoodFast.
 * Cette classe gère le stockage et la recherche des commandes passées par les clients.
 * Elle fournit des méthodes pour placer des commandes, les rechercher par ID, par client ou par statut.
 */
public class DeliveryPlatform
{
    private final Map<String, Order> orders;
    private final Restaurant restaurant;

    /**
     * Constructeur de DeliveryPlatform.
     * Initialise une nouvelle instance avec une ConcurrentHashMap pour stocker les commandes
     * et crée une nouvelle instance de Restaurant.
     */
    public DeliveryPlatform()
    {
        this.orders = new ConcurrentHashMap<>();
        this.restaurant = new Restaurant();
    }

    /**
     * Retourne la Map de toutes les commandes stockées dans la plateforme.
     *
     * @return une Map contenant toutes les commandes avec leur ID comme clé
     */
    public Map<String, Order> getOrders()
    {
        return orders;
    }

    /**
     * Place une nouvelle commande sur la plateforme de manière thread-safe.
     * La commande est stockée dans la ConcurrentHashMap avec son ID comme clé.
     * Le restaurant tente de préparer la commande. En cas d'erreur de préparation,
     * la commande est passée au statut CANCELLED et un message d'erreur est affiché.
     * Cette méthode est synchronisée pour éviter les race conditions lors de la modification
     * simultanée de l'état de la commande.
     *
     * @param order la commande à placer
     * @throws IllegalArgumentException si la commande est nulle
     * @throws IllegalArgumentException si l'ID de la commande est null ou vide
     */
    public synchronized void placeOrder(Order order)
    {
        if (order == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }
        if (order.getId() == null || order.getId().isEmpty()) {
            throw new IllegalArgumentException("La commande doit avoir un ID");
        }

        try {
            restaurant.prepare(order);
            order.setStatus(OrderStatus.IN_PREPARATION);
        } catch (OrderPreparationException e) {
            order.setStatus(OrderStatus.CANCELLED);
            System.out.println("Erreur lors de la préparation : " + e.getMessage());
        }

        orders.put(order.getId(), order);
        this.saveOrder(order);
    }

    /**
     * Recherche une commande par son identifiant unique.
     *
     * @param orderId l'identifiant de la commande à rechercher
     * @return un Optional contenant la commande si trouvée, ou Optional.empty() sinon
     */
    public Optional<Order> findOrderById(String orderId)
    {
        if (orderId == null || orderId.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(orderId));
    }

    /**
     * Recherche toutes les commandes passées par un client spécifique.
     * La recherche se base sur l'ID du client.
     *
     * @param customer le client dont il faut trouver les commandes
     * @return une List contenant toutes les commandes du client, ou une liste vide si le client est null
     */
    public List<Order> findOrdersByCustomer(Customer customer)
    {
        if (customer == null) {
            return new ArrayList<>();
        }
        return orders.values().stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getId().equals(customer.getId()))
                .toList();
    }

    /**
     * Recherche toutes les commandes avec un statut spécifique.
     *
     * @param status le statut des commandes à rechercher (PENDING, IN_PREPARATION, COMPLETED, CANCELLED)
     * @return une List contenant toutes les commandes avec le statut spécifié, ou une liste vide si le statut est null
     */
    public List<Order> findOrdersByStatus(OrderStatus status)
    {
        if (status == null) {
            return new ArrayList<>();
        }
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    /**
     * Sauvegarde une commande et ses plats associés dans la base de données PostgreSQL.
     * Utilise PreparedStatement pour insérer de manière sécurisée :
     * 1. La commande dans la table orders
     * 2. Les plats de la commande dans la table order_dishes
     *
     * @param order la commande à sauvegarder
     * @return true si la sauvegarde est réussie, false en cas d'erreur
     * @throws IllegalArgumentException si la commande est nulle
     */
    public boolean saveOrder(Order order)
    {
        if (order == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Désactive l'autocommit pour gérer manuellement la transaction
            conn.setAutoCommit(false);

            try {
                // 1. Insertion de la commande dans la table orders
                String sqlOrder = "INSERT INTO orders (order_id, customer_id, status, order_date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {
                    pstmtOrder.setString(1, order.getId());

                    // Récupère l'ID du client s'il existe, sinon utilise null
                    String customerId = (order.getCustomer() != null) ? order.getCustomer().getId() : null;
                    if (customerId != null) {
                        pstmtOrder.setString(2, customerId);
                    } else {
                        pstmtOrder.setNull(2, java.sql.Types.VARCHAR);
                    }

                    // Sauvegarde le statut de la commande
                    pstmtOrder.setString(3, order.getStatus().name());

                    // Sauvegarde la date de commande ou la date actuelle
                    LocalDateTime orderDate = (order.getOrderDate() != null) ? order.getOrderDate() : LocalDateTime.now();
                    pstmtOrder.setObject(4, orderDate);

                    pstmtOrder.executeUpdate();
                }

                // 2. Insertion des plats de la commande dans la table order_dishes
                if (order.getDishes() != null && !order.getDishes().isEmpty()) {
                    String sqlDish = "INSERT INTO order_dishes (order_id, name, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmtDish = conn.prepareStatement(sqlDish)) {
                        for (Map.Entry<com.foodfast.business.Dish, Integer> entry : order.getDishes().entrySet()) {
                            com.foodfast.business.Dish dish = entry.getKey();
                            Integer quantity = entry.getValue();

                            pstmtDish.setString(1, order.getId());
                            pstmtDish.setString(2, dish.getName());
                            pstmtDish.setInt(3, quantity != null ? quantity : 1);

                            pstmtDish.addBatch();
                        }
                        pstmtDish.executeBatch();
                    }
                }

                // Valide la transaction
                conn.commit();
                System.out.println("Commande avec l'ID " + order.getId() + " et ses plats sauvegardés dans la base de données.");
                return true;

            } catch (SQLException e) {
                // Annule la transaction en cas d'erreur
                conn.rollback();
                System.err.println("Erreur lors de la sauvegarde de la commande : " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
