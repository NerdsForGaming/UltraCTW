package io.github.Leonardo0013YT.UltraCTW.cosmetics.taunts;

import io.github.Leonardo0013YT.UltraCTW.Main;
import io.github.Leonardo0013YT.UltraCTW.interfaces.Game;
import io.github.Leonardo0013YT.UltraCTW.interfaces.Purchasable;
import io.github.Leonardo0013YT.UltraCTW.interfaces.CTWPlayer;
import io.github.Leonardo0013YT.UltraCTW.utils.ItemBuilder;
import io.github.Leonardo0013YT.UltraCTW.xseries.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class Taunt implements Purchasable {

    private HashMap<String, TauntType> taunts = new HashMap<>();
    private String name, permission, title, subtitle, player, none, autoGivePermission;
    private boolean isBuy, needPermToBuy;
    private int id, slot, page, price;
    private ItemStack icon;

    public Taunt(Main plugin, String s) {
        this.id = plugin.getTaunt().getInt(s + ".id");
        this.name = plugin.getTaunt().get(null, s + ".name");
        this.permission = plugin.getTaunt().get(null, s + ".permission");
        this.title = plugin.getTaunt().get(null, s + ".title");
        this.subtitle = plugin.getTaunt().get(null, s + ".subtitle");
        this.player = plugin.getTaunt().get(null, s + ".player");
        this.none = plugin.getTaunt().get(null, s + ".none");
        this.slot = plugin.getTaunt().getInt(s + ".slot");
        this.page = plugin.getTaunt().getInt(s + ".page");
        this.price = plugin.getTaunt().getInt(s + ".price");
        this.isBuy = plugin.getTaunt().getBoolean(s + ".isBuy");
        this.icon = plugin.getTaunt().getConfig().getItemStack(s + ".icon");
        this.needPermToBuy = plugin.getTaunt().getBooleanOrDefault(s + ".needPermToBuy", false);
        this.autoGivePermission = plugin.getTaunt().getOrDefault(s + ".autoGivePermission", "ultraskywars.taunts.autogive." + name);
        ConfigurationSection conf = plugin.getTaunt().getConfig().getConfigurationSection(s + ".taunts");
        for (String d : conf.getKeys(false)) {
            taunts.put(d, new TauntType(d, plugin.getTaunt().getList(s + ".taunts." + d + ".msg")));
        }
        plugin.getTm().setLastPage(page);
    }

    public void execute(Player d, int id, Game game) {
        String msg = taunts.get("CONTACT").getRandomMessage();
        String death = d.getName();
        msg = msg.replaceAll("<death>", death);
        for (Player p : game.getPlayers()) {
            p.sendMessage(msg + none);
        }
        for (Player p : game.getSpectators()) {
            p.sendMessage(msg + none);
        }
    }

    public void execute(Player d, EntityDamageEvent.DamageCause cause, int id, Game game) {
        Player k = null;
        if (Main.get().getTgm().hasTag(d)) {
            k = Main.get().getTgm().getTagged(d).getLast();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (d != null) {
                    Main.get().getVc().getNMS().sendTitle(d, title, subtitle, 0, 60, 0);
                }
            }
        }.runTaskLater(Main.get(), 5L);
        if (k == null) {
            String msg = taunts.get(cause.name()).getRandomMessage();
            String death = d.getName();
            msg = msg.replaceAll("<death>", death);
            for (Player p : game.getCached()) {
                p.sendMessage(msg + none);
            }
        } else {
            String msg = taunts.get(cause.name()).getRandomMessage();
            String killer = player.replaceAll("<killer>", k.getName());
            String death = d.getName();
            msg = msg.replaceAll("<killer>", killer).replaceAll("<death>", death);
            for (Player p : game.getCached()) {
                p.sendMessage(msg + killer);
            }
        }
    }

    public HashMap<String, TauntType> getTypes() {
        return taunts;
    }

    @Override
    public boolean isBuy() {
        return isBuy;
    }

    @Override
    public boolean needPermToBuy() {
        return needPermToBuy;
    }

    @Override
    public String getAutoGivePermission() {
        return autoGivePermission;
    }

    public ItemStack getIcon(Player p) {
        if (!icon.hasItemMeta()) {
            return icon;
        }
        CTWPlayer sw = Main.get().getDb().getCTWPlayer(p);
        ItemStack icon = this.icon.clone();
        if (!p.hasPermission(autoGivePermission)) {
            if (price > 0) {
                if (Main.get().getCm().isRedPanelInLocked()) {
                    if (!sw.getTaunts().contains(id)) {
                        icon = ItemBuilder.item(XMaterial.matchXMaterial(Main.get().getCm().getRedPanelMaterial().name(), (byte) Main.get().getCm().getRedPanelData()).orElse(XMaterial.RED_STAINED_GLASS_PANE), 1, icon.getItemMeta().getDisplayName(), icon.getItemMeta().getLore());
                    }
                }
            }
        }
        ItemMeta iconM = icon.getItemMeta();
        List<String> lore = icon.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            switch (s) {
                case "<price>":
                    if (!p.hasPermission(autoGivePermission)) {
                        if (isBuy && !sw.getTaunts().contains(id)) {
                            lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.price").replaceAll("<price>", String.valueOf(price)));
                        } else if (!isBuy && !sw.getTaunts().contains(id)) {
                            if (needPermToBuy && p.hasPermission(permission)) {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.price").replaceAll("<price>", String.valueOf(price)));
                            } else {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.noBuyable"));
                            }
                        } else if (sw.getTaunts().contains(id) || !needPermToBuy) {
                            lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.buyed"));
                        }
                    } else {
                        lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.buyed"));
                    }
                    break;
                case "<status>":
                    if (!p.hasPermission(autoGivePermission)) {
                        if (sw.getTaunts().contains(id)) {
                            lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.hasBuy"));
                        } else if (isBuy) {
                            if (Main.get().getAdm().getCoins(p) > price) {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.buy"));
                            } else {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.noMoney"));
                            }
                        } else if (needPermToBuy) {
                            if (Main.get().getAdm().getCoins(p) > price) {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.buy"));
                            } else {
                                lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.noMoney"));
                            }
                        } else {
                            lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.noPermission"));
                        }
                    } else {
                        lore.set(i, Main.get().getLang().get(p, "menus.tauntsselector.hasBuy"));
                    }
                    break;
            }
        }
        iconM.setLore(lore);
        icon.setItemMeta(iconM);
        return icon;
    }

    public int getPage() {
        return page;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }

    public String getTitle() {
        return title;
    }

    public String getPlayer() {
        return null;
    }

    public String getNone() {
        return null;
    }

    @Override
    public String getPermission() {
        return permission;
    }
}