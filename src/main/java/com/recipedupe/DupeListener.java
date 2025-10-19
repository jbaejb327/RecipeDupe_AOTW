package com.recipedupe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DupeListener implements Listener {

    private final RecipeDupe plugin;
    private final HashMap<UUID, Long> lastInventoryClick = new HashMap<>();

    public DupeListener(RecipeDupe plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.isDupeEnabled()) {
            return;
        }
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            lastInventoryClick.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!plugin.isDupeEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID playerUuid = player.getUniqueId();

        if (lastInventoryClick.containsKey(playerUuid)) {
            long timeSinceClick = System.currentTimeMillis() - lastInventoryClick.get(playerUuid);

            if (timeSinceClick < plugin.getClickWindowMs()) {
                double success = plugin.getSuccessRate();
                if (success < 100.0) {
                    Random r = new Random();
                    double roll = r.nextDouble() * 100.0; 
                    if (roll > success) {
                        lastInventoryClick.remove(playerUuid);
                        return;
                    }
                }
                ItemStack itemStack = event.getItem().getItemStack();
                int originalAmount = itemStack.getAmount();
                int maxStackSize = itemStack.getMaxStackSize();

                int dupeAmount = Math.max(1, plugin.getDupeAmount());
                String mode = plugin.getMode();

                long totalAmountLong;
                if (mode.equals("exponent")) {
                    double pow = Math.pow((double) originalAmount, (double) dupeAmount);
                    totalAmountLong = (long) Math.max(1.0, Math.floor(pow));
                } else {
                    totalAmountLong = (long) originalAmount * (long) dupeAmount;
                }

                long SAFETY_MAX = 1_000L;
                if (totalAmountLong > SAFETY_MAX) totalAmountLong = SAFETY_MAX;

                int perStackCap = Math.min(64, maxStackSize);

                event.setCancelled(true);
                if (!event.getItem().isDead()) {
                    event.getItem().remove();
                }

                long remaining = totalAmountLong;
                while (remaining > 0) {
                    int take = (int) Math.min(perStackCap, remaining);
                    ItemStack chunk = itemStack.clone();
                    chunk.setAmount(take);

                    Map<Integer, ItemStack> leftovers = player.getInventory().addItem(chunk);
                    if (!leftovers.isEmpty()) {
                        // If any portion couldn't be added to inventory, drop it in the world
                        for (ItemStack leftover : leftovers.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                        }
                    }

                    remaining -= take;
                }

                lastInventoryClick.remove(playerUuid);
            } else {
                lastInventoryClick.remove(playerUuid);
            }
        }
    }
}
