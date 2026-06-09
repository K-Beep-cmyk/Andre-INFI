package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "flugzeuge")
public class Flugzeug {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String modell;

    @DatabaseField(canBeNull = false)
    private int maxSitzplaetze;

    public Flugzeug() {}

    public Flugzeug(String modell, int maxSitzplaetze) {
        this.modell = modell;
        this.maxSitzplaetze = maxSitzplaetze;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public int getMaxSitzplaetze() {
        return maxSitzplaetze;
    }

    public void setMaxSitzplaetze(int maxSitzplaetze) {
        this.maxSitzplaetze = maxSitzplaetze;
    }
}
