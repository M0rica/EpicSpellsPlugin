package epicspellsplugin.effects;

import epicspellsplugin.utils.DirectionalParticleCollection;
import epicspellsplugin.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RealisticExplosion {

    public RealisticExplosion(World world, Location location, int power, List<DirectionalParticleCollection> particles, double flyingBlockThreshold, boolean spawnFire){

        Block block = location.getBlock();
        Location blocklocation = block.getLocation();
        List<Block> removedBlocks = new ArrayList<>();
        List<Material> removedBlocksMaterials = new ArrayList<>();
        int rays = power*100;
        for(int i=0; i<rays; i++) {
            Vector vel = new Vector(Utils.randomFloat(-1, 1), Utils.randomFloat(-1, 1), Utils.randomFloat(-1, 1)).normalize();
            //System.out.println(vel);
            RayTraceResult result = world.rayTraceBlocks(blocklocation, vel, power, FluidCollisionMode.NEVER);
            if(result != null){
                Block hitBlock = result.getHitBlock();
                Material material = hitBlock.getType();
                if(!material.isAir()){
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
            if(Utils.randomFloat(0, 1) < flyingBlockThreshold) {
                Location l = temp.getLocation().add(0, 1, 0);
                FallingBlock fallingBlock = world.spawnFallingBlock(l, removedBlocksMaterials.get(i).createBlockData());
                try {
                    fallingBlock.setVelocity(l.subtract(location).toVector().normalize().setY(Utils.randomFloat(0.1F, 1)).multiply((double) power / 10));
                } catch (IllegalArgumentException e){
                    System.out.println("Rerun");
                    fallingBlock.remove();
                    i--;
                }
            }
        }

        for(DirectionalParticleCollection temp: particles){
            temp.randomizeLocations(power/2);
            temp.adjustVelocities();
            temp.setSpeed((double)power/25);
            temp.spawn();
        }

        Collection<Entity> nearbyEntities = world.getNearbyEntities(location, power, power, power);
        for(Entity entity: nearbyEntities){
            Vector velocity = entity.getLocation().subtract(location).toVector();
            double distance = velocity.length();
            double distancePower = (double) power/distance < power ? 1: (double) power/distance;
            if(!(entity instanceof FallingBlock)){
                entity.setVelocity(velocity.normalize().multiply(distancePower).add(new Vector(0, 1, 0)));
            }
            if(entity instanceof LivingEntity){
                ((LivingEntity) entity).damage(distancePower*2);
            }
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10, 0);
    }
}
