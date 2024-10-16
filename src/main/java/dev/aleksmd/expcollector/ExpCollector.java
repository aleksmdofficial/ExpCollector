package dev.aleksmd.expcollector;

import dev.aleksmd.expcollector.command.ForceGiveExpCommand;
import dev.aleksmd.expcollector.command.GiveExpCommand;
import dev.aleksmd.expcollector.command.ReloadCommand;
import dev.aleksmd.expcollector.event.ExpEvent;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExpCollector extends JavaPlugin {
    private static ExpCollector instance;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new ExpEvent(this), this);
        PluginCommand giveExpCommand = this.getCommand("expcollector");
        if (giveExpCommand != null) {
            giveExpCommand.setExecutor(new GiveExpCommand(this));
            giveExpCommand.setTabCompleter(new GiveExpCommand(this));
        }

        PluginCommand forceGiveExpCommand = this.getCommand("forceexpcollector");
        if (forceGiveExpCommand != null) {
            forceGiveExpCommand.setExecutor(new ForceGiveExpCommand(this));
            forceGiveExpCommand.setTabCompleter(new ForceGiveExpCommand(this));
        }

        PluginCommand reloadCommand = this.getCommand("expcollectorreload");
        if (reloadCommand != null) {
            reloadCommand.setExecutor(new ReloadCommand());
        }

    }

    public static ExpCollector getInstance() {
        return instance;
    }
}