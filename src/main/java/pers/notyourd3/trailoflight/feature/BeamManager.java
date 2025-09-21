package pers.notyourd3.trailoflight.feature;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Set;

public class BeamManager {
    public static final BeamManager INSTANCE = new BeamManager();
    public Set<Beam> beams = new java.util.HashSet<>();

    @SubscribeEvent
    public void onTick(ServerTickEvent.Pre event) {
        Set<Beam> beamsToProcess = beams;
        beams = new java.util.HashSet<>();
        for (Beam beam : beamsToProcess) {
            beam.spawn();
        }
    }

    public void addBeam(Beam beam) {
        beams.add(beam);
    }
}