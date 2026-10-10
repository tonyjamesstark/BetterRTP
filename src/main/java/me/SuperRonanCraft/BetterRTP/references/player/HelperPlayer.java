package me.SuperRonanCraft.BetterRTP.references.player;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HelperPlayer {

    public static PlayerData getData(Player p) {
        return getPl().getPlayerDataManager().getData(p);
    }

    @Nullable
    public static PlayerData find(UUID id) {
        return getPl().getPlayerDataManager().find(id);
    }

    public static void unload(Player p) {
        getPl().getPlayerDataManager().clear(p);
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
