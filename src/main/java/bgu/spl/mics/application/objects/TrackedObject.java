package bgu.spl.mics.application.objects;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    // TODO: Define fields and methods.

    private int id;
    private int time;
    private String description;
    private CloudPoint[] coordinates;

    public TrackedObject(int id, int time, String description, CloudPoint[] coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }
    
    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public CloudPoint[] getCoordinates() {
        return coordinates;
    }
}
