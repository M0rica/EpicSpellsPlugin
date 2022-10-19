package epicspellsplugin.spells;

import epicspellsplugin.BaseSpell;
import epicspellsplugin.SpellManager;
import epicspellsplugin.effects.RealisticExplosion;
import epicspellsplugin.utils.DirectionalParticleCollection;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Explosion extends BaseSpell {

    @Override
    public void init(SpellManager spellManager, Player player, int id, int parentID, String name){
        super.init(spellManager, player, id, parentID, name);
        startPosition = player.getTargetBlock(null, 80).getLocation();
        position = startPosition.clone();
        alive = false;
    }

    @Override
    public void tick(){
    }

    @Override
    public void terminate(Location location){
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        Vector velocity = new Vector();
        particles.add(new DirectionalParticleCollection(world, Particle.SMALL_FLAME, location, velocity, 80, 0.3));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_LARGE, location, velocity, 30, 0.3));
        particles.add(new DirectionalParticleCollection(world, Particle.SMOKE_NORMAL, location, velocity, 40, 0.3));
        particles.add(new DirectionalParticleCollection(world, Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 40, 0.1));
        new RealisticExplosion(world, location, 100, particles, 0.1, true);
    }
}
