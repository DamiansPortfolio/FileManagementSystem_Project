public class Directory {
  private String name;
  private SuperBlock superBlock; // Reference to the SuperBlock for inode management

  public Directory(String name, SuperBlock superBlock) {
    this.name = name;
    this.superBlock = superBlock;
  }

  // Create a new file in this directory
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
    System.out.println("File '" + fileName + "' created with size " + size + "KB.");
  }

  // Delete a file from this directory
  public void deleteFile(String fileName) {
    boolean success = superBlock.releaseInode(fileName);
    if (!success) {
      System.out.println("Error: Could not delete file '" + fileName + "'. File may not exist.");
      return;
    }
    System.out.println("File '" + fileName + "' deleted.");
  }

  // List all files within this directory
  public void listContents() {
    System.out.println("Listing contents of directory '" + name + "':");
    superBlock.listInodes();
  }

  // Copy a file within this directory
  public void copyFile(String sourceFileName, String newFileName) {
    Inode sourceInode = superBlock.findInode(sourceFileName);
    if (sourceInode == null) {
      System.out.println("Error: Source file '" + sourceFileName + "' does not exist.");
      return;
    }
    // Check if the new file name already exists
    if (superBlock.findInode(newFileName) != null) {
      System.out.println("Error: File '" + newFileName + "' already exists.");
      return;
    }
    // Attempt to create a new file with the same size as the source file
    createFile(newFileName, sourceInode.getSize());
    System.out.println("File '" + sourceFileName + "' copied to '" + newFileName + "'.");
  }

  // Move a file within this directory
  public void moveFile(String sourceFileName, String newFileName) {
    Inode sourceInode = superBlock.findInode(sourceFileName);
    if (sourceInode == null) {
      System.out.println("Error: Source file '" + sourceFileName + "' does not exist.");
      return;
    }
    // Check if the target file name already exists
    if (superBlock.findInode(newFileName) != null) {
      System.out.println("Error: File '" + newFileName + "' already exists.");
      return;
    }
    // Copy the file to the new name
    copyFile(sourceFileName, newFileName);
    // Delete the original file
    deleteFile(sourceFileName);
    System.out.println("File '" + sourceFileName + "' moved to '" + newFileName + "'.");
  }

  // Method to create a new directory within this directory
  public void createDirectory(String dirName) {
    // Since the current simulation scope includes only a root directory,
    // this method can print a message indicating directory creation is not
    // supported or
    // implement a conceptual operation that would be relevant in a more complex
    // filesystem.
    System.out.println("Creating subdirectories is not supported in the current filesystem simulation.");
  }

  // Method to delete a directory and all its contents within this directory
  public void deleteDirectory(String dirName) {
    // Similar to createDirectory, this would either provide a message or
    // a conceptual implementation for directory deletion.
    System.out.println("Deleting subdirectories is not supported in the current filesystem simulation.");
  }

  // Additional methods as needed...
}
