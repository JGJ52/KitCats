package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.GUI;
import hu.jgj52.libCats.Listeners.ChatListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static hu.jgj52.kitCats.KitCats.pages;

public class CreatePageGUI extends GUI {
    private Component name = getComponent("nameItemName", true);
    private Material icon = Material.APPLE;
    private boolean nameSet = false;
    private final GUI back;
    public CreatePageGUI(GUI back) {
        this.back = back;
    }
    @Override
    public void init(Player player) {
        ItemStack name = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = name.getItemMeta();
        nameMeta.displayName(this.name);
        name.setItemMeta(nameMeta);

        ItemStack icon = new ItemStack(this.icon);
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.displayName(getComponent("iconItemName", true));
        icon.setItemMeta(iconMeta);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(getComponent("backItemName", true));
        back.setItemMeta(backMeta);

        gui.setItem(0, back);
        gui.setItem(12, name);
        gui.setItem(14, icon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        switch (event.getSlot()) {
            case 0:
                if (!nameSet) {
                    back.open(player);
                    break;
                }
                pages.getConfig().set(name + ".icon", icon.name());
                pages.saveConfig();
                pages.reloadConfig();
                back.open(player);
                break;
            case 12:
                player.closeInventory();
                player.sendMessage(getComponent("setNameMessage"));
                ChatListener.add(player, e -> {
                    if (pages.getConfig().getKeys(false).contains(PlainTextComponentSerializer.plainText().serialize(e))) {
                        player.sendMessage(getComponent("alreadyName"));
                    } else {
                        nameSet = true;
                        this.name = e;
                    }
                    open(player);
                });
                break;
            case 14:
                player.closeInventory();
                player.sendMessage(getComponent("setIconMessage"));
                ChatListener.add(player, "done", e -> {
                    icon = player.getInventory().getItemInMainHand().getType();
                    open(player);
                });
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
