package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsTooltipMixin implements GuiGraphicsTooltipAccess {

    @Unique
    private ItemStack echoTooltip$pendingTooltipItem = ItemStack.EMPTY;

    @Override
    @Unique
    public ItemStack echoTooltip$getPendingTooltipItem() {
        return this.echoTooltip$pendingTooltipItem;
    }

    @Override
    @Unique
    public void echoTooltip$setPendingTooltipItem(ItemStack stack) {
        this.echoTooltip$pendingTooltipItem = stack != null ? stack : ItemStack.EMPTY;
    }

    @Inject(
            method = "renderDeferredElements",
            at = @At("RETURN")
    )
    private void echoTooltip$clearItem(CallbackInfo ci) {
        this.echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
    }
}