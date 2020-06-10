package io.github.Leonardo0013YT.UltraCTW.cosmetics.kits;

import io.github.Leonardo0013YT.UltraCTW.Main;
import io.github.Leonardo0013YT.UltraCTW.interfaces.CTWPlayer;
import io.github.Leonardo0013YT.UltraCTW.interfaces.Purchasable;
import io.github.Leonardo0013YT.UltraCTW.team.Team;
import io.github.Leonardo0013YT.UltraCTW.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

@Getter
public class KitLevel implements Purchasable {

    private ItemStack[] inv, armors;
    private ItemStack icon;
    private String permission;
    private int price, slot, level;
    private int kitID;

    public KitLevel(Main plugin, String path, int kitID) {
        this.kitID = kitID;
        this.level = plugin.getKits().getInt(path + ".level");
        this.permission = plugin.getKits().get(null, path + ".permission");
        this.icon = plugin.getKits().getConfig().getItemStack(path + ".icon");
        this.price = plugin.getKits().getInt(path + ".price");
        this.slot = plugin.getKits().getInt(path + ".slot");
        this.armors = ((List<String>) plugin.getKits().getConfig().get(path + ".armor")).toArray(new ItemStack[0]);
        this.inv = ((List<String>) plugin.getKits().getConfig().get(path + ".inv")).toArray(new ItemStack[0]);
    }

    public void giveKitLevel(Player p, Team team) {
        ItemStack[] nowArmor = new ItemStack[armors.length];
        for (int it = 0; it < armors.length; it++) {
            ItemStack i = armors.clone()[it];
            if (i == null || i.getType().equals(Material.AIR)) {
                nowArmor[it] = null;
                continue;
            }
            if (i.getType().equals(Material.LEATHER_HELMET) || i.getType().equals(Material.LEATHER_CHESTPLATE) || i.getType().equals(Material.LEATHER_LEGGINGS) || i.getType().equals(Material.LEATHER_BOOTS)) {
                LeatherArmorMeta armr = (LeatherArmorMeta) i.getItemMeta();
                armr.setColor(Utils.getColorByChatColor(team.getColor()));
                i.setItemMeta(armr);
            }
            nowArmor[it] = i;
        }
        p.getInventory().setArmorContents(nowArmor);
        p.getInventory().setContents(inv);
    }


    public ItemStack getIcon(Player p) {
        if (!icon.hasItemMeta()) {
            return icon;
        }
        CTWPlayer sw = Main.get().getDb().getCTWPlayer(p);
        ItemStack icon = this.icon.clone();
        return icon;
    }

    @Override
    public String getAutoGivePermission() {
        return "none.none.none";
    }

    @Override
    public boolean isBuy() {
        return true;
    }

    @Override
    public boolean needPermToBuy() {
        return false;
    }
}