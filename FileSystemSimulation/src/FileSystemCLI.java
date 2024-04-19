import java.util.Scanner;

public class FileSystemCLI {

  private Disk disk;
  private Directory currentDirectory;

  public FileSystemCLI() {
    this.disk = new Disk(); // Assuming Disk initializes SuperBlock and root Directory
    this.currentDirectory = this.disk.getRootDirectory(); // Assuming getRootDirectory() method exists
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
        currentDirectory.copyFile(inputParts[1], inputParts[2]);
        break;
      case "mvfile":
        if (inputParts.length < 3) {
          System.out.println("Usage: mvfile <source> <destination>");
          return;
        }
        currentDirectory.moveFile(inputParts[1], inputParts[2]);
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

  private void printHelp() {
    System.out.println("Available commands:");
    System.out.println("ls - List directory contents");
    System.out.println("mkfile <name> <size> - Create a new file");
    System.out.println("rmfile <name> - Remove a file");
    System.out.println("mkdir <name> - Create a new directory");
    System.out.println("rmdir <name> - Remove a directory and its contents");
    System.out.println("cpfile <source> <destination> - Copy a file");
    System.out.println("mvfile <source> <destination> - Move a file");
    System.out.println("diskinfo - Display disk information");
    System.out.println("fileinfo <file> - Display information about a file");
    System.out.println("showsystem - Show the file system's block allocation as an array");
    System.out.println("writefile < -a (append) | -r (remove) > <filename> <size> - Modify the size of a file");
    System.out.println("exit - Exit the simulator");
  }

  public static void main(String[] args) {
    new FileSystemCLI().start();
  }
}
