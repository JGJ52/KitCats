package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.CustomKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomKitPreviewGUI extends GUI {
    private final CustomKit kit;
    private GUI back;
    public CustomKitPreviewGUI(CustomKit kit) {
        this.kit = kit;
    }
    public CustomKitPreviewGUI(CustomKit kit, GUI back) {
        this.kit = kit;
        this.back = back;
    }

    @Override
    public void init(Player player) {
        ItemStack[] contents = kit.getContents();

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        ItemStack edit = new ItemStack(Material.BOOK);
        ItemMeta editMeta = edit.getItemMeta();
        editMeta.setDisplayName(getMessage("editItemName"));
        edit.setItemMeta(editMeta);

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
            } else if (i == 8) {
                gui.setItem(i, edit);
            } else if (this.back != null && i == 0) {
                gui.setItem(i, back);
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
            new CustomKitEditorGUI(this, kit).open(player);
        } else if (back != null && event.getSlot() == 0) {
            back.open(player);
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
