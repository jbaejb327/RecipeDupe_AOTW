package com.recipedupe;

import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeDupe extends JavaPlugin {

    private boolean dupeEnabled = true;
    private double successRate = 100.0;
    private int clickWindowMs = 600;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        successRate = getConfig().getDouble("success-rate", 100.0);
        clickWindowMs = getConfig().getInt("click-window-ms", 600);
        getServer().getPluginManager().registerEvents(new DupeListener(this), this);
        if (getCommand("recipedupe") != null) {
            getCommand("recipedupe").setExecutor(new DupeCommand(this));
        } else {
            getLogger().warning("Command 'recipedupe' not defined in plugin.yml");
        }
        getLogger().info("RecipeDupe has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RecipeDupe has been disabled!");
    }

    public boolean isDupeEnabled() {
        return dupeEnabled;
    }

    public void setDupeEnabled(boolean dupeEnabled) {
        this.dupeEnabled = dupeEnabled;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public int getClickWindowMs() {
        return clickWindowMs;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        successRate = getConfig().getDouble("success-rate", 100.0);
    }
}
