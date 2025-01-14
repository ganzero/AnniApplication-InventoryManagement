package me.ganzatron.anniApplication;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryManager {

    private final LifeManager lifeManager;
    private final ArmorStandManager armorStandManager;
    private final Inventory redInventory;
    private final Inventory yellowInventory;
    private final Inventory blueInventory;
    private final Set<String> completedColors = new HashSet<>();
    private final int initialSpawnDelay = 45;
    private static int gameRounds = 1;
    private int spawnDelay;
    private static boolean gameRunning;

    public InventoryManager(LifeManager lifeManager, ArmorStandManager armorStandManager) {
        this.lifeManager = lifeManager;
        this.armorStandManager = armorStandManager;
        this.redInventory = Bukkit.createInventory(null, 27, ChatColor.RED + "Red Inventory");
        this.yellowInventory = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "Yellow Inventory");
        this.blueInventory = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Blue Inventory");
        this.spawnDelay = initialSpawnDelay;
        gameRunning = false;
    }

    public static void setGameRunning(boolean gameRunning) {
        InventoryManager.gameRunning = gameRunning;
    }
    public Inventory getRedInventory() {
        return redInventory;
    }

    public Inventory getYellowInventory() {
        return yellowInventory;
    }

    public Inventory getBlueInventory() {
        return blueInventory;
    }

    public boolean isColorCompleted(String color) {
        return completedColors.contains(color.toLowerCase());
    }

    public void addCompletedColor(String color) {
        completedColors.add(color.toLowerCase());
    }


    public static int getGameRounds() {
        return gameRounds;
    }

    public void incrementGameRounds() {
        gameRounds++;
    }


    public void initializeInventories(Player player, boolean startGame) {
        gameRunning = startGame;
        player.getInventory().clear();

        ItemStack startGameItem = new ItemStack(Material.EMERALD);
        ItemStack stopGameItem = new ItemStack(Material.REDSTONE);
        ItemStack redInventoryItem = new ItemStack(Material.RED_DYE, 1);
        ItemStack yellowInventoryItem = new ItemStack(Material.YELLOW_DYE, 1);
        ItemStack blueInventoryItem = new ItemStack(Material.BLUE_DYE, 1);

        // Set the display names for the items
        setItemName(startGameItem, ChatColor.DARK_PURPLE + "Start Game");
        setItemName(stopGameItem, ChatColor.DARK_PURPLE + "Stop Game");
        setItemName(redInventoryItem, ChatColor.RED + "Red Inventory");
        setItemName(yellowInventoryItem, ChatColor.YELLOW + "Yellow Inventory");
        setItemName(blueInventoryItem, ChatColor.BLUE + "Blue Inventory");

        if (gameRunning) {
            player.getInventory().addItem(stopGameItem);
        } else {
            player.getInventory().addItem(startGameItem);
        }
        player.getInventory().addItem(redInventoryItem);
        player.getInventory().addItem(yellowInventoryItem);
        player.getInventory().addItem(blueInventoryItem);

        for (int i = 0; i < 27; i++) {
            redInventory.setItem(i, new ItemStack(Material.AIR));
            yellowInventory.setItem(i, new ItemStack(Material.AIR));
            blueInventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    public void startSpawningRandomArmor(Player player, List<ItemStack> randomizedItems) {
        new BukkitRunnable() {
            private int count = 0;

            @Override
            public void run() {
                if (!gameRunning) {
                    cancel();
                    return;
                }
                if (isInventoryFull(player) || count >= randomizedItems.size()) {
                    lifeManager.reducePlayerLives(player);
                    initializeInventories(player, true);
                    count = 0;
                    Collections.shuffle(randomizedItems);

                    if (lifeManager.getPlayerLives(player) <= 0) {
                        armorStandManager.clearArmorStands();
                        initializeInventories(player, false);
                        cancel();
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "Life Lost!");
                }

                ItemStack item = randomizedItems.get(count++);
                player.getInventory().addItem(item);
            }
        }.runTaskTimer(AnniApplication.getPlugin(AnniApplication.class), 0, spawnDelay); // Spawn every second
    }

    private boolean isInventoryFull(Player player) {
        Inventory inventory = player.getInventory();

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public void openColorInventory(Player player, String color) {
        Inventory inventoryToOpen = null;

        switch (color.toLowerCase()) {
            case "red":
                inventoryToOpen = redInventory;
                break;
            case "yellow":
                inventoryToOpen = yellowInventory;
                break;
            case "blue":
                inventoryToOpen = blueInventory;
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown color! Inventory cannot be opened.");
                return;
        }

        player.openInventory(inventoryToOpen);
    }

    public boolean checkInventoryForArmor(Player player, Inventory inventory, ArmorStand armorStand) {

        ItemStack helmet = armorStand.getHelmet();
        ItemStack chestplate = armorStand.getChestplate();
        ItemStack leggings = armorStand.getLeggings();
        ItemStack boots = armorStand.getBoots();
        String color = armorStandManager.getArmorStandColor(armorStand);

        Map<String, Integer> itemSlotMap = new HashMap<>();

        for (int i = 0; i < 4; i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && item.getType() != Material.AIR) {
                if (item.getType().toString().contains("HELMET")) {
                    itemSlotMap.put("helmet", i);
                } else if (item.getType().toString().contains("CHESTPLATE")) {
                    itemSlotMap.put("chestplate", i);
                } else if (item.getType().toString().contains("LEGGINGS")) {
                    itemSlotMap.put("leggings", i);
                } else if (item.getType().toString().contains("BOOTS")) {
                    itemSlotMap.put("boots", i);
                }
            }
        }

        boolean isComplete = true;

        if (itemSlotMap.containsKey("helmet") && !itemsMatch(inventory.getItem(itemSlotMap.get("helmet")), helmet)) {
            player.sendMessage(ChatColor.RED + "Helmet mismatch! Entered: " +
                    inventory.getItem(itemSlotMap.get("helmet")).getType() + ", Expected: " + helmet.getType());
            inventory.setItem(itemSlotMap.get("helmet"), null);
            lifeManager.reducePlayerLives(player);
            isComplete = false;
        }
        if (itemSlotMap.containsKey("chestplate") && !itemsMatch(inventory.getItem(itemSlotMap.get("chestplate")), chestplate)) {
            player.sendMessage(ChatColor.RED + "Chestplate mismatch! Entered: " +
                    inventory.getItem(itemSlotMap.get("chestplate")).getType() + ", Expected: " + chestplate.getType());
            inventory.setItem(itemSlotMap.get("chestplate"), null);
            lifeManager.reducePlayerLives(player);
            isComplete = false;
        }
        if (itemSlotMap.containsKey("leggings") && !itemsMatch(inventory.getItem(itemSlotMap.get("leggings")), leggings)) {
            player.sendMessage(ChatColor.RED + "Leggings mismatch! Entered: " +
                    inventory.getItem(itemSlotMap.get("leggings")).getType() + ", Expected: " + leggings.getType());
            inventory.setItem(itemSlotMap.get("leggings"), null);
            lifeManager.reducePlayerLives(player);
            isComplete = false;
        }
        if (itemSlotMap.containsKey("boots") && !itemsMatch(inventory.getItem(itemSlotMap.get("boots")), boots)) {
            player.sendMessage(ChatColor.RED + "Boots mismatch! Entered: " +
                    inventory.getItem(itemSlotMap.get("boots")).getType() + ", Expected: " + boots.getType());
            inventory.setItem(itemSlotMap.get("boots"), null);
            lifeManager.reducePlayerLives(player);
            isComplete = false;
        }

        ItemStack[] invArmor = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            invArmor[i] = inventory.getItem(i);
        }

        if (!containsArmor(invArmor, helmet) ||
                !containsArmor(invArmor, chestplate) ||
                !containsArmor(invArmor, leggings) ||
                !containsArmor(invArmor, boots)) {
            isComplete = false;
        }

        if (isComplete) {
            player.sendMessage(ChatColor.GREEN + color + " Complete!");
            removeDyeFromInventory(player, color);
            InventoryCompletionStatus.setCompleteByColor(color, true);
        }

        return isComplete;
    }

    private boolean containsArmor(ItemStack[] inventory, ItemStack armorPiece) {
        for (ItemStack item : inventory) {
            if (item != null && item.isSimilar(armorPiece)) {
                return true;
            }
        }
        return false;
    }


    private boolean itemsMatch(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        return item1.getType() == item2.getType();
    }

    private void removeDyeFromInventory(Player player, String color) {
        Material dyeMaterial = switch (color) {
            case "Red" -> Material.RED_DYE;
            case "Yellow" -> Material.YELLOW_DYE;
            case "Blue" -> Material.BLUE_DYE;
            default -> null;
        };

        if (dyeMaterial != null) {
            Inventory playerInventory = player.getInventory();
            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack item = playerInventory.getItem(i);
                if (item != null && item.getType() == dyeMaterial) {
                    item.setAmount(item.getAmount() - 1);
                    if (item.getAmount() <= 0) {
                        playerInventory.setItem(i, null);
                        ArmorStandManager.updateArmorStandName(color, () -> {
                            onGameWin(player);
                        });
                    }
                    break;
                }
            }
        }
    }

    public void onGameWin(Player player) {
        if(InventoryCompletionStatus.isAllComplete()) {
            incrementGameRounds();
            spawnDelay = Math.max(10, initialSpawnDelay - (5 * getGameRounds()));
            resetGame(player);
        }
    }

    private void resetGame(Player player) {
        armorStandManager.clearArmorStands();
        completedColors.clear();
        InventoryCompletionStatus.setCompleteByColor("Red", false);
        InventoryCompletionStatus.setCompleteByColor("Yellow", false);
        InventoryCompletionStatus.setCompleteByColor("Blue", false);
        initializeInventories(player, false);
        player.setLevel(getGameRounds()+1);
        player.sendMessage(ChatColor.GREEN + "Congratulations! The game continues... This time faster!");
    }

    private void setItemName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
    }
}
