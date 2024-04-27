import java.util.Arrays;

public class Disk {
  private static final int BLOCK_SIZE = 1024;
  private static final int DISK_SIZE_BYTE = 128 * BLOCK_SIZE;
  private static final int TOTAL_BLOCKS = (DISK_SIZE_BYTE / BLOCK_SIZE);
  private Block[] blocks;
  private SuperBlock superBlock;
  private Directory rootDirectory;
  private boolean[] blocksUsed;

  public Disk() {
    this.blocks = new Block[TOTAL_BLOCKS];
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      blocks[i] = new Block();
    }
    this.superBlock = new SuperBlock();
    this.rootDirectory = new Directory("root", this.superBlock, null); // Pass null as the parent for root
    this.blocksUsed = new boolean[TOTAL_BLOCKS];
    Arrays.fill(this.blocksUsed, false);
  }

  public int allocateBlocks(int size) {
    for (int i = 0; i <= TOTAL_BLOCKS - size; i++) {
      if (areBlocksFree(i, size)) {
        markBlocksUsed(i, size, true);
        return i;
      }
    }
    return -1;
  }

  private void markBlocksUsed(int start, int size, boolean used) {
    for (int i = start; i < start + size; i++) {
      blocksUsed[i] = used;
    }
  }

  private boolean areBlocksFree(int start, int size) {
    for (int i = start; i < start + size; i++) {
      if (blocksUsed[i]) {
        return false;
      }
    }
    return true;
  }

  public void freeBlocks(int start, int size) {
    markBlocksUsed(start, size, false);
  }

  public SuperBlock getSuperBlock() {
    return this.superBlock;
  }

  public boolean writeFile(String command) {
    String[] parts = command.split(" ");
    if (parts.length != 4) {
      System.out.println("Error: Invalid command format.");
      return false;
    }

    String option = parts[1]; // "-a" (append) or "-r" (reduce)
    String filename = parts[2];
    int sizeChangeKB = Integer.parseInt(parts[3]); // Size to change in KB

    Inode inode = superBlock.findInode(filename); // Assuming superBlock is accessible here

    if (inode == null) {
      System.out.println("Error: File not found.");
      return false;
    }

    int currentSizeKB = inode.getSize() / 1024;
    int newSizeKB;

    if (option.equals("-a")) {
      newSizeKB = currentSizeKB + sizeChangeKB;
      if (newSizeKB > 8) { // Max total size check (8 KB)
        System.out.println("Error: File size cannot exceed 8 KB.");
        return false;
      }
    } else if (option.equals("-r")) {
      newSizeKB = currentSizeKB - sizeChangeKB;
      if (newSizeKB < 0) { // Min size check
        System.out.println("Error: File size cannot be negative.");
        return false;
      }
    } else {
      System.out.println("Error: Invalid option. Use '-a' to append or '-r' to reduce.");
      return false;
    }

    // Calculate the new number of blocks needed
    int newBlocksNeeded = (int) Math.ceil(newSizeKB / (1024.0 / Block.BLOCK_SIZE));
    int currentBlocksAllocated = inode.getBlocksAllocated();

    if (newBlocksNeeded == currentBlocksAllocated) {
      inode.setSize(newSizeKB * 1024); // Update size without changing blocks
      System.out.println("File size updated without reallocating blocks.");
      return true;
    }

    // Free current blocks
    superBlock.freeBlocks(inode.getStartingBlock(), inode.getBlocksAllocated());

    // Allocate new blocks
    int newStartingBlock = superBlock.allocateBlocks(newBlocksNeeded);
    if (newStartingBlock == -1) {
      System.out.println("Error: Insufficient free blocks.");
      inode.allocateBlocks(inode.getStartingBlock(), inode.getBlocksAllocated()); // Reallocate old blocks to roll back
      return false;
    }

    // Update inode information
    inode.allocateBlocks(newStartingBlock, newBlocksNeeded);
    inode.setSize(newSizeKB * 1024); // Update the size in bytes

    // Output the correct amount of KB allocated or deallocated
    if (option.equals("-a")) {
      System.out.println("File '" + filename + "' updated: " + sizeChangeKB + " KB allocated.");
    } else {
      System.out.println("File '" + filename + "' updated: " + sizeChangeKB + " KB deallocated.");
    }

    return true;
  }

  public void deleteFile(String fileName) {
    Inode inode = superBlock.findInode(fileName);
    if (inode != null && inode.isUsed()) {
      freeBlocks(inode.getStartingBlock(), inode.getBlocksAllocated());
      superBlock.releaseInode(fileName);
      System.out.println("File \"" + fileName + "\" has been successfully deleted.");
    } else {
      System.out.println("Error: File not found or already deleted.");
    }
  }

  public void readFile(String fileName) {
    Inode inode = superBlock.findInode(fileName);
    if (inode == null || !inode.isUsed()) {
      System.out.println("Error: File not found.");
      return;
    }

    System.out.println("Reading file: " + fileName);
    System.out.println("File size: " + inode.getSize() + " bytes");

    // Since we're now using a starting block and blocks allocated approach,
    // construct the array of blocks used dynamically based on these values
    StringBuilder blocksUsedStr = new StringBuilder("[");
    for (int i = 0; i < inode.getBlocksAllocated(); i++) {
      if (i > 0)
        blocksUsedStr.append(", ");
      blocksUsedStr.append(inode.getStartingBlock() + i);
    }
    blocksUsedStr.append("]");

    System.out.println("Blocks used: " + blocksUsedStr.toString());
  }

  public void listFiles() {
    System.out.println("Listing files:");
    boolean found = false;
    for (Inode inode : superBlock.getInodes()) {
      if (inode.isUsed()) {
        System.out.println(inode.getName() + " - Size: " + inode.getSize() + " bytes");
        found = true;
      }
    }
    if (!found) {
      System.out.println("No files found.");
    }
  }

  public void printFileInfo(String fileName) {
    Inode inode = superBlock.findInode(fileName);
    if (inode == null || !inode.isUsed()) {
      System.out.println("File information not found for: " + fileName);
      return;
    }

    System.out.println("File Information for '" + fileName + "':");
    System.out.println("File size: " + inode.getSize() + " bytes");

    StringBuilder blocksUsedStr = new StringBuilder("[");
    for (int i = 0; i < inode.getBlocksAllocated(); i++) {
      if (i > 0)
        blocksUsedStr.append(", ");
      blocksUsedStr.append(inode.getStartingBlock() + i);
    }
    blocksUsedStr.append("]");

    System.out.println("Blocks used: " + blocksUsedStr);
    System.out.println("Last Modified Time: " + inode.getLastModifiedTime());
    System.out.println("Used: " + (inode.isUsed() ? "Yes" : "No"));
  }

  public void displayDiskInfo() {
    // Accesses SuperBlock methods to count free and used blocks.
    int freeBlocks = superBlock.countFreeBlocks();
    int usedBlocks = TOTAL_BLOCKS - freeBlocks; // Assumes TOTAL_BLOCKS is accurate for the disk.

    System.out.println("Disk Information:");
    System.out.println("Total Disk Space: " + DISK_SIZE_BYTE + " Bytes");
    System.out.println("Used Disk Space: " + usedBlocks * BLOCK_SIZE + " Bytes");
    System.out.println("Remaining Disk Space: " + (DISK_SIZE_BYTE - (usedBlocks * BLOCK_SIZE)) + " Bytes");
    System.out.println("Total Blocks: " + TOTAL_BLOCKS);
    System.out.println("Free Blocks: " + freeBlocks);
    System.out.println("Used Blocks: " + usedBlocks);

    int usedInodes = superBlock.countUsedInodes();
    System.out.println("Total Inodes: " + SuperBlock.MAX_FILES);
    System.out.println("Used Inodes: " + usedInodes);
    System.out.println("Free Inodes: " + (SuperBlock.MAX_FILES - usedInodes));
  }

  public Directory getRootDirectory() {
    return this.rootDirectory;
  }

  public void listFileSystem() {
    System.out.println(superBlock.listFileSystem());
  }

}
