package hu.jgj52.kitCats.GUIs;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KitGUI extends GUI {
    @Override
    protected Integer getSize() {
        return 54;
    }

    @Override
    protected boolean open(Player player, Inventory gui) {
        return true;
    }
}
