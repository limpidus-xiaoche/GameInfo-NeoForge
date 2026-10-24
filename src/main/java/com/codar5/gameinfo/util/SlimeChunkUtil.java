package com.codar5.gameinfo.util;

import net.minecraft.util.RandomSource;

public class SlimeChunkUtil {
    public static boolean isSlimeChunk(long seed, int chunkX, int chunkZ) {
        RandomSource random = RandomSource.create(seed + chunkX * chunkX * 4987142 + chunkX * 5947611L + (long) chunkZ * chunkZ * 4392871L + chunkZ * 389711L ^ 0x3AD8025FL);
        return random.nextInt(10) == 0;
    }
}
