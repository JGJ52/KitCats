package hu.jgj52.kitCats.GUIs;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TrimArmorGUI extends GUI {
    private final GUI back;
    private final ItemStack item;
    public TrimArmorGUI(GUI back, ItemStack item) {
        this.back = back;
        this.item = item;
    }

    @Override
    public void init(Player player) {

    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
