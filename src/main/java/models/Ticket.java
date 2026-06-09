package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "tickets")
public class Ticket {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Kunde kunde;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Flug flug;

    @DatabaseField(canBeNull = false)
    private String sitzplatz;

    @DatabaseField(canBeNull = false)
    private String klasse;

    @DatabaseField(canBeNull = false)
    private double preis;

    @DatabaseField(dataType = DataType.SERIALIZABLE, canBeNull = false)
    private LocalDateTime kaufdatum;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private LocalDateTime stornierungsdatum;

    @DatabaseField(canBeNull = false)
    private String status;

    public Ticket() {}  // gleiche wie bei Stadt

    public Ticket(Kunde kunde, Flug flug, String sitzplatz, String klasse, double preis, LocalDateTime kaufdatum, String status) {
        this.kunde = kunde;
        this.flug = flug;
        this.sitzplatz = sitzplatz;
        this.klasse = klasse;
        this.preis = preis;
        this.kaufdatum = kaufdatum;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Flug getFlug() {
        return flug;
    }

    public void setFlug(Flug flug) {
        this.flug = flug;
    }

    public String getSitzplatz() {
        return sitzplatz;
    }

    public void setSitzplatz(String sitzplatz) {
        this.sitzplatz = sitzplatz;
    }

    public String getKlasse() {
        return klasse;
    }

    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }

    public LocalDateTime getKaufdatum() {
        return kaufdatum;
    }

    public void setKaufdatum(LocalDateTime kaufdatum) {
        this.kaufdatum = kaufdatum;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public LocalDateTime getStornierungsdatum() {
        return stornierungsdatum;
    }

    public void setStornierungsdatum(LocalDateTime stornierungsdatum) {
        this.stornierungsdatum = stornierungsdatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
