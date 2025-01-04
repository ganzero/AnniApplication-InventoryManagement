package me.ganzatron.anniApplication;

import org.bukkit.plugin.java.JavaPlugin;

public final class AnniApplication extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Hello World!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
