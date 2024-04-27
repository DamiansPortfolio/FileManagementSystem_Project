import java.util.*;

public class Directory {
  private String name;
  private SuperBlock superBlock; // Reference to the SuperBlock for inode management
  private Map<String, Directory> subDirectories;
  private Directory parentDirectory; // Parent directory reference

  public Directory(String name, SuperBlock superBlock, Directory parent) {
    this.name = name;
    this.superBlock = superBlock;
    this.parentDirectory = parent; // Set parent directory
    this.subDirectories = new HashMap<>();
  }

  public SuperBlock getSuperBlock() {
    return this.superBlock;
  }

  public Map<String, Directory> getSubDirectories() {
    return subDirectories;
  }

  public String getName() {
    return name;
  }

  public void createDirectory(String dirName) {
    if (subDirectories.containsKey(dirName)) {
      System.out.println("Error: Directory '" + dirName + "' already exists.");
    } else {
      subDirectories.put(dirName, new Directory(dirName, superBlock, this)); // Pass 'this' as the parent
      System.out.println("Directory '" + dirName + "' created.");
    }
  }

  public void deleteDirectory(String dirName) {
    Directory dir = subDirectories.get(dirName);
    if (dir == null) {
      System.out.println("Error: Directory '" + dirName + "' does not exist.");
    } else {
      dir.deleteAllContents();
      subDirectories.remove(dirName);
      System.out.println("Directory '" + dirName + "' deleted.");
    }
  }

  private void deleteAllContents() {
    for (Directory subDir : subDirectories.values()) {
      subDir.deleteAllContents();
    }
    subDirectories.clear();
    deleteAllFiles();
  }

  private void deleteAllFiles() {
    for (Inode inode : superBlock.getInodes()) {
      if (inode.isUsed() && inode.getDirectoryName().equals(this.name)) {
        superBlock.releaseInode(inode.getName());
      }
    }
  }

  public void moveFile(String sourcePath, String destPath, String newFileName, Directory rootDirectory,
      Directory currentDirectory) {
    String sourceDirName = sourcePath.substring(0, sourcePath.lastIndexOf('/'));
    String sourceFileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
    Directory sourceDir = findDirectory(sourceDirName, rootDirectory, currentDirectory);
    Directory destDir = findDirectory(destPath, rootDirectory, currentDirectory);

    if (sourceDir == null || destDir == null) {
      System.out.println("Source or destination directory does not exist.");
      return;
    }

    Inode sourceInode = sourceDir.getSuperBlock().findInode(sourceFileName);
    if (sourceInode == null) {
      System.out.println("Source file does not exist: " + sourceFileName);
      return;
    }

    superBlock.moveInode(sourceInode, newFileName, destDir.getName());
    System.out.println("File '" + sourceFileName + "' moved to '" + newFileName + "' in directory '" + destPath + "'.");
  }

  public Directory findDirectory(String path, Directory rootDirectory, Directory currentDirectory) {
    Directory current = path.startsWith("/") ? rootDirectory : currentDirectory;
    String[] parts = path.split("/");
    for (String part : parts) {
      if (part.isEmpty() || part.equals("."))
        continue;
      if (part.equals("..")) {
        if (current == rootDirectory) {
          // Stay at root if already there and trying to go up
          return rootDirectory;
        } else {
          // Move up to parent directory by finding this directory's parent
          // We need a method to get parent, assuming we have 'parentDirectory' field in
          // Directory
          return current.parentDirectory;
        }
      } else {
        current = current.getSubDirectories().get(part);
        if (current == null) {
          System.out.println("Directory not found: " + part);
          return null;
        }
      }
    }
    return current;
  }

  public void createFile(String fileName, int size) {
    if (superBlock.findInode(fileName) != null) {
      System.out.println("Error: File '" + fileName + "' already exists.");
      return;
    }
    Inode inode = superBlock.allocateInode(fileName, size);
    if (inode == null) {
      System.out.println("Error: Could not create file '" + fileName + "'.");
      return;
    }
    inode.setDirectoryName(this.name);
    System.out.println("File '" + fileName + "' created with size " + size + "KB.");
  }

  public void deleteFile(String fileName) {
    boolean success = superBlock.releaseInode(fileName);
    if (!success) {
      System.out.println("Error: Could not delete file '" + fileName + "'. File may not exist.");
      return;
    }
    System.out.println("File '" + fileName + "' deleted.");
  }

  public void listContents() {
    System.out.println("Contents of directory '" + name + "':");
    for (String dirName : subDirectories.keySet()) {
      System.out.println("[Dir] " + dirName);
    }
    superBlock.listInodes(this.name);
  }

  public void copyFile(String sourceFileName, String destPath, Disk disk) {
    Directory rootDirectory = disk.getRootDirectory(); // Access root directory
    Directory destDir = findDirectory(destPath, rootDirectory, this);
    if (destDir == null) {
      System.out.println("Destination directory does not exist.");
      return;
    }

    Inode sourceInode = superBlock.findInode(sourceFileName);
    if (sourceInode == null) {
      System.out.println("Source file '" + sourceFileName + "' does not exist.");
      return;
    }

    // Adjusted to handle file naming by appending numbers to manage copies without
    // spaces
    String baseName = sourceFileName.replaceAll("\\(\\d+\\)$", "");
    int i = 1;
    String newFileName = baseName + "(" + i + ")";
    while (superBlock.findInode(newFileName) != null) {
      i++;
      newFileName = baseName + "(" + i + ")";
    }

    // Check if enough blocks are available before attempting to allocate
    int requiredBlocks = (int) Math.ceil(sourceInode.getSize() / (double) Block.BLOCK_SIZE);
    if (requiredBlocks > disk.getSuperBlock().countFreeBlocks()) {
      System.out.println("Error: Not enough disk space to copy the file.");
      return;
    }

    // Create the inode for the new file
    Inode newInode = superBlock.allocateInode(newFileName, sourceInode.getSize() / 1024); // Size needs to be in KB
    if (newInode == null) {
      System.out.println("Error: Could not create a copy of the file.");
      return;
    }
    newInode.setDirectoryName(destDir.getName());

    System.out
        .println("File '" + sourceFileName + "' copied to '" + newFileName + "' in directory '" + destPath + "'.");
  }

}
