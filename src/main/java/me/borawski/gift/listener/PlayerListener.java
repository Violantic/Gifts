/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.listener;

import me.borawski.gift.Gifts;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Ethan on 1/6/2017.
 */
public class PlayerListener implements Listener {

    private Gifts instace;

    public PlayerListener(Gifts instace) {
        this.instace = instace;
    }

    public Gifts getInstace() {
        return instace;
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        getInstace().getSQL().registerUser(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
        getInstace().getServer().getScheduler().runTaskLater(getInstace(), new Runnable() {
            @Override
            public void run() {
                int i = getInstace().getSQL().previewGifts(event.getPlayer().getUniqueId()).size();
                if(i > 0) {
                    event.getPlayer().sendMessage("&e&l(!) &eYou have &e&n%&r &egift(s) unopened, type /box to receive it.".replace("&", ChatColor.COLOR_CHAR + "").replace("%", i + ""));
                }
            }
        }, 20l);
    }
}

