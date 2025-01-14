package me.ganzatron.anniApplication;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Randomizer {
    private final List<ItemStack> guaranteedItems = new ArrayList<>();
    private final List<Material> randomMaterials = new ArrayList<>();

    public Randomizer(List<Material> armorMaterials) {
        randomMaterials.addAll(armorMaterials);
    }

    public void addGuaranteedItems(List<ItemStack> items) {
        guaranteedItems.clear();
        guaranteedItems.addAll(items);
    }

    public List<ItemStack> generateRandomizedItems() {
        List<ItemStack> allItems = new ArrayList<>();

        allItems.addAll(guaranteedItems);

        Random random = new Random();
        while (allItems.size() < 45) {
            Material randomMaterial = randomMaterials.get(random.nextInt(randomMaterials.size()));
            allItems.add(new ItemStack(randomMaterial));
        }

        Collections.shuffle(allItems);

        return allItems;
    }
}