package hu.jgj52.kitCats.Types;

import hu.jgj52.libCats.Utils.RegistryFromName;
import io.papermc.paper.registry.keys.TrimMaterialKeys;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Arrays;
import java.util.List;

public class Trim {
    private Material pattern;
    private Material material;
    public Trim(Material pattern, Material material) {
        this.pattern = pattern;
        this.material = material;
    }
    public Material getPattern() {
        return pattern;
    }
    public Material getMaterial() {
        return material;
    }
    public void setPattern(Material pattern) {
        this.pattern = pattern;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
    public ArmorTrim trim() {
        if (getPattern() == null || getMaterial() == null) return null;
        TrimPattern pattern = RegistryFromName.TRIM_PATTERN(getMaterial().name().split("_")[0]);
        TrimMaterial material = RegistryFromName.TRIM_MATERIAL(getMaterial().name().split("_")[0]);
        if (pattern == null || material == null) return null;
        return new ArmorTrim(material, pattern);
    }
    public static List<Material> patterns() {
        return Arrays.stream(Material.values())
                .filter(mat -> mat.name().endsWith("_SMITHING_TEMPLATE"))
                .filter(mat -> !mat.name().startsWith("NETHERITE_UPGRADE_"))
                .toList();
    }
    public static List<Material> materials() {
        return List.of(
                Material.COPPER_INGOT,
                Material.IRON_INGOT,
                Material.RESIN_BRICK,
                Material.REDSTONE,
                Material.LAPIS_LAZULI,
                Material.EMERALD,
                Material.AMETHYST_SHARD,
                Material.QUARTZ,
                Material.NETHERITE_INGOT,
                Material.DIAMOND,
                Material.GOLD_INGOT
        );
    }
}
