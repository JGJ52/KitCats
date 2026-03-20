package hu.jgj52.kitCats.GUIs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import hu.jgj52.kitCats.Listeners.ChatListener;
import hu.jgj52.kitCats.Types.Trim;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.List;

import static hu.jgj52.kitCats.KitCats.viaversion;

public class EditItemGUI extends GUI {
    private final GUI back;
    private final ItemStack item;
    private final ItemStack[] inv;
    private final boolean armor;
    private final Trim trim = new Trim(null, null);
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

        if (trim.trim() != null) {
            ArmorMeta am = (ArmorMeta) item.getItemMeta();
            am.setTrim(trim.trim());
            item.setItemMeta(am);
        }

        ItemStack trim;

        Material trimType = this.trim.getPattern() == null ? Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE : this.trim.getPattern();
        if (viaversion && Via.getAPI().getPlayerProtocolVersion(player.getUniqueId()).newerThanOrEqualTo(ProtocolVersion.v1_21_2) && Via.getAPI().getServerVersion().highestSupportedProtocolVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            trim = new ItemStack(Material.PAPER);
            ItemMeta trimMeta = trim.getItemMeta();
            trimMeta.setDisplayName(getMessage("trimItemName"));
            trimMeta.setItemModel(new NamespacedKey("minecraft", trimType.name().toLowerCase()));
            trim.setItemMeta(trimMeta);
        } else {
            trim = new ItemStack(trimType);
            ItemMeta trimMeta = trim.getItemMeta();
            trimMeta.setDisplayName(getMessage("trimItemName"));
            trim.setItemMeta(trimMeta);
        }

        ItemStack trimMaterial = new ItemStack(this.trim.getMaterial() == null ? Material.RAW_IRON : this.trim.getMaterial());
        ItemMeta trimMaterialMeta = trim.getItemMeta();
        trimMaterialMeta.setDisplayName(getMessage("trimMaterialItemName"));
        trimMaterial.setItemMeta(trimMaterialMeta);

        boolean enchantable = false;
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.canEnchantItem(this.item)) {
                enchantable = true;
                break;
            }
        }

        for (int i = 0; i < 27; i++) {
            if (List.of(0, 1, 2, 9, 11, 18, 19, 20).contains(i)) {
                gui.setItem(i, blue);
            } else if (i == 10) {
                gui.setItem(i, item);
            } else if (i == 8) {
                gui.setItem(i, back);
            } else if (i == 23 && enchantable && player.hasPermission("kitcats.customkits.enchant")) {
                gui.setItem(i, enchants);
            } else if(i == 14 && player.hasPermission("kitcats.customkits.name")) {
                gui.setItem(i, name);
            } else if (i == 5 && armor && player.hasPermission("kitcats.customkits.trim")) {
                gui.setItem(i, trim);
            } else if (i == 6 && armor && player.hasPermission("kitcats.customkits.trim")) {
                gui.setItem(i, trimMaterial);
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
                    new TrimPatternArmorGUI(this, this.item, trim).open(player);
                    break;
                case 6:
                    new TrimMaterialArmorGUI(this, this.item, trim).open(player);
                    break;
                case 14:
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
                case 23:
                    new EnchantItemGUI(this, this.item).open(player);
                    break;
            }
        }
    }

    @Override
    public boolean defaultInit() {
        return false;
    }

    @Override
    public int getSize() {
        return 27;
    }
}
