package hu.jgj52.kitCats.GUIs;

import hu.jgj52.kitCats.Types.GUI;
import hu.jgj52.libCats.Utils.RegistryFromName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.plugin;

public class EnchantItemGUI extends GUI {
    private final hu.jgj52.libCats.Types.GUI back;
    private final ItemStack item;
    private final Map<Enchantment, Integer> levels = new HashMap<>();
    private final Map<Integer, Enchantment> slots = new HashMap<>();
    public EnchantItemGUI(hu.jgj52.libCats.Types.GUI back, ItemStack item) {
        this.back = back;
        this.item = item;

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            levels.put(enchantment, item.getEnchantments().get(enchantment));
        }
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
        backMeta.displayName(getComponent("backItemName", true));
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

        int slot = 37;
        List<String> disabled = plugin.getConfig().getStringList("customkits.enchants.disabled");
        for (Enchantment enchantment : RegistryFromName.ENCHANTMENT()) {
            if (enchantment.canEnchantItem(item) && !disabled.contains(enchantment.getKey().getKey())) {
                slots.put(slot, enchantment);
                int level = levels.computeIfAbsent(enchantment, e -> 0);
                ItemStack enchant = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) enchant.getItemMeta();
                enchantMeta.displayName(Component.translatable("enchantment." + enchantment.getKey().getNamespace() + "." + enchantment.getKey().getKey()).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                enchantMeta.addStoredEnchant(enchantment, level, true);
                enchantMeta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
                if (level == 0) {
                    enchantMeta.setEnchantmentGlintOverride(false);
                }
                List<Component> lore = new ArrayList<>(List.of(
                        getComponent("level", true).append(level != 0 ? Component.translatable("enchantment.level." + level) : Component.text("0")),
                        getComponent("maxLevel", true).append(Component.translatable("enchantment.level." + enchantment.getMaxLevel()))
                ));
                for (Enchantment ench : item.getEnchantments().keySet()) {
                    if (ench == enchantment) continue;
                    if (ench.conflictsWith(enchantment)) {
                        lore.add(getComponent("incompatible", true).append(Component.translatable("enchantment." + ench.getKey().getNamespace() + "." + ench.getKey().getKey())));
                        enchantMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "incompatible"), PersistentDataType.BOOLEAN, true);
                    }
                }
                enchantMeta.lore(lore);
                enchant.setItemMeta(enchantMeta);

                gui.setItem(slot, enchant);
                slot++;
                if ((slot + 1) % 9 == 0) slot += 2;
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = gui.getItem(event.getSlot());
        if (item == null) return;
        if (event.getSlot() != 13 && item.getType() == Material.ENCHANTED_BOOK) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "incompatible"))) return;
            Enchantment enchantment = slots.get(event.getSlot());
            int level = levels.get(enchantment);
            int add = 0;
            if (event.getClick().isLeftClick()) {
                if (level >= enchantment.getMaxLevel()) return;
                add++;
            } else if (event.getClick().isRightClick()) {
                if (level == 0) return;
                add--;
            }
            levels.put(enchantment, level + add);
            level += add;
            if (level > 0) {
                this.item.addEnchantment(enchantment, level);
            } else {
                this.item.removeEnchantment(enchantment);
            }
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
