package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RTPEffect_Particles {

    private boolean enabled;
    private final List<Particle> effects = new ArrayList<>();
    private String shape;
    private final int precision = 16;

    //Some particles act very differently and might not care how they are shaped before animating, ex: EXPLOSION_NORMAL
    public static String[] shapeTypes = {
            "SCAN", //Body scan
            "EXPLODE", //Make an explosive entrance
            "TELEPORT" //Startrek type of portal
            };

    // ParticleLib/pre-1.20.5 names that existing configs still use
    private static final Map<String, String> LEGACY_NAMES = new HashMap<>();
    static {
        LEGACY_NAMES.put("BARRIER", "BLOCK_MARKER");
        LEGACY_NAMES.put("BLOCK_CRACK", "BLOCK");
        LEGACY_NAMES.put("BLOCK_DUST", "BLOCK");
        LEGACY_NAMES.put("CRIT_MAGIC", "ENCHANTED_HIT");
        LEGACY_NAMES.put("DRIP_LAVA", "DRIPPING_LAVA");
        LEGACY_NAMES.put("DRIP_WATER", "DRIPPING_WATER");
        LEGACY_NAMES.put("ENCHANTMENT_TABLE", "ENCHANT");
        LEGACY_NAMES.put("EXPLOSION_HUGE", "EXPLOSION_EMITTER");
        LEGACY_NAMES.put("EXPLOSION_LARGE", "EXPLOSION");
        LEGACY_NAMES.put("EXPLOSION_NORMAL", "POOF");
        LEGACY_NAMES.put("FIREWORKS_SPARK", "FIREWORK");
        LEGACY_NAMES.put("ITEM_CRACK", "ITEM");
        LEGACY_NAMES.put("MOB_APPEARANCE", "ELDER_GUARDIAN");
        LEGACY_NAMES.put("REDSTONE", "DUST");
        LEGACY_NAMES.put("SLIME", "ITEM_SLIME");
        LEGACY_NAMES.put("SMOKE_LARGE", "LARGE_SMOKE");
        LEGACY_NAMES.put("SMOKE_NORMAL", "SMOKE");
        LEGACY_NAMES.put("SNOWBALL", "ITEM_SNOWBALL");
        LEGACY_NAMES.put("SPELL", "EFFECT");
        LEGACY_NAMES.put("SPELL_INSTANT", "INSTANT_EFFECT");
        LEGACY_NAMES.put("SPELL_MOB", "ENTITY_EFFECT");
        LEGACY_NAMES.put("SPELL_WITCH", "WITCH");
        LEGACY_NAMES.put("SUSPENDED", "UNDERWATER");
        LEGACY_NAMES.put("TOTEM", "TOTEM_OF_UNDYING");
        LEGACY_NAMES.put("VILLAGER_ANGRY", "ANGRY_VILLAGER");
        LEGACY_NAMES.put("VILLAGER_HAPPY", "HAPPY_VILLAGER");
        LEGACY_NAMES.put("WATER_BUBBLE", "BUBBLE");
        LEGACY_NAMES.put("WATER_DROP", "RAIN");
        LEGACY_NAMES.put("WATER_SPLASH", "SPLASH");
        LEGACY_NAMES.put("WATER_WAKE", "FISHING");
    }

    private static final Set<String> warnedUnknown = new HashSet<>();

    void load() {
        effects.clear();
        FileOther.FILETYPE config = getPl().getFiles().getType(FileOther.FILETYPE.EFFECTS);
        enabled = config.getBoolean("Particles.Enabled");
        if (!enabled) return;
        //Enabled? Load all this junk
        List<String> types;
        if (config.isList("Particles.Type"))
            types = config.getStringList("Particles.Type");
        else {
            types = new ArrayList<>();
            types.add(config.getString("Particles.Type"));
        }
        for (String type : types) {
            Particle particle = resolveParticle(type);
            if (particle != null)
                effects.add(particle);
        }
        if (effects.isEmpty()) {
            effects.add(Particle.ASH);
            getPl().getLogger().severe("No valid particle configured! Default particle enabled... " +
                    "Try using '/rtp info particles' to get a list of available particles");
        }
        shape = config.getString("Particles.Shape").toUpperCase();
        if (!Arrays.asList(shapeTypes).contains(shape)) {
            getPl().getLogger().severe("The particle shape '" + shape + "' doesn't exist! Default particle shape enabled...");
            getPl().getLogger().severe("Try using '/rtp info shapes' to get a list of shapes, or: " + Arrays.asList(shapeTypes));
            shape = shapeTypes[0];
        }
    }

    private static Particle resolveParticle(String name) {
        if (name == null) return null;
        String upper = name.toUpperCase();
        String mapped = LEGACY_NAMES.getOrDefault(upper, upper);
        try {
            return Particle.valueOf(mapped);
        } catch (IllegalArgumentException e) {
            if (warnedUnknown.add(upper))
                BetterRTP.getInstance().getLogger().warning("The particle '" + name + "' doesn't exist on this version, skipping. " +
                        "Try using '/rtp info particles' to get a list of available particles");
            return null;
        }
    }

    public void display(Player p) {
        if (!enabled) return;
        AsyncHandler.async(() -> {
            try { //Incase a particle/shape combination errors out
                switch (shape) {
                    case "TELEPORT":
                        partTeleport(p);
                        break;
                    case "EXPLODE":
                        partExplosion(p);
                        break;
                    default: //Super redundant, but... just future proofing
                    case "SCAN":
                        partScan(p);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void partScan(Player p) { //Particles with negative velocity
        Location loc = p.getLocation().add(new Vector(0, 1.75, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (Particle effect : effects)
                spawnDirectional(p, effect, loc.clone().add(vec), new Vector(0, -0.125, 0), .15f);
        }
    }

    private void partTeleport(Player p) { //Static particles in a shape
        Location loc = p.getLocation();
        for (float y = 2.5f; y > 0; y -= .25f)
            for (int index = 1; index < precision; index++) {
                Vector vec = getVecCircle(index).add(new Vector(0, y, 0));
                for (Particle effect : effects)
                    spawnStatic(p, effect, loc.clone().add(vec));
            }
    }

    private void partExplosion(Player p) { //Particles with a shape and forward velocity
        Location loc = p.getLocation().add(new Vector(0, 1, 0));
        for (int index = 1; index < precision; index++) {
            Vector vec = getVecCircle(index);
            for (Particle effect : effects)
                spawnDirectional(p, effect, loc.clone().add(vec), vec, 1.5f);
        }
    }

    private void spawnStatic(Player p, Particle particle, Location loc) {
        spawn(p, particle, loc, 1, 0, 0, 0, 0);
    }

    // count 0 makes the offset the particle's velocity
    private void spawnDirectional(Player p, Particle particle, Location loc, Vector offset, float speed) {
        spawn(p, particle, loc, 0, offset.getX(), offset.getY(), offset.getZ(), speed);
    }

    private void spawn(Player p, Particle particle, Location loc, int count, double offX, double offY, double offZ, double extra) {
        try {
            Class<?> dataType = particle.getDataType();
            if (dataType == Void.class) {
                p.spawnParticle(particle, loc, count, offX, offY, offZ, extra);
                return;
            }
            Object data = defaultDataFor(dataType);
            if (data == null) return; //Can't synthesize sensible data for this particle, skip it rather than throw
            p.spawnParticle(particle, loc, count, offX, offY, offZ, extra, data);
        } catch (Throwable t) {
            //A misconfigured or unsupported particle must never break the teleport effects
        }
    }

    private static Object defaultDataFor(Class<?> dataType) {
        if (dataType == Particle.DustOptions.class)
            return new Particle.DustOptions(Color.RED, 1.0f);
        if (dataType == BlockData.class)
            return Material.STONE.createBlockData();
        if (dataType == ItemStack.class)
            return new ItemStack(Material.STONE);
        return null;
    }

    private Vector getVecCircle(int index) {
        double p1 = (index * Math.PI) / (precision / 2);
        double p2 = (index - 1) * Math.PI / (precision / 2);
        //Positions
        int radius = 3;
        double x1 = Math.cos(p1) * radius;
        double x2 = Math.cos(p2) * radius;
        double z1 = Math.sin(p1) * radius;
        double z2 = Math.sin(p2) * radius;
        return new Vector(x2 - x1, 0, z2 - z1);
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
