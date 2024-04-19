import java.util.Arrays;

public class SuperBlock {
  private boolean[] blockUsage; // Tracks whether each block is free or in use
  private static final int TOTAL_BLOCKS = 128; // Filesystem size in MB
  protected static final int MAX_FILES = 16;

  private static final int BLOCK_SIZE = 1024; // Block size in bytes
  private Inode[] inodes; // Array of inodes for file metadata

  public SuperBlock() {
    this.blockUsage = new boolean[TOTAL_BLOCKS]; // Initialize based on total disk blocks
    Arrays.fill(this.blockUsage, false); // Assume all blocks are initially free
    this.inodes = new Inode[MAX_FILES]; // Initialize inodes array
    for (int i = 0; i < MAX_FILES; i++) {
      this.inodes[i] = new Inode(); // Initialize each inode
    }
  }

  public int allocateBlock() {
    for (int i = 0; i < this.blockUsage.length; i++) {
      if (!this.blockUsage[i]) { // Free block found
        this.blockUsage[i] = true; // Mark as in use
        return i;
      }
    }
    return -1; // No free blocks available
  }

  public boolean freeBlock(int blockNumber) {
    if (blockNumber >= 0 && blockNumber < this.blockUsage.length && this.blockUsage[blockNumber]) {
      this.blockUsage[blockNumber] = false; // Mark as free
      return true;
    }
    return false; // Block number is invalid or already free
  }

  public Inode findInode(String fileName) {
    for (Inode inode : this.inodes) {
      if (inode.isUsed() && inode.getName().equals(fileName)) {
        return inode;
      }
    }
    return null;
  }

  // Method to list all inodes (files) along with their details
  public void listInodes() {
    System.out.println("Files in the filesystem:");
    for (Inode inode : inodes) {
      if (inode.isUsed()) {
        // Assuming size is in bytes and needs conversion to KB for display,
        // or directly display if size is already in KB based on your implementation.
        int sizeInKB = inode.getSize() / 1024;
        System.out.println(
            "Name: " + inode.getName() + ", Size: " + sizeInKB + "KB, Last Modified: " + inode.getLastModifiedTime());
      }
    }
  }

  public Inode allocateInode(String fileName, int fileSizeInKB) {
    if (fileSizeInKB > TOTAL_BLOCKS / MAX_FILES) { // Assuming each file can use up to 8 blocks
      System.out.println("Error: File size exceeds maximum limit.");
      return null;
    }

    for (Inode inode : this.inodes) {
      if (!inode.isUsed()) {
        inode.setName(fileName);
        inode.setSize(fileSizeInKB * 1024); // Convert size back to bytes for storage

        int requiredBlocks = fileSizeInKB; // Each block is 1KB, so this is direct
        int startingBlock = allocateBlocks(requiredBlocks);
        if (startingBlock != -1) {
          inode.allocateBlocks(startingBlock, requiredBlocks);
          inode.setUsed(true);
          return inode;
        } else {
          System.out.println("Error: Not enough space on disk.");
          return null;
        }
      }
    }

    System.out.println("Error: Maximum file count reached.");
    return null; // No unused inodes available
  }

  public boolean releaseInode(String fileName) {
    Inode inode = findInode(fileName);
    if (inode != null && inode.isUsed()) {
      freeBlocks(inode.getStartingBlock(), inode.getBlocksAllocated()); // Free sequential blocks
      inode.setUsed(false); // Mark inode as unused
      inode.clearBlockAllocation(); // Reset block allocation info
      inode.setName("");
      inode.setSize(0);
      return true;
    }
    return false; // File not found
  }

  public int allocateBlocks(int requiredBlocks) {
    int firstFreeBlock = -1;
    int consecutiveFree = 0;
    for (int i = 0; i < blockUsage.length; i++) {
      if (!blockUsage[i]) {
        if (firstFreeBlock == -1)
          firstFreeBlock = i;
        if (++consecutiveFree == requiredBlocks) {
          for (int j = firstFreeBlock; j < firstFreeBlock + requiredBlocks; j++) {
            blockUsage[j] = true;
          }
          return firstFreeBlock;
        }
      } else {
        firstFreeBlock = -1;
        consecutiveFree = 0;
      }
    }
    return -1;
  }

  public void freeBlocks(int startingBlock, int numberOfBlocks) {
    for (int i = startingBlock; i < startingBlock + numberOfBlocks; i++) {
      blockUsage[i] = false;
    }
  }

  // Utility methods
  public Inode[] getInodes() {
    return inodes;
  }

  public int countFreeBlocks() {
    int count = 0;
    for (boolean used : blockUsage) {
      if (!used)
        count++;
    }
    return count;
  }

  public int countUsedInodes() {
    int count = 0;
    for (Inode inode : inodes) {
      if (inode.isUsed())
        count++;
    }
    return count;
  }

  public String listFileSystem() {
    StringBuilder representation = new StringBuilder("[");
    for (int i = 0; i < blockUsage.length; i++) {
      if (i > 0 && i % 16 == 0) { // Insert a new line after every 16 blocks
        representation.append("\n ");
      }
      representation.append(blockUsage[i] ? "1" : "0");
      if (i < blockUsage.length - 1) {
        representation.append(" "); // Add space between block indicators
      }
    }
    representation.append("]");
    return representation.toString();
  }

}
