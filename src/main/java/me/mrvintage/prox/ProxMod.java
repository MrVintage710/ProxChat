package me.mrvintage.prox;

import me.mrvintage.prox.bot.BotCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProxMod implements DedicatedServerModInitializer {

    public static JDA jda;

    @Override
    public void onInitializeServer() {

        ServerTickCallback.EVENT.register((server) -> {
            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                List<LivingEntity> ent = player.getEntityWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(1), livingEntity -> !livingEntity.getUuid().equals(player.getUuid()));
                for(LivingEntity e : ent) {
                    System.out.println(e.getEntityName());
                }
            }
        });

        ServerStopCallback.EVENT.register((server) -> {
            jda.shutdown();
        });

        System.out.println("Server!");

        JDABuilder builder = JDABuilder.createDefault("NzkxOTMzNTQ5NjE5MDUyNTU0.X-WXlw.tAylyfVTbc2VV09nOst7i3txdfc");
        builder.addEventListeners(new BotCommands());

        try {
            jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
