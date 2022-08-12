package epicspellsplugin.spellcasting;

import epicspellsplugin.Mage;
import epicspellsplugin.MageManager;
import epicspellsplugin.SpellManager;
import epicspellsplugin.utils.DirectionalParticle;
import epicspellsplugin.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;

public class Spellcaster {

    private Logger log;
    private SpellManager spellManager;
    private MageManager mageManager;
    private Map<Mage, SpellcastPatternRecord> castingMap;
    private Map<Mage, SpellcastPatternMapping> patternMappings;
    private Map<Mage, String> pendingSpellBinding;

    public Spellcaster(SpellManager spellManager, MageManager mageManager, Logger log){
        this.spellManager = spellManager;
        this.mageManager = mageManager;
        castingMap = new HashMap<>();
        patternMappings = new HashMap<>();
        pendingSpellBinding = new HashMap<>();
        this.log = log;
    }

    public void tick(){
        for(Mage mage: mageManager.getMages()){

            if(!patternMappings.containsKey(mage)){
                patternMappings.put(mage, SpellcastPatternMapping.DEFAULT);
            }

            Player player = mage.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            // check if player is holding a wand and not flying
            if(!player.isFlying() && item.getType() == Material.STICK && item.getItemMeta().getDisplayName().equals("Magic Wand")){
                // if player is sneaking, he is casting
                if(player.isSneaking()){
                    // create point in front of the player
                    Location point = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(3));
                    if(isCasting(mage)) {
                        SpellcastPatternRecord spellcastPatternRecord = getSpellcastPatternRecord(mage);
                        List<Location> castingPoints = spellcastPatternRecord.getCastingPoints();
                        if(castingPoints.size() > 0) {
                            Vector castingNormalPlane = spellcastPatternRecord.getNormalPlane();
                            point = LocationUtils.projectPointOnPlane(castingPoints.get(0), castingNormalPlane, point);
                            Location lastPoint = castingPoints.get(castingPoints.size() - 1);
                            // only record point if it has a certain distance to the last point
                            // to avoid adding unnecessary points if the player is not moving
                            if (point.distance(lastPoint) > 0.1) {
                                Location between = lastPoint.clone().add(point).multiply(0.5);
                                spellcastPatternRecord.addCastingPoint(between);
                                spellcastPatternRecord.addCastingPoint(point);
                            }
                            // show pattern to the player by drawing particles
                            for (Location loc : castingPoints) {
                                DirectionalParticle.spawn(Particle.ELECTRIC_SPARK, loc, new Vector(), 0);
                            }
                        } else {
                            spellcastPatternRecord.addCastingPoint(point);
                        }
                    // player just started casting, so start new pattern recording
                    } else {
                        Vector normalizedPlane = player.getEyeLocation().getDirection().normalize().multiply(-1);
                        SpellcastPatternRecord spellcastPatternRecord = new SpellcastPatternRecord(normalizedPlane);
                        castingMap.put(mage, spellcastPatternRecord);
                    }
                // if the player recorded a pattern and stopped sneaking, he is done with casting
                // and the pattern has to be analysed
                } else if(isCasting(mage)){
                    SpellcastPatternRecord spellcastPatternRecord = getSpellcastPatternRecord(mage);
                    List<Location> castingPoints = spellcastPatternRecord.getCastingPoints();
                    Vector castingNormalPlane = spellcastPatternRecord.getNormalPlane();
                    List<Location> transformedLocations = new ArrayList<>();
                    double angle = Math.acos(castingNormalPlane.dot(new Vector(0, 0, 1))/castingNormalPlane.length());
                    boolean mirror = false;
                    if(angle > Math.PI/2){
                        angle -= Math.PI;
                        mirror = true;
                    }
                    Vector axis = castingNormalPlane.clone().crossProduct(new Vector(0, 0, 1)).normalize();
                    for(int i = 1; i < castingPoints.size(); i++){
                        Vector temp = castingPoints.get(i).clone().toVector().subtract(castingPoints.get(0).toVector());
                        temp.rotateAroundAxis(axis, angle);
                        temp.setZ(0);
                        if(mirror){
                            temp.rotateAroundY(Math.PI);
                        }
                        Location loc = castingPoints.get(0).clone().add(temp);
                        transformedLocations.add(loc);
                    }
                    List<int[]> filteredList = pointsToFilteredVectors(transformedLocations, 4);
                    List<Integer> lines = filteredVectorsToPattern(filteredList);
                    log.info(String.format("Recorded pattern %s", Arrays.toString(lines.toArray())));
                    // check if player is binding a spell to a custom pattern
                    if(isSpellBinding(mage)){
                        completeSpellBinding(mage, lines);
                    // else try to map the drawn pattern to a spell
                    } else {
                        SpellcastPatternMapping patternMapping = patternMappings.get(mage);
                        String spellName = patternMapping.mapPattern(lines);
                        if (spellName != null) {
                            log.info(String.format("Triggering spell %s", spellName));
                            spellManager.castSpell(spellName, player);
                        }
                    }
                    castingMap.remove(mage);
                }
            } else {
                castingMap.remove(mage);
            }
        }
    }

    /**
     * Turn a list of transformed points into vectors between each following point
     * and filter the list to only contain vectors that occur >= minSequenceSize successively.
     * @param points the list of transformed points
     * @param minSequenceSize the minimum number of times the same vector has to occur successively for the system to recognize it as a line (to be included in the return list)
     * @return a list of int[2] with {x, y} vector direction having values of -1, 0 or 1
     */
    private List<int[]> pointsToFilteredVectors(List<Location> points, int minSequenceSize){
    List<int[]> filteredTempList = new ArrayList<>();
    List<int[]> filteredList = new ArrayList<>();
    for(int i = 1; i < points.size(); i++){
        // create vector between points
        Vector temp = points.get(i).clone().toVector().subtract(points.get(i-1).clone().toVector()).normalize();
        List<Double> values = Arrays.asList(temp.getX(), temp.getY());
        int[] indexes = new int[2];
        // round values to be either -1, 0 or 1
        for(int j = 0; j < indexes.length; j++){
            double value = values.get(j);
            double index;
            // use adjusted rounding for better diagonal line detection
            if(value > 0){
                index = Math.floor(value+0.7);
            } else {
                index = Math.ceil(value-0.7);
            }
            indexes[j] = (int) Math.round(index);
        }
        // check if the list of vectors that gets analysed is empty or the current vector equals the last vector
        if(filteredTempList.isEmpty() || Arrays.equals(filteredTempList.get(0), indexes)){
            // if the vector is already in the final list, ignore it to not add it twice
            if(!filteredList.isEmpty() && Arrays.equals(filteredList.get(filteredList.size()-1), indexes)){
                continue;
            }
            filteredTempList.add(indexes);
        } else {
            // if the vector occurs >= minSequenceSize times, add it to the final list
            if(filteredTempList.size() >= minSequenceSize){
                filteredList.add(filteredTempList.get(0));
            }
            filteredTempList.clear();
            filteredTempList.add(indexes);
        }
    }
    // check again at the end to possibly add last line which would otherwise be ignored
    if(filteredTempList.size() >= minSequenceSize){
        filteredList.add(filteredTempList.get(0));
    }
    return filteredList;
}

    /**
     * Turn a list of vectors filtered with pointsToFilteredVectors into a list of indexes of lines for easier pattern comparison
     * @param filteredList a list of int[] with values -1, 0 or 1
     * @return a list with values between 0 and 8
     */
    private List<Integer> filteredVectorsToPattern(List<int[]> filteredList){
    List<int[]> lineTypes = Arrays.asList(
            new int[]{1, 1}, new int[]{1, 0}, new int[]{1, -1},
            new int[]{0, 1}, new int[]{0, 0}, new int[]{0, -1},
            new int[]{-1, 1}, new int[]{-1, 0}, new int[]{-1, -1}
    );
    List<Integer> lines = new ArrayList<>();
    for(int[] line: filteredList){
        for(int i = 0; i < lineTypes.size(); i++){
            if(Arrays.equals(lineTypes.get(i), line)){
                lines.add(i);
            }
        }
    }
    return lines;
}

    /**
     * Function to start the process of binding a spell to a custom pattern.
     * This tells the system to catch the next pattern the player draws and bind it to the spell instead of trying to cast a spell.
     * @param mage the mage that wants to bind a spell
     * @param spellName the spell the mage wants to bind to a custom pattern
     */
    public void setupSpellBinding(Mage mage, String spellName){
        Player player = mage.getPlayer();
        if(spellManager.getSpellNames().contains(spellName)){
            if(isSpellBinding(mage)){
                player.sendMessage(String.format("You are already binding a spell (%s)!", pendingSpellBinding.get(mage)));
            } else {
                // tell the system to catch the next pattern drawn by this player
                pendingSpellBinding.put(mage, spellName);
                player.sendMessage(String.format("Please draw the shape you want to bind %s to using your magic wand", spellName));
            }
        } else {
            player.sendMessage("No such spell");
        }
    }

    /**
     * Function to bind the spell to a custom pattern
     * @param mage the mage that completed the spell binding
     * @param pattern the custom pattern
     */
    private void completeSpellBinding(Mage mage, List<Integer> pattern){
        Player player = mage.getPlayer();
        SpellcastPatternMapping patternMapping = patternMappings.get(mage);
        // check if a spell is already mapped to this pattern and if so, override it
        String spellName = patternMapping.mapPattern(pattern);
        if(spellName != null){
            patternMapping.unbindPattern(pattern);
            player.sendMessage(String.format("You already have a spell mapped to this pattern (%s), this binding will be overwritten", spellName));
        }
        spellName = pendingSpellBinding.get(mage);
        patternMapping.bindPattern(pattern, spellName);
        pendingSpellBinding.remove(mage);
        player.sendMessage(String.format("Successfully bound spell %s to the pattern you just drew!", spellName));
    }

    private boolean isSpellBinding(Mage mage){
        return pendingSpellBinding.containsKey(mage);
    }

    public SpellcastPatternRecord getSpellcastPatternRecord(Mage mage){
        return castingMap.get(mage);
    }

    public boolean isCasting(Mage mage){
        return castingMap.containsKey(mage);
    }

}
