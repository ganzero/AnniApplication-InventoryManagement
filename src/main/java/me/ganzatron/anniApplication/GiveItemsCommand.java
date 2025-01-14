package me.ganzatron.anniApplication;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Create the items to give
            ItemStack emerald = new ItemStack(Material.EMERALD, 1); // 1 emerald
            ItemStack redDye = new ItemStack(Material.RED_DYE, 1); // 1 red dye
            ItemStack yellowDye = new ItemStack(Material.YELLOW_DYE, 1); // 1 yellow dye
            ItemStack blueDye = new ItemStack(Material.BLUE_DYE, 1); // 1 blue dye

            setItemName(emerald, ChatColor.DARK_PURPLE + "Start Game");
            setItemName(redDye, ChatColor.RED + "Red Inventory");
            setItemName(yellowDye, ChatColor.YELLOW + "Yellow Inventory");
            setItemName(blueDye, ChatColor.BLUE + "Blue Inventory");

            // Give the items to the player
            player.getInventory().addItem(emerald, redDye, yellowDye, blueDye);

            // Send feedback to the player
            player.sendMessage("You have received an emerald, red dye, yellow dye, and blue dye!");

            return true;
        }
        return false;
    }

    private void setItemName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
    }
}
