package gitlet;



import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import static gitlet.Utils.*;
import java.util.Set;

/** Represents a gitlet commit object.
 *  Holds an individual snapshot of files at a given time
 *
 *  @author Bryce Harrell
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** Represents the timestamp where this commit was created. */
    private Date timestamp;
    /** The String of the hashcode of the first parent commit */
    private String parent;
    /** HashMap of all file names and blobs in commit
     * Key: file name, Value: blob hash*/
    private HashMap<String, String> commitMap;
    /** Sha1 Hash for the commit */
    private String sha1;
    /** The String of the hashcode of the second parent commit **/
    private String secondParent;


    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parent = null;
        this.secondParent = null;
        this.commitMap = new HashMap<>();
        this.sha1 = sha1(serialize(this));
    }

    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        this.secondParent = null;
        this.timestamp = new Date();
        this.commitMap = createCurrentCommitMap();
        this.sha1 = sha1(serialize(this));
    }

    public Commit(String message, String parent, String secondParent) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.timestamp = new Date();
        this.commitMap = createMergeCommitMap();
        this.sha1 = sha1(serialize(this));

    }


    public HashMap<String, String> getParentCommitMap() {
        if (this.parent == null) {
            return null;
        }
        File parentCommitFile = join(Repository.COMMITS_DIR, this.parent);
        Commit parentCommit = readObject(parentCommitFile, Commit.class);
        return parentCommit.getCommitMap();
    }

    public HashMap<String, String> getSecondParentCommitMap() {
        if (this.secondParent == null) {
            return null;
        }
        File secondParentCommitFile = join(Repository.COMMITS_DIR, this.secondParent);
        Commit secondParentCommit = readObject(secondParentCommitFile, Commit.class);
        return secondParentCommit.getCommitMap();
    }

    //might have to remove all things from commitMap that are in removeStagingArea
    public HashMap<String, String> createCurrentCommitMap() {
        HashMap<String, String> currentCommitMap = new HashMap<>();
        if (getParentCommitMap() != null) {
            currentCommitMap.putAll(getParentCommitMap());
        }
        if (getSecondParentCommitMap() != null) {
            currentCommitMap.putAll(getSecondParentCommitMap());
        }
        HashMap<String, String> addStagingArea =
                readObject(Repository.STAGED_FOR_ADDITION, HashMap.class);
        HashMap<String, String> removeStagingArea =
                readObject(Repository.STAGED_FOR_REMOVAL, HashMap.class);
        currentCommitMap.putAll(addStagingArea);
        if (!removeStagingArea.isEmpty()) {
            Set<String> removeStagingAreaKeys = removeStagingArea.keySet();
            String[] removeStagingAreaKeysArray = removeStagingAreaKeys.
                    toArray(new String[removeStagingAreaKeys.size()]);
            for (int i = 0; i < removeStagingAreaKeysArray.length; i++) {
                currentCommitMap.remove(removeStagingAreaKeysArray[i]);
            }
        }
        return currentCommitMap;
    }

    public HashMap<String, String> createMergeCommitMap() {
        HashMap<String, String> mergeCommitMap = new HashMap<>();
        HashMap<String, String> addStagingArea =
                readObject(Repository.STAGED_FOR_ADDITION, HashMap.class);
        HashMap<String, String> removeStagingArea =
                readObject(Repository.STAGED_FOR_REMOVAL, HashMap.class);
        HashMap<String, String> firstParentCommitMap = getParentCommitMap();
        mergeCommitMap.putAll(firstParentCommitMap);
        mergeCommitMap.putAll(addStagingArea);
        if (!removeStagingArea.isEmpty()) {
            Set<String> removeStagingAreaKeys = removeStagingArea.keySet();
            String[] removeStagingAreaKeysArray = removeStagingAreaKeys.
                    toArray(new String[removeStagingAreaKeys.size()]);
            for (int i = 0; i < removeStagingAreaKeysArray.length; i++) {
                mergeCommitMap.remove(removeStagingAreaKeysArray[i]);
            }
        }
        return mergeCommitMap;
    }



    public String getMessage() {
        return this.message;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parent;
    }
    public String getSecondParent() {
        return this.secondParent;
    }

    public HashMap<String, String> getCommitMap() {
        return this.commitMap;
    }

    public String getSha1() {
        return this.sha1;
    }
}
