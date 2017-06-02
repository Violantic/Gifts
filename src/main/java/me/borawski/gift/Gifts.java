/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift;

import me.borawski.gift.command.BoxCommand;
import me.borawski.gift.command.GiftCommand;
import me.borawski.gift.database.ConnectionTracker;
import me.borawski.gift.database.MySQL;
import me.borawski.gift.listener.PlayerListener;
import me.borawski.gift.util.GiftUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Ethan on 1/6/2017.
 */
public class Gifts extends JavaPlugin {

    private static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "[FADECLOUD] " + ChatColor.RESET + "";

    /** Database variables **/
    private MySQL database;
    private ConnectionTracker tracker;

    /** Util variables **/
    private GiftUtil giftUtil;

    /** Constant variables **/
    private int cooldown;
    private List<?> blacklist;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(getConfig().contains("table"));
        saveConfig();

        database = new MySQL(this, getConfig().getString("host"), getConfig().getString("table"), getConfig().getString("name"), getConfig().getString("user"), getConfig().getString("pass"));
        tracker = new ConnectionTracker(this, database.getConnection());
        giftUtil = new GiftUtil(this);

        getCommand("gift").setExecutor(new GiftCommand(this));
        getCommand("box").setExecutor(new BoxCommand(this));

        cooldown = getConfig().getInt("cooldown");
        blacklist = getConfig().getList("blacklist");
        blacklist.stream().forEach(
                new Consumer<Object>() {
                    public void accept(Object o) {
                        System.out.println("[Gifts] blacklisting item: " + o.toString());
                    }
                }
        );

        getServer().getScheduler().runTaskTimer(this, tracker, 0L, 20 * 60L);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public MySQL getSQL() {
        return database;
    }

    public ConnectionTracker getTracker() {
        return tracker;
    }

    public GiftUtil getGiftUtil() {
        return giftUtil;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<?> getBlacklist() {
        return blacklist;
    }

    public void alert(String msg) {
        Bukkit.broadcastMessage(PREFIX + msg);
    }

}
