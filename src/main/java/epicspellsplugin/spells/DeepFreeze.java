package epicspellsplugin.spells;

import epicspellsplugin.BaseSpell;
import epicspellsplugin.SpellManager;
import epicspellsplugin.utils.DirectionalParticle;
import epicspellsplugin.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DeepFreeze extends BaseSpell {

    private List<Block> blocksToFreeze, frozenBlocks;
    private int freezeDistance;
    private boolean freeze;
    private Location hitLocation;
    private Random r;

    @Override
    public void init(SpellManager spellManager, Location location, Player player, int id, int parentID, String name){
        super.init(spellManager, location, player, id, parentID, name);
        maxLifeTime = 120;
        maxDistance = 50;
        collideWithFluids = true;
        velocity = player.getLocation().getDirection().multiply(2);
        size = 1;

        blocksToFreeze = new ArrayList<>();
        frozenBlocks = new ArrayList<>();
        freezeDistance = 10;
        freeze = false;
        r = new Random();
    }

    @Override
    public void tick(){
        super.tick();
        if(freeze){
            if(blocksToFreeze.isEmpty()){
                alive = false;
                return;
            }
            List<Block> newBlocks = new ArrayList<>();
            for(Block block: blocksToFreeze){
                Location loc = block.getLocation();
                double distance = loc.distance(hitLocation);
                if(distance <= freezeDistance && !frozenBlocks.contains(block) && !block.getType().isAir()){
                    double prop = 1 - (distance / freezeDistance) + 0.1;
                    if(prop > r.nextDouble()) {
                        boolean placed = false;
                        switch (block.getType()) {
                            case LAVA:
                                continue;
                            case WATER:
                                block.setType(Material.ICE);
                                placed = true;
                                newBlocks.addAll(getNeighbours(block));
                                break;
                            default:
                                if(!block.isPassable()) {
                                    Block temp = block.getRelative(0, 1, 0);
                                    Material m = temp.getType();
                                    if (m.isAir() || m == Material.FIRE) {
                                        temp.setType(Material.SNOW);
                                        placed = true;
                                        double speed = Utils.randomDouble(0.1, 0.5);
                                        DirectionalParticle.spawn(Particle.SNOWFLAKE, block.getLocation(), new Vector(0, 1, 0), speed);
                                        newBlocks.addAll(getNeighbours(block));
                                    }
                                } else {
                                    newBlocks.addAll(getNeighbours(block));
                                }
                        }
                        if(placed){
                            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 5, 20, false, false);
                            Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
                            for(Entity entity: entities){
                                if(entity instanceof LivingEntity){
                                    LivingEntity e = (LivingEntity) entity;
                                    e.damage(2);
                                    e.addPotionEffect(effect);
                                }
                            }
                        }
                    }
                }
                frozenBlocks.add(block);
            }
            blocksToFreeze.clear();
            blocksToFreeze.addAll(newBlocks);
        } else {
            position = position.add(velocity);
            for (int i = 0; i < 4; i++) {
                double x = Utils.randomDouble(-0.5, 0.5);
                double y = Utils.randomDouble(-0.1, -1);
                double z = Utils.randomDouble(-0.5, 0.5);
                Vector vel = new Vector(x, y, z);
                DirectionalParticle.spawn(Particle.END_ROD, position, vel, 0.3);
            }
        }
    }

    private List<Block> getNeighbours(Block block){
        List<Block> neighbours = new ArrayList<>();
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                for(int k=-1; k<2; k++){
                    if(!(i == 0 && j == 0 && k == 0)) {
                        Block temp = block.getRelative(i, j, k);
                        if(!frozenBlocks.contains(temp)) {
                            neighbours.add(temp);
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    @Override
    public void on_entity_hit(Location location, Entity entity) {
        if(freeze){
            return;
        }
        freeze = true;
    }

    @Override
    public void on_player_hit(Location location, Player player) {
        if(freeze){
            return;
        }
        freeze = true;
    }

    @Override
    public void on_block_hit(Location location, Block block, int wallThickness) {
        if(freeze){
            return;
        }
        freeze = true;
        hitLocation = block.getLocation();
        blocksToFreeze.add(block);
    }

    @Override
    public void on_out_of_range() {
        alive = false;
    }
}
