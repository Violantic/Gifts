/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.util;

import me.borawski.gift.Gifts;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.dustplanet.util.SilkUtil;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Created by Ethan on 1/6/2017.
 */
public class GiftUtil {

    private Gifts instance;

    public GiftUtil(Gifts instance) {
        this.instance = instance;
    }

    public Gifts getInstance() {
        return instance;
    }

    public void onSend(UUID sender, UUID receiver, ItemStack itemStack) {
        Player player = Bukkit.getPlayer(sender);
        Player target = Bukkit.getPlayer(receiver);
        getInstance().getSQL().sendGift(sender, receiver, itemStack);
        ItemStack hand = player.getItemInHand();
        player.getInventory().remove(hand);
        player.sendMessage("&e&l(!) &eYou have sent &e&n<name>&r &ea gift.".replace("&", ChatColor.COLOR_CHAR + "").replace("<name>", getInstance().getSQL().getName(receiver.toString())));

        try {
            target.sendMessage("&a&l(!) &aYou have received a gift from <name>!".replace("&", ChatColor.COLOR_CHAR + "").replace("<name>", player.getName()));
        } catch (NullPointerException e) {
            System.out.println("Player " + receiver.toString() + " not online!");
        }
    }

    public void onReceive(UUID receiver, int id) {
        Player player = Bukkit.getPlayer(receiver);
        getInstance().getSQL().receiveGift(receiver, id, -1);
        player.getInventory().addItem(getInstance().getSQL().getItem(id));
        player.sendMessage("&a&l(!) &aYou have accepted this gift, it is now out of your gift box.".replace("&", ChatColor.COLOR_CHAR + ""));
    }

    public void recieveSpawner(UUID receiver, String name, short i, int size, int id) {
        Player player = Bukkit.getPlayer(receiver);
        getInstance().getSQL().receiveGift(receiver, id, -1);
        ItemStack itemz = SilkUtil.hookIntoSilkSpanwers().newSpawnerItem(i, name, size, false);
        ItemMeta im = itemz.getItemMeta();
        im.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + name + " " + ChatColor.RESET + "Spawner");
        itemz.setItemMeta(im);
        player.getInventory().addItem(itemz);
        player.sendMessage("&a&l(!) &aYou have accepted this gift, it is now out of your gift box.".replace("&", ChatColor.COLOR_CHAR + ""));
    }

    public short sort(String name) {
        return (EntityType.valueOf(name.toUpperCase()) != null) ? EntityType.valueOf(name.toUpperCase()).getTypeId() : EntityType.PIG.getTypeId();
    }

}
