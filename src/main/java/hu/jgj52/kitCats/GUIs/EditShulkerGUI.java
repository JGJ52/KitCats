package hu.jgj52.kitCats.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.plugin;

public class EditShulkerGUI extends GUI {
    private final GUI back;
    private final ItemStack item;
    private ItemStack[] inv;
    public EditShulkerGUI(GUI back, ItemStack item) {
        this.back = back;
        this.item = item;
    }
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
        if (pages.getKeys(false).isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 1L);
            player.sendMessage(getMessage("noPages"));
            return;
        }

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);
        ItemStack inl = inline.clone();
        inlineMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "empty"), PersistentDataType.BOOLEAN, true);
        inl.setItemMeta(inlineMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("pageScrollerBackItemName"));
        back.setItemMeta(backMeta);

        ItemStack forward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = forward.getItemMeta();
        forwardMeta.setDisplayName(getMessage("pageScrollerForwardItemName"));
        forward.setItemMeta(forwardMeta);

        ItemStack saveBack = new ItemStack(Material.ARROW);
        ItemMeta saveBackMeta = saveBack.getItemMeta();
        saveBackMeta.setDisplayName(getMessage("backItemName"));
        saveBack.setItemMeta(saveBackMeta);

        ItemStack red = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = red.getItemMeta();
        redMeta.setHideTooltip(true);
        red.setItemMeta(redMeta);
        for (int i = 0; i < 54; i++) {
            if ((i + 2) % 9 == 0 || (i >= 36 && i <= 44)) {
                gui.setItem(i, outline);
            } else if (List.of(8, 17, 26, 35).contains(i)) {
                gui.setItem(i, red);
            } else if (i == 53) {
                gui.setItem(i, saveBack);
            } else {
                gui.setItem(i, i > 44 ? inl : inline);
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
        ConfigurationSection page = pages.getConfigurationSection(currentPage);
        if (page != null) {
            int i = 0;
            for (String m : page.getStringList("items")) {
                if (i > 33) break;
                Material material = Material.matchMaterial(m);
                ItemStack is;
                if (material == null || material == Material.AIR) {
                    is = inline;
                } else {
                    is = new ItemStack(material, material.getMaxStackSize());
                    ItemMeta im = is.getItemMeta();
                    ConfigurationSection metas = pages.getConfigurationSection(currentPage + ".metas");
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
    }

    @Override
    public void firstInit(Player player) {
        inv = player.getInventory().getContents().clone();
        ItemStack[] contents = new ItemStack[41];
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);
        for (int i = 0; i < 36; i++) {
            if (i >= 9) {
                contents[i] = shulker.getInventory().getContents()[i - 9];
            } else {
                contents[i] = inline;
            }
        }
        player.getInventory().setContents(contents);
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
            currentPage = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            init(player);
        }
        if (event.getSlot() <= 33) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN)) {
                ItemStack is = new ItemStack(item.getType(), item.getType().getMaxStackSize());
                ItemMeta im = item.getItemMeta();
                im.getPersistentDataContainer().remove(new NamespacedKey(plugin, "pageContent"));
                is.setItemMeta(im);
                player.setItemOnCursor(is);
            }
        } else if (event.getSlot() == 53) {
            BlockStateMeta meta = (BlockStateMeta) this.item.getItemMeta();
            ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
            ItemStack[] contents = new ItemStack[27];
            for (int i = 0; i < 27; i++) {
                contents[i] = player.getInventory().getContents()[i + 9];
            }
            shulker.getInventory().setContents(contents);
            meta.setBlockState(shulker);
            this.item.setItemMeta(meta);
            player.getInventory().setContents(inv);
            back.open(player);
        }
    }

    @Override
    public void onBottomClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if ((event.getSlot() >= 0 && event.getSlot() <= 8) || event.getCursor().getType().name().endsWith("SHULKER_BOX") || event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
            return;
        }
        if (event.isShiftClick()) {
            ItemStack item = player.getInventory().getItem(event.getSlot());
            if (item == null) return;
            boolean armor = false;
            for (String end : List.of("_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS")) {
                if (item.getType().name().endsWith(end)) {
                    armor = true;
                    break;
                }
            }
            new EditItemGUI(this, item, player.getInventory().getContents(), armor).open(player);
            player.getInventory().clear();
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        event.getPlayer().getInventory().clear();
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.getItemDrop().remove();
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        ItemStack cursor = event.getOldCursor();
        for (int slot : event.getRawSlots()) {
            ItemStack item = gui.getItem(slot);
            if (List.of(8, 17, 26, 35).contains(slot)) {
                String end = switch (slot) {
                    case 8 -> "_HELMET";
                    case 17 -> "_CHESTPLATE";
                    case 26 -> "_LEGGINGS";
                    case 35 -> "_BOOTS";
                    default -> "";
                };
                if (item == null && !cursor.getType().name().endsWith(end)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public boolean defaultInit() {
        return false;
    }
}
