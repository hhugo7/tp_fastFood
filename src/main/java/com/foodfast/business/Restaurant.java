package com.foodfast.business;

import com.foodfast.exceptions.OrderPreparationException;

public class Restaurant {
    /**
     * Prépare une commande. Cette méthode simule une préparation avec 20% de chances
     * de lancer une OrderPreparationException.
     *
     * @param order la commande à préparer
     * @throws OrderPreparationException si la préparation échoue (20% de probabilité)
     * @throws IllegalArgumentException si la commande est nulle
     */
    public void prepare(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }

        // Génère un nombre aléatoire entre 0 et 1
        double random = Math.random();

        // 20% de chances de lancer une exception
        if (random < 0.2) {
            throw new OrderPreparationException("Erreur lors de la préparation de la commande " + order.getId());
        }

        // Sinon, la préparation réussit
        System.out.println("Commande " + order.getId() + " en préparation...");
    }
}
