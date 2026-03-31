package hu.jgj52.kitCats.Types;

import hu.jgj52.kitCats.KitCats;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class GUI extends hu.jgj52.libCats.Types.GUI {
    @Override
    public JavaPlugin getPlugin() {
        return KitCats.plugin;
    }

    @Override
    public String getMessage(String msg) {
        return KitCats.messages.getConfig().getString("guis." + getClass().getSimpleName() + "." + msg);
    }

    @Override
    public List<String> getMessageList(String msg) {
        return KitCats.messages.getConfig().getStringList("guis." + getClass().getSimpleName() + "." + msg);
    }

    @Override
    public String getMsg(String msg) {
        return KitCats.messages.getConfig().getString(msg);
    }

    @Override
    public List<String> getMsgList(String msg) {
        return KitCats.messages.getConfig().getStringList(msg);
    }
}
