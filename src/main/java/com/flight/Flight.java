package com.flight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class Flight implements ModInitializer {
    public static final Identifier TOGGLE_PACKET = new Identifier("flight", "toggle");

    // Kecepatan terbang saat ON (Creative biasa = 0.05F). Ubah ke 0.1F untuk dua kali lebih cepat.
    private static final float FLY_SPEED = 0.05F;
    private static final float DEFAULT_FLY_SPEED = 0.05F;

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_PACKET,
            (server, player, handler, buf, responseSender) ->
                server.execute(() -> {
                    if (player.isCreative() || player.isSpectator()) {
                        return;
                    }

                    PlayerAbilities abilities = player.getAbilities();
                    if (!abilities.allowFlying) {
                        abilities.allowFlying = true;
                        abilities.flying = true;
                        abilities.setFlySpeed(FLY_SPEED);
                        player.sendAbilitiesUpdate();
                        player.sendMessage(new LiteralText("Terbang: ON"), true);
                    } else {
                        boolean inAir = !player.isOnGround();
                        abilities.allowFlying = false;
                        abilities.flying = false;
                        abilities.setFlySpeed(DEFAULT_FLY_SPEED);
                        player.fallDistance = 0.0F;
                        if (inAir) {
                            player.addStatusEffect(
                                new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0, false, false));
                        }
                        player.sendAbilitiesUpdate();
                        player.sendMessage(new LiteralText("Terbang: OFF"), true);
                    }
                }));
    }
}
