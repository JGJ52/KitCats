package hu.jgj52.kitCats.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static hu.jgj52.kitCats.KitCats.plugin;

public abstract class GUI implements InventoryHolder {
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(this, getSize(), plugin.getConfig().getString("guis." + getClass().getSimpleName() + ".title"));

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        for (int i = 0; i < getSize(); i++) {
            if (i <= 9 || i >= getSize() - 10 || (i % 9 == 0 || (i + 1) % 9 == 0)) {
                gui.setItem(i, outline);
            } else {
                gui.setItem(i, inline);
            }
        }

        if (open(player, gui)) {
            player.openInventory(gui);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    protected abstract Integer getSize();
    protected abstract boolean open(Player player, Inventory gui);

    private String getMessage(String msg) {
        return plugin.getConfig().getString("guis." + getClass().getSimpleName() + "." + msg);
    }
}
