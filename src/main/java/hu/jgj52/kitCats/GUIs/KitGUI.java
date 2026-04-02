package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.CustomKit;
import hu.jgj52.kitCats.Types.GUI;
import hu.jgj52.kitCats.Types.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.kits;
import static hu.jgj52.kitCats.KitCats.plugin;

public class KitGUI extends GUI {
    private int page = 0;
    @Override
    public void init(Player player) {
        ConfigurationSection section = kits.getConfig();
        List<String> list = new ArrayList<>(section.getKeys(false));
        Collections.sort(list);
        int start = page * 28;
        int end = Math.min(start + 28, list.size());
        int slot = 10;
        for (int j = 0; j < list.subList(start, end).size(); j++) {
            String name = list.get(start + j);
            Kit kit = Kit.of(name);
            if (kit == null) return;
            ItemStack icon = new ItemStack(kit.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.displayName(Component.text(kit.getName()).decoration(TextDecoration.ITALIC, false));
            if (player.hasPermission("kitcats.command.kit.create")) {
                iconMeta.lore(getComponentList("iconLore", true));
            }
            iconMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "kit"), PersistentDataType.STRING, kit.getName());
            icon.setItemMeta(iconMeta);
            gui.setItem(slot, icon);
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }
        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);
        for (int i = slot; slot < 44; i++) {
            gui.setItem(slot, inline);
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(getComponent("previousArrow", true));
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(getComponent("nextArrow", true));
        next.setItemMeta(nextMeta);

        ItemStack create = new ItemStack(Material.BOOK);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.displayName(getComponent("createItemName", true));
        create.setItemMeta(createMeta);

        if (player.hasPermission("kitcats.command.kit.create")) {
            gui.setItem(4, create);
        }
        if (page > 0) {
            gui.setItem(45, previous);
        } else {
            gui.setItem(45, outline);
        }
        if (list.size() > 28 && end == 28) {
            gui.setItem(53, next);
        } else {
            gui.setItem(53, outline);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = gui.getItem(event.getSlot());
        if (event.getSlot() == 45) {
            if (item != null && item.getType() == Material.ARROW) {
                page--;
                init(player);
            }
        } else if (event.getSlot() == 53) {
            if (item != null && item.getType() == Material.ARROW) {
                page++;
                init(player);
            }
        } else if (event.getSlot() == 4) {
            if (player.hasPermission("kitcats.command.kit.create")) {
                new KitCreateGUI().open(player);
            }
        } else {
            if (item != null) {
                String name = item.getPersistentDataContainer().get(new NamespacedKey(plugin, "kit"), PersistentDataType.STRING);
                if (name != null) {
                    Kit kit = Kit.of(name);
                    if (kit == null) return;
                    if (event.getClick().isLeftClick()) {
                        if (player.hasPermission("kitcats.command.kit.load")) {
                            player.getInventory().setContents(kit.getContents());
                        }
                    } else if (event.getClick().isRightClick()) {
                        if (player.hasPermission("kitcats.command.kit.preview")) {
                            new KitPreviewGUI(kit, this).open(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getSize() {
        return 54;
    }
}
