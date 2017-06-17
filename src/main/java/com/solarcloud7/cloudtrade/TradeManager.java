package com.solarcloud7.cloudtrade;

import static com.solarcloud7.cloudtrade.TradeChestLayout.ChestSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Ryan (solarcloud7)
 */
public class TradeManager {

    private CloudTrade p;

    private TradeRequest tradeRequest;
    private TradeChestLayout tcLayout;

    private boolean targetAccepted = false;
    private boolean targetDeclined = false;

    private TradePlayer TPsender;
    private TradePlayer TPtarget;

    private Inventory tradeChest;

    private boolean SenderAprove = false;
    private boolean TargetAprove = false;

    List<ItemStack> senderInventory;
    List<ItemStack> targetInventory;

    public static String endTradeMessage;

    //this is a slot number for each of the traders
    public static final List<Integer> senderSlots = Arrays.asList(0, 1, 2, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    public static final List<Integer> targetSlots = Arrays.asList(6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53);

    public enum Role {

        SENDER, TARGET
    };

    String[] protectedNames = {"Accept", "Divider", "Instructions", "Status"};
    List protNameList = Arrays.asList(protectedNames);

    public TradeManager(CloudTrade plugin, Player main, Player target) {
        this.p = plugin;
        TPsender = new TradePlayer(this, main, Role.SENDER);
        TPtarget = new TradePlayer(this, target, Role.TARGET);

        tcLayout = new TradeChestLayout(this);
        //sets vars
        endTradeMessage = p.c("tradeComplete");
    }

    public void onItemChange() {

        SenderAprove = false;
        TargetAprove = false;

        tcLayout.resetStatusBlock(SenderAprove, TargetAprove);
        updateRequest();

    }

    public void completeTrade() {
        List<ItemStack> targItems = getTargetItems();
        List<ItemStack> sendItems = getSenderItems();

        if (p.getConfig().getBoolean("logAllTrades")) {
            TradeLogger log = new TradeLogger(this, targItems, sendItems);
        }

        this.addToInventorySender(targItems);
        this.addToInventoryTarget(sendItems);

        TPsender.getPlayer().sendMessage(endTradeMessage);
        TPtarget.getPlayer().sendMessage(endTradeMessage);
        //removes players from the "busy" list
        p.getTraders().remove(TPsender.getPlayer().getName());
        p.getTraders().remove(TPtarget.getPlayer().getName());
        //removes target from request list
        p.getRequests().remove(TPtarget.getPlayer().getName());
        //returns inventory to same person
        TPsender.getPlayer().getOpenInventory().close();
        TPtarget.getPlayer().getOpenInventory().close();

        TPsender.getPlayer().playSound(TPsender.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10, 1);
        TPtarget.getPlayer().playSound(TPtarget.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10, 1);

        TPsender.getPlayer().updateInventory();
        TPtarget.getPlayer().updateInventory();

        //endTrade();
    }

    public void onItemAccept(boolean _isSender) {
        //Checks what roll is person who accepted.  
        if (_isSender) {
            SenderAprove = true;
        } else {
            TargetAprove = true;
        }
        //if both players accept,  complete the trade
        if (SenderAprove && TargetAprove) {
            completeTrade();
        } else {
            //updates our status block
            tcLayout.updateStatusBlock(SenderAprove, TargetAprove);
        }
        //sets the strings to add status to the lore on the STATUS block

        //updateRequest();
    }

    public void sendRequest() {

        //adds names to traders hashmap<playername,TradePlayer> to find "busy" players in trade already.
        p.getTraders().put(TPsender.getPlayer().getName(), TPsender);
        p.getTraders().put(TPtarget.getPlayer().getName(), TPtarget);

        //adds a reference to check if accepts
        p.getRequests().put(TPtarget.getPlayer().getName(), TPtarget);

        //starts a request
        tradeRequest = new TradeRequest(this);
        tradeRequest.requestTrade();

        //send the messages for trade requests.
        TPsender.getPlayer().sendMessage(p.c("requestSent").replace("%player", TPtarget.getPlayer().getName()));
        TPtarget.getPlayer().sendMessage(p.c("requestRecieved").replace("%player", TPsender.getPlayer().getName()));

    }

    public void acceptTrade() {
        targetAccepted = true;

        startTrade();
    }

    public void declineTrade() {

        p.getServer().getScheduler().cancelTask(tradeRequest.getTaskId());
        //removes players from the "busy" list
        p.getTraders().remove(TPsender.getPlayer().getName());
        p.getTraders().remove(TPtarget.getPlayer().getName());
        //removes target from request list
        p.getRequests().remove(TPtarget.getPlayer().getName());
        TPsender.getPlayer().sendMessage(p.c("denyRecieved").replace("%player", TPtarget.getPlayer().getName()));
        TPtarget.getPlayer().sendMessage(p.c("denyRecieved").replace("%player", TPsender.getPlayer().getName()));

    }

    public void startTrade() {
        tradeChest = tcLayout.createTradeChest();

        //opens players inventories
        TPsender.getPlayer().openInventory(tradeChest);
        TPsender.getPlayer().playSound(TPsender.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10, 1);
        TPtarget.getPlayer().openInventory(tradeChest);
        TPtarget.getPlayer().playSound(TPtarget.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10, 1);

    }

    public boolean isProtectedBlock(ItemStack testblock) {
        //our protected blocks have metadata
        if (testblock.getItemMeta() == null) {
            return false;
        }

        String displaName = testblock.getItemMeta().getDisplayName();

        if (protNameList.contains(displaName)) {
            return true;
        }
        return false;
    }

    public boolean isPlaceableSlot(int slot, boolean isSender) {
        if (isSender) {
            if (senderSlots.contains(slot)) {
                return true;
            }
        } else {
            if (targetSlots.contains(slot)) {
                return true;
            }
        }
        return false;
    }

    public void updateRequest() {
        try {
            p.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
                @Override
                public void run() {
                    TPsender.getPlayer().updateInventory();
                    TPtarget.getPlayer().updateInventory();
                }
            });
        } catch (Exception e) {
            // Request was already accepted, or something stupid happened.
        }
    }

    //abort the trade
    public void endTrade() {
        //removes players from the "busy" list
        p.getTraders().remove(TPsender.getPlayer().getName());
        p.getTraders().remove(TPtarget.getPlayer().getName());
        //removes target from request list
        p.getRequests().remove(TPtarget.getPlayer().getName());
        //returns inventory to same person
        this.addToInventorySender(getSenderItems());
        this.addToInventoryTarget(getTargetItems());

        TPsender.getPlayer().sendMessage(p.c("tradeCanceled"));
        TPsender.getPlayer().getOpenInventory().close();
        TPsender.getPlayer().playSound(TPsender.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_PIG_ANGRY, 10, 1);
        TPtarget.getPlayer().sendMessage(p.c("tradeCanceled"));
        TPtarget.getPlayer().getOpenInventory().close();
        TPtarget.getPlayer().playSound(TPtarget.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_PIG_ANGRY, 10, 1);

        TPsender.getPlayer().updateInventory();
        TPtarget.getPlayer().updateInventory();

        //updateRequest();
    }

    public void resetTrade() {
        //removes items from inventories

        //reset trade
    }

    //checks to see if person is in trade already.
    public boolean isSenderBusy() {
        boolean busy = p.getTraders().containsKey(TPsender.getPlayer().getName());
        return busy;

    }

    public boolean isTargetBusy() {
        boolean busy = p.getTraders().containsKey(TPtarget.getPlayer().getName());
        return busy;
    }

    public CloudTrade getPlugin() {
        return p;
    }

    public TradePlayer getTPsender() {
        return TPsender;
    }

    public TradePlayer getTPtarget() {
        return TPtarget;
    }

    // get/set for Accepted Trades
    public boolean isAccepted() {
        return targetAccepted;
    }

    public void setAccepted(boolean _targetAccepted) {
        targetAccepted = _targetAccepted;
    }

    // get/set for Declined trades
    public boolean isDeclined() {
        return targetDeclined;
    }

    public void setDeclined(boolean _targetDeclined) {
        targetDeclined = _targetDeclined;
    }

    public List<ItemStack> getSenderItems() {
        senderInventory = new ArrayList<ItemStack>();

        for (int i = 0; i < senderSlots.size(); i++) {
            int slot = senderSlots.get(i);
            senderInventory.add(tradeChest.getItem(slot));
        }
        return senderInventory;
    }

    public List<ItemStack> getTargetItems() {
        targetInventory = new ArrayList<ItemStack>();

        for (int i = 0; i < targetSlots.size(); i++) {
            int slot = targetSlots.get(i);
            targetInventory.add(tradeChest.getItem(slot));
        }
        return targetInventory;
    }

    public void addToInventorySender(List<ItemStack> addedItemStacks) {
        String messageItems = p.c("added");
        Inventory sortInventory = p.getServer().createInventory(null, ChestSize);
        //organises items into neat stacks inside this inventory
        for (int i = 0; i < addedItemStacks.size(); i++) {
            if (addedItemStacks.get(i) == null) {
                continue;
            }
            sortInventory.addItem(addedItemStacks.get(i));
        }
        //put organised inventory to player inventory... Redundant?  Maybe, but its ornganised.
        for (int i = 0; i < sortInventory.getContents().length; i++) {
            ItemStack oneItem = sortInventory.getItem(i);
            if (oneItem == null) {
                continue;
            }

            if (TPsender.getPlayer().getInventory().firstEmpty() == -1) {
                //no space in inventory
                Location loc = TPsender.getPlayer().getLocation();
                TPsender.getPlayer().getWorld().dropItem(loc, oneItem);

            } else {
                //space in inventory

                TPsender.getPlayer().getInventory().addItem(oneItem);
            }

            //gets Item Name with the ammount
            String oneItemDescription = ChatColor.AQUA + oneItem.getType().name() + "x" + ChatColor.DARK_GREEN + oneItem.getAmount() + "  ";
            //adds all Item strings to form a message
            messageItems = messageItems + oneItemDescription;
        }
        if (p.getConfig().getBoolean("displayItemsAfterTrade")) {
            TPsender.getPlayer().sendMessage(messageItems);
        }
    }

    public void addToInventoryTarget(List<ItemStack> addedItemStacks) {
        String messageItems = p.c("added");
        Inventory sortInventory = p.getServer().createInventory(null, ChestSize);
        //organises items into neat stacks inside this inventory
        for (int i = 0; i < addedItemStacks.size(); i++) {
            if (addedItemStacks.get(i) == null) {
                continue;
            }
            sortInventory.addItem(addedItemStacks.get(i));
        }
        //put organised inventory to player inventory... Redundant?  Maybe, but its ornganised.
        for (int i = 0; i < sortInventory.getContents().length; i++) {
            ItemStack oneItem = sortInventory.getItem(i);
            if (oneItem == null) {
                continue;
            }

            if (TPtarget.getPlayer().getInventory().firstEmpty() == -1) {
                //no space in inventory
                Location loc = TPtarget.getPlayer().getLocation();
                TPtarget.getPlayer().getWorld().dropItem(loc, oneItem);

            } else {
                //space in inventory

                TPtarget.getPlayer().getInventory().addItem(oneItem);
            }

            //gets Item Name with the ammount
            String oneItemDescription = ChatColor.AQUA + oneItem.getType().name() + "x" + ChatColor.DARK_GREEN + oneItem.getAmount() + "  ";
            //adds all Item strings to form a message
            messageItems = messageItems + oneItemDescription;
        }
        if (p.getConfig().getBoolean("displayItemsAfterTrade")) {
            TPtarget.getPlayer().sendMessage(messageItems);
        }
    }

}
