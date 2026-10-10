package me.SuperRonanCraft.BetterRTP.player.rtp;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CancelledEvent;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class RTPDelay implements Listener {
    private WrappedTask task;
    private final boolean cancelOnMove, cancelOnDamage;
    private final RTPPlayer rtp;

    RTPDelay(CommandSender sendi, RTPPlayer rtp, int delay, boolean cancelOnMove, boolean cancelOnDamage) {
        this.cancelOnMove = cancelOnMove;
        this.cancelOnDamage = cancelOnDamage;
        this.rtp = rtp;
        delay(sendi, delay);
    }

    private void delay(CommandSender sendi, int delay) {
        if (!getPl().getRTP().getTeleport().beforeTeleportDelay(rtp.getPlayer(), delay)) {
            task = AsyncHandler.syncLater(run(sendi, this), delay * 20L);
            Bukkit.getPluginManager().registerEvents(this, BetterRTP.getInstance());
        } else
            RTPPlayer.cleanupFailedRTP(sendi, rtp.getPlayer(), rtp.getWorldPlayer(), false);
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerMoveEvent e) {
        if (cancelOnMove)
            if (e.getPlayer().equals(rtp.getPlayer()) &&
                (e.getTo() != null &&
                        (e.getTo().getBlockX() != e.getFrom().getBlockX() ||
                        e.getTo().getBlockY() != e.getFrom().getBlockY() ||
                        e.getTo().getBlockZ() != e.getFrom().getBlockZ()))
            ) {
                cancel();
            }
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(EntityDamageEvent e) {
        if (cancelOnDamage)
            if (e.getEntity() instanceof Player){
                if (e.getEntity().equals(rtp.getPlayer()))
                    cancel();
            }
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void event(PlayerQuitEvent e) {
        if (e.getPlayer().equals(rtp.getPlayer())) {
            stop();
            RTPPlayer.cleanupFailedRTP(null, rtp.getPlayer(), rtp.getWorldPlayer(), false);
        }
    }

    private void cancel() {
        stop();
        getPl().getRTP().getTeleport().cancelledTeleport(rtp.getPlayer());
        RTPPlayer.cleanupFailedRTP(null, rtp.getPlayer(), rtp.getWorldPlayer(), false);
        Bukkit.getServer().getPluginManager().callEvent(new RTP_CancelledEvent(rtp.getPlayer()));
    }

    private void stop() {
        if (task != null)
            task.cancel();
        HandlerList.unregisterAll(this);
    }

    private Runnable run(final CommandSender sendi, final RTPDelay cls) {
        return () -> {
            HandlerList.unregisterAll(cls);
            if (getPl().getPInfo().isRtping(rtp.getPlayer()))
                rtp.randomlyTeleport(sendi);
        };
    }

    private BetterRTP getPl() {
        return BetterRTP.getInstance();
    }
}
