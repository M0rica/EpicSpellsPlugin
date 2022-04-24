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
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author M0rica
 */
public abstract class Spell {
    
    // player who casted the spell
    protected Player player;
    // current position and start position of spell, needed for spell manager
    protected Location position, startPosition;
    // current velocity of spell, needed to calculate potential hits
    protected Vector velocity;
    // max distance of spell from startPosition before terminated
    protected double maxDistance;
    // unique id needed to find spell in running spells, parentID needed to terminate spell if daemon set to true
    protected int id, parentID;
    // ticks since the spell got casted, if bigger than maxLifeTime spell will be terminated
    protected int lifetime, maxLifeTime;
    // spell size as the diameter in blocks
    protected float size;
    // indicates spell exists, if false spell will perform the terminate function and get deleted in the next tick
    // if daemon set to true, spell will perform terminate function if parents isAlive is false
    protected boolean alive, daemon;
    // wheter to trigger an block_hit event when colliding with fluids
    protected boolean collideWithFluids;
    // unique name of the spell
    protected String name;
    // spellmanager instance, can be used to cast new spells
    protected SpellManager spellManager;
    // the world this spell lives in
    protected World world;

    public Player getPlayer() {
        return player;
    }

    public Location getPosition() {
        return position;
    }

    public Location getStartPosition() {
        return startPosition;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public int getId() {
        return id;
    }

    public int getParentID() {
        return parentID;
    }

    public int getLifeTime() {
        return lifetime;
    }

    public int getMaxLifeTime() {
        return maxLifeTime;
    }
    
    public double getSize(){
        return size;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDaemon() {
        return daemon;
    }
    
    public boolean doFluidCollision(){
        return collideWithFluids;
    }

    public String getName() {
        return name;
    }

    public SpellManager getSpellManager() {
        return spellManager;
    }
    
    public World getWorld(){
        return world;
    }
    
    public void setAlive(boolean bool){
        alive = bool;
    }
    
}
