package hu.jgj52.kitCats.GUIs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static hu.jgj52.kitCats.KitCats.plugin;

public class PageGUI extends GUI {
    private String currentPage;
    private boolean f = false;
    private boolean b = false;
    private int pageOffset = 0;
    private final Map<String, Object> toSave = new HashMap<>();
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
        ItemStack dataLessInline = inline.clone();
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        dataLessInline.setItemMeta(inlineMeta);
        inlineMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
        inline.setItemMeta(inlineMeta);

        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = red.getItemMeta();
        redMeta.setHideTooltip(true);
        red.setItemMeta(redMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("pageScrollerBackItemName"));
        back.setItemMeta(backMeta);

        ItemStack forward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = forward.getItemMeta();
        forwardMeta.setDisplayName(getMessage("pageScrollerForwardItemName"));
        forward.setItemMeta(forwardMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.setDisplayName(getMessage("saveItemName"));
        save.setItemMeta(saveMeta);

        for (int i = 0; i < 54; i++) {
            if ((i + 2) % 9 == 0 || (i >= 36 && i <= 44)) {
                gui.setItem(i, outline);
            } else if (List.of(8, 17, 26, 35).contains(i)) {
                gui.setItem(i, red);
            } else if (i != 53) {
                gui.setItem(i, i <= 33 ? inline : dataLessInline);
            } else {
                gui.setItem(i, save);
            }
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
        List<String> items = toSave.containsKey("customkits.pages." + currentPage + ".items") ? (List<String>) toSave.get("customkits.pages." + currentPage + ".items") : pages.getStringList(currentPage + ".items");
        int i = 0;
        for (String m : items) {
            if (i > 33) break;
            Material material = Material.matchMaterial(m);
            ItemStack is;
            if (material == null || material == Material.AIR) {
                is = inline;
            } else {
                is = new ItemStack(material, material.getMaxStackSize());
                ItemMeta im = is.getItemMeta();
                im.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
                is.setItemMeta(im);
            }
            gui.setItem(i, is);
            i++;
            if ((i + 2) % 9 == 0) i += 2;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack item = gui.getItem(event.getSlot());
        if (item == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
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
                if (!event.getClick().isMouseClick()) return;
                ItemStack cursor = event.getCursor().clone();
                ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta inlineMeta = inline.getItemMeta();
                inlineMeta.setHideTooltip(true);
                inline.setItemMeta(inlineMeta);
                boolean n = false;
                cursor = new ItemStack(cursor.getType());
                if (cursor.getType() == Material.AIR) {
                    cursor = inline;
                    n = true;
                }
                ItemMeta cursorMeta = cursor.getItemMeta();
                cursorMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
                cursor.setItemMeta(cursorMeta);
                gui.setItem(event.getSlot(), cursor);
                player.setItemOnCursor(null);
                List<String> list = toSave.containsKey("customkits.pages." + currentPage + ".items") ? (List<String>) toSave.get("customkits.pages." + currentPage + ".items") : plugin.getConfig().getStringList("customkits.pages." + currentPage + ".items");
                int add = 0;
                if (event.getSlot() > 8) add--;
                if (event.getSlot() > 17) add--;
                if (event.getSlot() > 26) add--;
                int index = event.getSlot() + add * 2;
                if (index >= list.size()) {
                    list.addAll(Collections.nCopies(index - list.size() + 1, Material.AIR.name()));
                }
                list.set(index, n ? Material.AIR.name() : cursor.getType().name());
                toSave.put("customkits.pages." + currentPage + ".items", list);
            }
        }
        if (event.getSlot() == 53) {
            // this toSave's getter is very bad but i dont wanna make a class for it
            for (String key : toSave.keySet()) {
                plugin.getConfig().set(key, toSave.get(key));
            }
            plugin.saveConfig();
            plugin.reloadConfig();
            player.sendMessage(getMessage("saved"));
            player.closeInventory();
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public boolean defaultInit() {
        return false;
    }

    @Override
    public int getSize() {
        return 54;
    }
}
