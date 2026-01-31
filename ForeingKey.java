package Sqlite;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;


public class ForeingKey {
    private String dbName;

    public ForeingKey(String dbName) {
        this.dbName = dbName;
    }

    private Connection connect() {
	    try {
	        Properties props = new Properties();
	        props.load(new FileInputStream("C:\\Schule\\SWP\\INFI\\src\\db.properties"));

	        String url = props.getProperty("db.url");
	        String user = props.getProperty("db.user");
	        String password = props.getProperty("db.password");

	        Class.forName("com.mysql.cj.jdbc.Driver");
	        return DriverManager.getConnection(url, user, password);
	        

	    } catch (Exception e) {
	        System.out.println("Fehler beim Verbinden: " + e.getMessage());
	        return null;
	    }
	}

    public void createTables() {
        String sqlKunden = "CREATE TABLE IF NOT EXISTS KUNDEN (" +
                           "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                           "NAME VARCHAR(255), " +
                           "GEBURTSDATUM DATE);";

        String sqlArtikel = "CREATE TABLE IF NOT EXISTS ARTIKEL (" +
                            "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                            "BEZEICHNUNG VARCHAR(255), " +
                            "PREIS DOUBLE);";

        String sqlBestellungen = "CREATE TABLE IF NOT EXISTS BESTELLUNGEN (" +
                                 "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                                 "KUNDEN_ID INT, " +
                                 "ARTIKEL_ID INT, " +
                                 "BESTELLZEITPUNKT DATETIME, " +
                                 "FOREIGN KEY(KUNDEN_ID) REFERENCES KUNDEN(ID), " +
                                 "FOREIGN KEY(ARTIKEL_ID) REFERENCES ARTIKEL(ID));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            if (conn != null) {
                stmt.execute(sqlKunden);
                stmt.execute(sqlArtikel);
                stmt.execute(sqlBestellungen);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addKunde(String name, String geburtsdatum) {
        String sql = "INSERT INTO KUNDEN (NAME, GEBURTSDATUM) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;
            
            pstmt.setString(1, name);
            pstmt.setDate(2, Date.valueOf(geburtsdatum));
            pstmt.executeUpdate();
            System.out.println("Kunde angelegt.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addArtikel(String bezeichnung, double preis) {
        String sql = "INSERT INTO ARTIKEL (BEZEICHNUNG, PREIS) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;

            pstmt.setString(1, bezeichnung);
            pstmt.setDouble(2, preis);
            pstmt.executeUpdate();
            System.out.println("Artikel angelegt.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBestellung(int kundenId, int artikelId) {
        String sql = "INSERT INTO BESTELLUNGEN (KUNDEN_ID, ARTIKEL_ID, BESTELLZEITPUNKT) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;

            pstmt.setInt(1, kundenId);
            pstmt.setInt(2, artikelId);
            long now = System.currentTimeMillis();
            pstmt.setTimestamp(3, new Timestamp(now));
            pstmt.executeUpdate();
            System.out.println("Bestellung erfolgreich.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllBestellungen() {
        String sql = "SELECT k.NAME, k.GEBURTSDATUM, a.BEZEICHNUNG, b.BESTELLZEITPUNKT " +
                     "FROM BESTELLUNGEN b " +
                     "JOIN KUNDEN k ON b.KUNDEN_ID = k.ID " +
                     "JOIN ARTIKEL a ON b.ARTIKEL_ID = a.ID";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (conn == null) return;

            System.out.println("\n--- BESTELLÜBERSICHT ---");
            while (rs.next()) {
                String kunde = rs.getString("NAME");
                Date geb = rs.getDate("GEBURTSDATUM");
                String artikel = rs.getString("BEZEICHNUNG");
                Timestamp zeit = rs.getTimestamp("BESTELLZEITPUNKT");

                System.out.println(zeit + " | " + kunde + " (" + geb + ") kaufte " + artikel);
            }
            System.out.println("------------------------\n");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void dropAllTables() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            if (conn == null) return;

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.executeUpdate("DROP TABLE IF EXISTS BESTELLUNGEN");
            stmt.executeUpdate("DROP TABLE IF EXISTS ARTIKEL");
            stmt.executeUpdate("DROP TABLE IF EXISTS KUNDEN");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            System.out.println("Datenbank wurde zurückgesetzt.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        ForeingKey shop = new ForeingKey("shop.db");
        
        shop.createTables();

        boolean running = true;

        while (running) {
            System.out.println("HAUPTMENÜ");
            System.out.println("1) Kunde anlegen");
            System.out.println("2) Artikel anlegen");
            System.out.println("3) Bestellung aufgeben");
            System.out.println("4) Alle Bestellungen anzeigen");
            System.out.println("5) Datenbank zurücksetzen");
            System.out.println("0) Beenden");
            System.out.print("Ihre Wahl: ");

            String wahlStr = sc.nextLine();
            int wahl = -1;
            try {
                wahl = Integer.parseInt(wahlStr);
            } catch (NumberFormatException e) {
            }

            switch (wahl) {
                case 1:
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Geburtsdatum (YYYY-MM-DD): ");
                    String geb = sc.nextLine();
                    shop.addKunde(name, geb);
                    break;
                case 2:
                    System.out.print("Artikel: ");
                    String bez = sc.nextLine();
                    System.out.print("Preis: ");
                    try {
                        double preis = Double.parseDouble(sc.nextLine().replace(",", "."));
                        shop.addArtikel(bez, preis);
                    } catch (Exception e) { System.out.println("Ungültig"); }
                    break;
                case 3:
                    try {
                        System.out.print("Kunden ID: ");
                        int kId = Integer.parseInt(sc.nextLine());
                        System.out.print("Artikel ID: ");
                        int aId = Integer.parseInt(sc.nextLine());
                        shop.addBestellung(kId, aId);
                    } catch (Exception e) { System.out.println("Ungültig"); }
                    break;
                case 4:
                    shop.showAllBestellungen();
                    break;
                case 5:
                    shop.dropAllTables();
                    shop.createTables();
                    break;
                case 0:
                    running = false;
                    System.out.println("Ende.");
                    break;
                default:
                    System.out.println("Ungültig.");
            }
            System.out.println();
        }
        sc.close();
    }
}
