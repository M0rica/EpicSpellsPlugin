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

import epicspellsplugin.EpicSpellsPlugin;
import epicspellsplugin.SpellManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

/**
 *
 * @author M0rica
 */
public class SpellTabExecutor implements TabExecutor{

    private EpicSpellsPlugin esp;
    private SpellManager spellManager;
    
    public SpellTabExecutor(EpicSpellsPlugin esp){
        this.esp = esp;
        spellManager = esp.getSpellManager();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        
        if(args.length == 1){
            
            commands.add("cast");
            commands.add("kill");
            commands.add("terminate");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if(args.length == 2){
            switch(args[0]){
                case "cast":
                    String[] spellNames = spellManager.getSpellNames();
                    commands.addAll(Arrays.asList(spellNames));
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
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] strings) {
        return spellManager.onCommand(cs, cmd, string, strings);
    }
}
