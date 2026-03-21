package hu.jgj52.kitCats.Types;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static hu.jgj52.kitCats.KitCats.plugin;

public class CustomKit {
    public static CustomKit of(String name, Player player) {
        return new CustomKit(name, player);
    }
    private static void save() {
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    public static List<CustomKit> all(Player player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.customkits." + player.getUniqueId());
        List<CustomKit> list = new ArrayList<>();
        if (section == null) return list;
        for (String name : section.getKeys(false)) {
            list.add(of(name, player));
        }
        return list;
    }

    private final String name;
    private final Player player;
    private ItemStack[] contents;
    private Material icon;

    private CustomKit (String name, Player player) {
        this.name = name;
        this.player = player;
        Object obj = plugin.getConfig().get("data.customkits." + player.getUniqueId() + "." + name + ".contents");
        if (obj instanceof List<?> list) contents = list.toArray(new ItemStack[0]); else contents = new ItemStack[0];
        String icon = plugin.getConfig().getString("data.customkits." + player.getUniqueId() + "." + name + ".icon");
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
        plugin.getConfig().set("data.customkits." + player.getUniqueId() + "." + name + ".contents", contents);
        plugin.getConfig().set("data.customkits." + player.getUniqueId() + "." + name + ".player", null);
        save();
    }
}
