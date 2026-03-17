package hu.jgj52.kitCats.Listeners;

import hu.jgj52.kitCats.GUIs.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof GUI gui)) return;
        gui.onClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUI gui)) return;
        gui.onClose(event);
    }
}
