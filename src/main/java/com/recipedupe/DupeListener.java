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

                int newAmount;
                if (maxStackSize == 1) {
                    newAmount = (originalAmount >= 1) ? Math.min(2, originalAmount * 2) : originalAmount;
                } else {
                    if (originalAmount == 64) {
                        newAmount = 127;
                    } else {
                        newAmount = originalAmount * 2;
                    }
                }

                event.setCancelled(true);
                if (event.getItem() != null && !event.getItem().isDead()) {
                    event.getItem().remove();
                }

                ItemStack giveStack = itemStack.clone();
                giveStack.setAmount(newAmount);

                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(giveStack);
                if (leftovers != null && !leftovers.isEmpty()) {
                    for (ItemStack leftover : leftovers.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                    }
                }

                lastInventoryClick.remove(playerUuid);
            } else {
                lastInventoryClick.remove(playerUuid);
            }
        }
    }
}
