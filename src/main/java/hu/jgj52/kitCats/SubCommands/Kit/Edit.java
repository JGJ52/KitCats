package hu.jgj52.kitCats.SubCommands.Kit;

import hu.jgj52.kitCats.GUIs.EditKitGUI;
import hu.jgj52.kitCats.Types.Kit;
import hu.jgj52.kitCats.Types.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.kits;

public class Edit extends SubCommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player) {
        Kit kit = Kit.of(strings[1]);
        if (kit == null) {
            player.sendMessage(getComp("kitNotFound"));
            return true;
        }
        new EditKitGUI(kit).open(player);
        return true;
    }

    @Override
    public List<String> complete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return new ArrayList<>(kits.getConfig().getKeys(false));
    }

    @Override
    public boolean firstComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return !kits.getConfig().getKeys(false).isEmpty();
    }
}
