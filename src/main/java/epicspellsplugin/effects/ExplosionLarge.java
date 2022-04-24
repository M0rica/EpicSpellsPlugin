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
package epicspellsplugin.effects;

import epicspellsplugin.utils.DirectionalParticleCollection;
import epicspellsplugin.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ExplosionLarge {

    public ExplosionLarge(World world, Location location, boolean spawnFire){
        int size = 2;
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        Vector velocity = new Vector();
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, location, velocity, 100, 0.5F));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, location, velocity, 50, 0.5F));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, location, velocity, 80, 0.5F));
        particles.add(new DirectionalParticleCollection(world, Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 80, 0.2F));

        Block block = location.getBlock();
        Location blocklocation = block.getLocation();
        List<Block> removedBlocks = new ArrayList<>();
        List<Material> removedBlocksMaterials = new ArrayList<>();
        for(int i=0; i<1000; i++) {
            Vector vel = new Vector(Utils.randomFloat(-1, 1), Utils.randomFloat(-1, 1), Utils.randomFloat(-1, 1)).normalize();
            //System.out.println(vel);
            RayTraceResult result = world.rayTraceBlocks(blocklocation, vel, 6, FluidCollisionMode.NEVER);
            if(result != null){
                Block hitBlock = result.getHitBlock();
                Material material = hitBlock.getType();
                if(material != Material.AIR && material != Material.CAVE_AIR && material != Material.VOID_AIR){
                    float blastResistance = material.getBlastResistance();
                    if(blastResistance < 100){
                        boolean removeBlock = Utils.randomFloat(0, 1) > blastResistance/100;
                        if(removeBlock){
                            hitBlock.setType(Material.AIR);
                            removedBlocks.add(hitBlock);
                            removedBlocksMaterials.add(material);
                        }
                    }
                }
            }
        }

        for(int i=0; i<removedBlocks.size(); i++){
            Block temp = removedBlocks.get(i);
            if(Utils.randomFloat(0, 1) < 0.3){
                Location l = temp.getLocation().add(0, 1, 0);
                FallingBlock fallingBlock = world.spawnFallingBlock(l, removedBlocksMaterials.get(i).createBlockData());
                fallingBlock.setVelocity(l.subtract(location).toVector().normalize().setY(Utils.randomFloat(0.1F, 1)).multiply(2));
            }
        }

        for(DirectionalParticleCollection temp: particles){
            temp.randomizeLocations(size);
            temp.adjustVelocities();
            temp.spawn();
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10, 0);
    }
}
