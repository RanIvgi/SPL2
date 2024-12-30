package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.SaveStateFolder;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 *
 * This service interacts with the LiDarTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker liDarTracker;
    private List<DetectedObjectsEvent> waitingDetectedObjectsEvents;
    private int currentTick;
    private int cameraAmount;
    private final CountDownLatch lanch;

    /**
     * Constructor for LiDarService.
     *
     * @param liDarTracker The LiDAR tracker object that this service will use
     *                     to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarTracker, int cameraAmount, CountDownLatch lanch) {
        super("LiDarService");
        this.liDarTracker = liDarTracker;
        this.waitingDetectedObjectsEvents = new LinkedList<DetectedObjectsEvent>();
        this.currentTick = 0;
        this.cameraAmount = cameraAmount;
        this.lanch = lanch;
    }

    /**
     * //TODO: write what happend here
     * 
     * @param detObj
     */
    public void prepareAndToSend(DetectedObjectsEvent detObj) {
        // create a new TrackedObjectEvent
        TrackedObjectEvent event = new TrackedObjectEvent(String.valueOf(liDarTracker.getID()), detObj.getTime());
        for (int j = 0; j < detObj.getDetectedObject().size(); j++) {
            TrackedObject trackedObject = liDarTracker.getTrackedObject(
                    detObj.getDetectedObject().get(j),
                    detObj.getTime());
            // if the tracked object is not null, add it to the event
            if (trackedObject != null) {
                event.addTrackedObject(trackedObject);
            } else if (liDarTracker.getStatus() == STATUS.ERROR) {
                // if the status of the LiDarTracker is ERROR, terminate the LiDarService
                sendBroadcast(new CrashedBroadcast("LiDar",
                        "Sensor LidarWorker " + this.liDarTracker.getID() + " disconnected"));
                terminate();
            }
        }
        // Save State
        SaveStateFolder.getInstance().updateLidarWorker(event);
        // increment the number of tracked objects in the statistical folder
        StatisticalFolder.getInstance().incrementNumTrackedObjects(event.getTrackedObject().size());
        // complete the DetectedObjectsEvent
        MessageBusImpl.getInstance().complete(detObj, true);
    }

    /**
     * Check if need to termenate the LiDar Worker
     */
    public void checkIfFinish() {
        // Assuming the list is sorted by time
        if (cameraAmount == 0 && this.waitingDetectedObjectsEvents.isEmpty()) {
            this.liDarTracker.setStatus(STATUS.DOWN);
        }
    }

    /**
     * End the Thread
     */
    public void endLiDarWorker() {
        sendBroadcast(new TerminatedBroadcast("LiDarWorker"));
        terminate();
    }

    /**
     * Initializes the LiDarService. Registers the service to handle
     * DetectObjectsEvents and TickBroadcasts, and sets up the necessary
     * callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectedObjectsEvent.class, (DetectedObjectsEvent detectedObjectsEvent) -> {
            if (this.liDarTracker.getStatus() == STATUS.UP) {
                if (currentTick >= detectedObjectsEvent.getTime() + liDarTracker.getFrequency()) {
                    prepareAndToSend(detectedObjectsEvent);
                } else {
                    waitingDetectedObjectsEvents.add(detectedObjectsEvent);
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (this.liDarTracker.getStatus() == STATUS.UP) {
                this.currentTick = tick.getTick();
                // by going from the last link to the first, we make sure that removing funcion
                // doesn't change the order of the next indexes.
                for (int i = waitingDetectedObjectsEvents.size(); i > 0; i--) {
                    if (tick.getTick() >= waitingDetectedObjectsEvents.get(i).getTime() + liDarTracker.getFrequency()) {
                        prepareAndToSend(waitingDetectedObjectsEvents.get(i));
                        waitingDetectedObjectsEvents.remove(i);
                    }
                    this.checkIfFinish();
                    // If finish end service
                    if (this.liDarTracker.getStatus() == STATUS.DOWN) {
                        this.endLiDarWorker();
                    }
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            // if the terminated service is TimeService, terminate the LiDarService.
            if (terminated.getSenderId().equals("TimeService")) {
                this.liDarTracker.setStatus(STATUS.DOWN);
                this.endLiDarWorker();
            }
            // if camera is finished
            if (terminated.getSenderId().equals("Camera")) {
                this.cameraAmount--;
            }
            this.checkIfFinish();
            // If finish end service
            if (this.liDarTracker.getStatus() == STATUS.DOWN) {
                this.endLiDarWorker();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            // TODO: Handle the case where other service was Crashed.
            terminate();
        });

        lanch.countDown();
    }
}
