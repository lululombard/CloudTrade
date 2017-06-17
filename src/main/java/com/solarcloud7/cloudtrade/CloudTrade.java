package com.solarcloud7.cloudtrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class CloudTrade extends JavaPlugin {

    private final PlayerListener playerListener = new PlayerListener(this);
    private final BlockListener blockListener = new BlockListener(this);
    private final EntityListener entityListener = new EntityListener(this);
    private final CommandListener commandListener = new CommandListener(this);
    private final InventoryListener inventoryListener = new InventoryListener(this);

    private FileConfiguration lang = null;
    private File langFile = null;

    //private TradeManager tradeManager = new TradeManager(this);
    private static HashMap<String, TradePlayer> traders = new HashMap<String, TradePlayer>();
    private static HashMap<String, TradePlayer> requests = new HashMap<String, TradePlayer>();
    //private static  ArrayList<TradePlayer> traders = new ArrayList<TradePlayer>();
    //private static  ArrayList<TradePlayer> requests = new ArrayList<TradePlayer>();

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(inventoryListener, this);

        // Register our commands
        getCommand("trade").setExecutor(commandListener);
        getCommand("tradereload").setExecutor(commandListener);

        //config
        getConfig().options().copyDefaults(true);
        saveConfig();

        //custom player log
        
        saveDefaultLangConfig();
        reloadLangFile();
        

    }

    @Override
    public void onDisable() {

    }

    public void reloadLangFile() {
        if (langFile == null) {
            langFile = new File(getDataFolder(), "lang.yml");
        }
        lang = YamlConfiguration.loadConfiguration(langFile);

        // Look for defaults in the jar
        /*
        InputStream defConfigStream = this.getResource("lang.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            lang.setDefaults(defConfig);
        }*/
        this.getLogger().info("Loaded Language File: "+lang.getString("LanguageName"));
    }

    public FileConfiguration getLangFile() {
        if (lang == null) {
            reloadLangFile();
        }
        return lang;
    }
    public void saveDefaultLangConfig() {
    if (langFile == null) {
        langFile = new File(getDataFolder(), "lang.yml");
    }
    if (!langFile.exists()) {            
         this.saveResource("lang.yml", false);
     }
}

    public void saveLangFile() {
        if (lang == null || langFile == null) {
            return;
        }
        try {
            getLangFile().save(langFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + langFile, ex);
        }
    }

    public String c(String name) {
        String caption = getLangFile().getString(name);
        if (caption == null) {
            this.getLogger().warning("Missing caption: " + name);
            caption = "&c[missing caption]";
        }

        caption = ChatColor.translateAlternateColorCodes('&', caption);

        return caption;
    }

    public HashMap<String, TradePlayer> getRequests() {
        return requests;
    }

    public HashMap<String, TradePlayer> getTraders() {
        return traders;
    }
}
