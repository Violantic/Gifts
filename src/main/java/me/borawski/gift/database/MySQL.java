/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.gift.database;

import me.borawski.gift.Gifts;
import me.borawski.gift.gui.CustomIS;
import me.borawski.gift.util.Callback;
import me.borawski.gift.util.ItemUtil;
import me.borawski.gift.util.MapUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Ethan on 1/6/2017.
 */
public class MySQL {

    private Gifts instance;

    private String host;
    private String name;
    private String user;
    private String pass;
    private String table;
    private Connection connection;

    public MySQL(Gifts instance, String host, String table, String name, String user, String pass) {
        try {
            this.instance = instance;
            this.host = host;
            this.table = table;
            this.name = name;
            this.user = user;
            this.pass = pass;

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + getHost(), getUser(), getPass());


            SETUP_TABLE();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Gifts getInstance() {
        return instance;
    }

    /**
     * Core SQL
     **/

    public String getHost() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getTable() {
        return table;
    }

    public Connection getConnection() {
        return connection;
    }

    public void SETUP_TABLE() {
        String query = "CREATE TABLE IF NOT EXISTS " + getTable() + " (id INT NOT NULL AUTO_INCREMENT, receiver VARCHAR(60), sender VARCHAR(60), item TEXT(65535), sent BIGINT, status INT(11), PRIMARY KEY (id));";
        String users = "CREATE TABLE IF NOT EXISTS " + getTable() + "_users (id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(60), name VARCHAR(16), PRIMARY KEY(id));";
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(query);
            executeAsync(statement);
            statement = getConnection().prepareStatement(users);
            executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String GET_TABLE() {
        return "";
    }

    public Callback<Connection> invokeConnection() {
        return new Callback<Connection>() {
            public void call(Connection callback) {
                try {
                    callback = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + getHost(), getUser(), getPass());
                    System.out.println("[GIFTS] Refreshing connection @ " + host);
                    getInstance().getTracker().setLastUpdate(System.currentTimeMillis());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * API
     **/

    @Deprecated
    public List<ItemStack> getGifts(UUID uuid) {
        List<ItemStack> stack = new ArrayList<ItemStack>();
        ResultSet set = query("received", uuid.toString());
        try {
            while(set.next()) {
                if(set.getInt("status") != 0) break;

                String item = set.getString("item");
                ItemStack tryItem = ItemStack.deserialize(MapUtil.stringToMap(item));
                stack.add(tryItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stack;
    }

    //TODO: Use a future callback for asynchronous gift querying?
    /**
     * Orders a players gifts by itemstack AND id of database
     * @param uuid
     * @return
     */
    public Map<Integer, ItemStack> previewGifts(UUID uuid) {
        Map<Integer, ItemStack> stack = new HashMap<Integer, ItemStack>();
        String query = "SELECT * FROM " + getTable() + " WHERE receiver='" + uuid + "'";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();

            while(set.next()) {
                if(set.getInt("status") == 0) {
                    String item = set.getString("item");
                    ItemStack tryItem = ItemUtil.stringToItem(item);
                    CustomIS is = new CustomIS(tryItem);

                    long time = set.getLong("sent");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

                    is.getLore().addAll(Arrays.asList("", "&b&l* &7Gifted By:&b <name>".replace("<name>", getName(set.getString("sender"))).replace("&", ChatColor.COLOR_CHAR + ""),
                            "&b&l* &7Date: &b".replace("&", ChatColor.COLOR_CHAR + "") + sdf.format(new Date(time))));
                    stack.put(set.getInt("id"), is.get());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stack;
    }

    public void sendGift(UUID sender, UUID receiver, ItemStack gift) {
        PreparedStatement statement = null;
        try {
            String query = "INSERT INTO " + getTable() + " (id,receiver,sender,item,sent,status) VALUES (NULL, '" + receiver.toString() + "', '" + sender.toString() + "', '" + ItemUtil.itemToString(gift) + "', " + System.currentTimeMillis() + ", 0)";
            statement = getConnection().prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        executeAsync(statement);
    }

    public void receiveGift(final UUID receiver, final int id, final int status) {
        String query = "UPDATE " + getTable() + " SET status='" + status + "' WHERE id='" + id + "';";
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        executeAsync(statement);
    }

    public ItemStack getItem(int id) {
        String query = "SELECT * FROM " + getTable() + " WHERE id='" + id + "';";
        try {
            ResultSet set = getConnection().prepareStatement(query).executeQuery();
            while (set.next()) {
                return ItemUtil.stringToItem(set.getString("item"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Util **/

    public void executeAsync(final PreparedStatement statement) {
        instance.getServer().getScheduler().runTaskAsynchronously(getInstance(),
                new Runnable() {
                    public void run() {
                        try {
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * Query for a specific row of data with the modifier, and the value.
     * Example, if you would like to find an entire User row, you would
     * call this method, and on invocation the parameters would be 'uuid', and '{desired uuid you want to find here}'.
     *
     * @param modifier
     * @param desiredTarget
     * @return
     */
    public ResultSet query(String modifier, String desiredTarget) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + GET_TABLE() + " WHERE " + modifier + "='" + desiredTarget + "';");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerUser(String uuid, String name) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            if(!set.next()) {
                String insert = "INSERT INTO " + getTable() + "_users (id,uuid,name) VALUES (NULL, '" + uuid + "', '" + name + "')";
                PreparedStatement register = getConnection().prepareStatement(insert);
                executeAsync(register);
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUUID(String name) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE name='" + name + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName(String uuid) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while(set.next()) {
                return set.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
