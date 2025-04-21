package com.custompanel.master;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 *
 *@author Sma1lo
 */

@Mod(CustomPanel.MODID)
public class CustomPanel {
    public static final String MODID = "custom_panel";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static long startTime = System.currentTimeMillis();

    private static int panelX = 10;
    private static int panelY = 10;

    public CustomPanel() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class RenderOverlay {

        @SubscribeEvent
        public static void onRenderGameOverlay(RenderGuiEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.screen != null) return;

            GuiGraphics guiGraphics = event.getGuiGraphics();
            Font fontRenderer = mc.font;

            String playerName = mc.player != null ? mc.player.getName().getString() : "N/A";
            int fps = mc.getFps();
            int ping = mc.getConnection() != null ? mc.getConnection().getPlayerInfo(mc.player.getUUID()).getLatency() : 0;
            int x = (int) mc.player.getX();
            int y = (int) mc.player.getY();
            int z = (int) mc.player.getZ();

            long time = System.currentTimeMillis();
            boolean blink = (time / 500) % 2 == 0;
            ChatFormatting fpsColor = fps >= 60 ? ChatFormatting.GREEN : (fps >= 30 ? ChatFormatting.YELLOW : (blink ? ChatFormatting.RED : ChatFormatting.WHITE));

            MutableComponent textComponent = Component.literal("User: ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(playerName).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(" | FPS: ").withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(String.valueOf(fps)).withStyle(fpsColor));

            MutableComponent coords = Component.literal("XYZ: ")
                    .append(Component.literal(x + " " + y + " " + z).withStyle(ChatFormatting.AQUA));

            MutableComponent pingText = Component.literal("Ping: ")
                    .append(Component.literal(ping + " ms").withStyle(ChatFormatting.LIGHT_PURPLE));

            int alpha = Math.min(255, (int) ((System.currentTimeMillis() - startTime) / 10));
            int color = (alpha << 24) | 0xFFFFFF;

            int panelHeight = 50;

            guiGraphics.fill(panelX, panelY, panelX + 200, panelY + panelHeight, 0x90000000);

            guiGraphics.drawString(fontRenderer, textComponent, panelX + 5, panelY + 5, color);
            guiGraphics.drawString(fontRenderer, coords, panelX + 5, panelY + 20, color);
            guiGraphics.drawString(fontRenderer, pingText, panelX + 5, panelY + 35, color);
        }
    }
}