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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author M0rica
 */
public class BaseSpell extends Spell implements SpellInteraction{

    @Override
    public void init(SpellManager spellManager, Location location, Player player, int id, int pID, String name){
        this.world = location.getWorld();
        this.spellManager = spellManager;
        this.player = player;
        this.id = id;
        parentID = pID;
        this.name = name;
        velocity = new Vector(0, 0, 0);
        startPosition = location;
        position = startPosition.clone();
        lifetime = 0;
    }

    public void init(SpellManager spellManager, Player player, int id, int pID, String name){
        Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
        init(spellManager, loc, player, id, pID, name);
    }

    @Override
    public void tick() {
        lifetime++;
    }

    @Override
    public void on_entity_hit(Location location, Entity entity) {

    }

    @Override
    public void on_player_hit(Location location, Player player) {

    }

    @Override
    public void on_block_hit(Location location, Block block, int wallThickness) {

    }

    @Override
    public void on_out_of_range() {

    }

    @Override
    public void on_lifetime_end() {

    }

    @Override
    public void terminate(Location location) {

    }

    @Override
    public void kill() {

    }
    
}
