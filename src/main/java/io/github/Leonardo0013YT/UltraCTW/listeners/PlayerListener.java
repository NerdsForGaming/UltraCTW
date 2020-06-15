package io.github.Leonardo0013YT.UltraCTW.listeners;

import com.nametagedit.plugin.NametagEdit;
import io.github.Leonardo0013YT.UltraCTW.Main;
import io.github.Leonardo0013YT.UltraCTW.api.events.PlayerLoadEvent;
import io.github.Leonardo0013YT.UltraCTW.cosmetics.trails.Trail;
import io.github.Leonardo0013YT.UltraCTW.enums.NPCType;
import io.github.Leonardo0013YT.UltraCTW.enums.State;
import io.github.Leonardo0013YT.UltraCTW.interfaces.CTWPlayer;
import io.github.Leonardo0013YT.UltraCTW.interfaces.Game;
import io.github.Leonardo0013YT.UltraCTW.interfaces.NPC;
import io.github.Leonardo0013YT.UltraCTW.objects.Squared;
import io.github.Leonardo0013YT.UltraCTW.team.Team;
import io.github.Leonardo0013YT.UltraCTW.utils.NBTEditor;
import io.github.Leonardo0013YT.UltraCTW.utils.Tagged;
import io.github.Leonardo0013YT.UltraCTW.utils.Utils;
import io.github.Leonardo0013YT.UltraCTW.xseries.XMaterial;
import io.github.Leonardo0013YT.UltraCTW.xseries.XSound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    private Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().loadPlayer(p);
        Bukkit.getOnlinePlayers().stream()
                .filter(pl -> check(p, pl))
                .forEach(pl -> pl.hidePlayer(p));
        Bukkit.getOnlinePlayers().stream()
                .filter(pl -> check(p, pl))
                .forEach(p::hidePlayer);
        givePlayerItems(p);
    }

    @EventHandler
    public void onLoad(PlayerLoadEvent e) {
        Player p = e.getPlayer();
        plugin.getLvl().checkUpgrade(p);
        Utils.updateSB(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().savePlayer(p.getUniqueId(), false);
        plugin.getGm().removePlayerGame(p, true);
        NametagEdit.getApi().clearNametag(p);
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        plugin.getDb().savePlayer(p.getUniqueId(), false);
        plugin.getGm().removePlayerGame(p, true);
        NametagEdit.getApi().clearNametag(p);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Entity entity = e.getRightClicked();
        NPC npc = plugin.getNpc().getNPC(p, entity.getUniqueId());
        if (npc == null) {
            return;
        }
        e.setCancelled(true);
        if (npc.getNpcType().equals(NPCType.KITS)) {
            plugin.getUim().getPages().put(p, 1);
            plugin.getUim().createKitSelectorMenu(p);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Game g = plugin.getGm().getGameByPlayer(p);
        if (plugin.getCm().getMainLobby() != null) {
            World w = plugin.getCm().getMainLobby().getWorld();
            if (w == null) return;
            if (p.getWorld().getName().equals(w.getName())) {
                e.getRecipients().clear();
                e.getRecipients().addAll(w.getPlayers());
                String msg = formatMainLobby(p, e.getMessage());
                msg = msg.replaceAll("%", "%%");
                e.setFormat(msg);
            }
        }
        if (g == null) return;
        e.getRecipients().clear();
        String msg;
        if (g.getInLobby().contains(p)) {
            msg = formatLobby(p, e.getMessage());
            e.getRecipients().addAll(g.getInLobby());
        } else {
            Team t = g.getTeamPlayer(p);
            if (e.getMessage().startsWith("!")) {
                msg = formatGame(p, t, e.getMessage());
                e.getRecipients().addAll(g.getCached());
            } else {
                msg = formatTeam(p, t, e.getMessage());
                e.getRecipients().addAll(t.getMembers());
            }
        }
        msg = msg.replaceAll("%", "%%");
        e.setFormat(msg);
    }

    private String formatMainLobby(Player p, String msg) {
        return plugin.getLang().get(p, "chat.mainLobby").replaceAll("<player>", p.getName()).replaceAll("<msg>", msg);
    }

    private String formatLobby(Player p, String msg) {
        return plugin.getLang().get(p, "chat.lobby").replaceAll("<player>", p.getName()).replaceAll("<msg>", msg);
    }

    private String formatTeam(Player p, Team team, String msg) {
        return plugin.getLang().get(p, "chat.team").replaceAll("<team>", team.getName()).replaceAll("<player>", p.getName()).replaceAll("<msg>", msg);
    }

    private String formatGame(Player p, Team team, String msg) {
        return plugin.getLang().get(p, "chat.global").replaceAll("<team>", team.getName()).replaceAll("<player>", p.getName()).replaceAll("<msg>", msg.replaceFirst("!", ""));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Team team = g.getTeamPlayer(p);
        if (team == null) {
            e.setCancelled(true);
            return;
        }
        Squared s1 = g.getPlayerSquared(e.getBlock().getLocation());
        Squared s2 = team.getPlayerSquared(e.getBlock().getLocation());
        Block b = e.getBlock();
        Location l = b.getLocation();
        if (g.getWools().containsKey(l)) {
            e.setCancelled(true);
            b.setType(Material.AIR);
            ItemStack i = g.getWools().get(l);
            l.getWorld().dropItemNaturally(l, i);
            g.getWools().remove(l);
            return;
        }
        if (s1 != null) {
            e.setCancelled(s1.isNoBreak());
            p.sendMessage(plugin.getLang().get("messages.noBreak"));
            return;
        }
        if (s2 != null) {
            e.setCancelled(s2.isNoBreak());
            p.sendMessage(plugin.getLang().get("messages.noBreak"));
            return;
        }
        CTWPlayer ctw = plugin.getDb().getCTWPlayer(p);
        ctw.setBroken(ctw.getBroken() + 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Team team = g.getTeamPlayer(p);
        if (team == null) {
            e.setCancelled(true);
            return;
        }
        Location l = e.getBlockPlaced().getLocation();
        ItemStack item = p.getItemInHand();
        if (team.getWools().containsKey(l)) {
            if (item == null || item.getType().equals(Material.AIR)) return;
            String co = NBTEditor.getString(item, "TEAM", "WOOL", "CAPTURE");
            if (co == null) {
                e.setCancelled(true);
                return;
            }
            ChatColor c = ChatColor.valueOf(co);
            ChatColor to = team.getWools().get(l);
            if (!to.equals(c)) {
                e.setCancelled(true);
                p.sendMessage(plugin.getLang().get("messages.incorrectPlace").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")));
                return;
            }
            CTWPlayer ctw = plugin.getDb().getCTWPlayer(p);
            ctw.addWoolCaptured();
            p.sendMessage(plugin.getLang().get("messages.placeTeam").replaceAll("<place>", c + plugin.getLang().get("scoreboards.wools.captured")));
            team.getCaptured().add(c);
            team.sendTitle(plugin.getLang().get("titles.captured.title").replaceAll("<color>", c + ""), plugin.getLang().get("titles.captured.subtitle").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")), 0, 30, 10);
            g.getTeams().values().stream().filter(t -> t.getId() != team.getId()).forEach(t -> t.sendTitle(plugin.getLang().get("titles.otherCaptured.title").replaceAll("<name>", team.getName()).replaceAll("<color>", team.getColor() + ""), plugin.getLang().get("titles.otherCaptured.subtitle").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")), 0, 30, 10));
            team.playSound(XSound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            if (team.checkWools()) {
                g.win(team);
            }
            return;
        } else {
            if (item != null) {
                String co = NBTEditor.getString(item, "TEAM", "WOOL", "CAPTURE");
                if (co != null) {
                    ItemStack i = item.clone();
                    i.setAmount(1);
                    g.getWools().put(e.getBlockPlaced().getLocation(), i);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        if (!p.getInventory().containsAtLeast(item, 1)) {
                            ChatColor c = Utils.getColorByXMaterial(XMaterial.matchXMaterial(i));
                            if (team.getInProgress().containsKey(c)) {
                                team.getInProgress().get(c).remove(p.getUniqueId());
                            }
                        }
                    }, 1L);
                }
            }
        }
        Squared s1 = g.getPlayerSquared(l);
        Squared s2 = team.getPlayerSquared(l);
        if (s1 != null) {
            e.setCancelled(s1.isNoBreak());
            p.sendMessage(plugin.getLang().get("messages.noPlace"));
            return;
        }
        if (s2 != null) {
            e.setCancelled(s2.isNoBreak());
            p.sendMessage(plugin.getLang().get("messages.noPlace"));
            return;
        }
        CTWPlayer ctw = plugin.getDb().getCTWPlayer(p);
        ctw.setPlaced(ctw.getPlaced() + 1);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Team team = g.getTeamPlayer(p);
        if (team == null || g.isState(State.WAITING) || g.isState(State.STARTING)) {
            if (g.getLobbyProtection() != null) {
                Squared s = g.getLobbyProtection();
                if (!s.isInCuboid(p)) {
                    p.teleport(g.getLobby());
                }
            }
            return;
        }
        Squared s2 = team.getPlayerSquared(e.getTo());
        if (s2 != null) {
            e.setCancelled(s2.isNoEntry());
            p.teleport(e.getFrom());
            p.setVelocity(p.getVelocity().multiply(-1));
            p.sendMessage(plugin.getLang().get("messages.noEntry"));
            return;
        }
        Location to = e.getTo();
        Location from = e.getFrom();
        if (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()){
            CTWPlayer ctw = plugin.getDb().getCTWPlayer(p);
            ctw.setWalked(ctw.getWalked() + 1);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Game g = plugin.getGm().getGameByPlayer(p);
            if (g == null) return;
            e.setCancelled(true);
            e.setFoodLevel(40);
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e) {
        Item i = e.getEntity();
        if (i.hasMetadata("DROPPED")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItemDrop().getItemStack();
        if (item.equals(plugin.getIm().getPoints()) || item.equals(plugin.getIm().getLobby()) || item.equals(plugin.getIm().getTeams()) || item.equals(plugin.getIm().getLeave()) || item.equals(plugin.getIm().getSetup())) {
            e.setCancelled(true);
            return;
        }
        String co = NBTEditor.getString(item, "TEAM", "WOOL", "CAPTURE");
        if (co == null) return;
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Team team = g.getTeamPlayer(p);
        if (team == null) return;
        e.getItemDrop().setMetadata("DROPPED", new FixedMetadataValue(Main.get(), co));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!p.getInventory().containsAtLeast(item, 1)) {
                ChatColor c = Utils.getColorByXMaterial(XMaterial.matchXMaterial(item));
                if (team.getInProgress().containsKey(c)) {
                    team.getInProgress().get(c).remove(p.getUniqueId());
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        Item i = e.getItem();
        ChatColor c = null;
        if (i.hasMetadata("DROPPED")) {
            c = ChatColor.valueOf(i.getMetadata("DROPPED").get(0).asString());
        } else if (NBTEditor.contains(i.getItemStack(), "TEAM", "WOOL", "CAPTURE")) {
            c = ChatColor.valueOf(NBTEditor.getString(i.getItemStack(), "TEAM", "WOOL", "CAPTURE"));
        }
        if (c == null) return;
        Game g = plugin.getGm().getGameByPlayer(p);
        if (g == null) return;
        Team team = g.getTeamPlayer(p);
        if (team == null) return;
        if (!team.getColors().contains(c)){
            e.setCancelled(true);
            e.getItem().setItemStack(null);
            return;
        }
        ArrayList<Team> others = g.getTeams().values().stream().filter(t -> t.getId() != team.getId()).collect(Collectors.toCollection(ArrayList::new));
        team.getDropped().remove(c);
        team.getInProgress().putIfAbsent(c, new ArrayList<>());
        if (!team.getInProgress().get(c).contains(p.getUniqueId())) {
            team.getInProgress().get(c).add(p.getUniqueId());
            team.sendTitle(plugin.getLang().get("titles.teampick.title").replaceAll("<color>", c + ""), plugin.getLang().get("titles.teampick.subtitle").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")), 0, 30, 10);
            ChatColor finalC = c;
            others.forEach(t -> t.sendTitle(plugin.getLang().get("titles.otherpick.title").replaceAll("<color>", finalC + ""), plugin.getLang().get("titles.otherpick.subtitle").replaceAll("<wool>", finalC + plugin.getLang().get("scoreboards.wools.captured")), 0, 30, 10));
            team.playSound(XSound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
            others.forEach(t -> t.playSound(XSound.ENTITY_WITHER_HURT, 1.0f, 1.0f));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Game g = plugin.getGm().getGameByPlayer(p);
            if (g == null) return;
            Team team = g.getTeamPlayer(p);
            if (team == null) {
                e.setCancelled(true);
                return;
            }
            if (e.getFinalDamage() >= p.getHealth()) {
                if (plugin.getTgm().hasTag(p)) {
                    Player d = plugin.getTgm().getTagged(p).getLast();
                    CTWPlayer sk = plugin.getDb().getCTWPlayer(d);
                    EntityDamageEvent.DamageCause cause = e.getCause();
                    if (sk != null) {
                        plugin.getTm().execute(p, cause, g, sk.getTaunt());
                    } else {
                        plugin.getTm().execute(p, cause, g, 0);
                    }
                    g.addKill(d);
                } else {
                    plugin.getTm().execute(p, e.getCause(), g, 0);
                }
                e.setCancelled(true);
                respawn(team, g, p);
                g.addDeath(p);
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player d = (Player) e.getDamager();
            Game g = plugin.getGm().getGameByPlayer(d);
            if (g == null) return;
            Entity entity = e.getEntity();
            NPC npc = plugin.getNpc().getNPC(d, entity.getUniqueId());
            if (npc != null) {
                e.setCancelled(true);
            }
        }
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getDamager() instanceof Player) {
                Player d = (Player) e.getDamager();
                Game g = plugin.getGm().getGameByPlayer(p);
                if (g == null) return;
                Team tp = g.getTeamPlayer(p);
                Team td = g.getTeamPlayer(d);
                if (tp == null || td == null) {
                    e.setCancelled(true);
                    return;
                }
                if (tp.getId() == td.getId()) {
                    e.setCancelled(true);
                    return;
                }
                double damage = e.getFinalDamage();
                plugin.getTgm().setTag(d, p, damage, g);
                if (e.getFinalDamage() >= p.getHealth()) {
                    CTWPlayer sk = plugin.getDb().getCTWPlayer(d);
                    g.addKill(d);
                    plugin.getStm().addKill(d);
                    if (p.getLastDamageCause() == null || p.getLastDamageCause().getCause() == null) {
                        EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CONTACT;
                        if (sk != null) {
                            plugin.getTm().execute(p, cause, g, sk.getTaunt());
                        }
                    } else {
                        EntityDamageEvent.DamageCause cause = p.getLastDamageCause().getCause();
                        if (sk != null) {
                            plugin.getTm().execute(p, cause, g, sk.getTaunt());
                        }
                    }
                    if (sk != null) {
                        plugin.getKem().execute(g, d, p, p.getLocation(), sk.getKillEffect());
                        plugin.getKsm().execute(d, p, sk.getKillSound());
                    } else {
                        plugin.getTm().execute(p, e.getCause(), g, 0);
                    }
                    e.setCancelled(true);
                    respawn(tp, g, p);
                    g.addDeath(p);
                    plugin.getStm().resetStreak(p);
                    d.sendMessage(plugin.getLang().get("messages.kill").replaceAll("<xp>", String.valueOf(plugin.getCm().getXpKill())).replaceAll("<coins>", String.valueOf(plugin.getCm().getCoinsKill())).replaceAll("<streak>", plugin.getStm().getPrefix(p)));
                } else {
                    Tagged tag = plugin.getTgm().getTagged(p);
                    tag.addPlayerDamage(p, e.getFinalDamage());
                }
            }
            if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
                Player d = (Player) ((Projectile) e.getDamager()).getShooter();
                Game g = plugin.getGm().getGameByPlayer(p);
                if (g == null) return;
                Team tp = g.getTeamPlayer(p);
                Team td = g.getTeamPlayer(d);
                if (tp == null || td == null) {
                    e.setCancelled(true);
                    return;
                }
                if (tp.getId() == td.getId()) {
                    e.setCancelled(true);
                    return;
                }
                double damage = e.getFinalDamage();
                plugin.getTgm().setTag(d, p, damage, g);
                if (e.getFinalDamage() >= p.getHealth()) {
                    g.addKill(d);
                    plugin.getStm().addKill(d);
                    CTWPlayer sk = plugin.getDb().getCTWPlayer(d);
                    if (p.getLastDamageCause() == null || p.getLastDamageCause().getCause() == null) {
                        EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CONTACT;
                        if (sk != null) {
                            plugin.getTm().execute(p, cause, g, sk.getTaunt());
                        }
                    } else {
                        EntityDamageEvent.DamageCause cause = p.getLastDamageCause().getCause();
                        if (sk != null) {
                            plugin.getTm().execute(p, cause, g, sk.getTaunt());
                        }
                    }
                    if (sk != null) {
                        plugin.getKem().execute(g, d, p, p.getLocation(), sk.getKillEffect());
                        plugin.getKsm().execute(d, p, sk.getKillSound());
                    } else {
                        plugin.getTm().execute(p, e.getCause(), g, 0);
                    }
                    e.setCancelled(true);
                    respawn(tp, g, p);
                    g.addDeath(p);
                    plugin.getStm().resetStreak(p);
                    d.sendMessage(plugin.getLang().get("messages.kill").replaceAll("<xp>", String.valueOf(plugin.getCm().getXpKill())).replaceAll("<coins>", String.valueOf(plugin.getCm().getCoinsKill())).replaceAll("<streak>", plugin.getStm().getPrefix(p)));
                } else {
                    Tagged tag = plugin.getTgm().getTagged(p);
                    tag.addPlayerDamage(p, e.getFinalDamage());
                }
                CTWPlayer ctw = plugin.getDb().getCTWPlayer(d);
                ctw.setsShots(ctw.getsShots() + 1);
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            Game g = plugin.getGm().getGameByPlayer(p);
            if (g == null) {
                return;
            }
            CTWPlayer ctw = plugin.getDb().getCTWPlayer(p);
            if (ctw == null) return;
            ctw.setShots(ctw.getShots() + 1);
            Projectile proj = e.getEntity();
            Trail trail = plugin.getTlm().getTrails().get(ctw.getTrail());
            if (trail == null) {
                return;
            }
            plugin.getTlm().spawnTrail(proj, trail);
        }
    }

    @EventHandler
    public void onHealth(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!plugin.getTgm().hasTag(p)) return;
            Tagged tag = plugin.getTgm().getTagged(p);
            tag.removeDamage(e.getAmount());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand() == null || p.getItemInHand().getType().equals(Material.AIR)) {
            return;
        }
        ItemStack item = p.getItemInHand();
        if (item.equals(plugin.getIm().getTeams())) {
            Game game = plugin.getGm().getGameByPlayer(p);
            if (game == null) return;
            plugin.getGem().createTeamsMenu(p, game);
        }
        if (item.equals(plugin.getIm().getLobby())) {
            plugin.getUim().openContentInventory(p, plugin.getUim().getMenus("lobby"));
        }
        if (item.equals(plugin.getIm().getLeave())) {
            plugin.getGm().removePlayerGame(p, true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to.getWorld().getName().equals(from.getWorld().getName())) {
            return;
        }
        Player p = e.getPlayer();
        from.getWorld().getPlayers().forEach(pl -> pl.hidePlayer(p));
        from.getWorld().getPlayers().forEach(p::hidePlayer);
        to.getWorld().getPlayers().forEach(pl -> pl.showPlayer(p));
        to.getWorld().getPlayers().forEach(p::showPlayer);
    }

    private boolean check(Player p1, Player p2) {
        return !p1.getWorld().getName().equals(p2.getWorld().getName());
    }

    private void respawn(Team team, Game g, Player p) {
        p.setNoDamageTicks(40);
        p.setFallDistance(0);
        p.setHealth(p.getMaxHealth());
        p.teleport(team.getSpawn());
        for (ChatColor c : team.getColors()) {
            if (team.getInProgress().get(c).isEmpty()) continue;
            team.getInProgress().get(c).remove(p.getUniqueId());
            if (team.getInProgress().get(c).isEmpty()) {
                g.sendGameMessage(plugin.getLang().get("messages.lost").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")));
                team.sendTitle(plugin.getLang().get("titles.dropped.title"), plugin.getLang().get("titles.dropped.subtitle").replaceAll("<wool>", c + plugin.getLang().get("scoreboards.wools.captured")), 0, 20, 0);
            }
        }
        p.getInventory().clear();
        plugin.getKm().giveDefaultKit(p, g, team);
        p.updateInventory();
    }

    private void givePlayerItems(Player p) {
        p.getInventory().setItem(4, plugin.getIm().getLobby());
    }

}