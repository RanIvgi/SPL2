package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.messages.PoseEvent;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents
 * from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private FusionSlam fusionSlam; // The FusionSLAM object responsible for managing the global map.
    
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global
     *                   map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and
     * TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // TickEvent - TrackedObjectEvent
        subscribeEvent(TrackedObjectEvent.class, (TrackedObjectEvent TrackedObjectEvent) -> {
            this.fusionSlam.checkReadyToProcess(TrackedObjectEvent); // Check if the tracked object is ready to be processed.
            this.fusionSlam.getTrackedObjectsReciv().add(TrackedObjectEvent); // Add the new tracked object event to the list.
        });

        // TickEvent - PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent PoseEvent) -> {
            this.fusionSlam.checkReadyToProcess(PoseEvent.getPose()); // Check if the pose is ready to be processed.
            this.fusionSlam.getPoses().add(PoseEvent.getPose()); // Add the new pose to the list.
        });

        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            // Process all the ready to process pairs. TODO: האם צריך לעשות את זה כל זמן שיש מה לעשות ?
            while (!this.fusionSlam.isReadyToProcessPairsEmpty()) {
                ReadyToProcessPair<Pose, TrackedObjectEvent> toProcess = this.fusionSlam.getReadyToProcessPairs().remove(0);
                // Process the pair of pose and every tracked object.
                this.fusionSlam.ProcessReadyToProcessPair(toProcess);
            }
        });

        // TerminatedBroadCast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // TODO: Handle the case where other service was terminated.
            // terminate();
        });

        // CrashedBroadCast
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service crashed.
            terminate();
        });
    }
}
