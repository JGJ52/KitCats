package hu.jgj52.kitCats.SubCommands.CustomKit;

import hu.jgj52.kitCats.GUIs.CustomKitCreateGUI;
import hu.jgj52.kitCats.Types.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Create extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player) {
        new CustomKitCreateGUI().open(player);
        return true;
    }

    @Override
    public List<String> complete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }

    @Override
    public boolean firstComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return true;
    }
}
