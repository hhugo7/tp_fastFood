package com.foodfast.business;

/**
 * Représente un client du service de livraison de nourriture.
 * Contient les informations personnelles et l'adresse de livraison du client.
 */
public class Customer
{
    private String id;
    private String name;
    private String address;

    /**
     * Retourne l'identifiant unique du client.
     *
     * @return l'ID du client
     */
    public String getId()
    {
        return id;
    }

    /**
     * Définit l'identifiant unique du client.
     *
     * @param id l'identifiant à définir
     * @return l'objet Customer pour permettre le chaînage d'appels (fluent builder)
     */
    public Customer setId(String id)
    {
        this.id = id;
        return this;
    }

    /**
     * Retourne le nom du client.
     *
     * @return le nom du client
     */
    public String getName()
    {
        return name;
    }

    /**
     * Définit le nom du client.
     *
     * @param name le nom à définir
     * @return l'objet Customer pour permettre le chaînage d'appels (fluent builder)
     */
    public Customer setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Retourne l'adresse de livraison du client.
     *
     * @return l'adresse du client
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * Définit l'adresse de livraison du client.
     *
     * @param address l'adresse à définir
     * @return l'objet Customer pour permettre le chaînage d'appels (fluent builder)
     */
    public Customer setAddress(String address)
    {
        this.address = address;
        return this;
    }
}
