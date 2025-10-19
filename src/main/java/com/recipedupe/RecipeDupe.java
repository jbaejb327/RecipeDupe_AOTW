package com.recipedupe;

import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeDupe extends JavaPlugin {

    private boolean dupeEnabled = true;
    private double successRate = 100.0;
    private int clickWindowMs = 600;
    private int dupeAmount = 2;
    private String mode = "multiple";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        successRate = getConfig().getDouble("success-rate", 100.0);
        clickWindowMs = getConfig().getInt("click-window-ms", 600);
    dupeAmount = getConfig().getInt("dupe-amount", 2);
    mode = getConfig().getString("mode", "multiple");
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
        clickWindowMs = getConfig().getInt("click-window-ms", 600);
        dupeAmount = getConfig().getInt("dupe-amount", 2);
        mode = getConfig().getString("mode", "multiple");
    }

    public int getDupeAmount() {
        return dupeAmount;
    }

    public boolean setDupeAmount(int amount) {
        if (amount < 1) return false;
        this.dupeAmount = amount;
        return true;
    }

    public String getMode() {
        return mode != null ? mode.toLowerCase() : "multiple";
    }

    /**
     * Set the plugin mode. Valid values are "multiple" and "exponent".
     * Returns true if the mode was accepted and set, false otherwise.
     */
    public boolean setMode(String newMode) {
        if (newMode == null) return false;
        String m = newMode.toLowerCase();
        if (m.equals("multiple") || m.equals("exponent")) {
            this.mode = m;
            return true;
        }
        return false;
    }
}
