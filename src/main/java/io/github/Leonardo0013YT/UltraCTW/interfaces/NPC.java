package io.github.Leonardo0013YT.UltraCTW.interfaces;

import io.github.Leonardo0013YT.UltraCTW.cosmetics.shopkeepers.KeeperData;
import io.github.Leonardo0013YT.UltraCTW.enums.NPCType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface NPC {

    void spawnHologram();

    void spawn(Player p, Location loc, EntityType type, KeeperData kd);

    void respawn();

    Location getLoc();

    NPCType getNpcType();

    void destroy();

    org.bukkit.entity.Entity getBukkitEntity();

}