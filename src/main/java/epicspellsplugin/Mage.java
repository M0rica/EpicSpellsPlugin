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

import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 *
 * @author M0rica
 */
public class Mage {
    
    private Player player;
    private int mana;
    private HashMap<String, Integer> spellCooldowns;
    
    public Mage(Player p){
        player = p;
        mana = 1000;
        spellCooldowns = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public int getMana() {
        return mana;
    }
    
    public boolean hasCooldown(String spellName){
        return spellCooldowns.containsKey(spellName);
    }
    
    public void tick(){
        if(mana < 1000){
            mana++;
        }
        for(String spell: spellCooldowns.keySet()){
            int cooldown = spellCooldowns.get(spell)-1;
            if(cooldown <= 0){
                spellCooldowns.remove(spell);
            } else {
                spellCooldowns.put(spell, cooldown);
            }
        }
    }
    
    public void updateMana(int value){
        mana += value;
    }
    
    public void addCooldown(String spellName, int cooldown){
        spellCooldowns.put(spellName, cooldown);
    }
}
