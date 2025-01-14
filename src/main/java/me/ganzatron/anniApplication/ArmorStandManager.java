package me.ganzatron.anniApplication;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArmorStandManager {

    private ArmorStand leftStand;
    private ArmorStand middleStand;
    private ArmorStand rightStand;

    private static final Map<String, ArmorStand> armorStandMap = new HashMap<>();
    private final Randomizer randomizer;

    public ArmorStandManager(List<Material> armorMaterials) {
        this.randomizer = new Randomizer(armorMaterials);
    }

    public void spawnArmorStands(Location leftLocation, Location middleLocation, Location rightLocation) {
        leftStand = createArmorStand(leftLocation, ChatColor.RED + "Red");
        middleStand = createArmorStand(middleLocation, ChatColor.YELLOW + "Yellow");
        rightStand = createArmorStand(rightLocation, ChatColor.BLUE + "Blue");

        assignRandomArmor(leftStand);
        assignRandomArmor(middleStand);
        assignRandomArmor(rightStand);

        armorStandMap.put("red", leftStand);
        armorStandMap.put("yellow", middleStand);
        armorStandMap.put("blue", rightStand);
    }

    private ArmorStand createArmorStand(Location location, String name) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setVisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setCanPickupItems(false);
        return armorStand;
    }

    private void assignRandomArmor(ArmorStand armorStand) {
        Random random = new Random();
        List<Material> armorMaterials = ArmorMaterials.getArmorMaterials();

        ItemStack helmet = new ItemStack(armorMaterials.get(random.nextInt(5)));
        ItemStack chestplate = new ItemStack(armorMaterials.get(random.nextInt(5) + 5));
        ItemStack leggings = new ItemStack(armorMaterials.get(random.nextInt(5) + 10));
        ItemStack boots = new ItemStack(armorMaterials.get(random.nextInt(5) + 15));

        armorStand.setHelmet(helmet);
        armorStand.setChestplate(chestplate);
        armorStand.setLeggings(leggings);
        armorStand.setBoots(boots);
    }

    public static ArmorStand getStandByColor(String color) {
        return armorStandMap.get(color.toLowerCase());
    }

    public String getArmorStandColor(ArmorStand armorStand) {
        if (armorStand == null) {
            return null;
        }

        String name = armorStand.getCustomName();
        if (name != null) {
            if (name.contains(ChatColor.RED.toString())) {
                return "Red";
            } else if (name.contains(ChatColor.YELLOW.toString())) {
                return "Yellow";
            } else if (name.contains(ChatColor.BLUE.toString())) {
                return "Blue";
            }
        }
        return null;
    }

    public void clearArmorStands() {
        if (leftStand != null) leftStand.remove();
        if (middleStand != null) middleStand.remove();
        if (rightStand != null) rightStand.remove();
    }


    public static void updateArmorStandName(String color, Runnable callback) {
        Bukkit.getScheduler().runTask(AnniApplication.getPlugin(AnniApplication.class), () -> {
            ArmorStand armorStand = getStandByColor(color);
            if (armorStand != null) {
                armorStand.setCustomName("Completed");
            }
            if (callback != null) {
                callback.run();
            }
        });
    }

    public List<ItemStack> getArmorStandArmor() {
        List<ItemStack> armorPieces = new ArrayList<>();

        for (ArmorStand stand : armorStandMap.values()) {
            if (stand.getHelmet() != null) armorPieces.add(stand.getHelmet());
            if (stand.getChestplate() != null) armorPieces.add(stand.getChestplate());
            if (stand.getLeggings() != null) armorPieces.add(stand.getLeggings());
            if (stand.getBoots() != null) armorPieces.add(stand.getBoots());
        }

        return armorPieces;
    }

    public ArmorStand getLeftStand() {
        return leftStand;
    }

    public ArmorStand getMiddleStand() {
        return middleStand;
    }

    public ArmorStand getRightStand() {
        return rightStand;
    }
}
