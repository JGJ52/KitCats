package hu.jgj52.kitCats;

import hu.jgj52.kitCats.Commands.KitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitCats extends JavaPlugin {

    public static KitCats plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("kit").setExecutor(new KitCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
