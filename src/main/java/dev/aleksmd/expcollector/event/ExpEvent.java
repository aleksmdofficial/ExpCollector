package dev.aleksmd.expcollector.event;

import dev.aleksmd.expcollector.ExpCollector;
import dev.aleksmd.expcollector.util.Exp;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class ExpEvent implements Listener {
    @NotNull
    private final ExpCollector plugin;

    public ExpEvent(@NotNull ExpCollector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
    }

    @EventHandler
    public void onBottleBreak(@NotNull ExpBottleEvent event) {
        ThrownExpBottle bottle = event.getEntity();
        PersistentDataContainer pdc = bottle.getItem().getItemMeta().getPersistentDataContainer();
        NamespacedKey expKey = NamespacedKey.fromString("exp");
        if (expKey != null && pdc.has(expKey, PersistentDataType.INTEGER)) {
            Collection<Player> nearbyPlayers = bottle.getLocation().getNearbyPlayers(30.0D);
            Player closestPlayer = null;
            double closestDistance = Double.MAX_VALUE;
            Iterator var9 = nearbyPlayers.iterator();

            while(var9.hasNext()) {
                Player player = (Player)var9.next();
                double distance = bottle.getLocation().distance(player.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }

            if (closestPlayer != null) {
                Integer level = (Integer)pdc.get(expKey, PersistentDataType.INTEGER);
                if (level != null) {
                    event.setExperience(0);
                    if (this.plugin.getConfig().getBoolean("vanilla-mode")) {
                        closestPlayer.giveExp(Exp.INSTANCE.calculateExp(level));
                    } else {
                        closestPlayer.giveExpLevels(level);
                    }

                }
            }
        }
    }
}