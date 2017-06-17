package com.solarcloud7.cloudtrade;


import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TradeLogger {

    TradeLogger(TradeManager _tm, List<ItemStack> _senderItems, List<ItemStack> _targetItems) {
        try {
            File a = new File("plugins/CloudTrade/tradelogs.txt");
            FileWriter f = new FileWriter(a, true);
            if (!a.exists()) {
                a.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(f);

            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
            Calendar cal = Calendar.getInstance();
            String sDate = dateFormat.format(cal.getTime());

            String sTPsenderName = _tm.getTPsender().getPlayer().getName();
            String sTPtargetName = _tm.getTPtarget().getPlayer().getName();

            out.write("[" + sDate + "]  [" + sTPsenderName + "]-[" + sTPtargetName + "]");
            out.newLine();
            out.write("   " + sTPsenderName + "]:");
            for (ItemStack i : _senderItems) {
                if(i == null){
                    continue;
                }
                out.write(" {" + i.getType().name()+" x"+ i.getAmount()+"}");
            } 
            out.newLine();
            out.write("   " + sTPtargetName + "]:");
            for (ItemStack i : _senderItems) {
                if(i == null){
                    continue;
                }
                out.write(" {" + i.getType().name()+" x"+ i.getAmount()+"}");
            } 
            out.newLine();
            out.newLine();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
