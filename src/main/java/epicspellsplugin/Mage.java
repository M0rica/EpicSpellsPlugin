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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author M0rica
 */
public class Mage {
    
    private Player player;
    private float mana, maxMana, manaRegeneration;
    private HashMap<String, Integer> spellCooldowns;
    
    public Mage(Player p){
        player = p;
        mana = 0;
        maxMana = 1000;
        manaRegeneration = 1F;
        spellCooldowns = new HashMap<>();
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
            if(mana > maxMana){
                mana = maxMana;
            }
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
    }

    private String generateManaProgressbar(float manaRatio){
        String fill = "█";
        String empty = "▒";
        String progressbar = fill.repeat((int) Math.floor(manaRatio*10)) + empty.repeat((int) Math.ceil(10-manaRatio*10));
        return progressbar;
    }
    
    public void updateMana(int value){
        mana += value;
    }
    
    public void addCooldown(String spellName, int cooldown){
        spellCooldowns.put(spellName, cooldown);
    }
}
