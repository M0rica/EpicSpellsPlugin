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
package epicspellsplugin.utils;

import java.util.Arrays;
import java.util.Random;

public class Utils {

    public static float randomFloat(float min, float max){
        Random r = new Random();
        return r.nextFloat() * (max-min) + min;
    }

    public static double randomDouble(double min, double max){
        Random r = new Random();
        return r.nextDouble() * (max-min) + min;
    }

    public static boolean arraysEqual(Integer[] array, Integer[] other){
        if(array.length != other.length){
            return false;
        }
        Arrays.sort(array);
        Arrays.sort(other);
        return Arrays.equals(array, other);
    }
}
