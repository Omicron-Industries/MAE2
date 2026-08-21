package stone.mae2.item.faulty;

import appeng.api.util.AEColor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import stone.mae2.MAE2;
import stone.mae2.util.TransHelper;

public class UberMode extends FaultyCardMode {
  private Mode mode;
  
	@Override
	public ResourceLocation getType() {
    return MAE2.toKey("uber_mode");
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'onItemUseFirst'");
	}

  @Override
  public InteractionResultHolder<ItemStack> onItemUse(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    mode = mode == Mode.COPY ? Mode.PASTE : Mode.COPY;
    this.save(stack.getOrCreateTag());
    player.displayClientMessage(Component.translatable(TransHelper.GUI.toKey("faulty", "uber")), true);
    return InteractionResultHolder.consume(stack);
  }

	@Override
	protected Component getName() {
    return Component.translatable(TransHelper.GUI.toKey("faulty", "uber"));
	}

	@Override
	public int getTintColor() {
    return AEColor.LIGHT_BLUE.mediumVariant;
	}

  private enum Mode {
    COPY(AEColor.MAGENTA.mediumVariant), PASTE(AEColor.LIME.mediumVariant);
    private int color;

    Mode(int color) { this.color = color; }
  }
}
