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
package epicspellsplugin.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DirectionalParticleCollection {

    private World world;
    private Location location;
    private Particle particle;
    private Vector velocity;
    private int amount;
    private double speed;

    public List<Location> particleLocations;
    public List<Vector> particleVelocities;

    public DirectionalParticleCollection(World world, Particle particle, Location location, Vector velocity, int amount, double speed){
        this.world = world;
        this.location = location;
        this.particle = particle;
        this.velocity = velocity;
        this.amount = amount;
        this.speed = speed;

        particleLocations = new ArrayList<>();
        particleVelocities = new ArrayList<>();
        for(int i=0; i<amount; i++){
            particleLocations.add(location.clone());
            particleVelocities.add(velocity.clone());
        }
    }

    public void spawn(){
        for(int i=0; i<particleLocations.size(); i++){
            //System.out.println("spawn");
            //System.out.println(world.toString() + particleLocations.get(i).toString() + particle.toString() + particleVelocities.get(i).toString() + String.valueOf(speed));
            new DirectionalParticle(world, particleLocations.get(i), particle, particleVelocities.get(i), speed);
        }
    }

    public void adjustVelocities(){
        for(int i=0; i<particleLocations.size(); i++){
            particleVelocities.set(i, particleLocations.get(i).clone().subtract(location).toVector());
        }
    }

    public void randomizeLocationsInDirection(){
        for(int i=0; i<particleLocations.size(); i++){
            particleLocations.set(i, LocationUtils.randomOffsetLocationInDirection(particleLocations.get(i), velocity));
        }
    }

    public void randomizeLocations(double maxDistance){
        for(int i=0; i<particleLocations.size(); i++){
            particleLocations.set(i, LocationUtils.randomOffsetLocation(particleLocations.get(i), maxDistance, maxDistance, maxDistance));
        }
    }
}
