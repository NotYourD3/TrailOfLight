package pers.notyourd3.trailoflight.feature;


import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Copied from refraction mod.
 */
public final class RotationHelper {
    public static Vec3 toVec3(float pitch, float yaw) {
        double sinPitch = Math.sin(pitch);
        double cosPitch = Math.cos(pitch);
        double sinYaw = Math.sin(yaw);
        double cosYaw = Math.cos(yaw);
        return new Vec3(cosPitch * cosYaw, sinPitch, cosPitch * sinYaw);
    }

    /**
     * Calculates the spherical inates, given a cartesian vector
     *
     * @param vec Vector given in (x, y, z)
     * @return Vector given in (rho, theta, phi)
     */
    public static Vec3 toSpherical(Vec3 vec) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        double radius = Math.sqrt(x * x + y * y + z * z);
        double theta = Math.atan2(z, x);
        double phi = Math.atan2(Math.sqrt(x * x + z * z), y);
        return new Vec3(radius, theta, phi);
    }

    /**
     * Takes several cartesian vectors, and returns one going in the average
     * direction, regardless of vector magnitues
     *
     * @param vectors The list of vectors to average. Will be normalized.
     * @return The average direction of all the given vectors.
     */
    public static Vec3 averageDirection(List<Vec3> vectors) {
        if (vectors == null) return Vec3.ZERO;
        if (vectors.isEmpty()) return Vec3.ZERO;
        double x = 0;
        double y = 0;
        double z = 0;

        for (Vec3 vec : vectors) {
            if (vec == null) continue;
            vec.normalize();
            x += vec.x;
            y += vec.y;
            z += vec.z;
        }

        return new Vec3(x, y, z);
    }

    /**
     * Calculates the reflection of a vector over a line
     *
     * @param vector The vector being reflected
     * @param line   The line over which the vector is being reflected
     * @return A reflected vector
     */
    public static Vec3 reflectLine(Vec3 vector, Vec3 line) {
        line = line.normalize();
        return vector.scale(-1).add(line.scale(2 * vector.dot(line)));
    }

    /**
     * Calculates the reflection of a vector on a plane
     *
     * @param vector The vector being reflected
     * @param normal The normal of the plane
     * @return A reflected vector
     */
    public static Vec3 reflectPlane(Vec3 vector, Vec3 normal) {
        normal = normal.normalize();
        return vector.subtract(normal.scale(vector.dot(normal) * 2));
    }

    public static Vec3 rotateAroundVector(Vec3 vector, Vec3 axis, float angle) {
        double cos = Math.cos(angle * Math.PI / 180);
        double sin = Math.sin(angle * Math.PI / 180);
        Vec3 cross = vector.cross(axis);
        return vector.scale(cos).add(cross.scale(sin)).add(axis.scale(axis.dot(vector)).scale(1 - cos));
    }
}