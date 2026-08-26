package stone.mae2.parts.p2p;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.helpers.patternprovider.PatternProviderTargetCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import stone.mae2.MAE2;
import stone.mae2.bootstrap.MAE2Config.TickRates.TickRate;
import stone.mae2.parts.p2p.PatternP2PTunnelLogic.Target;

import java.util.ArrayList;
import java.util.List;

public class PatternP2PPartLogic implements IGridTickable {

  private static final String NBT_SEND_LIST = "sendList";
  // a List, not a Set: two identical stacks are two separate debts
  private final List<GenericStack> sendList = new ArrayList<>();
  private final PatternP2PPartLogicHost part;

  public PatternP2PPartLogic(PatternP2PPartLogicHost part) {
    this.part = part;
    this.part.getMainNode().addService(IGridTickable.class, this);
  }

  @Override
  public TickingRequest getTickingRequest(IGridNode node) {
    TickRate rate = MAE2.CONFIG.parts().rates().PatternP2PTunnel();
    return new TickingRequest(rate.minRate(), rate.maxRate(),
      this.sendList.isEmpty(), true);
  }

  @Override
  public TickRateModulation tickingRequest(IGridNode node,
    int ticksSinceLastCall) {
    if (this.sendList.isEmpty())
      return TickRateModulation.SLEEP;
    if (!this.part.getMainNode().isActive())
      return TickRateModulation.IDLE;
    PatternProviderTargetCache cache = this.part.getCache();
    if (cache == null) {
      return TickRateModulation.IDLE;
    }
    PatternProviderTarget target = cache.find();
    if (target == null) {
      return TickRateModulation.IDLE;
    }

    boolean didSomething = false;

    for (var it = sendList.listIterator(); it.hasNext();) {
      var stack = it.next();
      var what = stack.what();
      long amount = stack.amount();

      var inserted = target.insert(what, amount, Actionable.MODULATE);
      if (inserted >= amount) {
        it.remove();
        didSomething = true;
      } else if (inserted > 0) {
        it.set(new GenericStack(what, amount - inserted));
        didSomething = true;
      }
    }

    if (sendList.isEmpty()) {
      return TickRateModulation.SLEEP;
    }

    if (didSomething)
      return TickRateModulation.FASTER;
    else
      return TickRateModulation.SLOWER;

  }

  public boolean isSendListEmpty() { return this.sendList.isEmpty(); }

  public void addToSendList(AEKey what, long l) {
    boolean wasEmpty = this.sendList.isEmpty();
    if (l > 0) {
      this.sendList.add(new GenericStack(what, l));
      if (wasEmpty) {
        this.part
          .getMainNode()
          .ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
      }
    }
  }

  public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
    for (var stack : this.sendList) {
      stack
        .what()
        .addDrops(stack.amount(), drops, this.part.getBlockEntity().getLevel(),
          this.part.getBlockEntity().getBlockPos());
    }
  }

  public void readFromNBT(CompoundTag data) {
    var sendListTag = data.getList(NBT_SEND_LIST, Tag.TAG_COMPOUND);
    for (int i = 0; i < sendListTag.size(); ++i) {
      var stack = GenericStack.readTag(sendListTag.getCompound(i));
      if (stack != null) {
        this.addToSendList(stack.what(), stack.amount());
      }
    }
  }

  public void writeToNBT(CompoundTag data) {
    ListTag sendListTag = new ListTag();
    for (var toSend : sendList) {
      sendListTag.add(GenericStack.writeTag(toSend));
    }
    data.put(NBT_SEND_LIST, sendListTag);
  }

  public static interface PatternP2PPartLogicHost extends Target {

    IManagedGridNode getMainNode();

    BlockEntity getBlockEntity();
  }

}
