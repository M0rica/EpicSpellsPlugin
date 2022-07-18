/*
 * Copyright (C) 2022 M0rica
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package epicspellsplugin;

import epicspellsplugin.listener.PlayerServerEventListener;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import tabExecutors.MageTabExecutor;
import tabExecutors.SpellTabExecutor;
import epicspellsplugin.spellcasting.Spellcaster;

/**
 *
 * @author M0rica
 */
public class EpicSpellsPlugin extends JavaPlugin{
    
    private Logger log;
    private BukkitScheduler scheduler;
    private SpellManager spellManager;
    private MageManager mageManager;
    private Spellcaster spellcaster;
    
    @Override
    public void onEnable(){
        log = this.getLogger();
        
        mageManager = new MageManager();
        spellManager = new SpellManager(log, mageManager);
        spellcaster = new Spellcaster(spellManager, mageManager);
        
        scheduler = Bukkit.getScheduler();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerServerEventListener(this), this);
        TabExecutor spellTabExecutor = new SpellTabExecutor(this);
        this.getCommand("spell").setTabCompleter(spellTabExecutor);
        this.getCommand("spell").setExecutor(spellTabExecutor);
        TabExecutor mageTabExecutor = new MageTabExecutor(this);
        this.getCommand("mage").setTabCompleter(mageTabExecutor);
        this.getCommand("mage").setExecutor(mageTabExecutor);
        
        spellManager.setup();
        setup();
        
        log.info("Plugin enabled");
    }
    @Override
    public void onDisable(){
        log.info("Plugin disabled");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        switch (cmd.getName()) {
            case "addmage":
                if (args.length == 1) {
                    if(sender.isOp()) {
                        Player player = Bukkit.getPlayer(args[0]);
                        if(player != null) {
                            if(mageManager.getMage(player) == null) {
                                mageManager.addPlayer(player);
                                sender.sendMessage(String.format("%s is now a Mage", player.getDisplayName()));
                                player.sendMessage(String.format("%s made you a Mage", sender.getName()));
                            } else {
                                sender.sendMessage(String.format("%s is already a Mage!", player.getDisplayName()));
                            }
                        } else {
                            sender.sendMessage(String.format("No player named %s", args[0]));
                        }
                    } else {
                        sender.sendMessage("You don't have permissions to run this command!");
                    }
                } else {
                    if (sender instanceof Player) {
                        mageManager.addPlayer((Player) sender);
                        sender.sendMessage("You are now a Mage");
                    }
                }
                return true;
            case "removemage":
                if (args.length == 1) {
                    if(sender.isOp()) {
                        Player player = Bukkit.getPlayer(args[0]);
                        if(player != null) {
                            if(mageManager.getMage(player) != null) {
                                mageManager.removePlayer(player);
                                sender.sendMessage(String.format("%s is no longer a Mage", player.getDisplayName()));
                                player.sendMessage(String.format("%s removed you from being a Mage", sender.getName()));
                            } else {
                                sender.sendMessage(String.format("%s is not a Mage!", player.getDisplayName()));
                            }
                        } else {
                            sender.sendMessage(String.format("No player named %s", args[0]));
                        }
                    } else {
                        sender.sendMessage("You don't have permissions to run this command!");
                    }
                } else {
                    if (sender instanceof Player) {
                        mageManager.removePlayer((Player) sender);
                        sender.sendMessage("You are no longer a Mage");
                    }
                }
                return true;
        }
        return false;
    }
    
    public SpellManager getSpellManager(){
        return spellManager;
    }
    
    public MageManager getMageManager(){
        return mageManager;
    }
    
    public void setup(){
        for(Player player: Bukkit.getOnlinePlayers()){
            mageManager.addPlayer(player);
        }
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                mageManager.tick();
                spellManager.tick();
                spellcaster.tick();
            }
        }, 1, 1);
    }
}
