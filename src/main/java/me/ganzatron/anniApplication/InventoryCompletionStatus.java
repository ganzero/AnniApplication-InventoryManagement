package me.ganzatron.anniApplication;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class InventoryCompletionStatus {

    private static boolean redComplete;
    private static boolean yellowComplete;
    private static boolean blueComplete;

    static {
        redComplete = false;
        yellowComplete = false;
        blueComplete = false;
    }


    public static boolean isAllComplete() {
        return redComplete && yellowComplete && blueComplete;
    }

    public static void setCompleteByColor(String color, boolean isCompleted) {
        switch (color.toLowerCase()) {
            case "red":
                redComplete = isCompleted;
                break;
            case "yellow":
                yellowComplete = isCompleted;
                break;
            case "blue":
                blueComplete = isCompleted;
                break;
            default:
                throw new IllegalArgumentException("Invalid color: " + color);
        }
    }
}

