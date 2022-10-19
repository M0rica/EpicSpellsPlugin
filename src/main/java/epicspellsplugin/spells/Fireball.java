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

import epicspellsplugin.BaseSpell;

import java.util.ArrayList;
import java.util.List;

import epicspellsplugin.SpellManager;
import epicspellsplugin.effects.ExplosionMedium;
import epicspellsplugin.utils.DirectionalParticleCollection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author M0rica
 */
public class Fireball extends BaseSpell{
    
    private int wallThickness;
    private Material hitMaterial;
    
    @Override
    public void init(SpellManager spellManager, Location location, Player player, int id, int parentID, String name){
        super.init(spellManager, location, player, id, parentID, name);
        collideWithFluids = true;
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
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, position, velocity, 20, 0.1));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, position, velocity, 15, 0.1));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, position, velocity, 16, 0.1));
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
        Material material = block.getType();
        if(material.equals(Material.BEDROCK)){
            this.wallThickness = 100;
        }
        System.out.println(wallThickness);
        hitMaterial = material;
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
        if (hitMaterial != null && hitMaterial.equals(Material.WATER)){
            DirectionalParticleCollection temp = new DirectionalParticleCollection(world, Particle.CAMPFIRE_COSY_SMOKE, location, new Vector(0,0.1,0), 20, 0.3);
            temp.randomizeLocations(2, 0.5, 2);
            temp.spawn();
            world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 5, 0);
        } else {
            new ExplosionMedium(world, location, true);
        }
    }

    @Override
    public void kill() {
        
    }
    
}
