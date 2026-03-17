package hu.jgj52.kitCats.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static hu.jgj52.kitCats.KitCats.plugin;

public class ChatListener implements Listener {
    private static final Map<Player, Function<AsyncChatEvent, Boolean>> functions = new HashMap<>();
    public static void add(Player player, Function<AsyncChatEvent, Boolean> function) {
        functions.put(player, function);
    }
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (functions.containsKey(event.getPlayer())) {
                boolean cancel = functions.get(event.getPlayer()).apply(event);
                if (cancel) {
                    event.setCancelled(true);
                    functions.remove(event.getPlayer());
                }
            }
        });
    }
}
