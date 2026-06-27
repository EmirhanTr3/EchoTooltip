package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    @Redirect(
            method = "extractTooltipBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    private static void echoTooltip$tintFrame(
            GuiGraphicsExtractor graphics,
            RenderPipeline pipeline,
            Identifier sprite,
            int x, int y, int width, int height
    ) {
        int color = echoTooltip$resolveColor(graphics);
        graphics.blitSprite(pipeline, sprite, x, y, width, height, 0xFF000000 | color);
    }

    @Unique
    private static int echoTooltip$resolveColor(GuiGraphicsExtractor graphics) {
        GuiGraphicsTooltipAccess access = (GuiGraphicsTooltipAccess) graphics;
        ItemStack stack = access.echoTooltip$getPendingTooltipItem();
        if (stack != null && !stack.isEmpty()) {
            return echoTooltip$getItemColor(stack);
        }
        return access.echoTooltip$getPendingColor();
    }

    @Unique
    private static int echoTooltip$getItemColor(ItemStack stack) {
        // 1. First color in custom name
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            int[] found = {-1};
            customName.visit((style, text) -> {
                if (found[0] == -1 && !text.isEmpty() && style.getColor() != null) {
                    found[0] = style.getColor().getValue();
                }
                return found[0] == -1 ? Optional.empty() : Optional.of(Unit.INSTANCE);
            }, Style.EMPTY);
            if (found[0] != -1) return found[0];
        }
        // 2. Rarity color
        ChatFormatting formatting = stack.getRarity().color();
        Integer colorValue = formatting.getColor();
        if (colorValue != null) return colorValue;
        // 3. White fallback
        return 0xFFFFFF;
    }
}