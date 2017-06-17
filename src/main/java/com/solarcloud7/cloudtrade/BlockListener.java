
package com.solarcloud7.cloudtrade;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * Sample block listener
 * @author Dinnerbone
 */
public class BlockListener implements Listener {
    
    private final CloudTrade plugin;
    
    public BlockListener(CloudTrade instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        
    }

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        
    }
}
