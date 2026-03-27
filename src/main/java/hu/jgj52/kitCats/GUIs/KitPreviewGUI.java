package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitPreviewGUI extends GUI {
    private final Kit kit;
    private GUI back;
    private boolean displayDefault = false;
    public KitPreviewGUI(Kit kit) {
        this.kit = kit;
    }
    public KitPreviewGUI(Kit kit, GUI back) {
        this.kit = kit;
        this.back = back;
    }

    @Override
    public void init(Player player) {
        ItemStack[] contents = displayDefault ? kit.getContents() : kit.getContents(player);

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

        ItemStack displayDefault = new ItemStack(Material.TARGET);
        ItemMeta displayDefaultMeta = displayDefault.getItemMeta();
        displayDefaultMeta.setDisplayName(getMessage("displayDefaultItemName"));
        displayDefaultMeta.setLore(List.of(getMessage(this.displayDefault ? "default" : "customized")));
        displayDefault.setItemMeta(displayDefaultMeta);

        for (int i = 0; i < 54; i++) {
            if (i >= 2 && i <= 5) {
                gui.setItem(i, contents[36 + (5 - i)]);
            } else if (i >= 9 && i <= 35) {
                gui.setItem(i, contents[i]);
            } else if (i == 6) {
                gui.setItem(i, contents[40]);
            } else if (i >= 45) {
                gui.setItem(i, contents[i - 45]);
            } else if (i == 8 && player.hasPermission("kitcats.command.kit.edit")) {
                gui.setItem(i, edit);
            } else if (this.back != null && i == 0) {
                gui.setItem(i, back);
            } else if (i == 7) {
                gui.setItem(i, displayDefault);
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
            if (player.hasPermission("kitcats.command.kit.edit")) {
                new EditKitGUI(kit, this).open(player);
            }
        } else if (back != null && event.getSlot() == 0) {
            back.open(player);
        } else if (event.getSlot() == 7) {
            displayDefault = !displayDefault;
            init(player);
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
