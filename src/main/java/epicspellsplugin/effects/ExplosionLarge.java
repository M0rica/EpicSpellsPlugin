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
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        Vector velocity = new Vector();
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, location, velocity, 200, 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, location, velocity, 100, 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, location, velocity, 160, 0.5));
        particles.add(new DirectionalParticleCollection(world, Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 160, 0.2));

        new RealisticExplosion(world, location, 10, particles, 0.2, spawnFire);
    }
}
