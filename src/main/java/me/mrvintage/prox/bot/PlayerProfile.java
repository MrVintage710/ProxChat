package me.mrvintage.prox.bot;

import java.util.UUID;

public class PlayerProfile {

    private UUID uuid;
    private String mincraftID;
    private String username;
    private long discordID;

    public boolean hasSetUUID() {
        if(uuid == null) return true;
        return false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerProfile setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getMincraftID() {
        return mincraftID;
    }

    public PlayerProfile setMincraftID(String mincraftID) {
        this.mincraftID = mincraftID;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PlayerProfile setUsername(String username) {
        this.username = username;
        return this;
    }

    public long getDiscordID() {
        return discordID;
    }

    public PlayerProfile setDiscordID(long discordID) {
        this.discordID = discordID;
        return this;
    }
}
