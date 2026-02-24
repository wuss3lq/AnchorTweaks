package net.chaosvanilla.anchortweaks.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "tick", at = @At("TAIL"))
    private void anchortweaks$removeAnchorUseDelay(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player == null || client.world == null) {
            return;
        }

        if (itemUseCooldown <= 0) {
            return;
        }

        ItemStack mainHand = client.player.getMainHandStack();
        ItemStack offHand = client.player.getOffHandStack();
        if (mainHand.isOf(Items.RESPAWN_ANCHOR) || mainHand.isOf(Items.GLOWSTONE)
                || offHand.isOf(Items.RESPAWN_ANCHOR) || offHand.isOf(Items.GLOWSTONE)) {
            itemUseCooldown = 0;
            return;
        }

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
            if (client.world.getBlockState(hit.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR)) {
                itemUseCooldown = 0;
            }
        }
    }
}
