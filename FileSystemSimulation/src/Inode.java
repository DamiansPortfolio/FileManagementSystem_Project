import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Inode {
  public static final int MAX_BLOCKS = 8; // Maximum number of blocks a single file can occupy
  private String name; // File name
  private int size; // File size in bytes
  private int startingBlock = -1; // Starting block of the file's data
  private int blocksAllocated = 0; // Number of blocks allocated to the file
  private String lastModifiedTime; // Last modification time as a string
  private boolean used; // Indicates whether the inode is in use

  public Inode() {
    this.name = "";
    this.size = 0;
    this.lastModifiedTime = getCurrentTime(); // Initialize with the current time
    this.used = false;
  }

  // Utility method to get the current time as a string
  private String getCurrentTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    return dtf.format(LocalDateTime.now());
  }

  // Method to allocate blocks to this inode
  public void allocateBlocks(int startingBlock, int blocksAllocated) {
    this.startingBlock = startingBlock;
    this.blocksAllocated = blocksAllocated;
    updateModifiedTime();
  }

  // Method to clear the block allocation when a file is deleted
  public void clearBlockAllocation() {
    this.startingBlock = -1;
    this.blocksAllocated = 0;
    updateModifiedTime();
  }

  // Update the last modified time to the current time
  private void updateModifiedTime() {
    this.lastModifiedTime = getCurrentTime();
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name.length() <= 10) {
      this.name = name;
      updateModifiedTime();
    } else {
      System.out.println("Error: File name exceeds 10 characters limit.");
    }
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
    updateModifiedTime();
  }

  public int getStartingBlock() {
    return startingBlock;
  }

  // Note: Setting starting block directly is not exposed as a setter to maintain
  // encapsulation

  public int getBlocksAllocated() {
    return blocksAllocated;
  }

  // Note: Setting blocks allocated directly is not exposed as a setter to
  // maintain encapsulation

  public String getLastModifiedTime() {
    return lastModifiedTime;
  }

  // Note: Setting the last modified time directly is not provided to ensure it's
  // always accurate

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
    updateModifiedTime();
  }
}
