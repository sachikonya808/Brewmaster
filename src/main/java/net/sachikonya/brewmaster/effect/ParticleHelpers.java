package net.sachikonya.brewmaster.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Objects;

public class ParticleHelpers {
    public static void spawnPoofExplosion(ServerLevel level, double posX, double posY, double posZ, float visualRadius) {
        RandomSource randomSource = level.getRandom();

        float density = 2f;
        int count = Mth.clamp(Math.round(visualRadius/2 * visualRadius/2 * visualRadius/2 * density), 10, 1500);

        for(int i = 0; i < count; i++) {
            double x, y, z;
            do {
                x = randomSource.nextDouble() * 2 - 1;
                y = randomSource.nextDouble() * 2 - 1;
                z = randomSource.nextDouble() * 2 - 1;
            } while(((x * x) + (y * y) + (z * z)) > 1);

            level.sendParticles(ParticleTypes.POOF,
                    posX + x * visualRadius,
                    posY + y * visualRadius,
                    posZ + z * visualRadius,
                    0,
                    x * 0.05, y * 0.05, z * 0.05, 1.0);
        }

        // Non-loop version in case the loop is too heavy
        //level.sendParticles(ParticleTypes.POOF, posX, posY, posZ, count, visualRadius/2, visualRadius / 2, visualRadius / 2, 0.02);
    }

    public static void spawnLightningStrikeParticles(ServerLevel level, double posX, double posY, double posZ) {
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                posX, posY, posZ,
                40, 0.5, 0.6, 0.5, 0.15);

        level.sendParticles(ParticleTypes.CRIT,
                posX, posY, posZ,
                15, 0.4, 0.5, 0.4, 0.2);
    }

    public static void spawnBigSmallParticles(ServerLevel level, double posX, double posY, double posZ, float visualRadius, String name) {
        float density = 2f;
        int count = Mth.clamp(Math.round(visualRadius /2 * visualRadius/2 * visualRadius/2 * density), 10, 1500);

        if(Objects.equals(name, "big")) {
            level.sendParticles(ParticleTypes.POOF, posX, posY+visualRadius, posZ, count,
                    visualRadius/2, visualRadius/2, visualRadius/2, 0.2);
        } else {
            level.sendParticles(ParticleTypes.POOF, posX, posY, posZ, 8 * (int)visualRadius,
                    0.4, 0.5, 0.4, 0.2);
        }
    }
}
