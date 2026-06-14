package models;

import database.DatabaseManager;

public class TestdatenLoader {

    // Diese Methode ist 'public static', damit wir sie aus der Main-Klasse ganz einfach aufrufen können
    public static void lade() {
        try {
            // Prüft, ob schon Städte existieren, damit wir die Testdaten nicht doppelt anlegen
            if (DatabaseManager.stadtDao.countOf() == 0) {
                System.out.println("Lade erweiterte Testdaten in die Datenbank...");

                // --- 1. STÄDTE ERSTELLEN ---
                models.Stadt fra = new models.Stadt("Frankfurt am Main", "Deutschland", "EDDF");
                models.Stadt bud = new models.Stadt("Budapest Liszt Ferenc", "Ungarn", "LHBP");
                models.Stadt vie = new models.Stadt("Wien-Schwechat", "Österreich", "LOWW");
                models.Stadt par = new models.Stadt("Paris-Charles-de-Gaulle", "Frankreich", "LFPG");

                DatabaseManager.stadtDao.create(fra);
                DatabaseManager.stadtDao.create(bud);
                DatabaseManager.stadtDao.create(vie);
                DatabaseManager.stadtDao.create(par);

                // --- 2. FLUGZEUGE ERSTELLEN ---
                models.Flugzeug boeingSmall = new models.Flugzeug("Boeing 737-8", 162);
                models.Flugzeug airbusLarge = new models.Flugzeug("Airbus A320-200", 150);
                models.Flugzeug privatJet = new models.Flugzeug("Airbus A330-300", 260);

                DatabaseManager.flugzeugDao.create(boeingSmall);
                DatabaseManager.flugzeugDao.create(airbusLarge);
                DatabaseManager.flugzeugDao.create(privatJet);

                // --- 3. FLÜGE ERSTELLEN ---
                models.Flug flug1 = new models.Flug(fra, bud, boeingSmall, java.time.LocalDateTime.now().plusDays(5));
                models.Flug flug2 = new models.Flug(vie, par, airbusLarge, java.time.LocalDateTime.now().plusDays(2));
                models.Flug flug3 = new models.Flug(fra, vie, privatJet, java.time.LocalDateTime.now().plusDays(1));
                // Flug in der Vergangenheit (für den Test der Validierung)
                models.Flug flug4 = new models.Flug(par, fra, airbusLarge, java.time.LocalDateTime.now().minusDays(3));

                DatabaseManager.flugDao.create(flug1);
                DatabaseManager.flugDao.create(flug2);
                DatabaseManager.flugDao.create(flug3);
                DatabaseManager.flugDao.create(flug4);

                System.out.println("Erweiterte Testdaten erfolgreich angelegt!");
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Anlegen der Testdaten: " + e.getMessage());
        }
    }
}