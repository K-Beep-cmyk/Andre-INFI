package models;

import database.DatabaseManager;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.ERROR);
        System.out.println("Initialisiere System...");
        DatabaseManager.setupDatabase();

        ladeTestdaten();

        boolean running = true;

        while (running) {
            System.out.println("\n=======================================");
            System.out.println("GUTE in Frankfurt");
            System.out.println("=======================================");
            System.out.println("1. Verfügbare Flüge anzeigen");
            System.out.println("2. Als Kunde registrieren");
            System.out.println("3. Ticket buchen");
            System.out.println("4. Ticket stornieren");
            System.out.println("0. Beenden");
            System.out.print("\nBitte wähle eine Option: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    fluegeAnzeigen();
                    break;
                case "2":
                    kundeRegistrieren();
                    break;
                case "3":
                    ticketBuchen();
                    break;
                case "4":
                    ticketStornieren();
                    break;
                case "0":
                    System.out.println("Vielen Dank für die Nutzung des Systems. Auf Wiedersehen!");
                    running = false;
                    break;
                default:
                    System.out.println("Ungültige Eingabe! Bitte wähle eine Zahl von 0 bis 4.");
            }
        }

        DatabaseManager.closeConnection();
        scanner.close();
    }

    private static void fluegeAnzeigen() {
        System.out.println("\n--- VERFÜGBARE FLÜGE ---");
        try {
            java.util.List<models.Flug> fluege = database.DatabaseManager.flugDao.queryForAll();

            if (fluege.isEmpty()) {
                System.out.println("Noch keine Flüge im System hinterlegt.");
            } else {
                for (models.Flug flug : fluege) {
                    System.out.printf("Flug [%d] | %s -> %s | Abflug: %s | Flugzeug: %s (Kapazität: %d)%n",
                            flug.getId(),
                            flug.getStartStadt().getName(),
                            flug.getZielStadt().getName(),
                            flug.getAbflugzeit().toString(),
                            flug.getFlugzeug().getModell(),
                            flug.getFlugzeug().getMaxSitzplaetze()
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Flüge: " + e.getMessage());
        }
    }

    private static void ladeTestdaten() {
        try {
            if (database.DatabaseManager.stadtDao.countOf() == 0) {
                System.out.println("Lade Testdaten in die Datenbank...");

                models.Stadt fra = new models.Stadt("Frankfurt", "Deutschland");
                models.Stadt bud = new models.Stadt("Budapest", "Ungarn");
                database.DatabaseManager.stadtDao.create(fra);
                database.DatabaseManager.stadtDao.create(bud);

                models.Flugzeug airbus = new models.Flugzeug("Boeing 737", 8);
                database.DatabaseManager.flugzeugDao.create(airbus);

                models.Flug flug1 = new models.Flug(fra, bud, airbus, java.time.LocalDateTime.now().plusDays(5));
                database.DatabaseManager.flugDao.create(flug1);

                System.out.println("Testdaten erfolgreich angelegt!");
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Anlegen der Testdaten.");
        }
    }

    private static void kundeRegistrieren() {
        System.out.println("\n--- NEUER KUNDE ---");
        try {
            System.out.print("Vorname: ");
            String vorname = scanner.nextLine();

            System.out.print("Nachname: ");
            String nachname = scanner.nextLine();

            System.out.print("Alter: ");
            int alter = Integer.parseInt(scanner.nextLine());

            models.Kunde neuerKunde = new models.Kunde(vorname, nachname, alter);

            database.DatabaseManager.kundeDao.create(neuerKunde);

            System.out.println("Passt: Kunde " + vorname + " " + nachname + " wurde im System registriert!");

        } catch (NumberFormatException e) {
            System.out.println("Eingabefehler: Bitte gib für das Alter eine gültige Zahl ein.");
        } catch (Exception e) {
            System.out.println("Datenbankfehler: Der Kunde konnte nicht gespeichert werden.");
            e.printStackTrace();
        }
    }

    private static void ticketBuchen() {
        System.out.println("\n--- TICKET BUCHEN ---");
        try {
            System.out.print("Bitte gib deine Kunden-ID ein: ");
            int kundenId = Integer.parseInt(scanner.nextLine());
            models.Kunde kunde = database.DatabaseManager.kundeDao.queryForId(kundenId);

            if (kunde == null) {
                System.out.println("Kunde nicht gefunden! Bitte registriere dich zuerst.");
                return;
            }

            fluegeAnzeigen();
            System.out.print("Welchen Flug möchtest du buchen (Flug-ID eingeben)? ");
            int flugId = Integer.parseInt(scanner.nextLine());
            models.Flug flug = database.DatabaseManager.flugDao.queryForId(flugId);

            if (flug == null) {
                System.out.println("Flug nicht gefunden!");
                return;
            }

            if (flug.getAbflugzeit().isBefore(java.time.LocalDateTime.now())) {
                System.out.println("Fehler: Dieser Flug liegt in der Vergangenheit und kann nicht gebucht werden.");
                return;
            }

            java.util.List<models.Ticket> flugTickets = database.DatabaseManager.ticketDao.queryBuilder()
                    .where()
                    .eq("flug_id", flug.getId())
                    .query();

            long aktiveBuchungen = flugTickets.stream().filter(t -> !"storniert".equals(t.getStatus())).count();

            if (aktiveBuchungen >= flug.getFlugzeug().getMaxSitzplaetze()) {
                System.out.println("Fehler: Dieser Flug ist leider komplett ausgebucht!");
                return;
            }

            zeigeSitzplan(flug, flugTickets);
            System.out.print("\nBitte wähle einen freien Sitzplatz (z.B. a1): ");
            String sitzplatzWahl = scanner.nextLine().toLowerCase();

            if (flugTickets.stream().anyMatch(t -> !"storniert".equals(t.getStatus()) && t.getSitzplatz().equalsIgnoreCase(sitzplatzWahl))) {
                System.out.println("Fehler: Dieser Sitzplatz ist bereits belegt!");
                return;
            }

            System.out.print("Wähle die Klasse (1 = Economy, 2 = Business): ");
            String klasseWahl = scanner.nextLine();
            String klasseName = klasseWahl.equals("2") ? "Business" : "Economy";
            double preis = klasseWahl.equals("2") ? 250.00 : 99.99;

            models.Ticket neuesTicket = new models.Ticket(
                    kunde,
                    flug,
                    sitzplatzWahl,
                    klasseName,
                    preis,
                    java.time.LocalDateTime.now(),
                    "gebucht"
            );

            database.DatabaseManager.ticketDao.create(neuesTicket);
            System.out.println("Erfolgreich! Dein Ticket (" + klasseName + ") für Sitz " + sitzplatzWahl + " wurde gebucht.");

        } catch (NumberFormatException e) {
            System.out.println("Eingabefehler: Bitte gib nur Zahlen für IDs ein.");
        } catch (Exception e) {
            System.out.println("Datenbankfehler bei der Buchung: " + e.getMessage());
        }
    }

    private static void ticketStornieren() {
        System.out.println("\n--- TICKET STORNIEREN ---");
        try {
            System.out.print("Bitte gib die ID des Tickets ein, das du stornieren möchtest: ");
            int ticketId = Integer.parseInt(scanner.nextLine());

            models.Ticket ticket = database.DatabaseManager.ticketDao.queryForId(ticketId);

            if (ticket == null) {
                System.out.println("Ticket nicht gefunden!");
                return;
            }

            if ("storniert".equals(ticket.getStatus())) {
                System.out.println("Dieses Ticket ist bereits storniert!");
                return;
            }

            java.time.LocalDateTime abflug = ticket.getFlug().getAbflugzeit();
            java.time.LocalDateTime spaetesteStornoZeit = abflug.minusHours(24);

            if (java.time.LocalDateTime.now().isAfter(spaetesteStornoZeit)) {
                System.out.println("Stornierung fehlgeschlagen: Die 24-Stunden-Frist vor Abflug ist leider abgelaufen!");
                return;
            }

            ticket.setStatus("storniert");
            ticket.setStornierungsdatum(java.time.LocalDateTime.now());

            database.DatabaseManager.ticketDao.update(ticket);

            System.out.println("Erfolgreich: Das Ticket (ID: " + ticketId + ", Platz: " + ticket.getSitzplatz() + ") wurde storniert.");
            System.out.println("Der Sitzplatz ist nun wieder für andere Kunden freigegeben.");

        } catch (NumberFormatException e) {
            System.out.println("Eingabefehler: Bitte gib nur Zahlen für die Ticket-ID ein.");
        } catch (Exception e) {
            System.out.println("Datenbankfehler bei der Stornierung: " + e.getMessage());
        }
    }

    private static void zeigeSitzplan(models.Flug flug, java.util.List<models.Ticket> gebuchteTickets) {
        System.out.println("\n--- SITZPLAN ---");
        int kapazitaet = flug.getFlugzeug().getMaxSitzplaetze();

        java.util.List<String> vergebenePlaetze = new java.util.ArrayList<>();
        for (models.Ticket ticket : gebuchteTickets) {
            if (!"storniert".equals(ticket.getStatus())) {
                vergebenePlaetze.add(ticket.getSitzplatz().toLowerCase());
            }
        }

        String[] spalten = {"a", "b", "c", "d"};
        int reihen = (int) Math.ceil((double) kapazitaet / 4.0);

        int sitzCounter = 0;

        for (int r = 1; r <= reihen; r++) {
            StringBuilder reihenAusgabe = new StringBuilder();

            for (int s = 0; s < 4; s++) {
                if (sitzCounter >= kapazitaet) break;

                String sitzName = spalten[s] + r;

                if (vergebenePlaetze.contains(sitzName)) {
                    reihenAusgabe.append("[ XX) ");
                } else {
                    reihenAusgabe.append("[ ").append(sitzName).append(") ");
                }

                if (s == 1) {
                    reihenAusgabe.append("  || GANG ||  ");
                }

                sitzCounter++;
            }
            System.out.println(reihenAusgabe.toString());
        }
        System.out.println("Legende: [a1] = Frei | [XX] = Belegt");
    }
}