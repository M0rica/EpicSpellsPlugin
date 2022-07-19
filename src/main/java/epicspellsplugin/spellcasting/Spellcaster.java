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
            if(item.getType() == Material.STICK && item.getItemMeta().getDisplayName().equals("Magic Wand")){
                if(player.isSneaking()){
                    Location point = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(3));
                    if(isCasting(mage)) {
                        SpellcastPatternRecord spellcastPatternRecord = getSpellcastPatternRecord(mage);
                        List<Location> castingPoints = spellcastPatternRecord.getCastingPoints();
                        if(castingPoints.size() > 0) {
                            Location lastPoint = castingPoints.get(castingPoints.size() - 1);
                            if (point.distance(lastPoint) > 0.1) {
                                Location between = lastPoint.clone().add(point).multiply(0.5);
                                spellcastPatternRecord.addCastingPoint(between);
                                spellcastPatternRecord.addCastingPoint(point);
                            }
                            for (Location loc : castingPoints) {
                                new DirectionalParticle(player.getWorld(), Particle.ELECTRIC_SPARK, loc, new Vector(), 0);
                            }
                        } else {
                            spellcastPatternRecord.addCastingPoint(point);
                        }
                    } else {
                        Vector normalizedPlane = player.getEyeLocation().getDirection().normalize().multiply(-1);
                        SpellcastPatternRecord spellcastPatternRecord = new SpellcastPatternRecord(normalizedPlane);
                        castingMap.put(mage, spellcastPatternRecord);
                    }
                } else if(isCasting(mage)){
                    SpellcastPatternRecord spellcastPatternRecord = getSpellcastPatternRecord(mage);
                    List<Location> castingPoints = spellcastPatternRecord.getCastingPoints();
                    Vector castingNormalPlane = spellcastPatternRecord.getNormalPlane();
                    List<Location> transformedLocations = new ArrayList<>();
                    Location direction = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(2));
                    for(int i = 1; i < castingPoints.size(); i++){
                        Location point = LocationUtils.projectPointOnPlane(castingPoints.get(0), castingNormalPlane, castingPoints.get(i));
                        castingPoints.set(i, point);
                    }
                    double angle = Math.acos(castingNormalPlane.dot(new Vector(0, 0, 1))/castingNormalPlane.length());
                    boolean mirror = false;
                    if(angle > Math.PI/2){
                        angle -= Math.PI;
                        mirror = true;
                    }
                    System.out.println("Angle " + angle);
                    Vector axis = castingNormalPlane.clone().crossProduct(new Vector(0, 0, 1)).normalize();
                    for(int i = 1; i < castingPoints.size(); i++){
                        Vector temp = castingPoints.get(i).clone().toVector().subtract(castingPoints.get(0).toVector());
                        temp.rotateAroundAxis(axis, angle);
                        temp.setZ(0);
                        if(mirror){
                            temp.rotateAroundY(Math.PI);
                        }
                        Location loc = direction.clone().add(temp);
                        transformedLocations.add(loc);
                        //new DirectionalParticle(player.getWorld(), Particle.DRAGON_BREATH, loc, new Vector(), 0);
                    }
                    int minSequenceSize = 4;
                    List<int[]> filteredTempList = new ArrayList<>();
                    List<int[]> filteredList = new ArrayList<>();
                    System.out.println("============");
                    for(int i = 1; i < transformedLocations.size(); i++){
                        Vector temp = transformedLocations.get(i).clone().toVector().subtract(transformedLocations.get(i-1).clone().toVector()).normalize();
                        List<Double> values = Arrays.asList(temp.getX(), temp.getY());
                        int[] indexes = new int[2];
                        for(int j = 0; j < indexes.length; j++){
                            double value = values.get(j);
                            double index;
                            if(value > 0){
                                index = Math.floor(value+0.7);
                            } else {
                                index = Math.ceil(value-0.7);
                            }
                            indexes[j] = (int) Math.round(index);
                        }
                        if(filteredTempList.isEmpty() || Arrays.equals(filteredTempList.get(0), indexes)){
                            if(!filteredList.isEmpty() && Arrays.equals(filteredList.get(filteredList.size()-1), indexes)){
                                continue;
                            }
                            filteredTempList.add(indexes);
                        } else {
                            if(filteredTempList.size() >= minSequenceSize){
                                filteredList.add(filteredTempList.get(0));
                            }
                            filteredTempList.clear();
                            filteredTempList.add(indexes);
                        }
                    }
                    if(filteredTempList.size() >= minSequenceSize){
                        filteredList.add(filteredTempList.get(0));
                    }
                    filteredList.forEach(array -> System.out.println(Arrays.toString(array)));
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
                    System.out.println(Arrays.toString(lines.toArray()));
                    if(isSpellBinding(mage)){
                        completeSpellBinding(mage, lines);
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
            }
        }
    }

    public void setupSpellBinding(Mage mage, String spellName){
        Player player = mage.getPlayer();
        if(spellManager.getSpellNames().contains(spellName)){
            if(isSpellBinding(mage)){
                player.sendMessage(String.format("You are already binding a spell (%s)!", pendingSpellBinding.get(mage)));
            } else {
                pendingSpellBinding.put(mage, spellName);
                player.sendMessage(String.format("Please draw the shape you want to bind %s to using your magic wand", spellName));
            }
        } else {
            player.sendMessage("No such spell");
        }
    }

    private void completeSpellBinding(Mage mage, List<Integer> pattern){
        Player player = mage.getPlayer();
        SpellcastPatternMapping patternMapping = patternMappings.get(mage);
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
