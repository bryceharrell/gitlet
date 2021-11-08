package gitlet;

import java.util.HashMap;

public class StagingArea {

    private HashMap<String, String> addMap;
    private HashMap<String, String> removeMap;

    public StagingArea() {
        this.addMap = new HashMap<String, String>();
        this.removeMap = new HashMap<String, String>();
    }

    public HashMap<String, String> getAddMap() {
        return this.addMap;
    }

    public HashMap<String, String> getRemoveMap() {
        return this.removeMap;
    }

}
