package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.KitCats;
import hu.jgj52.kitCats.Types.CustomKit;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.customkits;
import static hu.jgj52.kitCats.KitCats.plugin;

public class CustomKitEditorGUI extends GUI {
    private final GUI back;
    private final ItemStack[] contents;
    private final CustomKit kit;
    private ItemStack[] inv;
    public CustomKitEditorGUI(GUI back, ItemStack[] contents) {
        this.back = back;
        this.contents = contents;
        this.kit = null;
    }
    public CustomKitEditorGUI(GUI back, CustomKit kit) {
        this.back = back;
        this.contents = null;
        this.kit = kit;
    }
    public CustomKitEditorGUI(CustomKit kit) {
        this.back = null;
        this.contents = null;
        this.kit = kit;
    }
    private String currentPage;
    private boolean f = false;
    private boolean b = false;
    private int pageOffset = 0;
    @Override
    public void init(Player player) {
        f = false;
        b = false;
        ConfigurationSection pages = KitCats.pages.getConfig();
        if (pages.getKeys(false).isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 1L);
            player.sendMessage(getComponent("noPages"));
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
        backMeta.displayName(getComponent("pageScrollerBackItemName", true));
        back.setItemMeta(backMeta);

        ItemStack forward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = forward.getItemMeta();
        forwardMeta.displayName(getComponent("pageScrollerForwardItemName", true));
        forward.setItemMeta(forwardMeta);

        ItemStack saveBack = new ItemStack(Material.ARROW);
        ItemMeta saveBackMeta = saveBack.getItemMeta();
        saveBackMeta.displayName(getComponent("backItemName", true));
        saveBack.setItemMeta(saveBackMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.displayName(getComponent("saveItemName", true));
        save.setItemMeta(saveMeta);

        for (int i = 0; i < 54; i++) {
            if ((i + 2) % 9 == 0 || (i >= 36 && i <= 44)) {
                gui.setItem(i, outline);
            } else if (!List.of(8, 17, 26, 35, 53).contains(i)) {
                gui.setItem(i, i > 44 ? inl : inline);
            } else if (i == 53) {
                gui.setItem(i, kit == null ? saveBack : save);
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
        player.getInventory().clear();
        ItemStack[] contents = new ItemStack[0];
        if (this.contents != null) {
            contents = this.contents;
        } else if (kit != null) {
            contents = kit.getContents();
        }
        Map<Integer, Integer> map = Map.of(
                39, 8,
                38, 17,
                37, 26,
                36, 35
        );
        for (int i = 0; i < contents.length; i++) {
            if (map.containsKey(i)) {
                gui.setItem(map.get(i), contents[i]);
            } else {
                player.getInventory().setItem(i, contents[i]);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack item = gui.getItem(event.getSlot());
        ItemStack cursor = event.getCursor();
        if (List.of(8, 17, 26, 35).contains(event.getSlot())) {
            String end = switch (event.getSlot()) {
                case 8 -> "_HELMET";
                case 17 -> "_CHESTPLATE";
                case 26 -> "_LEGGINGS";
                case 35 -> "_BOOTS";
                default -> "";
            };
            if ((item != null && !item.getType().name().endsWith(end)) || cursor.getType() != Material.AIR && !cursor.getType().name().endsWith(end)) {
                event.setCancelled(true);
                return;
            }
            if (!event.getClick().isShiftClick()) return;
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (item == null) return;
            new EditItemGUI(this, item, player.getInventory().getContents(), true).open(player);
            player.getInventory().clear();
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
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
        if (event.getSlot() <= 33) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "pageContent"), PersistentDataType.BOOLEAN)) {
                ItemStack is = new ItemStack(item.getType(), item.getType().getMaxStackSize());
                ItemMeta im = item.getItemMeta();
                im.getPersistentDataContainer().remove(new NamespacedKey(plugin, "pageContent"));
                is.setItemMeta(im);
                player.setItemOnCursor(is);
            }
        } else if (event.getSlot() == 53 && contents != null) {
            Map<Integer, Integer> armors = Map.of(
                    39, 8,
                    38, 17,
                    37, 26,
                    36, 35
            );
            for (int i = 0; i < contents.length; i++) {
                if (armors.containsKey(i)) {
                    contents[i] = gui.getItem(armors.get(i));
                    continue;
                }
                contents[i] = player.getInventory().getContents()[i];
            }

            if (back != null) { // i know its never null here but i hate when idea is crying
                back.open(player);
            }
        } else if (event.getSlot() == 53 && kit != null) {
            ItemStack[] contents = new ItemStack[41];
            Map<Integer, Integer> armors = Map.of(
                    39, 8,
                    38, 17,
                    37, 26,
                    36, 35
            );
            for (int i = 0; i < contents.length; i++) {
                if (armors.containsKey(i)) {
                    contents[i] = gui.getItem(armors.get(i));
                    continue;
                }
                contents[i] = player.getInventory().getContents()[i];
            }
            customkits.getConfig().set(player.getUniqueId() + "." + kit.getName() + ".contents", contents);
            customkits.saveConfig();
            customkits.reloadConfig();
            kit.reloadContents();
            player.getInventory().setContents(inv);
            if (back != null) {
                back.open(player);
            } else {
                player.closeInventory();
                player.sendMessage(getComponent("saved"));
            }
        }
    }

    @Override
    public void onBottomClick(InventoryClickEvent event) {
        if (!event.getClick().isShiftClick()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
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

    @Override
    public void onMove(PlayerMoveEvent event) {
        event.getPlayer().closeInventory();
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        event.getPlayer().closeInventory();
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.getItemDrop().remove();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        event.getPlayer().getInventory().setContents(inv);
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
