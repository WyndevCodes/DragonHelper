package me.wyndev.dragonhelper.client.data;

/**
 * Tracks data about the server.
 */
public class ServerDataTracker {

    private long lastSpawnedEndstoneProtector = 0;

    public long getLastSpawnedEndstoneProtectorTime() {
        return lastSpawnedEndstoneProtector;
    }

    public void setLastSpawnedEndstoneProtectorTime(long lastSpawnedEndstoneProtector) {
        this.lastSpawnedEndstoneProtector = lastSpawnedEndstoneProtector;
    }

}
