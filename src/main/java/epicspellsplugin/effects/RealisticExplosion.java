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

import java.util.*;

public class RealisticExplosion {

    public RealisticExplosion(World world, Location location, int power, List<DirectionalParticleCollection> particles, double flyingBlockThreshold, boolean spawnFire){
        Location blocklocation = location.getBlock().getLocation();
        List<Block> removedBlocks = new ArrayList<>();
        List<Material> removedBlocksMaterials = new ArrayList<>();
        int rays = power*100;
        for(int i=0; i<rays; i++) {
            Random r = new Random();
            double x = r.nextGaussian();
            double y = r.nextGaussian();
            double z = r.nextGaussian();
            double ratio = 1/Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            x *= ratio*power;
            y *= ratio*power;
            z *= ratio*power;
            Vector vel = new Vector(x, y, z);
            double length = vel.length()/2;
            RayTraceResult result = world.rayTraceBlocks(blocklocation, vel, length, FluidCollisionMode.ALWAYS);
            if(result != null){
                Block hitBlock = result.getHitBlock();
                Material material = hitBlock.getType();
                if(!material.isAir()){
                    float blastResistance = material.getBlastResistance();
                    if(blastResistance < power*10){
                        boolean removeBlock = Utils.randomFloat(0, power) > blastResistance/(power*10);
                        if(removeBlock){
                            hitBlock.setType(Material.AIR);
                            removedBlocks.add(hitBlock);
                            removedBlocksMaterials.add(material);
                        }
                    }
                }
            }
        }
        if(spawnFire) {
            int numFire = 0;
            for (int i = 0; i < rays / 5; i++) {
                if(numFire >= power) {
                    break;
                }
                Random r = new Random();
                double x = r.nextGaussian();
                double y = r.nextGaussian();
                double z = r.nextGaussian();
                Vector vel = new Vector(x, y, z).normalize();
                double length = Math.min(power, 100);
                RayTraceResult result = world.rayTraceBlocks(blocklocation, vel, length, FluidCollisionMode.NEVER);
                if (result != null) {
                    Block block = result.getHitPosition().toLocation(world).getBlock();
                    if(block.getType().getBlastResistance() < power*10) {
                        block.setType(Material.FIRE);
                        numFire++;
                    }
                }
            }
        }

        for(int i=0; i<removedBlocks.size(); i++){
            Block temp = removedBlocks.get(i);
            if(Utils.randomFloat(0, 1) < flyingBlockThreshold) {
                Location l = temp.getLocation().add(0, 1, 0);
                Material material = removedBlocksMaterials.get(i);
                if(!material.equals(Material.WATER) && !material.equals(Material.LAVA)) {
                    FallingBlock fallingBlock = world.spawnFallingBlock(l, material.createBlockData());
                    try {
                        fallingBlock.setVelocity(l.subtract(location).toVector().normalize().setY(Utils.randomFloat(0.1F, 1)).multiply((double) power / 10));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Rerun");
                        fallingBlock.remove();
                        i--;
                    }
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
            double distancePower = (double) power/distance < power ? (double) power/distance: power;
            if(!(entity instanceof FallingBlock)){
                //velocity.normalize().multiply(distancePower).add(new Vector(0, 1, 0))
                entity.setVelocity(velocity.normalize().multiply(distancePower));
            }
            if(entity instanceof LivingEntity){
                ((LivingEntity) entity).damage(distancePower*2);
            }
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10F, 0.6F);
    }
}
