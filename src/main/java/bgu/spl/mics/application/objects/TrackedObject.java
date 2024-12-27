package bgu.spl.mics.application.objects;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    // TODO: Define fields and methods.

    private String id;
    private int time;
    private String description;
    private CloudPoint[] coordinates;

    public TrackedObject(String id, int time, String description, CloudPoint[] coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public CloudPoint[] getCoordinates() {
        return coordinates;
    }

    public int getTime() {
        return time;
    }
}
