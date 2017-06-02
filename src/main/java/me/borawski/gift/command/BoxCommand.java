/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.command;

import me.borawski.gift.Gifts;
import me.borawski.gift.gui.custom.GiftsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 1/12/2017.
 */
public class BoxCommand implements CommandExecutor {

    private Gifts instance;

    public BoxCommand(Gifts instance) {
        this.instance = instance;
    }

    public Gifts getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            new GiftsGUI(getInstance(), player).show();
        }
        return false;
    }
}
