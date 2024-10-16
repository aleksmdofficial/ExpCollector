package dev.aleksmd.expcollector.command;

import dev.aleksmd.expcollector.ExpCollector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import dev.aleksmd.expcollector.util.Hex;
import dev.aleksmd.expcollector.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ForceGiveExpCommand implements TabExecutor {
    private final ExpCollector plugin;

    public ForceGiveExpCommand(ExpCollector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("expcollector.use")) {
            return true;
        }
        if (args == null || args.length < 2) {
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            return true;
        }

        Integer level = null;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return true;
        }

        // Создание бутылки опыта с кастомными параметрами
        ItemBuilder itemBuilder = new ItemBuilder(Material.EXPERIENCE_BOTTLE, 1);
        String name = this.plugin.getConfig().getString("item.name");
        itemBuilder.setName(name, java.util.Map.of("%level%", String.valueOf(level)));

        List<String> lore = this.plugin.getConfig().getStringList("item.lore");
        ItemStack bottle = itemBuilder
                .setLore(lore, java.util.Map.of("%level%", String.valueOf(level)))
                .addPDC("exp", PersistentDataType.INTEGER, level)
                .build();

        // Отправка сообщения игроку
        String successMessage = this.plugin.getConfig().getString("success.message");
        if (successMessage != null && !successMessage.isEmpty()) {
            player.sendMessage(Hex.INSTANCE.color(successMessage));
        }

        // Проигрывание звука, если он указан
        String soundName = this.plugin.getConfig().getString("success.sound");
        if (soundName != null && !soundName.equals("null")) {
            Location location = player.getLocation();
            player.playSound(location, Sound.valueOf(soundName), 1.0f, 0.0f);
        }

        // Проверка свободного места в инвентаре
        if (getFreeSlots(player) > 0) {
            player.getInventory().addItem(bottle);
        } else {
            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), bottle);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args != null && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        if (args != null && args.length == 2) {
            return List.of("15", "30", "45");
        }

        return new ArrayList<>();
    }

    // Метод для подсчета свободных слотов в инвентаре игрока
    private int getFreeSlots(Player player) {
        int freeSlots = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack item : contents) {
            if (item == null) {
                freeSlots++;
            }
        }

        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            freeSlots--;
        }
        if (player.getInventory().getHelmet() == null) {
            freeSlots--;
        }
        if (player.getInventory().getChestplate() == null) {
            freeSlots--;
        }
        if (player.getInventory().getLeggings() == null) {
            freeSlots--;
        }
        if (player.getInventory().getBoots() == null) {
            freeSlots--;
        }

        return freeSlots;
    }
}
