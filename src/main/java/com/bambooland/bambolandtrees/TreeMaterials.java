package com.bambooland.bambolandtrees;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

/**
 * Todos los materiales relacionados con árboles.
 * Incluye troncos normales (con textura de anillos arriba/abajo),
 * madera con corteza (stripped o wood, corteza en las 6 caras),
 * y hojas de todos los tipos.
 */
public class TreeMaterials {

    // Troncos normales (LOG): tienen anillos en las caras superior/inferior
    public static final Set<Material> LOGS = EnumSet.of(
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,
        Material.DARK_OAK_LOG,
        Material.MANGROVE_LOG,
        Material.CHERRY_LOG,
        Material.PALE_OAK_LOG, 
        Material.BAMBOO_BLOCK  
    );

    // Madera con corteza en las 6 caras (WOOD): visualmente uniforme
    public static final Set<Material> WOOD_BLOCKS = EnumSet.of(
        Material.OAK_WOOD,
        Material.SPRUCE_WOOD,
        Material.BIRCH_WOOD,
        Material.JUNGLE_WOOD,
        Material.ACACIA_WOOD,
        Material.DARK_OAK_WOOD,
        Material.MANGROVE_WOOD,
        Material.CHERRY_WOOD,
        Material.PALE_OAK_WOOD 
    );

    // Troncos sin corteza (stripped logs)
    public static final Set<Material> STRIPPED_LOGS = EnumSet.of(
        Material.STRIPPED_OAK_LOG,
        Material.STRIPPED_SPRUCE_LOG,
        Material.STRIPPED_BIRCH_LOG,
        Material.STRIPPED_JUNGLE_LOG,
        Material.STRIPPED_ACACIA_LOG,
        Material.STRIPPED_DARK_OAK_LOG,
        Material.STRIPPED_MANGROVE_LOG,
        Material.STRIPPED_CHERRY_LOG,
        Material.STRIPPED_PALE_OAK_LOG, 
        Material.STRIPPED_BAMBOO_BLOCK
    );

    // Madera sin corteza (stripped wood) — 6 caras, pelada
    public static final Set<Material> STRIPPED_WOOD_BLOCKS = EnumSet.of(
        Material.STRIPPED_OAK_WOOD,
        Material.STRIPPED_SPRUCE_WOOD,
        Material.STRIPPED_BIRCH_WOOD,
        Material.STRIPPED_JUNGLE_WOOD,
        Material.STRIPPED_ACACIA_WOOD,
        Material.STRIPPED_DARK_OAK_WOOD,
        Material.STRIPPED_MANGROVE_WOOD,
        Material.STRIPPED_CHERRY_WOOD,
        Material.STRIPPED_PALE_OAK_WOOD 
    );

    // Todos los bloques que cuentan como "madera" del árbol (consumen hacha)
    public static final Set<Material> ALL_WOOD = EnumSet.noneOf(Material.class);

    static {
        ALL_WOOD.addAll(LOGS);
        ALL_WOOD.addAll(WOOD_BLOCKS);
        ALL_WOOD.addAll(STRIPPED_LOGS);
        ALL_WOOD.addAll(STRIPPED_WOOD_BLOCKS);
    }

    // Hojas (no consumen hacha, se rompen gratis)
    public static final Set<Material> LEAVES = EnumSet.of(
        Material.OAK_LEAVES,
        Material.SPRUCE_LEAVES,
        Material.BIRCH_LEAVES,
        Material.JUNGLE_LEAVES,
        Material.ACACIA_LEAVES,
        Material.DARK_OAK_LEAVES,
        Material.MANGROVE_LEAVES,
        Material.CHERRY_LEAVES,
        Material.PALE_OAK_LEAVES,
        Material.AZALEA_LEAVES,
        Material.FLOWERING_AZALEA_LEAVES
    );

    // Herramientas tipo hacha
    public static final Set<Material> AXES = EnumSet.of(
        Material.WOODEN_AXE,
        Material.STONE_AXE,
        Material.COPPER_AXE,
        Material.IRON_AXE,
        Material.GOLDEN_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE
    );

    public static boolean isWood(Material m) {
        return ALL_WOOD.contains(m);
    }

    public static boolean isLeaf(Material m) {
        return LEAVES.contains(m);
    }

    public static boolean isAxe(Material m) {
        return AXES.contains(m);
    }

    public static boolean isTreeBlock(Material m) {
        return isWood(m) || isLeaf(m);
    }
}