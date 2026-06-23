package com.bambooland.bambolandtrees;

import org.bukkit.plugin.java.JavaPlugin;

public class BambooLandTrees extends JavaPlugin {

    private PlayerStateManager playerStateManager;

    @Override
    public void onEnable() {
        playerStateManager = new PlayerStateManager();

        // Registrar el listener de eventos
        getServer().getPluginManager().registerEvents(
            new TreeChopListener(this, playerStateManager),
            this
        );

        // Registrar el comando
        BLTreeCommand command = new BLTreeCommand(playerStateManager);
        getCommand("bltree").setExecutor(command);
        getCommand("bltree").setTabCompleter(command);

        getLogger().info("BambooLandTrees activado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BambooLandTrees desactivado.");
    }
}
