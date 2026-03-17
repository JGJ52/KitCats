package hu.jgj52.kitCats.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static hu.jgj52.kitCats.KitCats.plugin;

public abstract class GUI implements InventoryHolder {
    protected Inventory gui;
    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
    public String getMessage(String msg) {
        return plugin.getConfig().getString("guis." + getClass().getSimpleName() + "." + msg);
    }
    public String getMsg(String msg) {
        return plugin.getConfig().getString("messages." + msg);
    }

    public void open(Player player) {
        reGui(player);
        player.openInventory(gui);
    }

    private void reGui(Player player) {
        gui = Bukkit.createInventory(this, getSize(), getName());

        ItemStack outline = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta outlineMeta = outline.getItemMeta();
        outlineMeta.setHideTooltip(true);
        outline.setItemMeta(outlineMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        for (int i = 0; i < getSize(); i++) {
            if (i <= 9 || List.of(17, 18, 26, 27, 35, 36, 44).contains(i) || i >= getSize() - 10) gui.setItem(i, outline);
            else gui.setItem(i, inline);
        }

        init(player);
    }

    public abstract void init(Player player);
    public abstract void onClick(InventoryClickEvent event);
    public abstract void onClose(InventoryCloseEvent event);
    public abstract int getSize();
    public String getName() {
        return getMessage("name");
    }
}
