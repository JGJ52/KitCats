package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitPreviewGUI extends GUI {
    private final Kit kit;
    public KitPreviewGUI(Kit kit) {
        this.kit = kit;
    }

    @Override
    public void init(Player player) {
        ItemStack[] contents = kit.getContents(player);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        ItemStack edit = new ItemStack(Material.BOOK);
        ItemMeta editMeta = edit.getItemMeta();
        editMeta.setDisplayName(getMessage("editItemName"));
        edit.setItemMeta(editMeta);

        for (int i = 0; i < 54; i++) {
            if (i >= 2 && i <= 5) {
                gui.setItem(i, contents[36 + (5 - i)]);
            } else if (i >= 9 && i <= 35) {
                gui.setItem(i, contents[i]);
            } else if (i == 6) {
                gui.setItem(i, contents[40]);
            } else if (i >= 45) {
                gui.setItem(i, contents[i - 45]);
            } else if (i == 8) {
                gui.setItem(i, edit);
            } else {
                gui.setItem(i, inline);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() == 8) {
            new EditKitGUI(kit).open(player);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public int getSize() {
        return 54;
    }
}
