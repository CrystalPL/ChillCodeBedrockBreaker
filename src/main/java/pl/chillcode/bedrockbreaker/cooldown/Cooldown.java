package pl.chillcode.bedrockbreaker.cooldown;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Cooldown {
    Cache<UUID, Instant> cooldownMap;

    public Cooldown(final Duration cooldownTime) {
        this.cooldownMap = CacheBuilder
                .newBuilder()
                .expireAfterWrite(cooldownTime.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public Instant getCooldown(final UUID uuid) {
        return cooldownMap.getIfPresent(uuid);
    }

    public void addCooldown(final UUID uuid) {
        cooldownMap.put(uuid, Instant.now());
    }
}
