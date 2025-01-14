package me.ganzatron.anniApplication;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    private final InventoryManager inventoryManager;
    private final ArmorStandManager armorStandManager;
    private final GameManager gameManager;

    public EventListener(InventoryManager inventoryManager, ArmorStandManager armorStandManager, GameManager gameManager) {
        this.inventoryManager = inventoryManager;
        this.armorStandManager = armorStandManager;
        this.gameManager = gameManager;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = event.getItem();

        if (itemInHand != null && itemInHand.getType() == Material.EMERALD) {
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                player.sendMessage(ChatColor.GREEN + "Starting the game...");
                gameManager.startGame(player);
                player.setLevel(1);
            }
        }
        if (itemInHand != null && (itemInHand.getType() == Material.RED_DYE ||
                itemInHand.getType() == Material.YELLOW_DYE ||
                itemInHand.getType() == Material.BLUE_DYE)) {

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                String color = getColorFromDye(itemInHand.getType());
                inventoryManager.openColorInventory(player, color);
            }
        }
        if (itemInHand != null && itemInHand.getType() == Material.REDSTONE) {
            if (event.getAction().toString().contains("RIGHT_CLICK")) {
                player.sendMessage(ChatColor.GREEN + "Game Stopped");
                gameManager.stopGame(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null) return;
        Inventory redInventory = inventoryManager.getRedInventory();
        Inventory yellowInventory = inventoryManager.getYellowInventory();
        Inventory blueInventory = inventoryManager.getBlueInventory();

        // Prevent moving items into or out of the armor stands' inventories
        if (clickedInventory.equals(redInventory) || clickedInventory.equals(yellowInventory) || clickedInventory.equals(blueInventory)) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType() != Material.AIR) {
                event.setCancelled(true);
                clickedInventory.remove(item); // Remove mismatched items

                // Close the inventory after interaction
                player.closeInventory();

                // Determine which armor stand and color inventory to check
                ArmorStand targetStand = null;
                String color = "";
                if (clickedInventory.equals(redInventory)) {
                    targetStand = armorStandManager.getLeftStand();
                    color = "Red";
                } else if (clickedInventory.equals(yellowInventory)) {
                    targetStand = armorStandManager.getMiddleStand();
                    color = "Yellow";
                } else if (clickedInventory.equals(blueInventory)) {
                    targetStand = armorStandManager.getRightStand();
                    color = "Blue";
                }

                if (targetStand != null) {
                    boolean complete = inventoryManager.checkInventoryForArmor(player, clickedInventory, targetStand);
                    if (complete) {
                        if (!inventoryManager.isColorCompleted(color)) {
                            inventoryManager.addCompletedColor(color);
                            player.sendMessage(ChatColor.GREEN + color + " inventory complete!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        Inventory redInventory = inventoryManager.getRedInventory();
        Inventory yellowInventory = inventoryManager.getYellowInventory();
        Inventory blueInventory = inventoryManager.getBlueInventory();

        if (closedInventory.equals(redInventory) || closedInventory.equals(yellowInventory) || closedInventory.equals(blueInventory)) {
            ArmorStand targetStand = null;
            String color = "";

            if (closedInventory.equals(redInventory)) {
                targetStand = armorStandManager.getLeftStand();
            } else if (closedInventory.equals(yellowInventory)) {
                targetStand = armorStandManager.getMiddleStand();
            } else if (closedInventory.equals(blueInventory)) {
                targetStand = armorStandManager.getRightStand();
            }

            if (targetStand != null) {
                inventoryManager.checkInventoryForArmor(player, closedInventory, targetStand);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();
            String color = armorStand.getCustomName().split(" ")[0].toLowerCase();
            inventoryManager.openColorInventory(event.getPlayer(), color); // Open corresponding inventory
        }
    }

    private String getColorFromDye(Material dye) {
        if (dye == Material.RED_DYE) {
            return "red";
        } else if (dye == Material.YELLOW_DYE) {
            return "yellow";
        } else if (dye == Material.BLUE_DYE) {
            return "blue";
        }
        return "";
    }
}
