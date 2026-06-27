package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.ClientTextTooltipAccessor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientTextTooltip.class)
public abstract class ClientTextTooltipMixin implements ClientTextTooltipAccessor {

    @Shadow
    private FormattedCharSequence text;

    @Override
    @Unique
    public FormattedCharSequence echoTooltip$getText() {
        return this.text;
    }
}