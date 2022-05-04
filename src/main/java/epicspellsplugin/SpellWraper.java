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

import epicspellsplugin.exceptions.NotEnoughManaException;
import epicspellsplugin.exceptions.SpellCooldownException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author M0rica
 */
public class SpellWraper {
    
    // cost of mana to cast this spell
    private int manaCost;
    // cooldown in ticks between casting another of this spell
    private int cooldown;
    // name of the spell this metadata corresponds to
    private String name;
    // actual Spell this wraper links to
    private BaseSpell spell;
    
    public SpellWraper(String name, BaseSpell spell, int manaCost, int cooldown){
        this.name = name;
        this.spell = spell;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }

    public int getManaCost(){
        return manaCost;
    }

    public int getCooldown(){
        return cooldown;
    }
    
    public String getSpellName(){
        return name;
    }
    
    public BaseSpell getSpell(){
        try{ 
            return spell.getClass().newInstance();
        } catch(IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }
    
}
