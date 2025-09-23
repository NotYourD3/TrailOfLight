package pers.notyourd3.trailoflight.feature;


import net.minecraft.world.phys.Vec3;

/**
 * Created by TheCodeWarrior
 */
public class Tri {

    public Vec3 a, b, c;

    public Tri(Vec3 a, Vec3 b, Vec3 c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    private static Vec3 intersectLineTriangle(Vec3 start, Vec3 end, Vec3 v1, Vec3 v2, Vec3 v3) {
        final double EPSILON = 1.0e-3;
        Vec3 dir = end.subtract(start).normalize();

        Vec3 edge1 = v2.subtract(v1);
        Vec3 edge2 = v3.subtract(v1);

        Vec3 pVec = dir.cross(edge2);

        double det = edge1.dot(pVec);
        if (det > -EPSILON && det < EPSILON)
            return null;
        double invDet = 1.0 / det;

        Vec3 tVec = start.subtract(v1);
        double u = tVec.dot(pVec) * invDet;
        if (u < 0 || u > 1)
            return null;

        Vec3 qVec = tVec.cross(edge1);
        double v = dir.dot(qVec) * invDet;
        if (v < 0 || u + v > 1)
            return null;

        double t = edge2.dot(qVec) * invDet;

        return start.add(dir.scale(t));

//		// Bring points to their respective coordinate frame
//		Vec3 pq = end.subtract(exciterPos);
//		Vec3 pa = end.subtract(a);
//		Vec3 pb = end.subtract(b);
//		Vec3 pc = end.subtract(c);
//
//		Vec3 m = pq.crossProduct(pc);
//
//		double u = pb.dotProduct(m);
//		double v = -pa.dotProduct(m);
//
//		if (Math.signum(u) != Math.signum(v)) {
//			return null;
//		}
//
//		// scalar triple product
//		double w = pq.dotProduct(pb.crossProduct(pa));
//
//		if (Math.signum(u) != Math.signum(w)) {
//			return null;
//		}
//
//		double denom = 1.0 / (u + v + w);
//
//		// r = ((u * denom) * a) + ((v * denom) * b) + ((w * denom) * c);
//		Vec3 compA = a.scale(u*denom);
//		Vec3 compB = b.scale(v*denom);
//		Vec3 compC = c.scale(w*denom);
//
//		// store result in Vector r
//		double x = compA.xCoord + compB.xCoord + compC.xCoord;
//		double y = compA.yCoord + compB.yCoord + compC.yCoord;
//		double z = compA.zCoord + compB.zCoord + compC.zCoord;
//
//		return new Vec3(x, y, z);
    }

    public Vec3 normal() {
        return (b.subtract(a)).cross(c.subtract(a));
    }

    public Vec3 trace(Vec3 start, Vec3 end) {
        return intersectLineTriangle(start, end, a, b, c);
    }
}