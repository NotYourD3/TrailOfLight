package pers.notyourd3.trailoflight.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import pers.notyourd3.trailoflight.block.entity.ILaserTrace;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Copied from Wizardry
 */
public class RayTrace {

    private final Level world;
    private final Vec3 slope;
    private final Vec3 origin;
    private final double range;

    private boolean skipBlocks = false;
    private boolean returnLastUncollidableBlock = true;
    private boolean skipEntities = false;
    private boolean ignoreBlocksWithoutBoundingBoxes = false;
    private Predicate<Entity> predicate;
    private Predicate<Block> predicateBlock;
    private HashSet<BlockPos> skipBlockList = new HashSet<>();

    public RayTrace(@Nonnull Level world, @Nonnull Vec3 slope, @Nonnull Vec3 origin, double range) {
        this.world = world;
        this.slope = slope;
        this.origin = origin;
        this.range = range;
    }

    public RayTrace addBlockToSkip(@Nonnull BlockPos pos) {
        skipBlockList.add(pos);
        return this;
    }

    public RayTrace setEntityFilter(Predicate<Entity> predicate) {
        this.predicate = predicate;
        return this;
    }

    public RayTrace setBlockFilter(Predicate<Block> predicate) {
        this.predicateBlock = predicate;
        return this;
    }

    public RayTrace setReturnLastUncollidableBlock(boolean returnLastUncollidableBlock) {
        this.returnLastUncollidableBlock = returnLastUncollidableBlock;
        return this;
    }

    public RayTrace setIgnoreBlocksWithoutBoundingBoxes(boolean ignoreBlocksWithoutBoundingBoxes) {
        this.ignoreBlocksWithoutBoundingBoxes = ignoreBlocksWithoutBoundingBoxes;
        return this;
    }

    public RayTrace setSkipEntities(boolean skipEntities) {
        this.skipEntities = skipEntities;
        return this;
    }

    private boolean isOrigin(BlockPos pos) {
        return (BlockPos.containing(origin).equals(pos));
    }

    /**
     * Credits to Masa on discord for providing the base of the code. I heavily modified it.
     * This raytracer will precisely trace entities and blocks (including misses) without snapping to any grid.
     *
     * @return The RaytraceResult.
     */
    @Nonnull
    public HitResult trace() {
        Vec3 lookVec = origin.add(slope.scale(range));

        HitResult entityResult = null;
        HitResult blockResult;// world.rayTraceBlocks(origin, lookVec, false, ignoreBlocksWithoutBoundingBoxes, returnLastUncollidableBlock);


        if (!skipEntities) {
            Entity targetEntity = null;
            Vec3 locationTmp = null;
            AABB bb = new AABB(origin.x, origin.y, origin.z, lookVec.x, lookVec.y, lookVec.z);
            List<Entity> list = world.getEntities((Entity) null, bb.expandTowards(range, range, range), input -> {
                if (predicate == null) return true;
                else return predicate.test(input);
            });
            double closest = 0.0D;

            for (Entity entity : list) {
                if (entity == null) continue;

                bb = entity.getBoundingBox();
                Optional<Vec3> traceTmp = bb.clip(lookVec, origin);

                if (traceTmp.isPresent()) {
                    double tmp = origin.distanceTo(traceTmp.get());

                    if (tmp < closest || closest == 0.0D) {
                        targetEntity = entity;
                        locationTmp = traceTmp.get();
                        closest = tmp;
                    }
                }
            }

            if (targetEntity != null) entityResult = new EntityHitResult(targetEntity, locationTmp);
        }

        blockResult = traceBlock(origin, lookVec);
        if (blockResult == null)
            blockResult = new BlockHitResult(
                    lookVec,
                    getDirectionFromVector(lookVec),
                    BlockPos.containing(lookVec),
                    false);

        return (entityResult != null && origin.distanceTo(entityResult.getLocation()) < origin.distanceTo(blockResult.getLocation())) ? entityResult : blockResult;
    }

    private HitResult traceBlock(@Nonnull Vec3 start, @Nonnull Vec3 end) {
        HitResult raytraceresult2 = null;
        BlockPos lastUncollidablePos = null;
        int i = Mth.floor(end.x);
        int j = Mth.floor(end.y);
        int k = Mth.floor(end.z);
        int l = Mth.floor(start.x);
        int i1 = Mth.floor(start.y);
        int j1 = Mth.floor(start.z);

        int k1 = 200;

        while (k1-- >= 0) {
            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = end.x - start.x;
            double d7 = end.y - start.y;
            double d8 = end.z - start.z;

            if (flag2) {
                d3 = (d0 - start.x) / d6;
            }

            if (flag) {
                d4 = (d1 - start.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - start.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            Direction enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? Direction.WEST : Direction.EAST;
                start = new Vec3(d0, start.y + d7 * d3, start.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? Direction.DOWN : Direction.UP;
                start = new Vec3(start.x + d6 * d4, d1, start.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? Direction.NORTH : Direction.SOUTH;
                start = new Vec3(start.x + d6 * d5, start.y + d7 * d5, d2);
            }

            l = Mth.floor(start.x) - (enumfacing == Direction.EAST ? 1 : 0);
            i1 = Mth.floor(start.y) - (enumfacing == Direction.UP ? 1 : 0);
            j1 = Mth.floor(start.z) - (enumfacing == Direction.SOUTH ? 1 : 0);

            BlockPos targetPos = new BlockPos(l, i1, j1);
            if (!world.isLoaded(targetPos)) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }
            BlockState targetState = world.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();


            if (!isOrigin(targetPos)) {
                if (skipBlockList.contains(targetPos) || (predicateBlock != null && !predicateBlock.test(targetBlock))) {
                    continue;
                }
                VoxelShape collisionShape = targetState.getCollisionShape(world, targetPos);
                if (!ignoreBlocksWithoutBoundingBoxes || !collisionShape.isEmpty()) {
                    if (!collisionShape.isEmpty()) {
                        HitResult hitResult = targetBlock instanceof ILaserTrace ?
                                ((ILaserTrace) targetBlock).collisionRayTraceLaser(targetState, world, targetPos, start, end) :
                                collisionShape.clip(start, end, targetPos);
                        if (hitResult != null) {
                            return hitResult;
                        }
                    } else {

                        lastUncollidablePos = targetPos;
                    }
                }
            }
        }

        if (returnLastUncollidableBlock && lastUncollidablePos != null) {

            return new BlockHitResult(new Vec3(lastUncollidablePos.getX(), lastUncollidablePos.getY(), lastUncollidablePos.getZ()),
                    getDirectionFromVector(start),
                    lastUncollidablePos,
                    false);
        }


        return null;
    }

    public Direction getDirectionFromVector(Vec3 vec) {
        float absX = Math.abs((float) vec.x);
        float absY = Math.abs((float) vec.y);
        float absZ = Math.abs((float) vec.z);

        if (absX > absY && absX > absZ) {
            return vec.x > 0 ? Direction.EAST : Direction.WEST;
        } else if (absY > absX && absY > absZ) {
            return vec.y > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return vec.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }
}