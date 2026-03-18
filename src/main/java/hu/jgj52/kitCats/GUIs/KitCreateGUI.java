package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Listeners.ChatListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.plugin;

public class KitCreateGUI extends GUI {
    private String name = getMessage("setNameItemName");
    private Material iconMaterial = Material.APPLE;
    private ItemStack[] content = null;

    @Override
    public void init(Player player) {
        ItemStack name = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = name.getItemMeta();
        nameMeta.setDisplayName(this.name);
        name.setItemMeta(nameMeta);

        ItemStack icon = new ItemStack(iconMaterial);
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(getMessage("setIconItemName"));
        icon.setItemMeta(iconMeta);

        ItemStack content = new ItemStack(Material.CHEST);
        ItemMeta contentMeta = content.getItemMeta();
        contentMeta.setDisplayName(getMessage("setContentItemName"));
        content.setItemMeta(contentMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.setDisplayName(getMessage("saveItemName"));
        save.setItemMeta(saveMeta);

        gui.setItem(11, name);
        gui.setItem(13, icon);
        gui.setItem(15, content);
        gui.setItem(26, save);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() == 11) {
            player.closeInventory();
            player.sendMessage(getMessage("setNameMessage"));
            ChatListener.add(player, e -> {
                List<String> names = new ArrayList<>();
                ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
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
        } else if (event.getSlot() == 13) {
            player.closeInventory();
            player.sendMessage(getMessage("setIconMessage"));
            ChatListener.add(player, e -> {
                if ("done".equals(PlainTextComponentSerializer.plainText().serialize(e.message()))) {
                    iconMaterial = player.getInventory().getItemInMainHand().getType();
                    open(player);
                    return true;
                }
                return false;
            });
        } else if (event.getSlot() == 15) {
            player.closeInventory();
            player.sendMessage(getMessage("setContentMessage"));
            ChatListener.add(player, e -> {
                if ("done".equals(PlainTextComponentSerializer.plainText().serialize(e.message()))) {
                    content = player.getInventory().getContents();
                    open(player);
                    return true;
                }
                return false;
            });
        } else if (event.getSlot() == 26) {
            player.closeInventory();
            if (name.equals(getMessage("setNameItemName")) || content == null) {
                player.sendMessage(getMessage("nameOrContentNotSet"));
                return;
            }
            plugin.getConfig().set("data.kits." + name + ".icon", iconMaterial.toString());
            plugin.getConfig().set("data.kits." + name + ".contents", content);
            plugin.saveConfig();
            plugin.reloadConfig();
            player.sendMessage(getMessage("saved"));
            gui = null;
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
