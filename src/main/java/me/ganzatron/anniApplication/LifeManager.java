package me.ganzatron.anniApplication;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LifeManager {

    private final Map<Player, Integer> playerLives = new HashMap<>();

    public void setPlayerLives(Player player, int lives) {
        playerLives.put(player, lives);
    }

    public int getPlayerLives(Player player) {
        return playerLives.getOrDefault(player, 3);  // Default to 3 lives if not set
    }

    public void reducePlayerLives(Player player) {
        int currentLives = getPlayerLives(player);
        currentLives--;
        setPlayerLives(player, currentLives);

        if (currentLives <= 0) {
            player.sendMessage("You have lost all your lives! Game Over!");
            InventoryManager.setGameRunning(false);
        } else {
            player.sendMessage("You have " + currentLives + " lives remaining.");
        }
    }
}
