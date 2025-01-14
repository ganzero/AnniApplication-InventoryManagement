package me.ganzatron.anniApplication;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class GameManager {
    private final InventoryManager inventoryManager;
    private final LifeManager lifeManager;
    private final ArmorStandManager armorStandManager;

    public GameManager(InventoryManager inventoryManager, LifeManager lifeManager, ArmorStandManager armorStandManager) {
        this.inventoryManager = inventoryManager;
        this.lifeManager = lifeManager;
        this.armorStandManager = armorStandManager;
    }

    public void startGame(Player player) {
        lifeManager.setPlayerLives(player, 3);
        inventoryManager.initializeInventories(player, true);
        spawnArmorStands(player);
        player.sendMessage(ChatColor.GREEN + "Starting Round " + InventoryManager.getGameRounds() + "!");
        Randomizer randomizer = new Randomizer(ArmorMaterials.getArmorMaterials());
        randomizer.addGuaranteedItems(armorStandManager.getArmorStandArmor());

        List<ItemStack> randomizedItems = randomizer.generateRandomizedItems();

        // Pass randomizedItems to the inventoryManager
        inventoryManager.startSpawningRandomArmor(player, randomizedItems);
    }

    public void stopGame(Player player) {
        armorStandManager.clearArmorStands();
        inventoryManager.initializeInventories(player, false);
    }

    public void spawnArmorStands(Player player) {
        Location playerLocation = player.getLocation();
        Location leftLocation = playerLocation.clone().add(-1, 0, -5); // Left stand
        Location middleLocation = playerLocation.clone().add(0, 0, -5); // Middle stand
        Location rightLocation = playerLocation.clone().add(1, 0, -5); // Right stand

        rotateLocation(leftLocation, playerLocation.getYaw() + 180);
        rotateLocation(middleLocation, playerLocation.getYaw() + 180);
        rotateLocation(rightLocation, playerLocation.getYaw() + 180);

        armorStandManager.spawnArmorStands(leftLocation, middleLocation, rightLocation);
    }

    private void rotateLocation(Location location, float yaw) {
        location.setYaw(yaw);
    }

}
