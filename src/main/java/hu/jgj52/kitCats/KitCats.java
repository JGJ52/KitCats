package hu.jgj52.kitCats;

import hu.jgj52.kitCats.Commands.CustomKitCommand;
import hu.jgj52.kitCats.Commands.KitCommand;
import hu.jgj52.kitCats.Commands.PageCommand;
import hu.jgj52.kitCats.Configurations.CustomKitsConfiguration;
import hu.jgj52.kitCats.Configurations.KitsConfiguration;
import hu.jgj52.kitCats.Configurations.MessagesConfiguration;
import hu.jgj52.kitCats.Configurations.PagesConfiguration;
import hu.jgj52.kitCats.Listeners.ChatListener;
import hu.jgj52.kitCats.Listeners.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class KitCats extends JavaPlugin {

    public static KitCats plugin;
    public static CustomKitsConfiguration customkits;
    public static KitsConfiguration kits;
    public static MessagesConfiguration messages;
    public static PagesConfiguration pages;
    public static boolean viaversion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        customkits = new CustomKitsConfiguration();
        kits = new KitsConfiguration();
        messages = new MessagesConfiguration();
        pages = new PagesConfiguration();
        viaversion = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("kit").setExecutor(new KitCommand());
        getCommand("customkit").setExecutor(new CustomKitCommand());
        getCommand("page").setExecutor(new PageCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
