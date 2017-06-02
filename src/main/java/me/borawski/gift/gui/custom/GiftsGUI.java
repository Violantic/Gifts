/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.gui.custom;

import me.borawski.gift.Gifts;
import me.borawski.gift.gui.CustomIS;
import me.borawski.gift.gui.ItemGUI;
import me.borawski.gift.gui.MenuItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Ethan on 1/6/2017.
 */
public class GiftsGUI extends ItemGUI {

    public GiftsGUI(Gifts instance, Player player) {
        super(instance, player);
    }

    @Override
    public String getName() {
        return "Your Gifts";
    }

    @Override
    public boolean isCloseOnClick() {
        return true;
    }

    @Override
    public void registerItems() {
        int i = 0;
        final Map<Integer, ItemStack> giftPreview = getInstance().getSQL().previewGifts(getPlayer().getUniqueId());
        int backSize = giftPreview.size();
        for(int b = backSize; b < 36; b++) {
            set(b, new MenuItem(new CustomIS(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7)).setName(ChatColor.AQUA + "Empty Slot"), new Runnable() {
                public void run() {
                    getPlayer().sendMessage(ChatColor.YELLOW + ":(");
                }
            }));
        }
        for(final Integer x : giftPreview.keySet()) {
            set(i, new MenuItem(new CustomIS(giftPreview.get(x)), new Runnable() {
                public void run() {
                    if(giftPreview.get(x).getType() == Material.MOB_SPAWNER) {
                        String[] name = giftPreview.get(x).getItemMeta().getDisplayName().split(" ");
                        String n = ChatColor.stripColor(name[0]);
                        GiftsGUI.this.getInstance().getGiftUtil().recieveSpawner(getPlayer().getUniqueId(), n, sort(n), giftPreview.get(x).getAmount(), x);
                        return;
                    }

                    GiftsGUI.this.getInstance().getGiftUtil().onReceive(GiftsGUI.this.getPlayer().getUniqueId(), x);
                }
            }));
            i++;
        }
    }

    public static short sort(String name) {
        short s = 0;
        for(EntityType t : EntityType.values()) {
            if(t.name().toUpperCase().equalsIgnoreCase(name.toUpperCase()) || t.name().toUpperCase().startsWith(name) || t.name().toUpperCase().contains(name.toUpperCase())) {
                return t.getTypeId();
            }
        }
        return s;
    }

}
