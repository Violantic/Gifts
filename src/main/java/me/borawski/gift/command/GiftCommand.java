/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.command;

import me.borawski.gift.Gifts;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ethan on 1/6/2017.
 */
public class GiftCommand implements CommandExecutor {

    private Gifts instance;
    private List<UUID> cooldown;
    private Map<UUID, Long> epoc;

    public GiftCommand(Gifts instance) {
        this.instance = instance;
        this.cooldown = new ArrayList<UUID>();
        this.epoc = new ConcurrentHashMap<UUID, Long>();
    }

    public Gifts getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        final Player player = (Player) commandSender;

        if(args.length == 0) {
            player.sendMessage("&e&l(!) &eUsage: /box|giftbox|mailbox".replace("&", ChatColor.COLOR_CHAR + ""));
            player.sendMessage("&e&l(!) &eUsage: /gift send <name>".replace("&", ChatColor.COLOR_CHAR + ""));
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("send")) {
                if(player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage("&c&l(!) &cYou can't send nothing!".replace("&", ChatColor.COLOR_CHAR + ""));
                    return false;
                } else if(check(player.getItemInHand())) {
                    player.sendMessage("&c&l(!) &cYou can't send this item on this server!".replace("&", ChatColor.COLOR_CHAR + ""));
                    return false;
                }

                String target = args[1];
                String uuid = getInstance().getSQL().getUUID(target);
                if (uuid == null) {
                    player.sendMessage("&c&l(!) &cThat player has never logged on here!".replace("&", ChatColor.COLOR_CHAR + ""));
                    return false;
                }

                if(cooldown.contains(player.getUniqueId())) {
                    double timeLeft = 5-Math.round((System.currentTimeMillis() - epoc.get(player.getUniqueId()))/1000.0D);
                    player.sendMessage(("&c&l(!) &cPlease wait (%) to send more gifts!").replace("&", ChatColor.COLOR_CHAR + "").replace("%", timeLeft + "s"));
                    return false;
                }

                getInstance().getGiftUtil().onSend(player.getUniqueId(), UUID.fromString(uuid), player.getItemInHand());
                cooldown.add(player.getUniqueId());
                epoc.put(player.getUniqueId(), System.currentTimeMillis());
                getInstance().getServer().getScheduler().runTaskLater(getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        cooldown.remove(player.getUniqueId());
                        epoc.remove(player.getUniqueId());
                    }
                }, (20 * getInstance().getCooldown()));
                return true;
            }
        }
        return false;
    }

    public boolean check(ItemStack item) {
        return getInstance().getBlacklist().contains(item.getType().name()) || getInstance().getBlacklist().contains(item.getTypeId());
    }
}
