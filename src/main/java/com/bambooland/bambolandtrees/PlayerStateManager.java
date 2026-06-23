package com.bambooland.bambolandtrees;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Gestiona qué jugadores tienen activado el modo BLTree.
 */
public class PlayerStateManager {

    // Conjunto de UUIDs de jugadores con el modo activo
    private final Set<UUID> activePlayers = new HashSet<>();

    public void enable(UUID uuid) {
        activePlayers.add(uuid);
    }

    public void disable(UUID uuid) {
        activePlayers.remove(uuid);
    }

    public boolean isEnabled(UUID uuid) {
        return activePlayers.contains(uuid);
    }

    public void toggle(UUID uuid) {
        if (isEnabled(uuid)) {
            disable(uuid);
        } else {
            enable(uuid);
        }
    }
}
