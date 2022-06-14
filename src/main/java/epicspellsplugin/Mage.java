/*
 * Copyright (C) 2022 M0rica
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package epicspellsplugin;

import java.util.*;

import epicspellsplugin.exceptions.NotEnoughManaException;
import epicspellsplugin.exceptions.SpellCooldownException;
import epicspellsplugin.utils.DirectionalParticle;
import epicspellsplugin.utils.LocationUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

/**
 *
 * @author M0rica
 */
public class Mage {
    
    private Player player;
    private float mana, maxMana, manaRegeneration;
    private HashMap<String, Integer> spellCooldowns;
    private List<Location> castingPoints;
    private Vector castingPlaneNormal;
    
    public Mage(Player p){
        player = p;
        mana = 0;
        maxMana = 1000;
        manaRegeneration = 1F;
        spellCooldowns = new HashMap<>();
        castingPoints = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public float getManaRegeneration() {
        return manaRegeneration;
    }

    public boolean isCasting(){return !castingPoints.isEmpty();}

    public void setMana(float mana) {
        this.mana = mana;
    }

    public void setMaxMana(float maxMana) {
        this.maxMana = maxMana;
    }

    public void setManaRegeneration(float manaRegeneration) {
        this.manaRegeneration = manaRegeneration;
    }

    public boolean hasCooldown(String spellName){
        return spellCooldowns.containsKey(spellName);
    }
    
    public void tick(){
        if(mana < maxMana){
            mana += manaRegeneration;
        }
        if(mana > maxMana){
            mana = maxMana;
        } else if(mana < 0){
            mana = 0;
        }
        for(String spell: spellCooldowns.keySet()){
            int cooldown = spellCooldowns.get(spell)-1;
            if(cooldown <= 0){
                spellCooldowns.remove(spell);
            } else {
                spellCooldowns.put(spell, cooldown);
            }
        }
        int manaDisplay = (int) Math.floor(mana);
        float manaRatio = mana/maxMana;
        String progressbar = generateManaProgressbar(manaRatio);
        String message = String.format("%sMana: |%s%s%s| %d", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, progressbar, ChatColor.DARK_PURPLE, manaDisplay);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.STICK && item.getItemMeta().getDisplayName().equals("Magic Wand")){
            if(player.isSneaking()){
                Location point = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(3));
                // TODO calculate point between this and last point to have more points for more accuracy
                if(isCasting()) {
                    if(point.distance(castingPoints.get(castingPoints.size() - 1)) > 0.02){
                        castingPoints.add(point);
                    }
                } else {
                    castingPlaneNormal = player.getEyeLocation().getDirection().normalize().multiply(-1);
                    castingPoints.add(point);
                }
            } else if(isCasting()){
                List<Location> transformedLocations = new ArrayList<>();
                Location direction = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(3));
                for(int i = 1; i < castingPoints.size(); i++){
                    Location point = LocationUtils.projectPointOnPlane(castingPoints.get(0), castingPlaneNormal, castingPoints.get(i));
                    castingPoints.set(i, point);
                }
                double angle = Math.acos(castingPlaneNormal.dot(new Vector(0, 0, 1))/castingPlaneNormal.length());
                Vector axis = castingPlaneNormal.clone().crossProduct(new Vector(0, 0, 1)).normalize();
                for(int i = 1; i < castingPoints.size(); i++){
                    Vector temp = castingPoints.get(i).clone().toVector().subtract(castingPoints.get(0).toVector());
                    temp.rotateAroundAxis(axis, angle);
                    temp.setZ(0);
                    Location loc = direction.clone().add(temp);
                    transformedLocations.add(loc);
                    new DirectionalParticle(player.getWorld(), Particle.DRAGON_BREATH, loc, new Vector(), 0);
                }
                List<int[]> filteredTempList = new ArrayList<>();
                List<int[]> filteredList = new ArrayList<>();
                System.out.println("============");
                for(int i = 1; i < transformedLocations.size(); i++){
                    Vector temp = transformedLocations.get(i).clone().toVector().subtract(transformedLocations.get(i-1).clone().toVector()).normalize();
                    List<Double> values = Arrays.asList(temp.getX(), temp.getY());
                    int[] indexes = new int[]{(int)Math.round(values.get(0)), (int)Math.round(values.get(1))};
                    if(filteredTempList.isEmpty() || Arrays.equals(filteredTempList.get(0), indexes)){
                        if(!filteredList.isEmpty() && Arrays.equals(filteredList.get(filteredList.size()-1), indexes)){
                            continue;
                        }
                        filteredTempList.add(indexes);
                    } else {
                        if(filteredTempList.size() >= 3){
                            filteredList.add(filteredTempList.get(0));
                        }
                        filteredTempList.clear();
                    }
                    System.out.println(Arrays.toString(indexes));
                }
                if(filteredTempList.size() >= 3){
                    System.out.println("Add");
                    filteredList.add(filteredTempList.get(0));
                }
                System.out.println("============");
                filteredList.forEach(array -> System.out.println(Arrays.toString(array)));
                castingPoints.clear();
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
            }
        }
        if(isCasting()){
            for(Location point: castingPoints){
                new DirectionalParticle(player.getWorld(), Particle.ELECTRIC_SPARK, point, new Vector(), 0);
            }
        }
    }

    private String generateManaProgressbar(float manaRatio){
        String fill = "█";
        String empty = "▒";
        String progressbar = fill.repeat((int) Math.floor(manaRatio*10)) + empty.repeat((int) Math.ceil(10-manaRatio*10));
        return progressbar;
    }
    
    public void addMana(float value){
        mana += value;
    }

    public void removeMana(float value){mana -= value;}
    
    public void addCooldown(String spellName, int cooldown){
        spellCooldowns.put(spellName, cooldown);
    }

    public boolean canCastSpell(SpellWrapper spellWrapper) throws NotEnoughManaException, SpellCooldownException {
        if(mana >= spellWrapper.getManaCost()){
            if(!hasCooldown(spellWrapper.getSpellName())){
                return true;
            } else {
                throw new SpellCooldownException();
            }
        } else {
            throw new NotEnoughManaException();
        }
    }
}
