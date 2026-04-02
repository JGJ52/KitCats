package hu.jgj52.kitCats.Types;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.customkits;

public class CustomKit {
    public static CustomKit of(String name, Player player) {
        if (customkits.getConfig().get(player.getUniqueId() + "." + name) == null) return null;
        return new CustomKit(name, player);
    }
    private static void save() {
        customkits.saveConfig();
        customkits.reloadConfig();
    }

    public static List<CustomKit> all(Player player) {
        ConfigurationSection section = customkits.getConfig().getConfigurationSection(player.getUniqueId().toString());
        List<CustomKit> list = new ArrayList<>();
        if (section == null) return list;
        for (String name : section.getKeys(false)) {
            list.add(of(name, player));
        }
        return list;
    }

    public static void create(Player player, String name, Material icon, ItemStack[] content) {
        customkits.getConfig().set(player.getUniqueId() + "." + name + ".icon", icon.toString());
        customkits.getConfig().set(player.getUniqueId() + "." + name + ".contents", content);
        save();
    }

    private final String name;
    private final Player player;
    private ItemStack[] contents;
    private Material icon;

    private CustomKit (String name, Player player) {
        this.name = name;
        this.player = player;
        Object obj = customkits.getConfig().get(player.getUniqueId() + "." + name + ".contents");
        if (obj instanceof List<?> list) contents = list.toArray(new ItemStack[0]); else contents = new ItemStack[0];
        String icon = customkits.getConfig().getString(player.getUniqueId() + "." + name + ".icon");
        if (icon != null) this.icon = Material.matchMaterial(icon);
    }

    public void reloadContents() {
        Object obj = customkits.getConfig().get(player.getUniqueId() + "." + name + ".contents");
        if (obj instanceof List<?> list) contents = list.toArray(new ItemStack[0]); else contents = new ItemStack[0];
        String icon = customkits.getConfig().getString(player.getUniqueId() + "." + name + ".icon");
        if (icon != null) this.icon = Material.matchMaterial(icon);
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public ItemStack[] getContents() {
        ItemStack[] contents = this.contents.clone();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                contents[i] = contents[i].clone();
            }
        }
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
        customkits.getConfig().set(player.getUniqueId() + "." + name + ".contents", contents);
        save();
    }

    public void delete() {
        customkits.getConfig().set(player.getUniqueId() + "." + name, null);
        save();
    }
}
