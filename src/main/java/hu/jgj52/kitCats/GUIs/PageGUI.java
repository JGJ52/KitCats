package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.KitCats;
import hu.jgj52.kitCats.Types.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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

import static hu.jgj52.kitCats.KitCats.pages;
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
        ConfigurationSection pages = KitCats.pages.getConfig();
        if (pages == null) return;
        if (pages.getKeys(false).isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> new CreatePageGUI(this).open(player), 1L);
            return;
        }

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack dataLessInline = inline.clone();
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        ItemMeta dataLessInlineMeta = inlineMeta.clone();
        dataLessInlineMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "empty"), PersistentDataType.BOOLEAN, true);
        dataLessInline.setItemMeta(dataLessInlineMeta);
        inlineMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
        inline.setItemMeta(inlineMeta);

        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = red.getItemMeta();
        redMeta.setHideTooltip(true);
        red.setItemMeta(redMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(getComponent("pageScrollerBackItemName", true));
        back.setItemMeta(backMeta);

        ItemStack forward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = forward.getItemMeta();
        forwardMeta.displayName(getComponent("pageScrollerForwardItemName", true));
        forward.setItemMeta(forwardMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.displayName(getComponent("saveItemName", true));
        save.setItemMeta(saveMeta);

        ItemStack addItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta addMeta = addItem.getItemMeta();
        addMeta.displayName(getComponent("addItemName", true));
        addItem.setItemMeta(addMeta);

        ItemStack delete = new ItemStack(Material.RED_CONCRETE);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.displayName(getComponent("deleteItemName", true));
        delete.setItemMeta(deleteMeta);

        for (int i = 0; i < 54; i++) {
            if ((i + 2) % 9 == 0 || (i >= 36 && i <= 44)) {
                gui.setItem(i, outline);
            } else if (List.of(26, 35).contains(i)) {
                gui.setItem(i, red);
            } else if (i == 53) {
                gui.setItem(i, save);
            } else if (i == 8) {
                gui.setItem(i, addItem);
            } else if (i == 17) {
                gui.setItem(i, delete);
            }
            else gui.setItem(i, i <= 33 ? inline : dataLessInline);
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
            Material material = Material.matchMaterial(page.getString("icon", ""));
            if (material == null) material = Material.APPLE;
            ItemStack p = new ItemStack(material);
            ItemMeta pMeta = p.getItemMeta();
            if (name.equals(currentPage)) {
                pMeta.setEnchantmentGlintOverride(true);
            }
            pMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            p.setItemMeta(pMeta);

            gui.setItem(slot, p);
            slot++;
        }
        List<String> items = toSave.containsKey(currentPage + ".items") ? (List<String>) toSave.get(currentPage + ".items") : pages.getStringList(currentPage + ".items");
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
                ConfigurationSection metas = toSave.containsKey(currentPage + ".metas") ? (ConfigurationSection) toSave.get(currentPage + ".metas") : pages.getConfigurationSection(currentPage + ".metas");
                if (metas != null) {
                    int a = 0;
                    if (i > 8) a--;
                    if (i > 17) a--;
                    if (i > 26) a--;
                    if (metas.get(String.valueOf(i + a * 2)) instanceof ItemMeta meta) {
                        im = meta;
                    }
                }
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
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "empty"))) return;
            currentPage = PlainTextComponentSerializer.plainText().serialize(item.displayName());
            init(player);
        }
        if (event.getSlot() == 8) {
            new CreatePageGUI(this).open(player);
        } else if (event.getSlot() == 17) {
            ConfigurationSection section = pages.getConfig();
            if (section != null) {
                if (section.getKeys(false).size() <= 1) {
                    player.sendMessage(getComponent("atLeastOne"));
                    return;
                }
            }
            for (String key : toSave.keySet()) {
                pages.getConfig().set(key, toSave.get(key));
            }
            toSave.clear();
            pages.getConfig().set(currentPage, null);
            pages.saveConfig();
            pages.reloadConfig();
            player.sendMessage(getComponent("deleted"));
            player.closeInventory();
        } else if (event.getSlot() <= 33) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN)) {
                if (!event.getClick().isMouseClick()) return;
                ItemStack cursor = event.getCursor().clone();
                ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta inlineMeta = inline.getItemMeta();
                inlineMeta.setHideTooltip(true);
                inlineMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
                inline.setItemMeta(inlineMeta);
                boolean n = false;
                if (cursor.getType() == Material.AIR) {
                    cursor = inline;
                    n = true;
                }
                ItemMeta cursorMeta = cursor.getItemMeta();
                cursorMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN, true);
                ItemStack beforeCursor = cursor.clone();
                cursor.setItemMeta(cursorMeta);
                gui.setItem(event.getSlot(), cursor);
                ItemStack i = item.clone();
                ItemMeta iMeta = i.getItemMeta();
                iMeta.getPersistentDataContainer().remove(new NamespacedKey(plugin, "pageContent"));
                i.setItemMeta(iMeta);
                player.setItemOnCursor(!item.isSimilar(inline) ? i : null);
                List<String> list = toSave.containsKey(currentPage + ".items") ? (List<String>) toSave.get(currentPage + ".items") : pages.getConfig().getStringList(currentPage + ".items");
                int add = 0;
                if (event.getSlot() > 8) add--;
                if (event.getSlot() > 17) add--;
                if (event.getSlot() > 26) add--;
                int index = event.getSlot() + add * 2;
                if (index >= list.size()) {
                    list.addAll(Collections.nCopies(index - list.size() + 1, Material.AIR.name()));
                }
                list.set(index, n ? Material.AIR.name() : cursor.getType().name());
                ConfigurationSection metas = pages.getConfig().getConfigurationSection(currentPage + ".metas");
                if (metas == null) metas = pages.getConfig().createSection(currentPage + ".metas");
                if (!n) {
                    metas.set(String.valueOf(index), beforeCursor.getItemMeta());
                    toSave.put(currentPage + ".metas", metas);
                }
                toSave.put(currentPage + ".items", list);
            }
        } else if (event.getSlot() == 53) {
            // this toSave's getter is very bad but i dont wanna make a class for it
            for (String key : toSave.keySet()) {
                pages.getConfig().set(key, toSave.get(key));
            }
            toSave.clear();
            pages.saveConfig();
            pages.reloadConfig();
            player.sendMessage(getComponent("saved"));
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
