package hu.jgj52.kitCats;

import hu.jgj52.kitCats.Commands.KitCommand;
import hu.jgj52.kitCats.Listeners.ChatListener;
import hu.jgj52.kitCats.Listeners.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitCats extends JavaPlugin {

    public static KitCats plugin;
    public static boolean viaversion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        viaversion = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("kit").setExecutor(new KitCommand());

        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
