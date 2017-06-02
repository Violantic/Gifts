/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.gui;

import me.borawski.gift.Gifts;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Ethan on 9/5/2016.
 */
public abstract class ItemGUI {

    private ItemMenu itemMenu;

    private Gifts instance;

    public ItemGUI(Gifts instance, Player player) {
        this(instance, null, player);
    }

    public Gifts getInstance() {
        return instance;
    }

    public ItemGUI(Gifts instance, ItemGUI parent, Player p) {
        if (parent == null) {
            itemMenu = new ItemMenu(instance, getName(), p, 36);
        } else {
            itemMenu = new ItemMenu(instance, parent.getItemMenu(), getName(), p, 36);
        }
        //this.onlineUser = user;
        itemMenu.setCloseOnClick(this.isCloseOnClick());
        this.instance = instance;
        this.registerItems();
    }

    public void set(int slot, MenuItem item) {
        this.itemMenu.addItem(slot, item);
    }

    public void set(int x, int y, MenuItem item) {
        this.itemMenu.addItem(x, y, item);
    }

    public CustomIS createFast(Material m, int amount, String name, String... lore) {
        return this.createFast(m, amount, 0, name, lore);
    }

    public CustomIS createFast(Material m, int amount, int data, String name, String... lore) {
        ItemStack is = new ItemStack(m, amount, (short) data);
        ItemMeta meta = is.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        is.setItemMeta(meta);
        return new CustomIS(is);
    }

    public Player getPlayer() {
        return this.itemMenu.getPlayer();
    }

    public void refresh() {
        this.itemMenu.update();
    }

    public ItemMenu getItemMenu() {
        return this.itemMenu;
    }

    public void show() {
        this.itemMenu.show();
    }

    public void close() {
        this.itemMenu.close();
    }

    public abstract String getName();

    public abstract boolean isCloseOnClick();

    public abstract void registerItems();

    public void onClose(Runnable r) {
        this.itemMenu.onClose(r);
    }

}