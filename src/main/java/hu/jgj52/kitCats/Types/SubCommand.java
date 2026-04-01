package hu.jgj52.kitCats.Types;

import hu.jgj52.kitCats.KitCats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand extends hu.jgj52.libCats.Types.SubCommand {
    @Override
    public JavaPlugin getPlugin() {
        return KitCats.plugin;
    }

    @Override
    public String getMsg(String msg) {
        return KitCats.messages.getConfig().getString(msg);
    }

    @Override
    public List<String> getMsgList(String msg) {
        return KitCats.messages.getConfig().getStringList(msg);
    }

    @Override
    public abstract boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player);
}
