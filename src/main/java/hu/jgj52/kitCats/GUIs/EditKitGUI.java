package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.Kit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.plugin;

public class EditKitGUI extends GUI {
    private final Kit kit;
    private GUI back;
    public EditKitGUI(Kit kit) {
        this.kit = kit;
    }
    public EditKitGUI(Kit kit, GUI back) {
        this.kit = kit;
        this.back = back;
    }

    @Override
    public void init(Player player) {
        ItemStack[] contents = kit.getContents(player, true);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        ItemStack reset = new ItemStack(Material.RED_CONCRETE);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.setDisplayName(getMessage("resetItemName"));
        reset.setItemMeta(resetMeta);

        ItemStack save = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.setDisplayName(getMessage("saveItemName"));
        save.setItemMeta(saveMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("backItemName"));
        back.setItemMeta(backMeta);

        for (int i = 0; i < 54; i++) {
            if (i >= 2 && i <= 5) {
                gui.setItem(i, contents[36 + (5 - i)]);
            } else if (i >= 9 && i <= 35) {
                gui.setItem(i, contents[i]);
            } else if (i == 6) {
                gui.setItem(i, contents[40]);
            } else if (i >= 45) {
                gui.setItem(i, contents[i - 45]);
            } else if (i == 7) {
                gui.setItem(i, reset);
            } else if (i == 8) {
                gui.setItem(i, save);
            } else if (this.back != null && i == 0) {
                gui.setItem(i, back);
            } else {
                gui.setItem(i, inline);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
        }
        if (List.of(0, 1, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44).contains(event.getSlot())) {
            event.setCancelled(true);
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() == 7) {
            kit.removeContents(player);
            init(player);
        } else if (event.getSlot() == 8) {
            ItemStack[] contents = new ItemStack[41];
            for (int i = 0; i < 54; i++) {
                ItemStack is = gui.getItem(i);
                if (i >= 45) {
                    contents[i - 45] = is;
                } else if (i >= 9 && i <= 35) {
                    contents[i] = is;
                } else if (i == 2) {
                    contents[39] = is;
                } else if (i == 3) {
                    contents[38] = is;
                } else if (i == 4) {
                    contents[37] = is;
                } else if (i == 5) {
                    contents[36] = is;
                } else if(i == 6) {
                    contents[40] = is;
                }
            }
            Map<Integer, String> map = Map.of(
                    36, "BOOTS",
                    37, "LEGGINGS",
                    38, "CHESTPLATE",
                    39, "HELMET"
            );
            for (int key : map.keySet()) {
                if (contents[key] != null) {
                    if (!contents[key].getType().name().endsWith(map.get(key))) {
                        player.sendMessage(getMessage("wrongArmor"));
                        return;
                    }
                }
            }
            boolean success = kit.setContents(contents, player);
            if (success) {
                player.sendMessage(getMessage("success"));
            } else {
                player.sendMessage(getMessage("wrongContents"));
            }
            player.closeInventory();
            for (ItemStack is : player.getInventory().getContents()) {
                if (is != null && kit.getName().equals(is.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "kitName"), PersistentDataType.STRING))) {
                    player.getInventory().remove(is);
                }
            }
        } else if (back != null && event.getSlot() == 0) {
            back.open(player);
        }
    }

    @Override
    public void onBottomClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && kit.getName().equals(is.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "kitName"), PersistentDataType.STRING))) {
                player.getInventory().remove(is);
            }
        }
    }

    @Override
    public int getSize() {
        return 54;
    }
}
