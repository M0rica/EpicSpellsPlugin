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

/**
 *
 * @author M0rica
 */
public interface SpellInteraction {
    
    public void init(World world, Player player, int id, int pID, String name);
    public void tick();
    public void on_entity_hit(Location location, Entity entity);
    public void on_player_hit(Location location, Player player);
    public void on_block_hit(Location location, Block block, int wallThickness);
    public void on_out_of_range();
    public void on_lifetime_end();
    public void terminate(Location location);
    public void kill();
    
}
