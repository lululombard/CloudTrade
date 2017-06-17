package com.solarcloud7.cloudtrade;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.lang.reflect.Field;

/**
 *
 * @author Ryan
 */
public class TradeChestLayout {

    private CloudTrade plugin;
    private TradeManager tradeManager;
    private TradePlayer TPsender;
    private TradePlayer TPtarget;

    private Inventory tradeChest;

    //trade chest layout
    private ItemStack separator;
    private ItemStack status;
    private ItemStack cancelButton;
    private ItemStack pAccept;
    private ItemStack tAccept;

    public static int ChestSize = 54;

    // public static final List<Integer> senderSlots = Arrays.asList(0, 1, 2, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    //public static final List<Integer> targetSlots = Arrays.asList(6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53);
    public static final String tradeboxName = "Cloud Trading Network";

    String[] protectedNames = {"Accept", "Divider", "Instructions", "Status"};
    List protNameList = Arrays.asList(protectedNames);

    public TradeChestLayout(TradeManager _tradeManager) {
        tradeManager = _tradeManager;
        plugin = tradeManager.getPlugin();
        TPsender = tradeManager.getTPsender();
        TPtarget = tradeManager.getTPtarget();

    }

    public Inventory createTradeChest() {
        tradeChest = plugin.getServer().createInventory(null, ChestSize, tradeboxName);

        //Divider items 
        separator = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta IM = separator.getItemMeta();
        IM.setLore(null);
        IM.setDisplayName("Divider");
        List<String> separatorLore = Arrays.asList(TPsender.getPlayer().getName(), "<------", "", TPtarget.getPlayer().getName(), "------>");
        IM.setLore(separatorLore);
        separator.setItemMeta(IM);

        //SENDER Accept items 
        pAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta pAcceptMeta = pAccept.getItemMeta();
        pAcceptMeta.setLore(null);
        pAcceptMeta.setDisplayName("Accept");
        List<String> pAcceptMetaLore = Arrays.asList(TPsender.getPlayer().getName());
        pAcceptMeta.setLore(pAcceptMetaLore);
        pAccept.setItemMeta(pAcceptMeta);

        //TARGET Accept items 
        tAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta tAcceptMeta = tAccept.getItemMeta();
        tAcceptMeta.setLore(null);
        tAcceptMeta.setDisplayName("Accept");
        List<String> tAcceptMetaLore = Arrays.asList(TPtarget.getPlayer().getName());
        tAcceptMeta.setLore(tAcceptMetaLore);
        tAccept.setItemMeta(tAcceptMeta);

        //sets the strings to add status to the lore on the STATUS block
        String sender = TPsender.getPlayer().getName() + " Accepted: False";
        String target = TPtarget.getPlayer().getName() + " Accepted: False";
        //sets the block yellow for PENDING
        //status items 
        status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta statusButtonMeta = status.getItemMeta();
        statusButtonMeta.setDisplayName("Status");
        List<String> statusLore = Arrays.asList(sender, target);
        statusButtonMeta.setLore(statusLore);
        status.setItemMeta(statusButtonMeta);

        //dividers
        //tradeChest.setItem(18, instructionsBook);
        tradeChest.setItem(3, pAccept);
        tradeChest.setItem(4, status);
        tradeChest.setItem(5, tAccept);
        tradeChest.setItem(13, separator);
        tradeChest.setItem(22, separator);
        tradeChest.setItem(31, separator);
        tradeChest.setItem(40, separator);
        tradeChest.setItem(49, separator);
        return tradeChest;
    }

    public void updateStatusBlock(boolean SenderAprove, boolean TargetAprove) {
        String sender = TPtarget.getPlayer().getName() + " Accepted: " + SenderAprove;
        String target = TPsender.getPlayer().getName() + " Accepted: " + TargetAprove;
        //sets the block yellow for PENDING
        //status items 
        status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta statusButtonMeta = status.getItemMeta();
        statusButtonMeta.setDisplayName("Status");
        List<String> statusLore = Arrays.asList(sender, target);
        statusButtonMeta.setLore(statusLore);
        status.setItemMeta(statusButtonMeta);
        tradeChest.setItem(4, status);

        //SENDER Accept items 
        pAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta pAcceptMeta = pAccept.getItemMeta();
        pAcceptMeta.setLore(null);
        pAcceptMeta.setDisplayName("Accept");
        List<String> pAcceptMetaLore = Arrays.asList(TPsender.getPlayer().getName());
        pAcceptMeta.setLore(pAcceptMetaLore);
        pAccept.setItemMeta(pAcceptMeta);
        if (SenderAprove) {
            tradeChest.setItem(3, (pAccept));
        } else {
            tradeChest.setItem(3, pAccept);
        }

        //TARGET Accept items 
        tAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta tAcceptMeta = tAccept.getItemMeta();
        tAcceptMeta.setLore(null);
        tAcceptMeta.setDisplayName("Accept");
        List<String> tAcceptMetaLore = Arrays.asList(TPtarget.getPlayer().getName());
        tAcceptMeta.setLore(tAcceptMetaLore);
        tAccept.setItemMeta(tAcceptMeta);
        if (TargetAprove) {
            tradeChest.setItem(5, tAccept);
        } else {
            tradeChest.setItem(5, tAccept);
        }

    }

    public void resetStatusBlock(boolean SenderAprove, boolean TargetAprove) {

        //sets the strings to add status to the lore on the STATUS block
        String sender = TPsender.getPlayer().getName() + " Accepted: False";
        String target = TPtarget.getPlayer().getName() + " Accepted: False";
        //sets the block yellow for PENDING
        //status items 
        status = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta statusButtonMeta = status.getItemMeta();
        statusButtonMeta.setDisplayName("Status");
        List<String> statusLore = Arrays.asList(sender, target);
        statusButtonMeta.setLore(statusLore);
        status.setItemMeta(statusButtonMeta);

        tradeChest.setItem(4, status);
        
         //SENDER Accept items 
        pAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta pAcceptMeta = pAccept.getItemMeta();
        pAcceptMeta.setLore(null);
        pAcceptMeta.setDisplayName("Accept");
        List<String> pAcceptMetaLore = Arrays.asList(TPsender.getPlayer().getName());
        pAcceptMeta.setLore(pAcceptMetaLore);
        pAccept.setItemMeta(pAcceptMeta);
        if (SenderAprove) {
            tradeChest.setItem(3, pAccept);
        } else {
            tradeChest.setItem(3, pAccept);
        }

        //TARGET Accept items 
        tAccept = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
        ItemMeta tAcceptMeta = tAccept.getItemMeta();
        tAcceptMeta.setLore(null);
        tAcceptMeta.setDisplayName("Accept");
        List<String> tAcceptMetaLore = Arrays.asList(TPtarget.getPlayer().getName());
        tAcceptMeta.setLore(tAcceptMetaLore);
        tAccept.setItemMeta(tAcceptMeta);
        if (TargetAprove) {
            tradeChest.setItem(5, tAccept);
        } else {
            tradeChest.setItem(5, tAccept);
        }
    }



   
}
