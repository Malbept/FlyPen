package me.vadim.penis;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PenisShootListener implements Listener {
    @EventHandler
    public void onShoot(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.isInsideVehicle()) return;

        // Стреляем белым блоком
        Snowball s = p.launchProjectile(Snowball.class);
        s.setVelocity(p.getLocation().getDirection().multiply(2));
        s.setCustomName("penis_shoot");
        s.setCustomNameVisible(false);

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }
}
