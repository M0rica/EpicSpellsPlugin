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

/**
 *
 * @author M0rica
 */
public class EpicSpellsPlugin extends JavaPlugin{
    
    private Logger log;
    private BukkitScheduler scheduler;
    private SpellManager spellManager;
    private MageManager mageManager;
    
    @Override
    public void onEnable(){
        log = this.getLogger();
        
        mageManager = new MageManager();
        spellManager = new SpellManager(log, mageManager);
        
        scheduler = Bukkit.getScheduler();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerServerEventListener(this), this);
        TabExecutor spellTabExecutor = new SpellTabExecutor(this);
        //this.getCommand("spell").setExecutor(tabExecutor);
        this.getCommand("spell").setTabCompleter(spellTabExecutor);
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
            case "spell":
                if (args.length >= 1 && sender instanceof Player) {
                    spellManager.castSpell(args[1], (Player) sender);
                    return true;
                } else {
                    return false;
                }
            case "addmage":
                if (args.length == 1) {
                    mageManager.addPlayer(Bukkit.getPlayer(args[0]));
                } else {
                    if (sender instanceof Player) {
                        mageManager.addPlayer((Player) sender);
                    }
                }
                return true;
            case "removemage":
                if (args.length == 1) {
                    mageManager.removePlayer(Bukkit.getPlayer(args[0]));
                } else {
                    if (sender instanceof Player) {
                        mageManager.removePlayer((Player) sender);
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
            }
        }, 1, 1);
    }
}
