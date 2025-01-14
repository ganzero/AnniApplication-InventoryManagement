package me.ganzatron.anniApplication;

import org.bukkit.plugin.java.JavaPlugin;

import static me.ganzatron.anniApplication.ArmorMaterials.armorMaterials;


public class AnniApplication extends JavaPlugin {

    private GameManager gameManager;
    private LifeManager lifeManager;
    private InventoryManager inventoryManager;
    private ArmorStandManager armorStandManager;
    private EventListener eventListener;

    @Override
    public void onEnable() {
        this.getCommand("giveitems").setExecutor(new GiveItemsCommand());
        this.lifeManager = new LifeManager();
        this.armorStandManager = new ArmorStandManager(armorMaterials);
        this.inventoryManager = new InventoryManager(lifeManager, armorStandManager);
        this.gameManager = new GameManager(inventoryManager, lifeManager, armorStandManager);
        this.eventListener = new EventListener(inventoryManager, armorStandManager, gameManager);

        getServer().getPluginManager().registerEvents(eventListener, this);

        getLogger().info("AnniApplication enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnniApplication disabled!");
    }

}
