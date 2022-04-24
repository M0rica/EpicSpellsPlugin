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

import effects.ExplosionLarge;
import epicspellsplugin.BaseSpell;
import epicspellsplugin.utils.DirectionalParticle;
import java.util.ArrayList;
import java.util.List;

import epicspellsplugin.utils.DirectionalParticleCollection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author M0rica
 */
public class Fireball extends BaseSpell{
    
    private int wallThickness;
    
    @Override
    public void init(World world, Player player, int id, int parentID, String name){
        super.init(world, player, id, parentID, name);
        maxDistance = 50;
        maxLifeTime = 300;
        velocity = player.getLocation().getDirection();
        wallThickness = 0;
        size = 1;
    }
    
    @Override
    public void tick() {
        super.tick();
        position = position.add(velocity);
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, position, velocity, 20, 0.1F));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, position, velocity, 15, 0.1F));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, position, velocity, 16, 0.1F));
        //world.spawnParticle(Particle.SMALL_FLAME, position, 20, size, size, size, 0.1);
        //world.spawnParticle(Particle.SMOKE_LARGE, position, 15, size, size, size, 0.1);
        //world.spawnParticle(Particle.SMOKE_NORMAL, position, 16, size, size, size, 0.1);
        for(DirectionalParticleCollection temp: particles){
            temp.randomizeLocations(1);
            temp.adjustVelocities();
            temp.spawn();
        }
    }

    @Override
    public void on_entity_hit(Location location, Entity entity) {
        alive = false;
    }

    @Override
    public void on_player_hit(Location location, Player player) {
        alive = false;
    }

    @Override
    public void on_block_hit(Location location, Block block, int wallThickness) {
        this.wallThickness = wallThickness;
        if(block.getType().equals(Material.BEDROCK)){
            this.wallThickness = 100;
        }
        System.out.println(wallThickness);
        alive = false;
    }

    @Override
    public void on_out_of_range() {
        alive = false;
    }

    @Override
    public void on_lifetime_end() {
        
    }

    @Override
    public void terminate(Location location) {
        /*world.createExplosion(location, 8);
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, location, velocity, 100, (float) 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, location, velocity, 50, (float) 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, location, velocity, 80, (float) 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 80, (float) 0.2));
        for(DirectionalParticleCollection temp: particles){
            if(wallThickness <= 2){
                temp.randomizeLocationsInDirection();
            } else {
                temp.randomizeLocations(size);
            }
            temp.adjustVelocities();
            temp.spawn();*/
        new ExplosionLarge(world, location, true);
    }

    @Override
    public void kill() {
        
    }
    
}
