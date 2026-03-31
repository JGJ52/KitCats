package hu.jgj52.kitCats.Commands;

import hu.jgj52.kitCats.GUIs.PageGUI;
import hu.jgj52.kitCats.Types.Command;
import hu.jgj52.libCats.Types.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PageCommand extends Command {

    @Override
    public String getName() {
        return "page";
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return List.of();
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player) {
        new PageGUI().open(player);
        return true;
    }
}