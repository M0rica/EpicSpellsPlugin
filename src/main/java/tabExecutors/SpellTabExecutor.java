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
package tabExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import epicspellsplugin.*;
import epicspellsplugin.spellcasting.Spellcaster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

/**
 *
 * @author M0rica
 */
public class SpellTabExecutor implements TabExecutor{

    private EpicSpellsPlugin esp;
    private SpellManager spellManager;
    private MageManager mageManager;
    private Spellcaster spellcaster;
    
    public SpellTabExecutor(EpicSpellsPlugin esp){
        this.esp = esp;
        spellManager = esp.getSpellManager();
        mageManager = esp.getMageManager();
        spellcaster = esp.getSpellcaster();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        
        if(args.length == 1){
            
            commands.add("cast");
            commands.add("kill");
            commands.add("terminate");
            commands.add("bind");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if(args.length == 2){
            switch(args[0]){
                case "cast": case "bind":
                    List<String> spellNames = spellManager.getSpellNames();
                    commands.addAll(spellNames);
                    break;
                case "kill": case "terminate":
                    for(int id: spellManager.getActiveSpellIDs()){
                        commands.add(String.valueOf(id));
                    }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){
        switch (args[0]) {
            case "cast":
                if (args.length >= 2 && sender instanceof Player) {
                    int spellID = spellManager.castSpell(args[1], (Player) sender);
                    if(spellID != -1) {
                        sender.sendMessage(String.format("Cast spell %s with ID %s", args[1], spellID));
                    }
                    return true;
                } else {
                    return false;
                }
            case "bind":
                if(args.length == 2 && sender instanceof Player){
                    Player player = (Player) sender;
                    Mage mage = mageManager.getMage(player);
                    if(mage != null){
                        spellcaster.setupSpellBinding(mage, args[1]);
                    } else {
                        player.sendMessage("Your are not a mage");
                    }
                    return true;
                } else {
                    return false;
                }
            case "terminate": case "kill":
                if(args.length >= 2){
                    String action = args[0];
                    try {
                        int spellID = Integer.parseInt(args[1]);
                        if(Arrays.stream(spellManager.getActiveSpellIDs()).anyMatch(x -> x==spellID)){
                            BaseSpell spell = spellManager.getSpell(spellID);
                            if(action.equals("terminate")){
                                spellManager.terminateSpell(spellID);
                                sender.sendMessage(String.format("Terminated spell with ID %d (%s)", spellID, spell.getName()));
                            } else {
                                spellManager.killSpell(spellID);
                                sender.sendMessage(String.format("Killed spell with ID %d (%s)", spellID, spell.getName()));
                            }
                        }
                    } catch (NumberFormatException e){
                        sender.sendMessage(String.format("Error: %s is not a number!", args[1]));
                    }
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
}
