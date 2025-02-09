package com.example.mod.utils.render.skija;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.example.mod.client.GameAccessor.mc;

public class SkijaExample {

    private static float currentHealth = 100.0f;
    private static float targetHealth = 100.0f;
    private static float animationSpeed = 1.0f;
    private static float currentHealthBarWidth = 0.0f;

    private static Font font = new Font(); // 缓存字体对象
    private static Font smallFont = new Font(); // 缓存小字体对象

    public static void renderSimpleGUI() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        float rectWidth = screenWidth * 0.3f;
        float rectHeight = screenHeight * 0.2f;
        float rectX = (screenWidth - rectWidth) / 2;
        float rectY = screenHeight * 0.1f;

        List<Entity> entities = new ArrayList<>();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != player && entity.isAlive()) {
                entities.add(entity);
            }
        }

        Optional<Entity> closestTarget = entities.stream()
                .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(player)));

        if (closestTarget.isPresent()) {
            LivingEntity targetEntity = (LivingEntity) closestTarget.get();
            String name = targetEntity.getName().getString();
            String uuid = targetEntity.getUuid().toString();
            float health = targetEntity.getHealth();
            float maxHealth = targetEntity.getMaxHealth();

            if (Math.abs(targetHealth - health) > animationSpeed) {
                targetHealth += Math.signum(health - targetHealth) * animationSpeed;
            } else {
                targetHealth = health;
            }

            float targetHealthBarWidth = rectWidth * 0.85f * (targetHealth / maxHealth);
            currentHealthBarWidth = smoothTransition(currentHealthBarWidth, targetHealthBarWidth, animationSpeed);

            try (Paint textPaint = new Paint()) {
                textPaint.setARGB(255, 255, 255, 255);

                Skija.getInstance().draw(canvas -> {
                    float cornerRadius = 20;
                    RRect rRect = RRect.makeLTRB(rectX, rectY, rectX + rectWidth, rectY + rectHeight, cornerRadius, cornerRadius);
                    Paint backgroundPaint = new Paint();
                    backgroundPaint.setARGB(150, 0, 0, 0);
                    canvas.drawRRect(rRect, backgroundPaint);

                    float barHeight = rectHeight * 0.1f;
                    float barWidth = rectWidth * 0.95f;
                    float barXStart = rectX + (rectWidth - barWidth) / 2;
                    float barY = rectY + rectHeight * 0.05f;

                    Paint barPaint = new Paint();
                    barPaint.setARGB(255, 153, 0, 0);
                    canvas.drawRRect(RRect.makeLTRB(barXStart, barY, barXStart + barWidth, barY + barHeight, cornerRadius, cornerRadius), barPaint);

                    float textX = rectX + rectWidth * 0.05f;
                    float textY = rectY + rectHeight * 0.3f;

                    String displayName = name.length() > 15 ? name.substring(0, 15) + "..." : name;
                    drawText(canvas, "Target: " + displayName, textX, textY, font, textPaint);

                    drawText(canvas, "UUID: " + uuid, textX, textY + 25, smallFont, textPaint);

                    String healthText = String.format("Health: %.1f / %.1f", targetHealth, maxHealth);
                    drawText(canvas, healthText, textX, textY + 50, font, textPaint);

                    float healthBarHeight = rectHeight * 0.05f;
                    float healthBarX = rectX + rectWidth * 0.05f;
                    float healthBarY = rectY + rectHeight * 0.75f;

                    int healthColor = getHealthBarColor(targetHealth / maxHealth);
                    textPaint.setARGB(255, (healthColor >> 16) & 0xFF, (healthColor >> 8) & 0xFF, healthColor & 0xFF);

                    Paint healthBarBackground = new Paint();
                    healthBarBackground.setARGB(100, 255, 255, 255);
                    canvas.drawRRect(RRect.makeXYWH(healthBarX, healthBarY, rectWidth * 0.85f, healthBarHeight, 10, 10), healthBarBackground);

                    Paint healthBarPaint = new Paint();
                    healthBarPaint.setARGB(255, (healthColor >> 16) & 0xFF, (healthColor >> 8) & 0xFF, healthColor & 0xFF);
                    canvas.drawRRect(RRect.makeXYWH(healthBarX, healthBarY, currentHealthBarWidth, healthBarHeight, 10, 10), healthBarPaint);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static float smoothTransition(float currentWidth, float targetWidth, float speed) {
        if (Math.abs(currentWidth - targetWidth) < speed) {
            return targetWidth;
        }
        return currentWidth + Math.signum(targetWidth - currentWidth) * speed;
    }

    private static int getHealthBarColor(float healthPercentage) {
        if (healthPercentage >= 0.75f) {
            return 0x00FF00; // Green
        } else if (healthPercentage >= 0.5f) {
            return 0xFFFF00; // Yellow
        } else {
            return 0xFF0000; // Red
        }
    }

    private static void drawText(Canvas canvas, String text, float x, float y, Font font, Paint paint) {
        canvas.drawString(text, x, y, font, paint);
    }
}
