package com.solarcloud7.cloudtrade;

import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final CloudTrade p;

    public InventoryListener(CloudTrade instance) {
        p = instance;
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        //checks that the destination chest is one of our custom trade boxes
        if (event.getDestination().getTitle().equals(TradeChestLayout.tradeboxName) == false) {
            return;
        }
        //checks the source inventory viewer of trade.   Goal is to get who moved inventory item.
        TradePlayer target = p.getTraders().get(event.getSource().getViewers().get(0).getName());

        //Double checking to make sure target is in a trade
        if (target == null) {
            return;
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getView().getTitle().equalsIgnoreCase(TradeChestLayout.tradeboxName) == false) {
            return;
        }

        //Player player = (Player) event.getWhoClicked();
        TradePlayer target = p.getTraders().get(event.getWhoClicked().getName());
        //no request
        if (target == null) {
            return;
        }

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        //no request
 //     target.getPlayer().sendMessage(event.getRawSlot() + "");
//       target.getPlayer().sendMessage(event.getSlot() + "");
//       target.getPlayer().sendMessage(event.getView().getBottomInventory().getName());
////       target.getPlayer().sendMessage(event.getView().getBottomInventory().getTitle());
//        target.getPlayer().sendMessage(event.getView().getTitle() + "");
//        target.getPlayer().sendMessage(event.getView().getType().name() + "");
//        target.getPlayer().sendMessage(event.getAction().name());
//        target.getPlayer().sendMessage(event.getClick().name());
        TradeManager tradeManager = target.getTradeManager();
        //no shift clicking
        if (event.isShiftClick()) {
            target.getPlayer().sendMessage(p.c("errNoShiftClick"));
            event.setCancelled(true);
            target.getPlayer().updateInventory();
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        //checks current item to see if its proctected with TradeManager
        if (tradeManager.isProtectedBlock(item)) {
            event.setCancelled(true);

            //checks to see if the block title is accept
            if (item.getItemMeta().getDisplayName().contains("Accept")) {
                //checks to see if clicked the correct accept block
                if (item.getItemMeta().getLore().get(0).contains(event.getWhoClicked().getName())) {
                    tradeManager.onItemAccept(target.isSender);
                }

            }
            //checks to see if slot is protected (cant place blocks on wrong side of trade
        } else if (target.getTradeManager().isPlaceableSlot(event.getRawSlot(), target.isSender() == false)) {
            target.getPlayer().sendMessage(p.c("errUseOtherSide"));

            event.setCancelled(true);
            target.getTradeManager().updateRequest();
        } else {
            //only check change events for the top part of inventory (the chest)  not the player inventory.

            tradeManager.onItemChange();

        }
        //  tradeManager.updateRequest();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (event.getView().getTitle().equalsIgnoreCase(TradeChestLayout.tradeboxName) == false) {
            return;
        }

        Player sender = (Player) event.getPlayer();
        //sender.sendMessage("close inventory");

        TradePlayer target = p.getTraders().get(sender.getName());
        //no request        TradePlayer target = plugin.getTraders().get(sender.getName());

        if (target == null) {
            return;
            //sender.sendMessage(ChatColor.GRAY + "Why so negative? No trade was offered.");
        }
        //only close trades that have been accepted and are in negotiation.
        if (target.getTradeManager().isAccepted()) {
            target.getTradeManager().endTrade();
        }

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getTitle().equals(TradeChestLayout.tradeboxName)==false) {
            return;
        }
        //plugin.getLogger().info(event.getInventorySlots().size()+"");
        if (event.getInventorySlots().size() > 1) {

            event.setCancelled(true);
        }
        Set<Integer> test = event.getRawSlots();
        int rawSlot = test.iterator().next();


        TradePlayer target = p.getTraders().get(event.getWhoClicked().getName());
        //no request
        if (target == null) {
            return;
        }

        
        ItemStack item = event.getOldCursor();
        if (item == null) {
            return;
        }
        TradeManager tradeManager = target.getTradeManager();
        //checks current item to see if its proctected with TradeManager
        if (tradeManager.isProtectedBlock(item)) {
            event.setCancelled(true);

            //checks to see if the block title is accept
            if (item.getItemMeta().getDisplayName().contains("Accept")) {
                //checks to see if clicked the correct accept block
                if (item.getItemMeta().getLore().get(0).contains(event.getWhoClicked().getName())) {
                    tradeManager.onItemAccept(target.isSender);
                }

            }
            //checks to see if slot is protected (cant place blocks on wrong side of trade
        } else if (target.getTradeManager().isPlaceableSlot(rawSlot, target.isSender() == false)) {
            target.getPlayer().sendMessage(p.c("errUseOtherSide"));

            event.setCancelled(true);
            target.getTradeManager().updateRequest();
        } else {
            //only check change events for the top part of inventory (the chest)  not the player inventory.

            tradeManager.onItemChange();

        }

    }
}
