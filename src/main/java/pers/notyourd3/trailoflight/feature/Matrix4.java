package pers.notyourd3.trailoflight.feature;

import net.minecraft.world.phys.Vec3;

/**
 * Original code from Librarianlib,here only keeps the necessary parts.
 */
public class Matrix4 {
    public double m00;
    public double m01;
    public double m02;
    public double m03;
    public double m10;
    public double m11;
    public double m12;
    public double m13;
    public double m20;
    public double m21;
    public double m22;
    public double m23;
    public double m30;
    public double m31;
    public double m32;
    public double m33;

    public Matrix4() {
        m00 = 1;
        m11 = 1;
        m22 = 1;
        m33 = 1;
    }

    public Matrix4 rotate(double angle, Vec3 axis) {
        if (angle == 0.0) return this;

        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mc = 1.0f - c;
        double xy = axis.x * axis.y;
        double yz = axis.y * axis.z;
        double xz = axis.x * axis.z;
        double xs = axis.x * s;
        double ys = axis.y * s;
        double zs = axis.z * s;

        double f00 = axis.x * axis.x * mc + c;
        double f10 = xy * mc + zs;
        double f20 = xz * mc - ys;

        double f01 = xy * mc - zs;
        double f11 = axis.y * axis.y * mc + c;
        double f21 = yz * mc + xs;

        double f02 = xz * mc + ys;
        double f12 = yz * mc - xs;
        double f22 = axis.z * axis.z * mc + c;

        double t00 = m00 * f00 + m01 * f10 + m02 * f20;
        double t10 = m10 * f00 + m11 * f10 + m12 * f20;
        double t20 = m20 * f00 + m21 * f10 + m22 * f20;
        double t30 = m30 * f00 + m31 * f10 + m32 * f20;
        double t01 = m00 * f01 + m01 * f11 + m02 * f21;
        double t11 = m10 * f01 + m11 * f11 + m12 * f21;
        double t21 = m20 * f01 + m21 * f11 + m22 * f21;
        double t31 = m30 * f01 + m31 * f11 + m32 * f21;
        m02 = m00 * f02 + m01 * f12 + m02 * f22;
        m12 = m10 * f02 + m11 * f12 + m12 * f22;
        m22 = m20 * f02 + m21 * f12 + m22 * f22;
        m32 = m30 * f02 + m31 * f12 + m32 * f22;
        m00 = t00;
        m10 = t10;
        m20 = t20;
        m30 = t30;
        m01 = t01;
        m11 = t11;
        m21 = t21;
        m31 = t31;

        return this;
    }

    public Vec3 apply(Vec3 vec) {
        double x = vec.x * m00 + vec.y * m01 + vec.z * m02 + m03;
        double y = vec.x * m10 + vec.y * m11 + vec.z * m12 + m13;
        double z = vec.x * m20 + vec.y * m21 + vec.z * m22 + m23;

        return new Vec3(x, y, z);
    }
}
