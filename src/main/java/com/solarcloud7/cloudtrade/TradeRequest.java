package com.solarcloud7.cloudtrade;

public class TradeRequest {

    private TradeManager tradeManager;
    private CloudTrade plugin;

    TradePlayer sender;
    TradePlayer target;

    private int taskId;

    public TradeRequest(TradeManager _tradeManager) {
        this.tradeManager = _tradeManager;
        this.plugin = tradeManager.getPlugin();
    }

    public void requestTrade() {
        sender = tradeManager.getTPsender();
        target = tradeManager.getTPsender();

        //starts a timer to cancel the request
        timeRequest(plugin.getConfig().getInt("tradeAcceptTimeout"));

    }

    private void timeRequest(int seconds) {
        try {
            long s = seconds * 20;
            taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { //obtain task id so we can cancel it later on if need be.
                @Override
                public void run() {
                    cancelRequest();
                }
            }, s);
        } catch (Exception e) {
            // Request was already accepted, or something stupid happened.
        }
    }

    public void cancelRequest() {
        if (tradeManager.isAccepted()==true) {
            return;
        }
        if (tradeManager.isDeclined()==true) {
            return;
        }
        tradeManager.declineTrade();
    }

    public int getTaskId() {
        return taskId;
    }
}
