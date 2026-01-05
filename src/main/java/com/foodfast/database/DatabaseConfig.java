package com.foodfast.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de configuration pour la connexion à la base de données PostgreSQL.
 * Fournit une méthode pour obtenir une connexion JDBC à la base de données FoodFast.
 */
public class DatabaseConfig
{
    // Configuration de la base de données PostgreSQL depuis docker-compose.yml
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/foodfast_db";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";

    /**
     * Obtient une connexion JDBC à la base de données PostgreSQL.
     *
     * @return une Connection à la base de données PostgreSQL
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException
    {
        try {
            // Charge le driver JDBC PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Teste la connexion à la base de données.
     *
     * @return true si la connexion est réussie, false sinon
     */
    public static boolean testConnection()
    {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            return false;
        }
    }
}
