package dev.aleksmd.expcollector.command;

import dev.aleksmd.expcollector.ExpCollector;
import dev.aleksmd.expcollector.util.Exp;
import dev.aleksmd.expcollector.util.Hex;
import dev.aleksmd.expcollector.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class GiveExpCommand implements TabExecutor {
    private final ExpCollector plugin;

    public GiveExpCommand(ExpCollector plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("expcollector.use")) {
            return true;
        } else if (args != null && args.length >= 2) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (offlinePlayer != null && offlinePlayer.isOnline()) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    return true;
                } else {
                    Integer level = null;

                    try {
                        level = Integer.parseInt(args[1]);
                    } catch (NumberFormatException var18) {
                        return true;
                    }

                    String name;
                    if (!this.hasBottle(player)) {
                        String failMessage = this.plugin.getConfig().getString("fail.has-not-bottle");
                        if (failMessage != null && !failMessage.isEmpty()) {
                            player.sendMessage(Hex.INSTANCE.color(failMessage));
                        }

                        name = this.plugin.getConfig().getString("fail.sound");
                        if (name != null && !name.equals("null")) {
                            player.playSound(player.getLocation(), Sound.valueOf(name), 1.0F, 0.0F);
                        }

                        return true;
                    } else {
                        if (this.plugin.getConfig().getBoolean("vanilla-mode")) {
                            if (player.getTotalExperience() < Exp.INSTANCE.calculateExp(level)) {
                                this.sendFailMessage(player, "fail.not-enough-exp");
                                return true;
                            }
                        } else if (player.getLevel() < level) {
                            this.sendFailMessage(player, "fail.not-enough-exp");
                            return true;
                        }

                        ItemBuilder itemBuilder = new ItemBuilder(Material.EXPERIENCE_BOTTLE, 1);
                        name = this.plugin.getConfig().getString("item.name");
                        itemBuilder.setName(name, Map.of("%level%", String.valueOf(level)));
                        List<String> lore = this.plugin.getConfig().getStringList("item.lore");
                        ItemStack bottle = itemBuilder.setLore(lore, Map.of("%level%", String.valueOf(level))).addPDC("exp", PersistentDataType.INTEGER, level).build();
                        String successMessage = this.plugin.getConfig().getString("success.message");
                        if (successMessage != null && !successMessage.isEmpty()) {
                            player.sendMessage(Hex.INSTANCE.color(successMessage));
                        }

                        String successSound = this.plugin.getConfig().getString("success.sound");
                        if (successSound != null && !successSound.equals("null")) {
                            player.playSound(player.getLocation(), Sound.valueOf(successSound), 1.0F, 0.0F);
                        }

                        if (this.getFreeSlots(player) > 0) {
                            player.getInventory().addItem(new ItemStack[]{bottle});
                        } else {
                            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), bottle);
                        }

                        if (this.plugin.getConfig().getBoolean("vanilla-mode")) {
                            player.giveExp(-Exp.INSTANCE.calculateExp(level));
                        } else {
                            player.setLevel(player.getLevel() - level);
                        }

                        ItemStack[] var14 = player.getInventory().getContents();
                        int var15 = var14.length;

                        for(int var16 = 0; var16 < var15; ++var16) {
                            ItemStack item = var14[var16];
                            if (item != null && item.getType() == Material.GLASS_BOTTLE) {
                                if (item.getAmount() > 1) {
                                    item.setAmount(item.getAmount() - 1);
                                } else {
                                    player.getInventory().remove(item);
                                }
                                break;
                            }
                        }

                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args != null && args.length == 1) {
            return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else {
            return (List)(args != null && args.length == 2 ? List.of("15", "30", "45") : new ArrayList());
        }
    }

    private int getFreeSlots(Player player) {
        int freeSlots = 0;
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] var4 = contents;
        int var5 = contents.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ItemStack item = var4[var6];
            if (item == null) {
                ++freeSlots;
            }
        }

        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            --freeSlots;
        }

        if (player.getInventory().getHelmet() == null) {
            --freeSlots;
        }

        if (player.getInventory().getChestplate() == null) {
            --freeSlots;
        }

        if (player.getInventory().getLeggings() == null) {
            --freeSlots;
        }

        if (player.getInventory().getBoots() == null) {
            --freeSlots;
        }

        return freeSlots;
    }

    private boolean hasBottle(Player player) {
        ItemStack[] var2 = player.getInventory().getContents();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack item = var2[var4];
            if (item != null && item.getType() == Material.GLASS_BOTTLE) {
                return true;
            }
        }

        return false;
    }

    private void sendFailMessage(Player player, String configKey) {
        String failMessage = this.plugin.getConfig().getString(configKey);
        if (failMessage != null && !failMessage.isEmpty()) {
            player.sendMessage(Hex.INSTANCE.color(failMessage));
        }

        String failSound = this.plugin.getConfig().getString("fail.sound");
        if (failSound != null && !failSound.equals("null")) {
            player.playSound(player.getLocation(), Sound.valueOf(failSound), 1.0F, 0.0F);
        }

    }
}