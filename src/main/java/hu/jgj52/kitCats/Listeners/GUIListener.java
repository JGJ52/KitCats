package hu.jgj52.kitCats.Listeners;

import hu.jgj52.kitCats.GUIs.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getHolder() instanceof GUI gui) {
            gui.onClick(event);
        }
        if (event.getView().getTopInventory().getHolder() instanceof GUI gui && event.getClickedInventory() == event.getView().getBottomInventory()) {
            gui.onBottomClick(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GUI gui)) return;
        gui.onClose(event);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof GUI gui)) return;
        gui.onDrop(event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof GUI gui)) return;
        gui.onMove(event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof GUI gui)) return;
        gui.onInteract(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof GUI gui) {
            gui.onDrag(event);
        }
        if (event.getView().getTopInventory().getHolder() instanceof GUI gui && event.getInventory() == event.getView().getBottomInventory()) {
            gui.onBottomDrag(event);
        }
    }
}