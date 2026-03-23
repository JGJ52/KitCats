package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Listeners.ChatListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.plugin;

public class CreatePageGUI extends GUI {
    private String name = getMessage("nameItemName");
    private Material icon = Material.APPLE;
    private final GUI back;
    public CreatePageGUI(GUI back) {
        this.back = back;
    }
    @Override
    public void init(Player player) {
        ItemStack name = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = name.getItemMeta();
        nameMeta.setDisplayName(this.name);
        name.setItemMeta(nameMeta);

        ItemStack icon = new ItemStack(this.icon);
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(getMessage("iconItemName"));
        icon.setItemMeta(iconMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("backItemName"));
        back.setItemMeta(backMeta);

        gui.setItem(0, back);
        gui.setItem(12, name);
        gui.setItem(14, icon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        switch (event.getSlot()) {
            case 0:
                if (name.equals(getMessage("nameItemName"))) {
                    back.open(player);
                    break;
                }
                plugin.getConfig().set("customkits.pages." + name + ".icon", icon.name());
                plugin.saveConfig();
                plugin.reloadConfig();
                back.open(player);
                break;
            case 12:
                player.closeInventory();
                player.sendMessage(getMessage("setNameMessage"));
                ChatListener.add(player, e -> {
                    List<String> names = new ArrayList<>();
                    ConfigurationSection section = plugin.getConfig().getConfigurationSection("customkits.pages");
                    if (section != null) {
                        names.addAll(section.getKeys(false));
                    }
                    String name = PlainTextComponentSerializer.plainText().serialize(e.message());
                    if (names.contains(name)) {
                        player.sendMessage(getMessage("alreadyName"));
                    } else {
                        this.name = name;
                    }
                    open(player);
                    return true;
                });
                break;
            case 14:
                player.closeInventory();
                player.sendMessage(getMessage("setIconMessage"));
                ChatListener.add(player, e -> {
                    if ("done".equals(PlainTextComponentSerializer.plainText().serialize(e.message()))) {
                        icon = player.getInventory().getItemInMainHand().getType();
                        open(player);
                        return true;
                    }
                    return false;
                });
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
