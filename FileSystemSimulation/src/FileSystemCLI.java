import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileSystemCLI {
  private Disk disk;
  private Directory currentDirectory;
  private List<String> path = new ArrayList<>(); // Track the path for 'pwd'

  public FileSystemCLI() {
    this.disk = new Disk(); // This initializes the Disk, which in turn initializes the SuperBlock and root
                            // Directory
    this.currentDirectory = this.disk.getRootDirectory(); // Ensures correct initialization
    this.path = new ArrayList<>();
    this.path.add(currentDirectory.getName()); // Start with the root directory name
  }

  public void start() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Welcome to the Filesystem Simulator. Type 'help' for a list of commands.");

    while (true) {
      System.out.print("Enter command: ");
      String inputLine = scanner.nextLine();
      if ("exit".equalsIgnoreCase(inputLine.trim())) {
        System.out.println("Exiting simulator.");
        break;
      }
      processInput(inputLine);
    }
    scanner.close();
  }

  private void processInput(String inputLine) {
    String[] inputParts = inputLine.split("\\s+");
    if (inputParts.length == 0)
      return;

    String command = inputParts[0].toLowerCase();
    switch (command) {
      case "ls":
        currentDirectory.listContents();
        break;
      case "cd":
        String directoryName = inputParts.length > 1 ? inputParts[1] : ""; // Provide empty if no argument
        changeDirectory(directoryName);
        break;
      case "pwd":
        printWorkingDirectory();
        break;
      case "mkfile":
        if (inputParts.length < 3) {
          System.out.println("Usage: mkfile <name> <size>");
          return;
        }
        currentDirectory.createFile(inputParts[1], Integer.parseInt(inputParts[2]));
        break;
      case "rmfile":
        if (inputParts.length < 2) {
          System.out.println("Usage: rmfile <name>");
          return;
        }
        currentDirectory.deleteFile(inputParts[1]);
        break;
      case "mkdir":
        if (inputParts.length < 2) {
          System.out.println("Usage: mkdir <name>");
          return;
        }
        currentDirectory.createDirectory(inputParts[1]);
        break;
      case "rmdir":
        if (inputParts.length < 2) {
          System.out.println("Usage: rmdir <name>");
          return;
        }
        currentDirectory.deleteDirectory(inputParts[1]);
        break;
      case "cpfile":
        if (inputParts.length < 3) {
          System.out.println("Usage: cpfile <source> <destination>");
          return;
        }
        currentDirectory.copyFile(inputParts[1], inputParts[2], disk); // Pass the disk object
        break;
      case "mvfile":
        if (inputParts.length < 4) {
          System.out.println("Usage: mvfile <source> <destinationDirName> <newName>");
          return;
        }
        currentDirectory.moveFile(inputParts[1], inputParts[2], inputParts[3], disk.getRootDirectory(),
            currentDirectory);
        break;
      case "diskinfo":
        disk.displayDiskInfo();
        break;
      case "fileinfo":
        if (inputParts.length < 2) {
          System.out.println("Usage: fileinfo <file>");
          return;
        }
        disk.printFileInfo(inputParts[1]);
        break;
      case "help":
        printHelp();
        break;
      case "showsystem":
        disk.listFileSystem();
        break;
      case "writefile":
        if (inputParts.length < 4) {
          System.out.println("Usage: writefile <-a|-r> <filename> <size>");
          return;
        }
        if (!disk.writeFile(inputLine)) {
          System.out.println("Failed to modify file size.");
        }
        break;
      default:
        System.out.println("Unknown command. Type 'help' for a list of commands.");
    }
  }

  private void changeDirectory(String directoryName) {
    if (directoryName == null || directoryName.trim().isEmpty()) {
      // Reset to root directory if no directory name is provided or if it is an empty
      // string
      currentDirectory = disk.getRootDirectory();
      path.clear();
      path.add(currentDirectory.getName());
      System.out.println("Returned to root directory.");
    } else {
      Directory newDir = currentDirectory.findDirectory(directoryName, disk.getRootDirectory(), currentDirectory);
      if (newDir != null) {
        currentDirectory = newDir;
        if (directoryName.equals("..")) {
          if (path.size() > 1)
            path.remove(path.size() - 1);
        } else {
          path.add(directoryName);
        }
      } else {
        System.out.println("Directory not found: " + directoryName);
      }
    }
  }

  private void printWorkingDirectory() {
    System.out.println("/" + String.join("/", path));
  }

  private void printHelp() {
    System.out.println("Available commands:");
    System.out.println("ls - List directory contents");
    System.out.println("cd <directoryName> - Change directory");
    System.out.println("pwd - Print working directory");
    System.out.println("mkfile <name> <size> - Create a new file");
    System.out.println("rmfile <name> - Remove a file");
    System.out.println("mkdir <name> - Create a new directory");
    System.out.println("rmdir <name> - Remove a directory and its contents");
    System.out.println(
        "cpfile <source> <destination> - Copy a file to a specified destination. Example: cpfile myfile /destinationFolder");
    System.out
        .println(
            "mvfile <source> <destinationDirName> <newName> - Move a file to another directory and optionally rename it. Example: mvfile myfile /destinationFolder newfile");
    System.out.println("diskinfo - Display disk information");
    System.out.println("fileinfo <file> - Display information about a file");
    System.out.println("showsystem - Show the file system's block allocation as an array");
    System.out.println(
        "writefile <-a (append) | -r (reduce)> <filename> <sizeChange> - Modify the size of a file by appending or reducing its size");
    System.out.println("exit - Exit the simulator");
  }

  public static void main(String[] args) {
    new FileSystemCLI().start();
  }
}
