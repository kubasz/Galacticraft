package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSleepMP.class)
public class GuiSleepMPMxin {

    @Inject(method = "func_146418_g", at = @At("RETURN"), require = 1)
    private void onFunc_146418_g(CallbackInfo callbackInfo) {
            MinecraftForge.EVENT_BUS.post(new EventHandlerGC.SleepCancelledEvent());
    }
}
