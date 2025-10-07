package com.beyondcosmos.culinaria;

import com.beyondcosmos.culinaria.registry.ModBlockEntities;
import com.beyondcosmos.culinaria.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Culinaria implements ModInitializer {
	public static final String MOD_ID = "culinaria";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();

		LOGGER.info("Culinaria core load");
	}


}