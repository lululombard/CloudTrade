package com.solarcloud7.cloudtrade;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * Handle events for all Player related events
 *
 * @author Dinnerbone
 */
public class EntityListener implements Listener {

    private final CloudTrade plugin;
    private int itemNumber = 0;

    public EntityListener(CloudTrade instance) {
        plugin = instance;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        

    }

    

}
