package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position
 * and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    GPSIMU gpsimu;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the
     * current pose.
     */
    @Override
    protected void initialize() {
        // TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            PoseEvent poseEvent = new PoseEvent(this.gpsimu.getCurrentPose(tick.getTick())); // Create a new PoseEvent with the current pose
            Future<Boolean> f = sendEvent(poseEvent); // Send the PoseEvent
            Boolean result = null;
            if (f != null) {
                result = f.get(100, java.util.concurrent.TimeUnit.MILLISECONDS); // Wait for the result of the PoseEvent
            }
            // Do We need to check if the result is null????????
            if (result == null || !result) { // If the PoseEvent was not completed successfully
                sendBroadcast(new CrashedBroadcast("PoseService")); // Broadcast a Crashed message
                terminate(); // Terminate the service
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // TODO: Handle the case where other service was terminated.

            // if the terminated service is TimeService, terminate the PoseService.
            if (terminated.getSenderId().equals("TimeService")) {
                sendBroadcast(new TerminatedBroadcast("PoseService"));
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            sendBroadcast(new TerminatedBroadcast("PoseService"));
            terminate();
        });
    }
}
