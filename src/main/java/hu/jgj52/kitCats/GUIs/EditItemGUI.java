package hu.jgj52.kitCats.GUIs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import hu.jgj52.kitCats.Listeners.ChatListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static hu.jgj52.kitCats.KitCats.viaversion;

public class EditItemGUI extends GUI {
    private final GUI back;
    private final ItemStack item;
    private final ItemStack[] inv;
    private final boolean armor;
    public EditItemGUI(GUI back, ItemStack item, ItemStack[] inv, boolean armor) {
        this.back = back;
        this.item = item;
        this.inv = inv;
        this.armor = armor;
    }

    @Override
    public void init(Player player) {
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

        ItemStack enchants = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta enchantsMeta = enchants.getItemMeta();
        enchantsMeta.setDisplayName(getMessage("enchantsItemName"));
        enchants.setItemMeta(enchantsMeta);

        ItemStack name = new ItemStack(Material.PAPER);
        ItemMeta nameMeta = name.getItemMeta();
        nameMeta.setDisplayName(getMessage("nameItemName"));
        name.setItemMeta(nameMeta);

        ItemStack trim;

        if (viaversion && Via.getAPI().getPlayerProtocolVersion(player.getUniqueId()).newerThanOrEqualTo(ProtocolVersion.v1_21_2) && Via.getAPI().getServerVersion().highestSupportedProtocolVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            trim = new ItemStack(Material.PAPER);
            ItemMeta trimMeta = trim.getItemMeta();
            trimMeta.setDisplayName(getMessage("trimItemName"));
            trimMeta.setItemModel(new NamespacedKey("minecraft", "netherite_upgrade_smithing_template"));
            trim.setItemMeta(trimMeta);
        } else {
            trim = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            ItemMeta trimMeta = trim.getItemMeta();
            trimMeta.setDisplayName(getMessage("trimItemName"));
            trim.setItemMeta(trimMeta);
        }

        for (int i = 0; i < 27; i++) {
            if (List.of(0, 1, 2, 9, 11, 18, 19, 20).contains(i)) {
                gui.setItem(i, blue);
            } else if (i == 10) {
                gui.setItem(i, item);
            } else if (i == 8) {
                gui.setItem(i, back);
            } else if (i == 14 && player.hasPermission("kitcats.customkits.enchant")) {
                gui.setItem(i, enchants);
            } else if(i == 23 && player.hasPermission("kitcats.customkits.name")) {
                gui.setItem(i, name);
            } else if (i == 5 && armor) {
                gui.setItem(i, trim);
            }
            else gui.setItem(i, inline);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = gui.getItem(event.getSlot());
        if (event.getSlot() == 8) {
            player.getInventory().setContents(inv);
            back.open(player);
        } else if (item != null && item.getType() != Material.GRAY_STAINED_GLASS_PANE) {
            switch (event.getSlot()) {
                case 5:
                    if (armor) {
                        new TrimArmorGUI(this, this.item).open(player);
                    }
                    break;
                case 14:
                    new EnchantItemGUI(this, this.item).open(player);
                    break;
                case 23:
                    player.closeInventory();
                    player.sendMessage(getMessage("setNameMessage"));
                    ChatListener.add(player, e -> {
                        ItemMeta im = this.item.getItemMeta();
                        im.setDisplayName(PlainTextComponentSerializer.plainText().serialize(e.message()));
                        this.item.setItemMeta(im);
                        open(player);
                        return true;
                    });
                    break;
            }
        }
    }

    @Override
    public int getSize() {
        return 27;
    }
}
