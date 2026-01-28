package Sqlite;

import java.sql.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;


public class BestellSystem {

 
    private Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/shop";
            String user = "root";
            String password = "123456789";

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            System.out.println("Fehler beim Verbinden: " + e.getMessage());
            return null;
        }
    }

    
    public void createTables() {
        String kunden = "CREATE TABLE IF NOT EXISTS KUNDEN (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NAME VARCHAR(255), " +
                "GEBURTSDATUM DATE)";

        String artikel = "CREATE TABLE IF NOT EXISTS ARTIKEL (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "BEZEICHNUNG VARCHAR(255), " +
                "PREIS DECIMAL(10,2))";

        String bestellungen = "CREATE TABLE IF NOT EXISTS BESTELLUNGEN (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "KUNDEN_ID INT, " +
                "ARTIKEL_ID INT, " +
                "BESTELLZEITPUNKT DATETIME, " +
                "FOREIGN KEY (KUNDEN_ID) REFERENCES KUNDEN(ID), " +
                "FOREIGN KEY (ARTIKEL_ID) REFERENCES ARTIKEL(ID))";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(kunden);
            stmt.execute(artikel);
            stmt.execute(bestellungen);
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Tabellen: " + e.getMessage());
        }
    }

    
    public void addKunde(String name, String geburtsdatum) {
        try {
            Date date = Date.valueOf(geburtsdatum);
            String sql = "INSERT INTO KUNDEN (NAME, GEBURTSDATUM) VALUES (?, ?)";

            try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setDate(2, date);
                ps.executeUpdate();
            }
            System.out.println("Kunde '" + name + "' wurde erfolgreich angelegt.");

        } catch (Exception e) {
            System.out.println("Ungültiges Datumsformat! Bitte YYYY-MM-DD verwenden.");
        }
    }

    
    public void addArtikel(String bez, double preis) {
        String sql = "INSERT INTO ARTIKEL (BEZEICHNUNG, PREIS) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bez);
            ps.setDouble(2, preis);
            ps.executeUpdate();
            System.out.println("Artikel '" + bez + "' erfolgreich angelegt.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Anlegen des Artikels: " + e.getMessage());
        }
    }

    
    public void addBestellung(int kundenId, int artikelId) {
        String sql = "INSERT INTO BESTELLUNGEN (KUNDEN_ID, ARTIKEL_ID, BESTELLZEITPUNKT) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kundenId);
            ps.setInt(2, artikelId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            System.out.println("Bestellung erfolgreich angelegt.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Anlegen der Bestellung: " + e.getMessage());
        }
    }

    
    public void importKundenFromJSON(String datei) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(datei)));
            JSONArray arr = new JSONArray(content);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                addKunde(obj.getString("name"), obj.getString("geburtsdatum"));
            }
            System.out.println("JSON-Import abgeschlossen.");

        } catch (Exception e) {
            System.out.println("Fehler beim JSON-Import: " + e.getMessage());
        }
    }

    
    public void exportKundenToJSON(String datei) {
        JSONArray array = new JSONArray();
        String sql = "SELECT * FROM KUNDEN";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject o = new JSONObject();
                o.put("id", rs.getInt("ID"));
                o.put("name", rs.getString("NAME"));
                o.put("geburtsdatum", rs.getDate("GEBURTSDATUM").toString());
                array.put(o);
            }

            try (FileWriter fw = new FileWriter(datei)) {
                fw.write(array.toString(4)); 
            }
            System.out.println("JSON-Export abgeschlossen: " + datei);

        } catch (Exception e) {
            System.out.println("Fehler beim JSON-Export: " + e.getMessage());
        }
    }

    
    public static void main(String[] args) {
        BestellSystem shop = new BestellSystem();
        shop.createTables();

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("\n===== HAUPTMENÜ =====");
            System.out.println("1) Kunde anlegen");
            System.out.println("2) Artikel anlegen");
            System.out.println("3) Bestellung aufgeben");
            System.out.println("4) Kunden aus JSON importieren");
            System.out.println("5) Kunden als JSON exportieren");
            System.out.println("0) Beenden");
            System.out.print("Ihre Wahl: ");

            int w = sc.nextInt();
            sc.nextLine(); 

            switch (w) {
                case 1:
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Geburtsdatum (YYYY-MM-DD): ");
                    String geb = sc.nextLine();
                    shop.addKunde(name, geb);
                    break;
                case 2:
                    System.out.print("Bezeichnung: ");
                    String b = sc.nextLine();
                    System.out.print("Preis: ");
                    double preis = sc.nextDouble();
                    sc.nextLine();
                    shop.addArtikel(b, preis);
                    break;
                case 3:
                    System.out.print("Kunden ID: ");
                    int k = sc.nextInt();
                    System.out.print("Artikel ID: ");
                    int a = sc.nextInt();
                    sc.nextLine();
                    shop.addBestellung(k, a);
                    break;
                case 4:
                    shop.importKundenFromJSON("kunden_import.json");
                    break;
                case 5:
                    shop.exportKundenToJSON("kunden_export.json");
                    break;
                case 0:
                    run = false;
                    System.out.println("Programm beendet.");
                    break;
                default:
                    System.out.println("Ungültige Eingabe. Bitte wählen Sie erneut.");
            }
        }
        sc.close();
    }
}
