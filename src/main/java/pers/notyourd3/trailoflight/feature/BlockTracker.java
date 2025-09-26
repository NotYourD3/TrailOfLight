package pers.notyourd3.trailoflight.feature;


import com.google.common.collect.HashMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.references.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


import java.lang.ref.WeakReference;
import java.util.HashSet;

public class BlockTracker {
    public HashMultimap<BlockPos, Beam> locations;
    public WeakReference<Level> world;

    public BlockTracker(Level world) {
        this.world = new WeakReference<>(world);
        locations = HashMultimap.create();
    }

    public void addBeam(Beam beam) {
        HashSet<BlockPos> possible = new HashSet<>();
        Vec3 slope = beam.rotation;
        Vec3 curPos = beam.initLoc;
        boolean finished = false;
        while (!finished) {
            Vec3 nextPos = curPos.add(slope);
            for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(curPos),BlockPos.containing(nextPos)))
                possible.add(pos);
            if (curPos.distanceTo(beam.endLoc) <= curPos.distanceTo(nextPos))
                finished = true;
            curPos = nextPos;
        }

        Vec3 invSlope = new Vec3(1 / slope.x, 1 / slope.y, 1 / slope.z);

        for (BlockPos pos : possible) {
            if (collides(beam, pos, invSlope))
                locations.put(pos, beam);
        }

        locations.remove(BlockPos.containing(beam.initLoc), beam);
        locations.remove(BlockPos.containing(beam.trace.getLocation()), beam);
    }

    private boolean collides(Beam beam, BlockPos pos, Vec3 invSlope) {
        boolean signX = invSlope.x < 0;
        boolean signY = invSlope.y < 0;
        boolean signZ = invSlope.z < 0;

        double txMin, txMax, tyMin, tyMax, tzMin, tzMax;
        AABB axis = new AABB(pos);

        txMin = ((signX ? axis.maxX : axis.minX) - beam.initLoc.x) * invSlope.x;
        txMax = ((signX ? axis.minX : axis.maxX) - beam.initLoc.x) * invSlope.x;
        tyMin = ((signY ? axis.maxY : axis.minY) - beam.initLoc.y) * invSlope.y;
        tyMax = ((signY ? axis.minY : axis.maxY) - beam.initLoc.y) * invSlope.y;
        tzMin = ((signZ ? axis.maxZ : axis.minZ) - beam.initLoc.z) * invSlope.z;
        tzMax = ((signZ ? axis.minZ : axis.maxZ) - beam.initLoc.z) * invSlope.z;

        if (txMin > txMax || tyMin > txMax)
            return false;
        if (tyMin > txMin)
            txMin = tyMin;
        if (tyMax < txMax)
            txMax = tyMax;
        if (txMin > tzMax || tzMin > txMax)
            return false;
        if (tzMin > txMin)
            txMin = tzMin;
        if (tzMax < txMax)
            txMax = tzMax;

        return true;
    }
/*
    public void generateEffects() {
        if (world.get() != null) {
            for (BlockPos pos : locations.keySet()) {
                for (Beam beam : locations.get(pos)) {
                    EffectTracker.addEffect(world.get(), new Vec3(pos), beam.effect);
                }
            }
        }
        locations.clear();
    }
 */
}