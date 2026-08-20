package stone.mae2.core.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import appeng.core.AppEng;
import net.minecraft.commands.arguments.blocks.BlockStateParser.TagResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import stone.mae2.MAE2;
import stone.mae2.bootstrap.MAE2Blocks;
import stone.mae2.bootstrap.MAE2Tags;

// Why isn't this extending BlockTagsProvider? idk, doesn't work
public class MAE2BlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
  public MAE2BlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries,
                           ExistingFileHelper existingFileHelper) {
    super(packOutput, Registries.BLOCK, registries, block -> block.builtInRegistryHolder().key(), MAE2.MODID,
          existingFileHelper);
  }

	@Override
	protected void addTags(Provider provider) {
    System.out.println("Adding tags");
    this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MAE2Blocks.CLOUD_CHAMBER.get());
    this.tag(MAE2Tags.CLOUD_CHAMBERS).add(MAE2Blocks.CLOUD_CHAMBER.get());

    this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                                                  MAE2Blocks.ACCELERATOR_4x.get(),
                                                  MAE2Blocks.ACCELERATOR_16x.get(),
                                                  MAE2Blocks.ACCELERATOR_64x.get(),
                                                  MAE2Blocks.ACCELERATOR_256x.get());
                                                  
	}

}
