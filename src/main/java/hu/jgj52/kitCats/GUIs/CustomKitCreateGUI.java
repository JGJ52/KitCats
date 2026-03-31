package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.CustomKit;
import hu.jgj52.kitCats.Types.GUI;
import hu.jgj52.libCats.Listeners.ChatListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.customkits;

public class CustomKitCreateGUI extends GUI {
    private Component name = getComponent("setNameItemName", true);
    private Material iconMaterial = Material.APPLE;
    private boolean nameSet = false;
    private final ItemStack[] content = new ItemStack[41];

    @Override
    public void init(Player player) {
        ItemStack name = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = name.getItemMeta();
        nameMeta.displayName(this.name);
        name.setItemMeta(nameMeta);

        ItemStack icon = new ItemStack(iconMaterial);
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.displayName(getComponent("setIconItemName", true));
        icon.setItemMeta(iconMeta);

        ItemStack content = new ItemStack(Material.CHEST);
        ItemMeta contentMeta = content.getItemMeta();
        contentMeta.displayName(getComponent("setContentItemName", true));
        content.setItemMeta(contentMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.displayName(getComponent("saveItemName", true));
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
            player.sendMessage(getComponent("setNameMessage"));
            ChatListener.add(player, e -> {
                List<String> names = new ArrayList<>();
                ConfigurationSection section = customkits.getConfig().getConfigurationSection(player.getUniqueId().toString());
                if (section != null) {
                    names.addAll(section.getKeys(false));
                }
                String name = PlainTextComponentSerializer.plainText().serialize(e);
                if (names.contains(name)) {
                    player.sendMessage(getComponent("alreadyName"));
                } else {
                    this.name = e;
                }
                open(player);
            });
        } else if (event.getSlot() == 13) {
            player.closeInventory();
            player.sendMessage(getComponent("setIconMessage"));
            ChatListener.add(player, "done", e -> {
                iconMaterial = player.getInventory().getItemInMainHand().getType();
                open(player);
            });
        } else if (event.getSlot() == 15) {
            new CustomKitEditorGUI(this, content).open(player);
        } else if (event.getSlot() == 26) {
            player.closeInventory();
            if (!nameSet) {
                player.sendMessage(getComponent("nameNotSet"));
                return;
            }
            CustomKit.create(player, PlainTextComponentSerializer.plainText().serialize(name), iconMaterial, content);
            player.sendMessage(getComponent("saved"));
            gui = null;
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
