package mathax.legacy.client.mixin;

import mathax.legacy.client.mixininterface.IEntityRenderer;
import mathax.legacy.client.systems.modules.render.Nametags;
import mathax.legacy.client.systems.modules.render.NoRender;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> implements IEntityRenderer {
    @Shadow public abstract Identifier getTexture(Entity entity);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!(entity instanceof PlayerEntity)) return;
        if (Modules.get().isActive(Nametags.class)) info.cancel();
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().get(NoRender.class).noEntity(entity)) info.cancel();
        if (Modules.get().get(NoRender.class).noFallingBlocks() && entity instanceof FallingBlockEntity) info.cancel();
    }

    @Inject(method = "getSkyLight", at = @At("RETURN"), cancellable = true)
    private void onGetSkyLight(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(Math.max(Utils.minimumLightLevel, info.getReturnValueI()));
    }

    @Override
    public Identifier getTextureInterface(Entity entity) {
        return getTexture(entity);
    }
}
