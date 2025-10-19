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
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /recipedupe <toggle|reload|mode|dupeamount>").color(NamedTextColor.RED));
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

        if (args[0].equalsIgnoreCase("mode")) {
            if (args.length == 1) {
                sender.sendMessage(Component.text("Mode: " + plugin.getMode()).color(NamedTextColor.GREEN));
                return true;
            } else if (args.length == 2) {
                String requested = args[1].toLowerCase();
                if (plugin.setMode(requested)) {
                    // Also persist to config
                    plugin.getConfig().set("mode", requested);
                    plugin.saveConfig();
                    sender.sendMessage(Component.text("Mode set to: " + requested).color(NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Invalid mode. Use 'multiple' or 'exponent'.").color(NamedTextColor.RED));
                }
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("dupeamount")) {
            if (args.length == 1) {
                sender.sendMessage(Component.text("Dupe amount: " + plugin.getDupeAmount()).color(NamedTextColor.GREEN));
                return true;
            } else if (args.length == 2) {
                try {
                    int val = Integer.parseInt(args[1]);
                    if (val < 1) throw new NumberFormatException();
                    if (plugin.setDupeAmount(val)) {
                        plugin.getConfig().set("dupe-amount", val);
                        plugin.saveConfig();
                        sender.sendMessage(Component.text("Dupe amount set to: " + val).color(NamedTextColor.GREEN));
                    } else {
                        sender.sendMessage(Component.text("Invalid dupe amount. Must be >= 1.").color(NamedTextColor.RED));
                    }
                } catch (NumberFormatException ex) {
                    sender.sendMessage(Component.text("Invalid number. Usage: /recipedupe dupeamount <int>").color(NamedTextColor.RED));
                }
                return true;
            }
        }

        sender.sendMessage(Component.text("Unknown subcommand. Usage: /recipedupe <toggle|reload|mode|dupeamount>").color(NamedTextColor.RED));
        return true;
    }
}
