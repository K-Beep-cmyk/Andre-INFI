package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "fluege")
public class Flug {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Stadt startStadt;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Stadt zielStadt;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Flugzeug flugzeug;

    @DatabaseField(dataType = DataType.SERIALIZABLE, canBeNull = false)
    private LocalDateTime abflugzeit;

    public Flug() {} // gleiche wie bei Stadt

    public Flug(Stadt startStadt, Stadt zielStadt, Flugzeug flugzeug, LocalDateTime abflugzeit) { //
        this.startStadt = startStadt;
        this.zielStadt = zielStadt;
        this.flugzeug = flugzeug;
        this.abflugzeit = abflugzeit;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stadt getStartStadt() {
        return startStadt;
    }

    public void setStartStadt(Stadt startStadt) {
        this.startStadt = startStadt;
    }

    public Stadt getZielStadt() {
        return zielStadt;
    }

    public void setZielStadt(Stadt zielStadt) {
        this.zielStadt = zielStadt;
    }

    public Flugzeug getFlugzeug() {
        return flugzeug;
    }

    public void setFlugzeug(Flugzeug flugzeug) {
        this.flugzeug = flugzeug;
    }

    public LocalDateTime getAbflugzeit() {
        return abflugzeit;
    }

    public void setAbflugzeit(LocalDateTime abflugzeit) {
        this.abflugzeit = abflugzeit;
    }
}