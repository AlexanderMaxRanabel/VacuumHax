package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.world.PlaySoundEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance soundInstance, CallbackInfo info) {
        PlaySoundEvent event = MatHaxLegacy.EVENT_BUS.post(PlaySoundEvent.get(soundInstance));
        if (event.isCancelled()) info.cancel();
    }
}
