package hu.jgj52.kitCats.GUIs;

import org.bukkit.ChatColor;
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

import static hu.jgj52.kitCats.KitCats.plugin;

public class CreateCustomKitGUI extends GUI {
    private String currentPage;
    private boolean f = false;
    private boolean b = false;
    private int pageOffset = 0;
    @Override
    public void init(Player player) {
        f = false;
        b = false;
        ConfigurationSection pages = plugin.getConfig().getConfigurationSection("customkits.pages");
        if (pages == null) return;

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("pageScrollerBackItemName"));
        back.setItemMeta(backMeta);

        ItemStack forward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = forward.getItemMeta();
        forwardMeta.setDisplayName(getMessage("pageScrollerForwardItemName"));
        forward.setItemMeta(forwardMeta);

        for (int i = 0; i < 54; i++) {
            if ((i + 2) % 9 == 0 || (i >= 36 && i <= 44)) {
                gui.setItem(i, outline);
            } else if (List.of(8, 17, 26, 35).contains(i)) {
                gui.setItem(i, null);
            } else gui.setItem(i, inline);
        }

        if (pages.getKeys(false).size() > 7 && pageOffset + 6 != pages.getKeys(false).size()) {
            gui.setItem(51, forward);
            f = true;
        }
        if (pageOffset > 0 || f) {
            gui.setItem(45, back);
            b = true;
        }
        int add = 0;
        if (f) add++;
        if (b) add++;
        List<String> ps = new ArrayList<>(pages.getKeys(false));
        Collections.sort(ps);
        if (currentPage == null) currentPage = ps.getFirst();
        int start = pageOffset;
        int end = Math.min((start + 7) - add, ps.size());
        int slot = b ? 46 : 45;
        for (int j = 0; j < ps.subList(start, end).size(); j++) {
            String name = ps.get(j + start);
            ConfigurationSection page = pages.getConfigurationSection(name);
            if (page == null) continue;
            ItemStack p = new ItemStack(Material.matchMaterial(page.getString("icon")));
            ItemMeta pMeta = p.getItemMeta();
            if (name.equals(currentPage)) {
                pMeta.setEnchantmentGlintOverride(true);
            }
            pMeta.setDisplayName("§f" + name);
            p.setItemMeta(pMeta);

            gui.setItem(slot, p);
            slot++;
        }
        ConfigurationSection page = pages.getConfigurationSection(currentPage);
        if (page != null) {
            int i = 0;
            for (String m : page.getStringList("items")) {
                if (i > 33) break;
                Material material = Material.matchMaterial(m);
                if (material == null) continue;
                ItemStack is = new ItemStack(material, material.getMaxStackSize());
                ItemMeta im = is.getItemMeta();
                im.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
                is.setItemMeta(im);

                gui.setItem(i, is);
                i++;
                if ((i + 2) % 9 == 0) i += 2;
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (List.of(8, 17, 26, 35).contains(event.getSlot())) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = gui.getItem(event.getSlot());
        if (event.getSlot() == 51 && f) {
            pageOffset++;
            init(player);
        } else if (event.getSlot() == 45 && b) {
            if (pageOffset > 0) {
                pageOffset--;
                init(player);
            }
        } else if (event.getSlot() >= 45 && event.getSlot() <= 51) {
            currentPage = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            init(player);
        }
        if (event.getSlot() <= 33) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN)) {
                ItemStack is = new ItemStack(item.getType(), item.getType().getMaxStackSize());
                player.setItemOnCursor(is);
            }
        }
    }

    @Override
    public int getSize() {
        return 54;
    }
}
