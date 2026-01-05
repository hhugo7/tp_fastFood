package com.foodfast;

import com.foodfast.business.Customer;
import com.foodfast.business.Order;
import com.foodfast.business.OrderStatus;

import java.util.*;

/**
 * Plateforme de gestion des commandes et livraisons de FoodFast.
 * Cette classe gère le stockage et la recherche des commandes passées par les clients.
 * Elle fournit des méthodes pour placer des commandes, les rechercher par ID, par client ou par statut.
 */
public class DeliveryPlatform
{
    private final Map<String, Order> orders;

    /**
     * Constructeur de DeliveryPlatform.
     * Initialise une nouvelle instance avec une Map vide pour stocker les commandes.
     */
    public DeliveryPlatform()
    {
        this.orders = new HashMap<>();
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
     * Place une nouvelle commande sur la plateforme.
     * La commande est stockée dans la Map avec son ID comme clé.
     *
     * @param order la commande à placer
     * @throws IllegalArgumentException si la commande est nulle
     * @throws IllegalArgumentException si l'ID de la commande est null ou vide
     */
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
}
