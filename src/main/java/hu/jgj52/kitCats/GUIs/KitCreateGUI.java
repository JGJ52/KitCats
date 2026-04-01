package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.GUI;
import hu.jgj52.libCats.Listeners.ChatListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static hu.jgj52.kitCats.KitCats.kits;
import static hu.jgj52.kitCats.KitCats.plugin;

public class KitCreateGUI extends GUI {
    private Component name = getComponent("setNameItemName", true);
    private Material iconMaterial = Material.APPLE;
    private ItemStack[] content = null;
    private boolean nameSet = false;

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
                if (kits.getConfig().getKeys(false).contains(PlainTextComponentSerializer.plainText().serialize(e))) {
                    player.sendMessage(getComponent("alreadyName"));
                } else {
                    nameSet = true;
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
            player.closeInventory();
            player.sendMessage(getComponent("setContentMessage"));
            ChatListener.add(player, "done", e -> {
                content = player.getInventory().getContents();
                open(player);
            });
        } else if (event.getSlot() == 26) {
            player.closeInventory();
            if (!nameSet || content == null) {
                player.sendMessage(getComponent("nameOrContentNotSet"));
                return;
            }
            kits.getConfig().set(name + ".icon", iconMaterial.toString());
            kits.getConfig().set(name + ".contents", content);
            kits.saveConfig();
            kits.reloadConfig();
            player.sendMessage(getComponent("saved"));
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
