package com.solarcloud7.cloudtrade;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handle events for all Player related events
 *
 * @author Dinnerbone
 */
public class PlayerListener implements Listener {

    private final CloudTrade p;

    public PlayerListener(CloudTrade instance) {
        p = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // plugin.getLogger().info(event.getPlayer().getName() + " joined the server! :D");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player sender = (Player) event.getPlayer();

        TradePlayer target = p.getTraders().get(sender.getName());
        if (target == null) {
            return;
        }

        //Ends trade (if logged out in middle of trade) or declines trade offer (if logged out durring offer)
        if (target.getTradeManager().isAccepted()) {
            target.getTradeManager().endTrade();
        } else {
            target.getTradeManager().declineTrade();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {

        
        if (event.getRightClicked().getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Player target = (Player) event.getRightClicked();

        Boolean isShift = player.isSneaking();
        Boolean isControl = player.isSprinting();
        Boolean shift = p.getConfig().getBoolean("shift-right-click");
        Boolean control = p.getConfig().getBoolean("sprint-click");
        //aborts if both settins are false
        if(!shift && !control){
            return;
        }
        Boolean pass = false;
        //Checks if config shift true.   then abort if not shift.
        if (shift) {
            if (isShift) {
                pass = true;
            }
        }
        if (control) {
            if (isControl) {
                pass = true;
            }
        }
        if(!pass){
            return;
        }

        
        if (!player.hasPermission("cloudtrade.cantrade")) {
            player.sendMessage(p.c("errPermissions"));
            return;
        }
        if (!player.hasPermission("cloudtrade.rightclickrequest")) {
            player.sendMessage(p.c("errPermissionsRightClick"));
            return;
        }
        if (p.getConfig().getBoolean("bothPlayersMustHavePermissions")) {
            if (!target.hasPermission("cloudtrade.cantrade")) {
                player.sendMessage(p.c("errTargetNoPermission"));
                return;
            }
        }

        //Checks if player is requested for a trade!
        TradePlayer TPplayer = p.getRequests().get(player.getName());
        if (TPplayer != null) {
            TPplayer.getTradeManager().acceptTrade();
            return;
        }

        // sets up a trade manager
        TradeManager tm = new TradeManager(p, player, target);
        //aborts if players are already in a trade
        if (tm.isSenderBusy()) {
            player.sendMessage(p.c("errBusyWithTrade"));
            return;
        } else if (tm.isTargetBusy()) {
            player.sendMessage(p.c("errTargetBusy"));
            return;
        }
        tm.sendRequest();

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player sender = event.getEntity();

        TradePlayer target = p.getTraders().get(sender.getName());
        if (target == null) {
            return;
        }

        //Ends trade (if logged out in middle of trade) or declines trade offer (if logged out durring offer)
        if (target.getTradeManager().isAccepted()) {
            target.getTradeManager().endTrade();
        } else {
            target.getTradeManager().declineTrade();
        }
    }
}
