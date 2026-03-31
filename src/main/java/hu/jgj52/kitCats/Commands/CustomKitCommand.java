package hu.jgj52.kitCats.Commands;

import hu.jgj52.kitCats.GUIs.CustomKitGUI;
import hu.jgj52.kitCats.SubCommands.CustomKit.*;
import hu.jgj52.kitCats.Types.Command;
import hu.jgj52.libCats.Types.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomKitCommand extends Command {
    @Override
    public String getName() {
        return "customkit";
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
        new CustomKitGUI().open(player);
        return true;
    }
}
