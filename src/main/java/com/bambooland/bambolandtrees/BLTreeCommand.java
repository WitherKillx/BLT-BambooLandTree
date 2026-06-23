package com.bambooland.bambolandtrees;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BLTreeCommand implements CommandExecutor, TabCompleter {

    private final PlayerStateManager stateManager;

    public BLTreeCommand(PlayerStateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo los jugadores pueden usar este comando.");
            return true;
        }

        if (!player.hasPermission("bambolandtrees.use")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length == 0) {
            // Sin argumentos: mostrar estado actual
            boolean active = stateManager.isEnabled(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "[BLTree] " +
                ChatColor.WHITE + "Estado actual: " +
                (active ? ChatColor.GREEN + "ACTIVADO" : ChatColor.RED + "DESACTIVADO"));
            player.sendMessage(ChatColor.GRAY + "Usa /BLTree on o /BLTree off para cambiar.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                stateManager.enable(player.getUniqueId());
                player.sendMessage(ChatColor.GOLD + "[BLTree] " +
                    ChatColor.GREEN + "Tala automática ACTIVADA. " +
                    ChatColor.GRAY + "Rompe la base de un árbol con un hacha.");
            }
            case "off" -> {
                stateManager.disable(player.getUniqueId());
                player.sendMessage(ChatColor.GOLD + "[BLTree] " +
                    ChatColor.RED + "Tala automática DESACTIVADA.");
            }
            default -> {
                player.sendMessage(ChatColor.RED + "Uso incorrecto. Usa: /BLTree on | /BLTree off");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("on", "off");
        }
        return List.of();
    }
}
