package com.foodfast.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Représente une commande passée par un client.
 * Gère les informations de la commande : plats commandés, client, statut et date de commande.
 */
public class Order
{
    private String id = UUID.randomUUID().toString();
    private OrderStatus status = OrderStatus.PENDING;
    private Map<Dish, Integer> dishes;
    private Customer customer;
    private LocalDateTime orderDate;

    /**
     * Retourne l'identifiant unique de la commande.
     * L'ID est généré automatiquement lors de la création de la commande (UUID).
     *
     * @return l'ID unique de la commande
     */
    public String getId()
    {
        return id;
    }

    /**
     * Définit un identifiant personnalisé pour la commande.
     *
     * @param id l'identifiant à définir
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order setId(String id)
    {
        this.id = id;
        return this;
    }

    /**
     * Retourne le statut actuel de la commande.
     *
     * @return le statut de la commande (PENDING, IN_PREPARATION, COMPLETED, CANCELLED)
     */
    public OrderStatus getStatus()
    {
        return status;
    }

    /**
     * Définit le statut de la commande.
     *
     * @param status le nouveau statut de la commande
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order setStatus(OrderStatus status)
    {
        this.status = status;
        return this;
    }

    /**
     * Retourne la Map contenant les plats de la commande avec leurs quantités.
     *
     * @return une Map où la clé est un Dish et la valeur est la quantité commandée
     */
    public Map<Dish, Integer> getDishes()
    {
        return dishes;
    }

    /**
     * Définit la Map des plats pour cette commande.
     *
     * @param dishes la Map des plats avec leurs quantités
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order setDishes(Map<Dish, Integer> dishes)
    {
        this.dishes = dishes;
        return this;
    }

    /**
     * Ajoute un plat à la commande ou augmente la quantité si le plat est déjà présent.
     *
     * @param dish le plat à ajouter
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order addDish(Dish dish)
    {
        this.dishes.put(dish, dishes.getOrDefault(dish, 0) + 1);
        return this;
    }

    /**
     * Retourne le client qui a passé cette commande.
     *
     * @return l'objet Customer associé à cette commande
     */
    public Customer getCustomer()
    {
        return customer;
    }

    /**
     * Définit le client pour cette commande.
     *
     * @param customer le client qui passe la commande
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order setCustomer(Customer customer)
    {
        this.customer = customer;
        return this;
    }

    /**
     * Retourne la date et heure de la commande.
     *
     * @return la date et heure de création/passage de la commande
     */
    public LocalDateTime getOrderDate()
    {
        return orderDate;
    }

    /**
     * Définit la date et heure de la commande.
     *
     * @param orderDate la date et heure à définir
     * @return l'objet Order pour permettre le chaînage d'appels (fluent builder)
     */
    public Order setOrderDate(LocalDateTime orderDate)
    {
        this.orderDate = orderDate;
        return this;
    }

    /**
     * Calcule le prix total de la commande en additionnant les prix des plats.
     * Note : Actuellement, cette méthode additionne simplement les prix des plats uniques,
     * sans tenir compte des quantités.
     *
     * @return le prix total de la commande
     */
    public BigDecimal calculateTotalPrice()
    {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Dish, Integer> entry : dishes.entrySet()) {
            Dish dish = entry.getKey();
            totalPrice = totalPrice.add(dish.getPrice());
        }
        return totalPrice;
    }
}
