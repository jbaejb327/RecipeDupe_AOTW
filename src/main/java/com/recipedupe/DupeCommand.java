package com.recipedupe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DupeCommand implements CommandExecutor {

    private final RecipeDupe plugin;

    public DupeCommand(RecipeDupe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /recipedupe <toggle|reload>").color(NamedTextColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            plugin.setDupeEnabled(!plugin.isDupeEnabled());
            String state = plugin.isDupeEnabled() ? "enabled" : "disabled";
            sender.sendMessage(Component.text("Dupe is now " + state + ".").color(NamedTextColor.GREEN));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfig();
            sender.sendMessage(Component.text("RecipeDupe reloaded.").color(NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Unknown subcommand. Usage: /recipedupe <toggle|reload>").color(NamedTextColor.RED));
        return true;
    }
}
