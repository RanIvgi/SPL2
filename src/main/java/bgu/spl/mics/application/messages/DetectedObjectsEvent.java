package bgu.spl.mics.application.messages;
import java.util.List;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

public class DetectedObjectsEvent implements Event<Boolean> {

    private int cameraId;
    private List<DetectedObject> detectedObject;
    private int arrivalTime; //arrival to LiDar

    public DetectedObjectsEvent(int cameraId, List<DetectedObject> detectedObject, int time) {
        this.cameraId = cameraId;
        this.detectedObject = detectedObject;
        this.arrivalTime = time;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public List<DetectedObject> getDetectedObject() {
        return this.detectedObject;
    }   

    public int getTime() {
        return this.arrivalTime;
    }


}