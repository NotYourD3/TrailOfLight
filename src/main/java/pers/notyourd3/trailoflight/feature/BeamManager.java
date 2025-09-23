package pers.notyourd3.trailoflight.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import pers.notyourd3.trailoflight.block.IBeamHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeamManager {
    public static final BeamManager INSTANCE = new BeamManager();
    public Set<Beam> beams = new java.util.HashSet<>();
    private Map<BlockPos, Level> lastTickActivatedBlocks = new HashMap<>();

    @SubscribeEvent
    public void onTick(ServerTickEvent.Pre event) {
        Set<Beam> beamsToProcess = beams;
        beams = new HashSet<>();

        // 追踪本刻被激活的方块
        Map<BlockPos, Level> currentTickActivatedBlocks = new HashMap<>();

        for (Beam beam : beamsToProcess) {
            beam.spawn();
            // 假设你的 onBeam 方法会更新一个内部状态，或者你在这里捕捉光线追踪结果
            if (beam.trace instanceof BlockHitResult) {
                BlockPos pos = BlockPos.containing(beam.endLoc);
                BlockState state = beam.level.getBlockState(pos);
                if (state.getBlock() instanceof IBeamHandler) {
                    currentTickActivatedBlocks.put(pos, beam.level);
                }
            }
        }

        // 找出上一刻被激活但本刻没有的方块
        Set<BlockPos> toDeactivate = new HashSet<>(lastTickActivatedBlocks.keySet());
        toDeactivate.removeAll(currentTickActivatedBlocks.keySet());

        for (BlockPos pos : toDeactivate) {
            Level level = lastTickActivatedBlocks.get(pos);
            if (level != null) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof IBeamHandler handler) {
                    handler.onBeamRemoved(level, pos);
                }
            }
        }

        this.lastTickActivatedBlocks = currentTickActivatedBlocks;
    }

    public void addBeam(Beam beam) {
        beams.add(beam);
    }
}