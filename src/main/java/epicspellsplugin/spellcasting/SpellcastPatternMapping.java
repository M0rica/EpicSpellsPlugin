package epicspellsplugin.spellcasting;

import epicspellsplugin.utils.Utils;

import java.util.*;

public class SpellcastPatternMapping {

    public static SpellcastPatternMapping DEFAULT;
    static {
        DEFAULT = new SpellcastPatternMapping();
        DEFAULT.bindPattern(Arrays.asList(8, 1, 6), "Fireball");
        DEFAULT.bindPattern(Arrays.asList(5, 0, 7, 0), "PowerStrike");
        DEFAULT.bindPattern(Arrays.asList(5, 6, 1, 8), "ArrowStorm");
        DEFAULT.bindPattern(Arrays.asList(8, 2, 3), "Explosion");
    }
    private Map<List<Integer>, String> patternMap;

    public SpellcastPatternMapping(){
        patternMap = new HashMap<>();
    }

    public void bindPattern(List<Integer> pattern, String spellName){
        patternMap.put(pattern, spellName);
    }

    public void unbindPattern(List<Integer> pattern){
        patternMap.remove(pattern);
    }

    public String mapPattern(List<Integer> pattern){
        List<Integer> extendedPattern = new ArrayList<>(pattern);
        extendedPattern.addAll(pattern);
        for(List<Integer> temp: patternMap.keySet()){
            boolean same = false;
            if(!Utils.arraysEqual(pattern.toArray(new Integer[]{}), temp.toArray(new Integer[]{}))){
                continue;
            }
            for(int i = 0; i < extendedPattern.size(); i++){
                for(int j = 0; j < temp.size(); j++){
                    if((i+j) < extendedPattern.size()){
                        if(extendedPattern.get(i+j) == temp.get(j)){
                            same = true;
                        } else {
                            same = false;
                        }
                    }
                }
                if(same){
                    return patternMap.get(temp);
                }
            }
        }
        return null;
    }
}
