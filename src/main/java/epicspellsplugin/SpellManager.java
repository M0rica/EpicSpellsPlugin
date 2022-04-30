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

import epicspellsplugin.exceptions.NotEnoughManaException;
import epicspellsplugin.exceptions.SpellCooldownException;
import epicspellsplugin.spells.ArrowStorm;
import epicspellsplugin.spells.Explosion;
import epicspellsplugin.spells.Fireball;
import epicspellsplugin.spells.PowerStrike;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 *
 * @author M0rica
 */
public class SpellManager {
    
    private Logger log;
    private MageManager mageManager;
    
    private HashMap<String, SpellWraper> spells;
    private HashMap<Integer, BaseSpell> activeSpells;
    private int activeSpellID;
    
    public SpellManager(Logger log, MageManager mageManager){
        this.log = log;
        this.mageManager = mageManager;
        activeSpells = new HashMap<>();
        spells = new HashMap<>();
        activeSpellID = 0;
    }
    
    public void setup(){
        SpellWraper wraper = new SpellWraper("Fireball", new Fireball(), 50, 100);
        registerSpell(wraper);
        wraper = new SpellWraper("PowerStrike", new PowerStrike(), 150, 150);
        registerSpell(wraper);
        wraper = new SpellWraper("Explosion", new Explosion(), 1, 1);
        registerSpell(wraper);
        wraper = new SpellWraper("ArrowStorm", new ArrowStorm(), 150, 100);
        registerSpell(wraper);
    }
    
    public void registerSpell(SpellWraper wraper){
        String spellName = wraper.getSpellName();
        if(!spells.containsKey(spellName)){
            spells.put(spellName, wraper);
        } else {
            log.warning(String.format("Faild to register spell %s as there already is one with the same name!", spellName));
        }
    }
    
    public String[] getSpellNames(){
        Set<String> set = spells.keySet();
        return set.toArray(new String[set.size()]);
    }
    
    private SpellWraper getSpellWraper(String name){
        return spells.get(name);
    }
    
    private BaseSpell[] getChildSpells(int parentID){
        ArrayList<BaseSpell> children = new ArrayList<>();
        activeSpells.values().stream()
                .filter(spell -> (spell.getParentID() == parentID))
                .forEach(spell -> { children.add(spell); });
        return children.toArray(new BaseSpell[children.size()]);
    }
    
    public BaseSpell castSpell(String name, Player player){
        SpellWraper spellWraper = getSpellWraper(name);
        if(spellWraper != null){
            Mage mage = mageManager.getMage(player);
            try{
                spellWraper.canCastSpell(mage);
            } catch(NotEnoughManaException e){
                player.sendMessage("Not enough Mana to cast spell");
                return null;
            } catch(SpellCooldownException e){
                player.sendMessage("Spell has cooldown");
                return null;
            }
            BaseSpell spell = spellWraper.getSpell();
            mageManager.castSpell(mage, spellWraper);
            spawnSpell(spell, player, name, 0);
            return spell;
        } else {
            player.sendMessage("No such spell");
            return null;
        }
    }

    public BaseSpell castSpell(String name, int parentID, Player player){
        SpellWraper spellWraper = getSpellWraper(name);
        if(spellWraper != null){
            BaseSpell spell = spellWraper.getSpell();
            spawnSpell(spell, player, name, parentID);
            return spell;
        }
        return null;
    }
    
    public void tick(){
       for(int id: activeSpells.keySet()){
           BaseSpell spell = activeSpells.get(id);
           if(spell.isAlive()){
               World world = spell.getWorld();
                Vector velocity = spell.getVelocity();
                Location position = spell.getPosition();
                FluidCollisionMode fluidCollision = FluidCollisionMode.NEVER;
                if(spell.doFluidCollision()){
                    fluidCollision = FluidCollisionMode.ALWAYS;
                }
                //RayTraceResult trace = world.rayTrace(position, velocity, velocity.length(), fluidCollision, true, spell.getSize(), null);
                // round up length because spell will be in the most distant block
               velocity = velocity.length() != 0 ? velocity: new Vector(0, 0.000001, 0);
                BlockIterator blockIterator = new BlockIterator(world, position.toVector(), velocity, 0, (int) Math.ceil(velocity.length()));
                while(blockIterator.hasNext()){
                    Block block = blockIterator.next();
                    RayTraceResult result = block.rayTrace(position, velocity, velocity.length(), fluidCollision);
                    if(result != null){
                        BlockIterator iterator = new BlockIterator(world, result.getHitPosition(), velocity, 0, 50);
                        int wallThickness = 0;
                        while(iterator.hasNext()){
                            if(!iterator.next().isPassable()){
                                wallThickness++;
                            } else {
                                break;
                            }
                        }
                        spell.on_block_hit(result.getHitPosition().toLocation(world), block, wallThickness);
                        if(!spell.isAlive()){
                            terminateSpell(spell, result.getHitPosition().toLocation(world));
                        }
                    }
                    Collection<Entity> nearbyEntities = world.getNearbyEntities(block.getLocation(), spell.getSize(), spell.getSize(), spell.getSize());
                    for(Entity entity: nearbyEntities){
                        if(!spell.isAlive()){
                            terminateSpell(spell, block.getLocation());
                            break;
                        }
                        if(entity instanceof Player){
                            if(!entity.equals(spell.getPlayer())){
                                spell.on_player_hit(block.getLocation(), (Player) entity);
                                System.out.println("Playerhit");
                            }
                        } else if(!(entity instanceof Item)){
                            spell.on_entity_hit(block.getLocation(), entity);
                            System.out.println("Entityhit");
                        }
                    }
                }
                if(spell.isAlive()){
                    spell.tick();
                }
                if(spell.getLifeTime() > spell.getMaxLifeTime()){
                    spell.on_lifetime_end();
                    spell.setAlive(false);
                } else if(spell.getPosition().distance(spell.getStartPosition()) > spell.getMaxDistance()){
                    spell.on_out_of_range();
                    //spell.setAlive(false);
                }
           } else {
               terminateSpell(spell);
           }
       } 
    }
    
    public void terminateSpell(BaseSpell spell){
        terminateSpell(spell, spell.getPosition());
    }
    
    public void terminateSpell(BaseSpell spell, Location location){
        int id = spell.getId();
        activeSpells.remove(id);
        spell.terminate(location);
        for(BaseSpell childSpell: getChildSpells(id)){
            if(childSpell.isDaemon()){
                 childSpell.setAlive(false);
            }
        }
    }
    
    public void spawnSpell(BaseSpell spell, Player player, String name, int parentID){
        do{
            if(activeSpellID == Integer.MAX_VALUE){
                activeSpellID = 1;
            } else {
                activeSpellID++;
            }
        } while(activeSpells.containsKey(activeSpellID));
        spell.setAlive(true);
        spell.init(this, player.getWorld(), player, activeSpellID, parentID, name);
        activeSpells.put(activeSpellID, spell);
    }
    
}
