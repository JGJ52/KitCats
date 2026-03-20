package hu.jgj52.kitCats.Types;

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
        TrimPattern pattern = getPattern(getPattern());
        TrimMaterial material = getMaterial(getMaterial());
        if (pattern == null || material == null) return null;
        return new ArmorTrim(material, pattern);
    }
    public static TrimPattern getPattern(Material template) {
        return switch (template) {
            case Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.BOLT;
            case Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.COAST;
            case Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.DUNE;
            case Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.EYE;
            case Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.FLOW;
            case Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.HOST;
            case Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RAISER;
            case Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RIB;
            case Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SENTRY;
            case Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SHAPER;
            case Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SILENCE;
            case Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SNOUT;
            case Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SPIRE;
            case Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.TIDE;
            case Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.VEX;
            case Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WARD;
            case Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WAYFINDER;
            case Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WILD;
            default -> null;
        };
    }
    public static TrimMaterial getMaterial(Material material) {
        return switch (material) {
            case Material.COPPER_INGOT -> TrimMaterial.COPPER;
            case Material.IRON_INGOT -> TrimMaterial.IRON;
            case Material.RESIN_BRICK -> TrimMaterial.RESIN;
            case Material.REDSTONE_WIRE -> TrimMaterial.REDSTONE;
            case Material.LAPIS_LAZULI -> TrimMaterial.LAPIS;
            case Material.EMERALD -> TrimMaterial.EMERALD;
            case Material.AMETHYST_SHARD -> TrimMaterial.AMETHYST;
            case Material.QUARTZ -> TrimMaterial.QUARTZ;
            case Material.NETHERITE_INGOT -> TrimMaterial.NETHERITE;
            case Material.DIAMOND -> TrimMaterial.DIAMOND;
            case Material.GOLD_INGOT -> TrimMaterial.GOLD;
            default -> null;
        };
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
                Material.REDSTONE_WIRE,
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
