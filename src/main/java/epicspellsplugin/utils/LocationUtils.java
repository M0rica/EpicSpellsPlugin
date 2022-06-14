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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author M0rica
 */
public class LocationUtils {

    private static double randomDouble(double min, double max) {
        Random random = new Random();
        return min + random.nextDouble() * (max - min);
    }

    public static Location randomOffsetLocation(Location location, double offsetX, double offsetY, double offsetZ) {
        double posX = randomDouble(-offsetX, offsetX);
        double posY = randomDouble(-offsetY, offsetY);
        double posZ = randomDouble(-offsetZ, offsetZ);
        return location.clone().add(posX, posY, posZ);
    }

    public static Location randomOffsetLocationInDirection(Location location, Vector direction) {
        Vector temp = direction.clone().normalize();
        double offsetX = temp.getX();
        double offsetY = temp.getY();
        double offsetZ = temp.getZ();

        double posX = randomDouble(offsetX - 0.5, offsetX + 0.5);
        double posY = randomDouble(offsetY - 0.5, offsetY + 0.5);
        double posZ = randomDouble(offsetZ - 0.5, offsetZ + 0.5);
        Location l = location.clone();
        l.add(posX, posY, posZ);
        return l;
    }

    public static List<Location> generateCircle(Location location, double radius, int numPoints) {
        List<Location> locations = new ArrayList<>();
        for (double i = 0; i < Math.PI * 2; i += (double) 1 / numPoints) {
            double x = Math.cos(i) * 1;
            double y = 0;
            double z = Math.sin(i) * 1;
            Vector relativePosition = new Vector(x, y, z).normalize();
            locations.add(location.clone().add(relativePosition.multiply(radius)));
        }
        return locations;
    }

    public static Location randomPointInCircle(Location location, double radius){
        double value = Utils.randomDouble(0, Math.PI*2);
        double x = Math.cos(value) * 1;
        double y = 0;
        double z = Math.sin(value) * 1;
        Vector relativePosition = new Vector(x, y, z).normalize();
        return location.clone().add(relativePosition.multiply(Utils.randomDouble(0, radius)));
    }

    public static Location projectPointOnPlane(Location planePoint, Vector normal, Location point){
        double distance = point.clone().subtract(planePoint).toVector().dot(normal);
        Location projectedPoint = point.clone().subtract(normal.clone().multiply(distance));
        return projectedPoint;
    }
}
