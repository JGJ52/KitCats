package hu.jgj52.kitCats.SubCommands.CustomKit;

import hu.jgj52.kitCats.GUIs.CustomKitEditorGUI;
import hu.jgj52.kitCats.Types.CustomKit;
import hu.jgj52.kitCats.Types.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.customkits;

public class Edit extends SubCommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings, @NotNull Player player) {
        CustomKit kit = CustomKit.of(strings[1], player);
        if (kit == null) {
            player.sendMessage(getComp("customKitNotFound"));
            return true;
        }
        new CustomKitEditorGUI(kit).open(player);
        return true;
    }

    @Override
    public List<String> complete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) return List.of();
        ConfigurationSection section = customkits.getConfig().getConfigurationSection(player.getUniqueId().toString());
        if (section == null) return List.of();
        return new ArrayList<>(section.getKeys(false));
    }

    @Override
    public boolean firstComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) return false;
        ConfigurationSection section = customkits.getConfig().getConfigurationSection(player.getUniqueId().toString());
        if (section == null) return false;
        return !section.getKeys(false).isEmpty();
    }
}
