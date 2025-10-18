package com.recipedupe;

import java.util.HashMap;
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

            if (timeSinceClick < 200) {
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
                if (maxStackSize == 1) {
                    if (originalAmount == 1) {
                        itemStack.setAmount(2);
                    }
                } else {
                    if (originalAmount == 64) {
                        itemStack.setAmount(127); // so it doesn't crash the user's game and the server 
                        // due to integer overflow cuz stacks being controlled by a "byte" type (-128 to 127)
                    } else {
                        itemStack.setAmount(originalAmount * 2);
                    }
                }

                lastInventoryClick.remove(playerUuid);
            } else {
                lastInventoryClick.remove(playerUuid);
            }
        }
    }
}
