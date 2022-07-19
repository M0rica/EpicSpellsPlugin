package epicspellsplugin.spellcasting;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpellcastPatternRecord {

    private Vector normalPlane;
    private List<Location> castingPoints;

    public SpellcastPatternRecord(Vector normalPlane){
        this.normalPlane = normalPlane;
        castingPoints = new ArrayList<>();
    }

    public Vector getNormalPlane() {
        return normalPlane;
    }

    public List<Location> getCastingPoints() {
        return castingPoints;
    }

    public void addCastingPoint(Location point){
        castingPoints.add(point);
    }
}
