package dev.aleksmd.expcollector.command;

import dev.aleksmd.expcollector.ExpCollector;
import dev.aleksmd.expcollector.util.Hex;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class ReloadCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("expcollector.use")) {
            return true;
        } else {
            ExpCollector.getInstance().reloadConfig();
            sender.sendMessage(Hex.INSTANCE.color("Конфигурация перезагружена"));
            return true;
        }
    }
}