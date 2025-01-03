package bgu.spl.mics.application.Configurations;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.objects.DetectedObject;

/**
 * This class reads camera data from a JSON file and prints the detected objects for each camera and time frame.
 */
public class ReadCameraData {

    public ReadCameraData()
    {
    }
    public List<CameraDataPair> cameraDataJsonToList(String filePath) {
        List<CameraDataPair> cameras = new LinkedList<>();
        try (InputStream is = new FileInputStream(filePath)) {
            // Create a JSONTokener to parse the JSON file
            JSONTokener tokener = new JSONTokener(is);
            // Create a JSONObject from the tokener
            JSONObject jsonObject = new JSONObject(tokener);

            // Iterate over each camera in the JSON file
            for (String cameraKey : jsonObject.keySet()) {
                CameraDataPair newCamera = new CameraDataPair(cameraKey);
                // Get the data array for the current camera
                JSONArray cameraData = jsonObject.getJSONArray(cameraKey);
                // Iterate over each entry in the camera data array
                for (int i = 0; i < cameraData.length(); i++) {
                    // Get the current entry as a JSONObject
                    JSONObject entry = cameraData.getJSONObject(i);
                    // Get the time value from the entry
                    int time = entry.getInt("time");
                    // Get the detected objects array from the entry
                    JSONArray detectedObjects = entry.getJSONArray("detectedObjects");
                    //StampedDetectedObject to add to cameras list
                    List<DetectedObject> toAdd = new LinkedList<>();

                    // Iterate over each detected object in the detected objects array
                    for (int j = 0; j < detectedObjects.length(); j++) {
                        // Get the current detected object as a JSONObject
                        JSONObject detectedObject = detectedObjects.getJSONObject(j);
                        // Get the id and description of the detected object
                        String currID = detectedObject.getString("id");
                        String description = detectedObject.getString("description");
                        toAdd.add(new DetectedObject(currID, description));
                    }
                    if (!toAdd.isEmpty())
                    {
                        newCamera.addToData(new StampedDetectedObjects(time, toAdd));
                    }
                }
                cameras.add(newCamera);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cameras;
    }

    public List<StampedDetectedObjects> readCameraData(List<CameraDataPair> allData, String name)
    {
        for (CameraDataPair c : allData)
        {
            if (c.getCameraKey().equals(name))
            {
                return c.getData();
            }
        }
        return null;
    }

    //public static void main(String[] args) {
        /* 
        // Path to the JSON file containing camera data
        String filePath = "C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json";
        try (InputStream is = new FileInputStream(filePath)) {
            // Create a JSONTokener to parse the JSON file
            JSONTokener tokener = new JSONTokener(is);
            // Create a JSONObject from the tokener
            JSONObject jsonObject = new JSONObject(tokener);

            // Iterate over each camera in the JSON file
            for (String cameraKey : jsonObject.keySet()) {
                System.out.println("Camera: " + cameraKey);
                // Get the data array for the current camera
                JSONArray cameraData = jsonObject.getJSONArray(cameraKey);
                // Iterate over each entry in the camera data array
                for (int i = 0; i < cameraData.length(); i++) {
                    // Get the current entry as a JSONObject
                    JSONObject entry = cameraData.getJSONObject(i);
                    // Get the time value from the entry
                    int time = entry.getInt("time");
                    // Get the detected objects array from the entry
                    JSONArray detectedObjects = entry.getJSONArray("detectedObjects");

                    System.out.println("  Time: " + time);
                    // Iterate over each detected object in the detected objects array
                    for (int j = 0; j < detectedObjects.length(); j++) {
                        // Get the current detected object as a JSONObject
                        JSONObject detectedObject = detectedObjects.getJSONObject(j);
                        // Get the id and description of the detected object
                        String id = detectedObject.getString("id");
                        String description = detectedObject.getString("description");
                        System.out.println("    Detected Object ID: " + id + ", Description: " + description);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        */
    //}

   
    public static void main(String[] args) {
        
        //List<CameraDataPair> allData = cameraDataJsonToList("C:\\Users\\alona\\SPL2\\example_input_2\\camera_data.json");
        /*for (int i=0; i < allData.size(); i++) {
            CameraDataPair p = allData.get(i);
            System.out.println("Camera:" + p.getCameraKey());
            for (int j=0; j<p.getData().size(); j++) {
                StampedDetectedObjects d = p.getData().get(j);
                System.out.println("time: " + d.getTime());
                for (int k=0; k<d.getDetectedObject().size(); k++) {
                    DetectedObject det = d.getDetectedObject().get(k);
                    System.out.print("id: " + det.getID() + ", description: " + det.getDescreption());
                }
                System.out.println();

            }
            System.out.println();
        }
        List<StampedDetectedObjects> l = readCameraData(allData, "camera1");
        for (int j=0; j<l.size(); j++) {
            System.out.println("time: " + l.get(j).getTime());
            for (int k=0; k<l.get(j).getDetectedObject().size(); k++) {
                DetectedObject det = l.get(j).getDetectedObject().get(k);
                System.out.print("id: " + det.getID() + ", description: " + det.getDescreption());
            }
            System.out.println();

        }*/

    }
}


