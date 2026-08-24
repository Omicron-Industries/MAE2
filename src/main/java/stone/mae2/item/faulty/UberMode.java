package stone.mae2.item.faulty;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.api.util.AEColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import stone.mae2.MAE2;
import stone.mae2.util.TransHelper;

public class UberMode extends FaultyCardMode {
  private static final String START_POS = "start";
  private BlockPos start;
	@Override
	public ResourceLocation getType() {
    return MAE2.toKey("uber_mode");
	}
    
  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    if (this.start == null) {
      this.start = context.getClickedPos();
      this.save(stack.getOrCreateTag());
      context.getPlayer().displayClientMessage(Component.translatable(TransHelper.GUI.toKey("faulty", "uber", "start")), true);
      return InteractionResult.CONSUME;
    } else {
      BlockPos end = context.getClickedPos();
      Level level = context.getLevel();
      BlockEntity be = level.getBlockEntity(context.getClickedPos());
      if (be instanceof IPartHost originalHost) {
        SelectedPart selectedPart = originalHost.selectPartWorld(context.getClickLocation());
        boolean failed = false;
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
          BlockEntity maybeCable = level.getBlockEntity(pos);
          if (maybeCable instanceof IPartHost aoePartHost) {
            IPart part = aoePartHost.getPart(selectedPart.side);
            if (part != null) {
              // no idea what the Vec pos argument does here, doesn't seem used in any implementation
              failed |= !part.onActivate(context.getPlayer(), context.getHand(), context.getClickLocation());
            }
          }
        }

        if (failed) {
          context.getPlayer().displayClientMessage(Component.translatable(TransHelper.GUI.toKey("faulty", "multi", "failed")), true);
        } else {
          context.getPlayer().displayClientMessage(Component.translatable(TransHelper.GUI.toKey("faulty", "multi", "succeeded")), true);
        }
      }
      this.start = null;
      this.save(stack.getOrCreateTag());
      return InteractionResult.CONSUME;
    }
  }

  @Override
  protected FaultyCardMode load(CompoundTag tag) {
    if (tag.contains(START_POS)) {
      this.start = BlockPos.of(tag.getLong(START_POS));
    }
    return this;
  }
    
  @Override
  public CompoundTag save(CompoundTag tag) {
    CompoundTag data = super.save(tag);
    if (this.start != null) {
      data.putLong(START_POS, this.start.asLong());
    }
    return data;
  }

	@Override
	protected Component getName() {
    return Component.translatable(TransHelper.GUI.toKey("faulty", "uber"));
	}

	@Override
	public int getTintColor() {
    return AEColor.LIGHT_BLUE.mediumVariant;
	}
}
