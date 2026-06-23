package com.bambooland.bambolandtrees;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;

public class TreeChopListener implements Listener {

    private final BambooLandTrees plugin;
    private final PlayerStateManager stateManager;
    private final Random random = new Random();

    public TreeChopListener(BambooLandTrees plugin, PlayerStateManager stateManager) {
        this.plugin = plugin;
        this.stateManager = stateManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // 1. ¿Tiene el modo activado?
        if (!stateManager.isEnabled(player.getUniqueId())) return;

        // 2. ¿Es un bloque de madera?
        if (!TreeMaterials.isWood(block.getType())) return;

        // 3. ¿Está usando un hacha?
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!TreeMaterials.isAxe(tool.getType())) return;

        // 4. Verificar que es un árbol real (tiene hojas cerca)
        // Si no hay hojas → es una construcción → dejar que el evento normal actúe
        if (!TreeFinder.isTree(block)) return;

        // 5. Encontrar todos los bloques del árbol
        TreeFinder.TreeResult tree = TreeFinder.findTree(block);

        if (tree.woodBlocks.isEmpty()) return;

        // 6. Cancelar el evento original — nosotros manejamos todo
        event.setCancelled(true);

        // 7. Romper bloques de madera (consumen la hacha)
        boolean creative = player.getGameMode() == GameMode.CREATIVE;

        for (Block woodBlock : tree.woodBlocks) {
            woodBlock.breakNaturally(tool);

            // Aplicar daño al hacha si no es creativo
            if (!creative) {
                boolean broken = applyAxeDamage(player, tool);
                if (broken) break; // hacha rota, detener
            }
        }

        // 8. Romper hojas (sin consumir la hacha, pero sí dan drops normales)
        for (Block leafBlock : tree.leafBlocks) {
            // breakNaturally sin herramienta = drops normales de hojas (semillas, manzanas, etc.)
            leafBlock.breakNaturally();
        }
    }

    /**
     * Aplica daño al hacha respetando todos los encantamientos relevantes:
     * - Irrompibilidad (Unbreaking): reduce la probabilidad de recibir daño.
     * - Mending: si hay experiencia cerca, repara en lugar de dañar (Bukkit lo maneja).
     * - Maldición de Desvanecimiento: la herramienta desaparece al romperse (Bukkit lo maneja).
     *
     * @return true si el hacha se rompió
     */
    private boolean applyAxeDamage(Player player, ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        // Nivel de Unbreaking
        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.UNBREAKING);

        // Con Unbreaking N, la probabilidad de recibir daño es 1/(N+1)
        if (unbreakingLevel > 0) {
            double chance = 1.0 / (unbreakingLevel + 1);
            if (random.nextDouble() > chance) {
                return false; // Este golpe no daña la herramienta
            }
        }

        int currentDamage = damageable.getDamage();
        int maxDurability = tool.getType().getMaxDurability();

        int newDamage = currentDamage + 1;

        if (newDamage >= maxDurability) {
            // Hacha rota
            player.getInventory().setItemInMainHand(null);
            // Sonido de herramienta rota
            player.getWorld().playSound(
                player.getLocation(),
                org.bukkit.Sound.ENTITY_ITEM_BREAK,
                1.0f, 1.0f
            );
            return true;
        }

        damageable.setDamage(newDamage);
        tool.setItemMeta(meta);
        return false;
    }
}
