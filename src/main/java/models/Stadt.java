package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "staedte")
public class Stadt {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String land;

    public Stadt() {} // ORMlite braucht einen leeren Konstruktor weil zu erst
                      // dort erst eine Hülle erstellt wird dann werden erst die Werte reingeschrieben

    public Stadt(String name, String land) {
        this.name = name;
        this.land = land;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }
}
