package com.foodfast.business;

import java.math.BigDecimal;

/**
 * Représente un plat dans le catalogue de FoodFast.
 * Contient les informations essentielles d'un plat : nom, prix et taille.
 */
public class Dish
{
    private String name;
    private BigDecimal price;
    private DishSize size;

    /**
     * Retourne le nom du plat.
     *
     * @return le nom du plat
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom du plat.
     *
     * @param name le nom du plat à définir
     * @return l'objet Dish pour permettre le chaînage d'appels (fluent builder)
     */
    public Dish setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Retourne le prix du plat.
     *
     * @return le prix du plat en tant que BigDecimal
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Définit le prix du plat.
     *
     * @param price le prix du plat à définir
     * @return l'objet Dish pour permettre le chaînage d'appels (fluent builder)
     */
    public Dish setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    /**
     * Retourne la taille du plat.
     *
     * @return la taille du plat (SMALL, MEDIUM ou LARGE)
     */
    public DishSize getSize() {
        return size;
    }

    /**
     * Définit la taille du plat.
     *
     * @param size la taille du plat à définir
     * @return l'objet Dish pour permettre le chaînage d'appels (fluent builder)
     */
    public Dish setSize(DishSize size) {
        this.size = size;
        return this;
    }
}
