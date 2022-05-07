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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    public Mage getMage(String name){
        for(Player player: players.keySet()){
            if(player.getDisplayName().equals(name)){
                return players.get(player);
            }
        }
        return null;
    }

    public List<Mage> getMages(){
        return new ArrayList<>(players.values());
    }
    
    public void tick(){
        for(Mage mage: players.values()){
            mage.tick();
        }
    }

    public void castSpell(Mage mage, SpellWrapper spellWrapper){
        mage.removeMana(spellWrapper.getManaCost());
        mage.addCooldown(spellWrapper.getSpellName(), spellWrapper.getCooldown());
    }
    
    public void addPlayer(Player player){
        if(!players.keySet().contains(player)){
            players.put(player, new Mage(player));
        }
    }
    
    public void removePlayer(Player player){
        players.remove(player);
    }
}
