package hu.jgj52.kitCats.Types;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Warning;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.plugin;

public class Kit {
    public static Map<String, Kit> kits = new HashMap<>();
    public static Kit of(String name) {
        if (kits.containsKey(name)) {
            return kits.get(name);
        }
        Kit kit = new Kit(name);
        kits.put(name, kit);
        return kit;
    }
    public static void save() {
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    private final String name;
    private ItemStack[] contents;
    private Material icon;

    private Kit (String name) {
        this.name = name;
        Object obj = plugin.getConfig().get("data.kits." + name + ".contents");
        if (obj instanceof List<?> list) contents = list.toArray(new ItemStack[0]); else contents = new ItemStack[0];
        String icon = plugin.getConfig().getString("data.kits." + name + ".icon");
        if (icon != null) this.icon = Material.matchMaterial(icon);
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public ItemStack[] getContents(Boolean defaultSlot) {
        ItemStack[] contents = this.contents.clone();
        if (defaultSlot) {
            for (int i = 0; i < contents.length; i++) {
                ItemStack is = contents[i];
                if (is != null) {
                    is = is.clone();
                    ItemMeta im = is.getItemMeta();
                    im.getPersistentDataContainer().set(new NamespacedKey(plugin, "defaultSlot"), PersistentDataType.INTEGER, i);
                    is.setItemMeta(im);
                    contents[i] = is;
                }
            }
        }
        return contents;
    }

    public ItemStack[] getContents() {
        return getContents(false);
    }

    public ItemStack[] getContents(Player player, Boolean defaultSlot) {
        ConfigurationSection custom = plugin.getConfig().getConfigurationSection("data.kits." + name + ".player." + player.getUniqueId());
        if (custom == null) return getContents(defaultSlot);
        ItemStack[] contents = new ItemStack[getContents(defaultSlot).length];
        for (String key : custom.getKeys(false)) {
            int c = Integer.parseInt(key);
            int o = custom.getInt(key);
            if (o == -1) {
                contents[c] = null;
                continue;
            }
            contents[c] = getContents(defaultSlot)[o];
        }
        return contents;
    }

    public ItemStack[] getContents(Player player) {
        return getContents(player, false);
    }

    @Warning(reason = "This will delete all player-customized kits")
    public void setContents(ItemStack[] contents) {
        this.contents = contents;
        plugin.getConfig().set("data.kits." + name + ".contents", contents);
        plugin.getConfig().set("data.kits." + name + ".player", null);
        save();
    }

    public void setContents(ItemStack[] contents, Player player) {
        Map<Integer, Integer> cont = new HashMap<>();
        for (int i = 0; i < contents.length; i++) {
            cont.put(i, contents[i] == null ? -1 : contents[i].getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "defaultSlot"), PersistentDataType.INTEGER));
        }
        plugin.getConfig().set("data.kits." + name + ".player." + player.getUniqueId(), cont);
        save();
    }
}
