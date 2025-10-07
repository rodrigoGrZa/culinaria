package com.beyondcosmos.culinaria.registry;

import com.beyondcosmos.culinaria.Culinaria;
import com.beyondcosmos.culinaria.block.entiy.CookingStationBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
  public static BlockEntityType<CookingStationBlockEntity> COOKING_STATION_BE;

  public static void register() {
    COOKING_STATION_BE =
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Culinaria.MOD_ID, "cooking_station"),
            FabricBlockEntityTypeBuilder.create(
                    CookingStationBlockEntity::new, ModBlocks.COOKING_STATION)
                .build());
  }
}
