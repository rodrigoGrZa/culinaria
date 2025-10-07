package com.beyondcosmos.culinaria.registry;

import com.beyondcosmos.culinaria.Culinaria;
import com.beyondcosmos.culinaria.block.CookingStationBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
  public static Block COOKING_STATION;

  public static void register() {
    Identifier id = Identifier.of(Culinaria.MOD_ID, "cooking_station");
    RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);

    AbstractBlock.Settings settings =
        AbstractBlock.Settings.create().registryKey(blockKey).strength(2.0f, 6.0f).nonOpaque();

    COOKING_STATION = Registry.register(Registries.BLOCK, id, new CookingStationBlock(settings));

    RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);

    Registry.register(
        Registries.ITEM,
        id,
        new BlockItem(COOKING_STATION, new Item.Settings().registryKey(itemKey)));

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
        .register(entries -> entries.add(COOKING_STATION.asItem()));
  }
}
