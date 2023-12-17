package com.faboslav.friendsandfoes.fabric;

import com.faboslav.friendsandfoes.FriendsAndFoes;
import com.faboslav.friendsandfoes.events.fabric.FabricReloadListener;
import com.faboslav.friendsandfoes.events.lifecycle.DatapackSyncEvent;
import com.faboslav.friendsandfoes.events.lifecycle.RegisterReloadListenerEvent;
import com.faboslav.friendsandfoes.events.lifecycle.SetupEvent;
import com.faboslav.friendsandfoes.init.FriendsAndFoesPointOfInterestTypes;
import com.faboslav.friendsandfoes.util.ServerWorldSpawnersUtil;
import com.faboslav.friendsandfoes.util.UpdateChecker;
import com.faboslav.friendsandfoes.world.spawner.IceologerSpawner;
import com.faboslav.friendsandfoes.world.spawner.IllusionerSpawner;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.world.dimension.DimensionTypes;

public final class FriendsAndFoesFabric implements ModInitializer
{
	@Override
	public void onInitialize() {
		UpdateChecker.checkForNewUpdates();
		FriendsAndFoes.init();
		FriendsAndFoes.postInit();
		FriendsAndFoesPointOfInterestTypes.postInit();

		initEvents();
	}

	private static void initEvents() {
		SetupEvent.EVENT.invoke(new SetupEvent(Runnable::run));

		RegisterReloadListenerEvent.EVENT.invoke(new RegisterReloadListenerEvent((id, listener) -> {
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new FabricReloadListener(id, listener));
		}));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
			DatapackSyncEvent.EVENT.invoke(new DatapackSyncEvent(player)));

		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (world.isClient() || world.getDimensionKey() != DimensionTypes.OVERWORLD) {
				return;
			}

			ServerWorldSpawnersUtil.register(world, new IceologerSpawner());
			ServerWorldSpawnersUtil.register(world, new IllusionerSpawner());
		}));
	}
}
