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
public class MageManager {
    
    private HashMap<Player, Mage> players;
    
    public MageManager(){
        players = new HashMap<>();
    }
    
    public Mage getMage(Player player){
        return players.get(player);
    }
    
    public void tick(){
        for(Mage mage: players.values()){
            mage.tick();
        }
    }

    public void castSpell(Mage mage, SpellWrapper spellWrapper){
        removeMana(mage, spellWrapper.getManaCost());
        addCooldown(mage, spellWrapper.getSpellName(), spellWrapper.getCooldown());
    }
    
    public void addPlayer(Player player){
        if(!players.keySet().contains(player)){
            players.put(player, new Mage(player));
        }
    }
    
    public void removePlayer(Player player){
        players.remove(player);
    }

    public void removeMana(Mage mage, int amount){
        mage.updateMana(-amount);
    }
    
    public void addCooldown(Mage mage, String spellName, int cooldown){
        mage.addCooldown(spellName, cooldown);
    }
}
