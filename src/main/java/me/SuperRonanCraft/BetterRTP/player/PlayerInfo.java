package me.SuperRonanCraft.BetterRTP.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;

import me.SuperRonanCraft.BetterRTP.references.invs.RTP_INV_SETTINGS;

public class PlayerInfo {

    //private final HashMap<Player, RTP_INV_SETTINGS> invType = new HashMap<>();
    private final Map<UUID, World> invWorld = new ConcurrentHashMap<>();
    private final Map<UUID, RTP_INV_SETTINGS> invNextInv = new ConcurrentHashMap<>();
    //private final HashMap<Player, CooldownData> cooldown = new HashMap<>();
    private final Map<UUID, Boolean> rtping = new ConcurrentHashMap<>();
    //private final HashMap<Player, List<Location>> previousLocations = new HashMap<>();
    //private final HashMap<Player, RTP_TYPE> rtpType = new HashMap<>();

    /*private void setInv(Player p, Inventory inv) {
        invs.put(p, inv);
    }*/

    /*private void setInvType(Player p, RTP_INV_SETTINGS type) {
        invType.put(p, type);
    }*/

    public void setInvWorld(Player p, World type) {
        invWorld.put(p.getUniqueId(), type);
    }

    public World getInvWorld(Player p) {
        return invWorld.get(p.getUniqueId());
    }

    public void setNextInv(Player p, RTP_INV_SETTINGS type) {
        invNextInv.put(p.getUniqueId(), type);
    }

    public RTP_INV_SETTINGS getInvNextInv(Player p) {
        return invNextInv.get(p.getUniqueId());
    }

    public boolean isRtping(Player p) {
        return rtping.getOrDefault(p.getUniqueId(), false);
    }

    public void setRtping(Player p, boolean value) {
        rtping.put(p.getUniqueId(), value);
    }

    public void removeRtping(Player p) {
        rtping.remove(p.getUniqueId());
    }

    //--Logic--

    public Boolean playerExists(Player p) {
        UUID id = p.getUniqueId();
        return invWorld.containsKey(id) || invNextInv.containsKey(id);
    }

    private void unloadAll() {
        //invType.clear();
        invWorld.clear();
        invNextInv.clear();
        //cooldown.clear();
        rtping.clear();
        //previousLocations.clear();
    }

    public void unload(Player p) {
        clearInvs(p);
        //cooldown.remove(p);
        rtping.remove(p.getUniqueId());
        //previousLocations.remove(p);
    }

    public void clearInvs(Player p) {
        UUID id = p.getUniqueId();
        //invType.remove(p);
        invWorld.remove(id);
        invNextInv.remove(id);
    }
}
