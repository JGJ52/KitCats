package hu.jgj52.kitCats.Commands;

import hu.jgj52.kitCats.GUIs.*;
import hu.jgj52.kitCats.Types.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static hu.jgj52.kitCats.KitCats.plugin;

public class KitCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Function<Context, Result>> subcommands = new HashMap<>();

    private record Context (Boolean command, Player player, String[] args) {}
    private record Result (Boolean canRun, List<String> tabComplete, Boolean returnValue) {}

    public String getMessage(String msg) {
        return plugin.getConfig().getString("messages." + msg);
    }
    public String player(String msg, Player player) {
        return msg.replaceAll("%player%", player.getName());
    }

    public KitCommand () {
        subcommands.put("create", context -> {
           Player player = context.player();

           if (context.command()) {
               new KitCreateGUI().open(player);

               return new Result(true, new ArrayList<>(), true);
           } else {
               return new Result(true, new ArrayList<>(List.of("")), true);
           }
        });
        subcommands.put("load", context -> {
           Player player = context.player();
           String[] args = context.args();

           if (context.command()) {
               if (args.length < 2) {
                   player.sendMessage(getMessage("noArgs"));
                   return new Result(false, new ArrayList<>(), true);
               }

               Kit kit = Kit.of(args[1]);
               player.getInventory().setContents(kit.getContents(player));
               return new Result(true, new ArrayList<>(), true);
           } else {
               List<String> complete = new ArrayList<>();
               ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
               if (section != null) {
                   for (String name : section.getKeys(false)) {
                       Kit kit = Kit.of(name);
                       complete.add(kit.getName());
                   }
               }
               return new Result(true, complete, true);
           }
        });
        subcommands.put("preview", context -> {
            Player player = context.player();
            String[] args = context.args();

            if (context.command()) {
                if (args.length < 2) {
                    player.sendMessage(getMessage("noArgs"));
                    return new Result(false, new ArrayList<>(), true);
                }

                Kit kit = Kit.of(args[1]);
                new KitPreviewGUI(kit).open(player);
                return new Result(true, new ArrayList<>(), true);
            } else {
                List<String> complete = new ArrayList<>();
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
                if (section != null) {
                    for (String name : section.getKeys(false)) {
                        Kit kit = Kit.of(name);
                        complete.add(kit.getName());
                    }
                }
                return new Result(true, complete, true);
            }
        });
        subcommands.put("edit", context -> {
            Player player = context.player();
            String[] args = context.args();

            if (context.command()) {
                if (args.length < 2) {
                    player.sendMessage(getMessage("noArgs"));
                    return new Result(false, new ArrayList<>(), true);
                }

                Kit kit = Kit.of(args[1]);
                new EditKitGUI(kit).open(player);
                return new Result(true, new ArrayList<>(), true);
            } else {
                List<String> complete = new ArrayList<>();
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
                if (section != null) {
                    for (String name : section.getKeys(false)) {
                        Kit kit = Kit.of(name);
                        complete.add(kit.getName());
                    }
                }
                return new Result(true, complete, true);
            }
        });
        subcommands.put("delete", context -> {
            Player player = context.player();
            String[] args = context.args();

            if (context.command()) {
                if (args.length < 2) {
                    player.sendMessage(getMessage("noArgs"));
                    return new Result(false, new ArrayList<>(), true);
                }

                plugin.getConfig().set("data.kits." + args[1], null);
                plugin.saveConfig();
                plugin.reloadConfig();
                return new Result(true, new ArrayList<>(), true);
            } else {
                List<String> complete = new ArrayList<>();
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
                if (section != null) {
                    for (String name : section.getKeys(false)) {
                        Kit kit = Kit.of(name);
                        complete.add(kit.getName());
                    }
                }
                return new Result(true, complete, true);
            }
        });
        subcommands.put("d", context -> {
            if (context.command()) {
                new CustomKitEditorGUI().open(context.player());
            }
           return new Result(true, new ArrayList<>(), true);
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMessage("youAreNotPlayer"));
            return true;
        }

        if (args.length > 0) {
            for (String subcommand : subcommands.keySet()) {
                if (subcommand.equals(args[0])) {
                    if (sender.hasPermission("kitcats.command.kit." + subcommand)) {
                        Result result = subcommands.get(subcommand).apply(new Context(true, player, args));
                        return result.returnValue();
                    }
                }
            }
        } else {
            new KitGUI().open(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return List.of();
        List<String> complete = new ArrayList<>();
        if (args.length == 1) {
            for (String subcommand : subcommands.keySet()) {
                if (sender.hasPermission("kitcats.command.kit." + subcommand)) {
                    Result result = subcommands.get(subcommand).apply(new Context(false, player, args));
                    if (result.canRun()) {
                        complete.add(subcommand);
                    }
                }
            }
        } else if (args.length == 2) {
            for (String subcommand : subcommands.keySet()) {
                if (args[0].equals(subcommand)) {
                    if (sender.hasPermission("kitcats.command.kit." + subcommand)) {
                        Result result = subcommands.get(subcommand).apply(new Context(false, player, args));
                        if (result.canRun()) {
                            return result.tabComplete();
                        }
                    }
                }
            }
        }
        return complete;
    }
}
