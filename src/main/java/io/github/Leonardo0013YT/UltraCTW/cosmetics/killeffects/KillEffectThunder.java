package io.github.Leonardo0013YT.UltraCTW.cosmetics.killeffects;

import io.github.Leonardo0013YT.UltraCTW.interfaces.KillEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KillEffectThunder implements KillEffect, Cloneable {

    @Override
    public void start(Player p, Player death, Location loc) {
        if (p == null || !p.isOnline()) {
            return;
        }
        if (death == null || !death.isOnline()) {
            return;
        }
        death.getLocation().getWorld().strikeLightningEffect(loc);
    }

    @Override
    public void stop() {
    }

    @Override
    public KillEffect clone() {
        return new KillEffectSquid();
    }
}