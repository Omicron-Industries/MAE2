package stone.mae2.bootstrap;

import appeng.api.integrations.igtooltip.PartTooltips;
import appeng.api.networking.GridServices;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import stone.mae2.api.features.MultiP2PTunnelAttunement;
import stone.mae2.core.datagen.MAE2BlockTagsProvider;
import stone.mae2.core.datagen.MAE2RecipeProvider;
import stone.mae2.integration.GregTechIntegration;
import stone.mae2.integration.MultiP2PStateDataProvider;
import stone.mae2.me.service.MultiP2PService;
import stone.mae2.parts.p2p.multi.MultiP2PTunnel;

public class ServerProxy implements Proxy {
  public void init(IEventBus bus) {
    MAE2Blocks.init(bus);
    MAE2Items.init(bus);
    MAE2Tags.init(bus);

    if (ModList.get().isLoaded("gtceu")) {
      GregTechIntegration.init(bus);
    }

    bus.addListener((FMLCommonSetupEvent event) -> {
        GridServices.register(MultiP2PService.class, MultiP2PService.class);
        MultiP2PTunnelAttunement.registerStockAttunements();
      });

    bus.addListener((GatherDataEvent event) -> {
        DataGenerator gen = event.getGenerator();
        DataGenerator.PackGenerator pack = gen.getVanillaPack(true);

        pack.addProvider(MAE2RecipeProvider::new);

        gen.addProvider(event.includeServer(), (Factory<TagsProvider>) ((output) -> new MAE2BlockTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper())));
      });

    PartTooltips
      .addServerData(MultiP2PTunnel.Part.class,
                     new MultiP2PStateDataProvider());
  }
}
