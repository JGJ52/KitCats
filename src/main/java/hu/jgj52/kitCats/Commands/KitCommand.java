package hu.jgj52.kitCats.Commands;

import hu.jgj52.kitCats.GUIs.KitGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static hu.jgj52.kitCats.KitCats.plugin;

public class KitCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Function<Context, Result>> subcommand = new HashMap<>();

    private record Context (Player player, String[] args) {}
    private record Result (Boolean canRun, List<String> tabComplete, Boolean returnValue) {}
    public String getMessage(String msg) {
        return plugin.getConfig().getString("messages." + msg);
    }
    public String player(String msg, Player player) {
        return msg.replaceAll("%player%", player.getName());
    }

    public KitCommand () {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMessage("youAreNotPlayer"));
            return true;
        }

        if (args.length > 0) {

        } else {
            new KitGUI().open(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
