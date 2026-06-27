package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.ClientTextTooltipAccessor;
import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsTooltipMixin implements GuiGraphicsTooltipAccess {

    @Unique
    private ItemStack echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
    @Unique
    private boolean echoTooltip$itemCaptured = false;
    @Unique
    private int echoTooltip$pendingColor = 0xFFFFFF;

    @Override
    @Unique
    public ItemStack echoTooltip$getPendingTooltipItem() {
        return this.echoTooltip$pendingTooltipItem;
    }

    @Override
    @Unique
    public void echoTooltip$setPendingTooltipItem(ItemStack stack) {
        this.echoTooltip$pendingTooltipItem = stack != null ? stack : ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = true;
    }

    @Override
    @Unique
    public int echoTooltip$getPendingColor() {
        return this.echoTooltip$pendingColor;
    }

    @Override
    @Unique
    public void echoTooltip$setPendingColor(int color) {
        this.echoTooltip$pendingColor = color;
    }

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    private void echoTooltip$captureFromItemStack(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        this.echoTooltip$pendingTooltipItem = stack != null ? stack : ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = true;
    }

    @Inject(
            method = "setTooltipForNextFrameInternal",
            at = @At("HEAD")
    )
    private void echoTooltip$handleInternal(Font font, List<ClientTooltipComponent> list, int i, int j,
                                            ClientTooltipPositioner positioner, Identifier identifier, boolean bl, CallbackInfo ci) {
        if (!this.echoTooltip$itemCaptured) {
            this.echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
            this.echoTooltip$pendingColor = echoTooltip$extractColorFromComponents(list);
        }
        this.echoTooltip$itemCaptured = false;
    }

    @Inject(
            method = "renderDeferredElements",
            at = @At("RETURN")
    )
    private void echoTooltip$clearAfterRender(CallbackInfo ci) {
        this.echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = false;
        this.echoTooltip$pendingColor = 0xFFFFFF;
    }

    @Unique
    private static int echoTooltip$extractColorFromComponents(List<ClientTooltipComponent> components) {
        if (components.isEmpty()) return 0xFFFFFF;
        ClientTooltipComponent first = components.get(0);
        if (!(first instanceof ClientTextTooltipAccessor accessor)) return 0xFFFFFF;
        int[] found = {-1};
        accessor.echoTooltip$getText().accept((index, style, codePoint) -> {
            if (style.getColor() != null) {
                found[0] = style.getColor().getValue();
                return false;
            }
            return true;
        });
        return found[0] != -1 ? found[0] : 0xFFFFFF;
    }
}