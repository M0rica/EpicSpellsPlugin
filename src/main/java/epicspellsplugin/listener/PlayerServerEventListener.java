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
package epicspellsplugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import epicspellsplugin.EpicSpellsPlugin;
import epicspellsplugin.MageManager;
import org.bukkit.event.player.PlayerQuitEvent;
/**
 *
 * @author M0rica
 */
public class PlayerServerEventListener implements Listener{
    
    private EpicSpellsPlugin esp;
    private MageManager mageManager;
    
    public PlayerServerEventListener(EpicSpellsPlugin esp){
        this.esp = esp;
        mageManager = esp.getMageManager();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        mageManager.addPlayer(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        mageManager.removePlayer(player);
    }
}
