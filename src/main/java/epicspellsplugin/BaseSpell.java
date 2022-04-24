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
import org.bukkit.World;
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
    public void init(World world, Player player, int id, int pID, String name){
        this.world = world;
        this.player = player;
        this.id = id;
        parentID = pID;
        this.name = name;
        velocity = new Vector(0, 0, 0);
        startPosition = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
        position = startPosition.clone();
        lifetime = 0;
    }

    @Override
    public void tick() {
        lifetime++;
    }

    @Override
    public void on_entity_hit(Location location, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void on_player_hit(Location location, Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void on_block_hit(Location location, Block block, int wallThickness) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void on_out_of_range() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void on_lifetime_end() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void terminate(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void kill() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
