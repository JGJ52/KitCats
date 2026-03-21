package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.Trim;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TrimPatternArmorGUI extends GUI {
    private final GUI back;
    private final ItemStack item;
    private final Trim trim;
    private Material current;
    public TrimPatternArmorGUI(GUI back, ItemStack item, Trim trim) {
        this.back = back;
        this.item = item;
        this.trim = trim;
    }

    @Override
    public void init(Player player) {
        if (trim.trim() != null) {
            ArmorMeta am = (ArmorMeta) item.getItemMeta();
            am.setTrim(trim.trim());
            item.setItemMeta(am);
        }

        ItemStack blue = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta blueMeta = blue.getItemMeta();
        blueMeta.setHideTooltip(true);
        blue.setItemMeta(blueMeta);

        ItemStack inline = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta inlineMeta = inline.getItemMeta();
        inlineMeta.setHideTooltip(true);
        inline.setItemMeta(inlineMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(getMessage("backItemName"));
        back.setItemMeta(backMeta);

        for (int i = 0; i < 54; i++) {
            if (List.of(1, 2, 6, 7, 46, 47, 48, 49, 50, 51, 52).contains(i)) {
                gui.setItem(i, inline);
            } else if (List.of(3, 4, 5, 12, 14, 21, 22, 23).contains(i)) {
                gui.setItem(i, blue);
            } else if (i == 13) {
                gui.setItem(i, item);
            } else if (i == 0) {
                gui.setItem(i, back);
            }
        }

        int slot = 28;
        for (Material template : Trim.patterns()) {
            ItemStack is = new ItemStack(template);
            if (current != null && current == template) {
                ItemMeta im = is.getItemMeta();
                im.setEnchantmentGlintOverride(true);
                is.setItemMeta(im);
            }
            gui.setItem(slot, is);
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = gui.getItem(event.getSlot());
        if (item != null && item.getType() != Material.BLACK_STAINED_GLASS_PANE && item.getType() != Material.GRAY_STAINED_GLASS_PANE && event.getSlot() != 0) {
            current = item.getType();
            trim.setPattern(item.getType());
            init(player);
        } else if (event.getSlot() == 0) {
            back.open(player);
        }
    }

    @Override
    public int getSize() {
        return 54;
    }
}
