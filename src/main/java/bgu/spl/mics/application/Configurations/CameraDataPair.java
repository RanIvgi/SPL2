package bgu.spl.mics.application.Configurations;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class CameraDataPair 
{
    private String cameraKey;
    private List<StampedDetectedObjects> data;

    public CameraDataPair(String cameraKey, List<StampedDetectedObjects> data)
    {
        this.cameraKey=cameraKey;
        this.data = data;
    }
    public CameraDataPair(String cameraKey)
    {
        this.cameraKey=cameraKey;
        this.data = new LinkedList<>();
    }

    public String getCameraKey()
    {
        return cameraKey;
    }

    public List<StampedDetectedObjects> getData()
    {
        return data;
    }

    public void addToData(StampedDetectedObjects d)
    {
        data.add(d);
    }
    

}
