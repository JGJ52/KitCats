package hu.jgj52.kitCats.Types;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Warning;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hu.jgj52.kitCats.KitCats.plugin;

public class Kit {
    public static Kit of(String name) {
        if (plugin.getConfig().get("data.kits." + name) == null) return null;
        return new Kit(name);
    }
    private static void save() {
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static List<Kit> all() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.kits");
        List<Kit> list = new ArrayList<>();
        if (section == null) return list;
        for (String name : section.getKeys(false)) {
            list.add(of(name));
        }
        return list;
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

    public void reloadContents() {
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
                    im.getPersistentDataContainer().set(new NamespacedKey(plugin, "kitName"), PersistentDataType.STRING, getName());
                    is.setItemMeta(im);
                    contents[i] = is;
                }
            }
        } else {
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    contents[i] = contents[i].clone();
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

    public boolean setContents(ItemStack[] contents, Player player) {
        int nulls = 0;
        int notNulls = 0;
        int dNulls = 0;
        int dNotNulls = 0;
        Map<Integer, Integer> cont = new HashMap<>();
        ItemStack[] iss = getContents(true);
        for (int i = 0; i < 41; i++) {
            if (contents[i] == null) {
                cont.put(i, -1);
                nulls++;
            } else {
                if (getName().equals(contents[i].getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "kitName"), PersistentDataType.STRING))) {
                    cont.put(i, contents[i].getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "defaultSlot"), PersistentDataType.INTEGER));
                    notNulls++;
                }
            }
            if (iss[i] == null) {
                dNulls++;
            } else {
                if (getName().equals(iss[i].getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "kitName"), PersistentDataType.STRING))) {
                    dNotNulls++;
                }
            }
        }
        if (nulls != dNulls || notNulls != dNotNulls) return false;
        plugin.getConfig().set("data.kits." + name + ".player." + player.getUniqueId(), cont);
        save();
        return true;
    }

    public void removeContents(Player player) {
        plugin.getConfig().set("data.kits." + name + ".player." + player.getUniqueId(), null);
        save();
    }
}
