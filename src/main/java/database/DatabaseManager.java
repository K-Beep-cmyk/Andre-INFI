package database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import models.Flug;
import models.Flugzeug;
import models.Kunde;
import models.Stadt;
import models.Ticket;

import java.sql.SQLException;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456789";

    private static ConnectionSource connectionSource;

    // ein DAO übernimmt das Speichern;Lesen; Updaten und Löschen von deN Objekten in der Datenbank,
    // ohne eigene SQL Befehle
    public static Dao<Stadt, Integer> stadtDao; // DAO für die Tabelle Stadt wird erstellt PK ist Integer
    public static Dao<Flugzeug, Integer> flugzeugDao;
    public static Dao<Kunde, Integer> kundeDao;
    public static Dao<Flug, Integer> flugDao;
    public static Dao<Ticket, Integer> ticketDao;

    public static void setupDatabase() {
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL, DB_USER, DB_PASSWORD);
            System.out.println("Datenbankverbindung erfolgreich hergestellt!");

            // Tabellen machen
            TableUtils.createTableIfNotExists(connectionSource, Stadt.class);
            TableUtils.createTableIfNotExists(connectionSource, Flugzeug.class);
            TableUtils.createTableIfNotExists(connectionSource, Kunde.class);
            TableUtils.createTableIfNotExists(connectionSource, Flug.class);
            TableUtils.createTableIfNotExists(connectionSource, Ticket.class);


            stadtDao = DaoManager.createDao(connectionSource, Stadt.class); // baut das Dao auf und verknüpf mit DB
            flugzeugDao = DaoManager.createDao(connectionSource, Flugzeug.class);
            kundeDao = DaoManager.createDao(connectionSource, Kunde.class);
            flugDao = DaoManager.createDao(connectionSource, Flug.class);
            ticketDao = DaoManager.createDao(connectionSource, Ticket.class);

            System.out.println("Tabellen und DAOs wurden erfolgreich geladen!");

        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankverbindung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
                System.out.println("Datenbankverbindung geschlossen.");
            } catch (Exception e) {
                System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }
}