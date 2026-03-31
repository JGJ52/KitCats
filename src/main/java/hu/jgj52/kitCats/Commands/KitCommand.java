package hu.jgj52.kitCats.Commands;

import hu.jgj52.kitCats.GUIs.KitGUI;
import hu.jgj52.kitCats.SubCommands.Kit.*;
import hu.jgj52.kitCats.Types.Command;
import hu.jgj52.libCats.Types.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KitCommand extends Command {

    @Override
    public String getName() {
        return "kit";
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return List.of(
                new Create(),
                new Delete(),
                new Edit(),
                new Load(),
                new Preview()
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player) {
        new KitGUI().open(player);
        return true;
    }
}