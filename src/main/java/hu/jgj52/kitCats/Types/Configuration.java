package hu.jgj52.kitCats.Types;

import hu.jgj52.kitCats.KitCats;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Configuration extends hu.jgj52.libCats.Types.Configuration {
    @Override
    public JavaPlugin getPlugin() {
        return KitCats.plugin;
    }
}
