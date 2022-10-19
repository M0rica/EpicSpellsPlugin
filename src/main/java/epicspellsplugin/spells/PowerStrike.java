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
package epicspellsplugin.spells;

import epicspellsplugin.SpellManager;
import epicspellsplugin.effects.ExplosionLarge;
import epicspellsplugin.effects.ExplosionMedium;
import epicspellsplugin.BaseSpell;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author M0rica
 */
public class PowerStrike extends BaseSpell{
    
    private List<Location> explosionLocations;
    private boolean move;
    private int ticksSinceHit;
    private int explosionsPerTick;
    int power;
    
    @Override
    public void init(SpellManager spellManager, Player player, int id, int parentID, String name){
        super.init(spellManager, player, id, parentID, name);
        maxDistance = 100;
        maxLifeTime = 300;
        velocity = player.getLocation().getDirection().multiply(3);
        explosionLocations = new ArrayList<>();
        move = true;
        ticksSinceHit = 0;
        explosionsPerTick = 1;
        size = 1;
        power = 2000;
    }
    
    @Override
    public void tick() {
        super.tick();
        if(move){
            double divide = 1;
            for(int i=0; i<(1/divide); i++){
                position = position.add(velocity.clone().multiply(divide));
                world.spawnParticle(Particle.SONIC_BOOM, position, 10, 0.2, 0.2, 0.2, 0);
            }
            explosionLocations.add(position.clone());
        }
            
        if(lifetime > 12){
            int explosions = 0;
            while(explosionLocations.size() > 0 && explosions < explosionsPerTick){
                Location explosionLocation = explosionLocations.remove(0);
                //world.createExplosion(explosionLocation, 3);
                new ExplosionMedium(world, explosionLocation, true);
                explosions++;
            }
        }
        if(!move && explosionLocations.isEmpty()){
            alive = false;
        }
        //System.out.println(explosionLocations.toString());
        /*if(explosionLocations.size() > 0){
            int explosions = 0;
            while(explosionLocations.size() > 0 && explosions < explosionsPerTick){
                Location explosionLocation = explosionLocations.remove(0);
                world.createExplosion(explosionLocation, 3);
                explosions++;
            }
        } else {
            alive = false;
        }*/
    }

    @Override
    public void on_entity_hit(Location location, Entity entity) {
        
    }

    @Override
    public void on_player_hit(Location location, Player player) {
        
    }
    
    @Override
    public void on_block_hit(Location location, Block block, int wallThickness) {
        if(move){
            Material material = block.getType();
            if(material.equals(Material.BEDROCK)){
                move = false;  
            } else {
                power -= material.getBlastResistance();
                if(power < 0){
                    move = false;
                }
            }
            //explosionLocations.add(location);
        }
    }

    @Override
    public void on_out_of_range() {
        move = false;
    }

    @Override
    public void on_lifetime_end() {
        
    }
    
    @Override
    public void terminate(Location location){
        new ExplosionLarge(world, location, true);
    }
}
