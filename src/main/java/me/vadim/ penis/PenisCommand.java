package me.vadim.penis;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PenisCommand implements CommandExecutor, Listener {

    private final Plugin plugin;
    private final Map<UUID, ArmorStand> riders = new HashMap<>();

    public PenisCommand(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return false;

        Location loc = p.getLocation();
        World world = p.getWorld();

        // создаём писюн: используем ArmorStand + блоки
        ArmorStand stand = world.spawn(loc, ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setCustomName("§dПисюн");
        stand.setCustomNameVisible(true);
        stand.addPassenger(p);

        // ставим "голову"
        stand.setHelmet(new ItemStack(Material.PINK_WOOL));

        // отмечаем кто на нём
        riders.put(p.getUniqueId(), stand);

        // Постоянно обновлять позицию писюна и стрелять
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!stand.isValid() || !p.isOnline() || !p.getPassengers().contains(stand)) {
                    stand.remove();
                    riders.remove(p.getUniqueId());
                    cancel();
                    return;
                }

                // Летаем по направлению взгляда
                Vector dir = p.getLocation().getDirection().multiply(0.8);
                stand.setVelocity(dir);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        return true;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!riders.containsKey(p.getUniqueId())) return;
        if (e.getAction().toString().contains("RIGHT_CLICK")) {
            Location loc = p.getEyeLocation().add(p.getLocation().getDirection().multiply(2));
            Snowball ball = p.getWorld().spawn(loc, Snowball.class);
            ball.setVelocity(p.getLocation().getDirection().multiply(2));
            ball.setShooter(p);
            ball.setCustomName("cum");
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball ball && "cum".equals(ball.getCustomName())) {
            Location hit = e.getHitBlock() != null ? e.getHitBlock().getLocation() : e.getEntity().getLocation();
            World w = hit.getWorld();

            // Уничтожить блок
            if (e.getHitBlock() != null && !e.getHitBlock().getType().equals(Material.BEDROCK)) {
                e.getHitBlock().setType(Material.AIR);
            }

            // Убить игрока рядом
            for (Entity ent : w.getNearbyEntities(hit, 2, 2, 2)) {
                if (ent instanceof Player target && !target.equals(ball.getShooter())) {
                    target.setHealth(0);
                }
            }

            // Эффект
            w.spawnParticle(Particle.EXPLOSION_LARGE, hit, 5);
            w.playSound(hit, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        }
    }

    @EventHandler
    public void onShiftExit(VehicleExitEvent e) {
        if (e.getExited() instanceof Player p && riders.containsKey(p.getUniqueId())) {
            ArmorStand stand = riders.remove(p.getUniqueId());
            if (stand != null && stand.isValid()) {
                stand.remove();
                p.sendMessage("§cТы слез с писюна");
            }
        }
    }
}
