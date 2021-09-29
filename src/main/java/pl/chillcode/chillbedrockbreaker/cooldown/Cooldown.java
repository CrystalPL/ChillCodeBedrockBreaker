package pl.chillcode.chillbedrockbreaker.cooldown;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Cooldown {
    Cache<UUID, Long> cooldownMap = CacheBuilder
            .newBuilder()
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            .build();

    public Long getCooldown(final UUID uuid) {
        return cooldownMap.getIfPresent(uuid);
    }

    public void addColdown(final UUID uuid) {
        cooldownMap.put(uuid, System.currentTimeMillis());
    }
}
