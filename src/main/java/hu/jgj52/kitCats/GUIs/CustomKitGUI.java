package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.CustomKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.plugin;

public class CustomKitGUI extends GUI {
    private int page = 0;
    @Override
    public void init(Player player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.customkits." + player.getUniqueId());
        if (section == null) return;
        List<String> list = new ArrayList<>(section.getKeys(false));
        Collections.sort(list);
        int start = page * 28;
        int end = Math.min(start + 28, list.size());
        int slot = 10;
        for (int j = 0; j < list.subList(start, end).size(); j++) {
            String name = list.get(start + j);
            CustomKit kit = CustomKit.of(name, player);
            ItemStack icon = new ItemStack(kit.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName("§f" + kit.getName());
            iconMeta.setLore(plugin.getConfig().getStringList("guis." + getClass().getSimpleName() + ".iconLore"));
            icon.setItemMeta(iconMeta);
            gui.setItem(slot, icon);
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(getMessage("previousArrow"));
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(getMessage("nextArrow"));
        next.setItemMeta(nextMeta);

        ItemStack create = new ItemStack(Material.BOOK);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setDisplayName(getMessage("createKitItemName"));
        create.setItemMeta(createMeta);

        gui.setItem(4, create);
        if (page > 0) {
            gui.setItem(45, previous);
        }
        if (end > 28) {
            gui.setItem(53, next);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() == 45) {
            if (page > 0) {
                page--;
                init(player);
            }
        } else if (event.getSlot() == 53) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.customkits" + player.getUniqueId());
            if (section == null) return;
            if ((section.getKeys(false).size() - 1) / 28 > page) {
                page++;
                init(player);
            }
        } else if (event.getSlot() == 4) {
            new KitCreateGUI().open(player);
        } else {
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            if (name.startsWith("§f")) {
                name = ChatColor.stripColor(name);
                CustomKit kit = CustomKit.of(name, player);
                if (event.getClick().isLeftClick()) {
                    player.getInventory().setContents(kit.getContents());
                } else if (event.getClick().isRightClick()) {
                    new CustomKitPreviewGUI(kit, this).open(player);
                }
            }
        }
    }

    @Override
    public int getSize() {
        return 54;
    }
}
