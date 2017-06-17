package com.solarcloud7.cloudtrade;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handler for the /pos sample command.
 *
 * @author SpaceManiac
 */
public class CommandListener implements CommandExecutor {

    private CloudTrade p; // pointer to your main class, unrequired if you don't need methods from the main class

    public CommandListener(CloudTrade plugin) {
        this.p = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //Not from cmd
        if (sender instanceof Player == false) {
            sender.sendMessage(p.c("errPlayer"));
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("tradereload")) {
            if (!sender.hasPermission("cloudtrade.reload")) {
                sender.sendMessage(p.c("errPermissions"));
                return true;
            }
            p.reloadConfig();
            p.reloadLangFile();
            sender.sendMessage(p.c("reloaded"));
            return true;
        }

        //Select our principle command or pass
        if (!cmd.getName().equalsIgnoreCase("trade")) {
            return false;
        }
        //permissions check
        if (!sender.hasPermission("cloudtrade.cantrade")) {
            sender.sendMessage(p.c("errPermissions"));
            return true;
        }
        //Trade commands ennabled in config?
        if(!p.getConfig().getBoolean("useCommandsForTrading")){
            sender.sendMessage(p.c("errTradeCommandsOff"));
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        //check for arguments from principle command
        if (args[0].equalsIgnoreCase("accept")) {
            return accept(sender, args);
        } else if (args[0].equalsIgnoreCase("deny")) {
            return decline(sender, args);
        } else {
            return request(sender, args);
        }

    }

    private boolean request(CommandSender sender, String[] args) {

        List<Player> matches = p.getServer().matchPlayer(args[0]);
        //checks for valid target
        if (matches.isEmpty()) {
            sender.sendMessage(p.c("errTargetOffline").replace("%target", args[0]));
            return true;
        }

        //sets player and target
        Player pSender = (Player) sender;
        Player pTarget = matches.get(0);

        if (p.getConfig().getBoolean("bothPlayersMustHavePermissions")) {
            if (!pTarget.hasPermission("cloudtrade.cantrade")) {
                pSender.sendMessage(p.c("errTargetNoPermission"));
                return true;
            }
        }
        //not trade with self
        if (pSender == pTarget) {
            pSender.sendMessage(p.c("errTradeSelf"));
            return true;
        }
        //funny creative message
        if (pSender.getGameMode() == GameMode.CREATIVE) {
            pSender.sendMessage(p.c("errTradeCreative"));
        }

        //distance check
        if (!distCheck(pSender, pTarget)) {
            return true;
        }

        // sets up a trade manager
        TradeManager tm = new TradeManager(p, pSender, pTarget);
        //aborts if players are already in a trade
        if (tm.isSenderBusy()) {
            pSender.sendMessage(p.c("errBusyWithTrade"));
            return true;
        } else if (tm.isTargetBusy()) {
            pSender.sendMessage(p.c("errTargetBusy"));
            return true;
        }
        tm.sendRequest();

        return true;
    }

    private boolean distCheck(Player pSender, Player pTarget) {
        int maxDistance = p.getConfig().getInt("max-distance");
        //must be same world
        if (p.getConfig().getBoolean("tradeFromDifferentWorlds") == false) {

            //must be same world AND not same world = cancel
            if (pSender.getWorld().getName().equalsIgnoreCase(pTarget.getWorld().getName()) == false) {
                pSender.sendMessage(p.c("errDifferentWorlds").replace("%player", pTarget.getName()));
                return false;
            }

            //same world
            double realDistance = pSender.getLocation().distance(pTarget.getLocation());
            int dist = (int) realDistance;
            //farther than config max distance.
            if (realDistance > maxDistance) {
                String m = p.c("errTooFarAway");
                m = m.replace("%target", pTarget.getName());
                m = m.replace("%maxDistance", Integer.toString(maxDistance));
                m = m.replace("%dist", Integer.toString(dist));
                pSender.sendMessage(m);
                return false;
            }

        }
        //if not same world (with the config tradeFromDifferentWorlds true
        if (pSender.getWorld().getName().equalsIgnoreCase(pTarget.getWorld().getName()) == false) {
            //checks to make sure there is not a trade distance.  If there is... force trade from same world.
            if (maxDistance != 0) {
                pSender.sendMessage(p.c("errDifferentWorldsWithMaxTradeDistance").replace("%target", pTarget.getName()));
                return false;
            }
        }

        return true;
    }

    private boolean accept(CommandSender sender, String[] args) {

        //Returns the TradePlayer from HashMap using the sender's name as a key.  OR it returns NULL   
        TradePlayer target = p.getRequests().get(sender.getName());

        //no request
        if (target == null) {
            sender.sendMessage(p.c("errNothingToAccept"));
            return true;
        } else {

            Player pSender = (Player) sender;
            if (pSender.getGameMode() == GameMode.CREATIVE) {
                pSender.sendMessage(p.c("errTradeCreative"));
            }

            Player s = target.getTradeManager().getTPsender().getPlayer();
            Player t = target.getTradeManager().getTPtarget().getPlayer();

            //switched Target and sender so the target player (the person who needs to accept) recieves the error messages for too far.
            if (!distCheck(t, s)) {
                return true;
            }

            target.getTradeManager().acceptTrade();
            return true;
        }
    }

    private boolean decline(CommandSender sender, String[] args) {
        //Returns the TradePlayer from HashMap using the sender's name as a key.  OR it returns NULL   
        TradePlayer target = p.getRequests().get(sender.getName());
        //no request
        if (target == null) {
            sender.sendMessage(p.c("errNothingToDeny"));
            return true;
        } else {

            Player s = target.getTradeManager().getTPsender().getPlayer();
            Player t = target.getTradeManager().getTPtarget().getPlayer();
            
            //switched Target and sender so the target player (the person who needs to accept) recieves the error messages for too far.
            if (!distCheck(t, s)) {
                return true;
            }

            target.getTradeManager().declineTrade();
            return true;
        }
    }

}
