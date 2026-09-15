package com.redpxnda.handson;

import com.redpxnda.handson.block.BackboardBlock;
import com.redpxnda.handson.block.WorkbenchBlock;
import com.redpxnda.handson.block.quadblock.QuadWorkbenchBlock;
import com.redpxnda.handson.blockentity.TinkeringMenu;
import com.redpxnda.handson.blockentity.WorkbenchBlockEntity;
import com.redpxnda.handson.client.TinkeringScreen;
import com.redpxnda.nucleus.registration.ItemGroupCreator;
import com.redpxnda.nucleus.registration.RegistryId;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class HandsOnRegistries {
    @RegistryId("workbench")
    public static final WorkbenchBlock workbenchBlock = new WorkbenchBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.5F)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.IGNORE)
            .noOcclusion()
    );

    @RegistryId("backboard")
    public static final BackboardBlock backboardBlock = new BackboardBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.5F)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.IGNORE)
            .noOcclusion()
    );

    @RegistryId("bench_board")
    public static final QuadWorkbenchBlock quadBench = new QuadWorkbenchBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.5F)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.IGNORE)
            .noOcclusion()
    );

    @RegistryId("workbench")
    public static final BlockItem workbenchItem = new BlockItem(workbenchBlock, new Item.Properties());

    @RegistryId("backboard")
    public static final BlockItem backboardItem = new BlockItem(backboardBlock, new Item.Properties());

    @RegistryId("bench_board")
    public static final BlockItem quadBenchItem = new BlockItem(quadBench, new Item.Properties());

    @RegistryId("tm_handson_tab")
    public static final CreativeModeTab group = ItemGroupCreator.populate(
            CreativeTabRegistry.create(Component.translatable("itemGroup.tm_handson.tab"), workbenchItem::getDefaultInstance),
            workbenchItem, backboardItem, quadBenchItem
    );

    @RegistryId("workbench")
    public static final BlockEntityType<WorkbenchBlockEntity> workbenchBEType = BlockEntityType.Builder.of(WorkbenchBlockEntity::new, quadBench).build(null);

    @RegistryId("workbench-menu")
    public static final MenuType<TinkeringMenu> TINKERING_SCREEN_MENU_TYPE = MenuRegistry.ofExtended(TinkeringMenu::new);
}
