package Sqlite;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class FerienHue {
    
    private static final String DB_URL = "jdbc:sqlite:testdb.db";
    private Connection connection;
    private Scanner scanner;
    
    public FerienHue() {
        scanner = new Scanner(System.in);
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Verbindung zur Datenbank hergestellt.");
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung: " + e.getMessage());
        }
    }
    

    public void tabelleAnlegen() {
        String sql = "CREATE TABLE IF NOT EXISTS daten (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "value INTEGER NOT NULL, " +
                     "value2 INTEGER NOT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelle 'daten' wurde angelegt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Anlegen der Tabelle: " + e.getMessage());
        }
    }
    

    public void zeilenEinfuegen() {
        System.out.println("\nWie möchten Sie die Werte eingeben?");
        System.out.println("1 - Zufallszahlen (1-10)");
        System.out.println("2 - Manuelle Eingabe");
        System.out.print("Ihre Wahl: ");
        
        int wahl = scanner.nextInt();
        String sql = "INSERT INTO daten (value, value2) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (wahl == 1) {
                
                Random random = new Random();
                for (int i = 0; i < 20; i++) {
                    int value = random.nextInt(10) + 1;
                    int value2 = value % 2;
                    
                    pstmt.setInt(1, value);
                    pstmt.setInt(2, value2);
                    pstmt.executeUpdate();
                }
                System.out.println("20 Zeilen mit Zufallszahlen wurden eingefügt.");
            } else if (wahl == 2) {
                
                for (int i = 1; i <= 20; i++) {
                    int value;
                    do {
                        System.out.print("Zeile " + i + " - Geben Sie eine Zahl zwischen 1 und 10 ein: ");
                        value = scanner.nextInt();
                        
                        if (value < 1 || value > 10) {
                            System.out.println("Fehler! Bitte nur Zahlen zwischen 1 und 10 eingeben.");
                        }
                    } while (value < 1 || value > 10);
                    
                    int value2 = value % 2;
                    
                    pstmt.setInt(1, value);
                    pstmt.setInt(2, value2);
                    pstmt.executeUpdate();
                }
                System.out.println("20 Zeilen mit manuellen Eingaben wurden eingefügt.");
            } else {
                System.out.println("Ungültige Wahl!");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Einfügen der Zeilen: " + e.getMessage());
        }
    }
    

    public void anzahlValue2Null() {
        String sql = "SELECT COUNT(*) as anzahl FROM daten WHERE value2 = 0";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int anzahl = rs.getInt("anzahl");
                System.out.println("Anzahl mit value2 = 0: " + anzahl);
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Zählen: " + e.getMessage());
        }
    }
    

    public void anzahlValue2Eins() {
        String sql = "SELECT COUNT(*) as anzahl FROM daten WHERE value2 = 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int anzahl = rs.getInt("anzahl");
                System.out.println("Anzahl mit value2 = 1: " + anzahl);
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Zählen: " + e.getMessage());
        }
    }
    

    public void alleDatenAnzeigen() {
        String sql = "SELECT * FROM daten";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Alle Daten in der Tabelle ---");
            System.out.println("ID\tValue\tValue2");
            System.out.println("--------------------------------");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int value = rs.getInt("value");
                int value2 = rs.getInt("value2");
                System.out.println(id + "\t" + value + "\t" + value2);
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Daten: " + e.getMessage());
        }
    }
    

    public void verbindungSchliessen() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("\nVerbindung zur Datenbank geschlossen.");
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }
    

    public static void main(String[] args) {
    	FerienHue dbManager = new FerienHue();
        
     
        dbManager.tabelleAnlegen();
        
        
        dbManager.zeilenEinfuegen();
        
        
        dbManager.alleDatenAnzeigen();
        
        System.out.println();
        dbManager.anzahlValue2Null();
        dbManager.anzahlValue2Eins();
        
      
        dbManager.verbindungSchliessen();
    }
}