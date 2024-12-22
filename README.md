# File System Simulator

[![GitHub Repository](https://img.shields.io/badge/github-repo-green.svg)](https://github.com/DamiansPortfolio/FileManagementSystem_Project)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A Java-based file system simulator developed as a final project for the Operating Systems course at Towson University. This comprehensive implementation demonstrates core concepts of operating system file management through a practical simulation that includes file creation, deletion, directory management, and block allocation.

## Repository

- **GitHub**: [FileManagementSystem_Project](https://github.com/DamiansPortfolio/FileManagementSystem_Project)
- **Author**: [DamiansPortfolio](https://github.com/DamiansPortfolio)
- **Language**: Java

## Overview

This simulator implements a simplified version of a Unix-like file system with the following components:

- **Disk Management**: 128KB total size with 1KB block size
- **Inode-based File System**: Supports up to 16 files
- **Directory Structure**: Hierarchical directory system
- **Block Allocation**: Contiguous allocation strategy
- **File Operations**: Create, delete, copy, and move files
- **Directory Operations**: Create, delete, and navigate directories

## System Architecture

The system consists of several key components:

### Core Components

1. **Disk (`Disk.java`)**
   - Manages the physical storage space
   - Handles block allocation and deallocation
   - Total size: 128KB
   - Block size: 1KB

2. **SuperBlock (`SuperBlock.java`)**
   - Tracks block usage
   - Manages inodes
   - Handles file allocation
   - Maximum files: 16

3. **Inode (`Inode.java`)**
   - Stores file metadata
   - Tracks file size and block allocation
   - Maintains modification times
   - Maximum file name length: 10 characters

4. **Block (`Block.java`)**
   - Represents a single storage block
   - Fixed size: 1KB
   - Handles data storage and retrieval

5. **Directory (`Directory.java`)**
   - Manages directory hierarchy
   - Handles file and subdirectory operations
   - Supports navigation (cd, pwd)

### Features

- File Management:
  - Create and delete files
  - Copy and move files
  - Modify file sizes
  - View file information

- Directory Management:
  - Create and delete directories
  - Navigate directory structure
  - List directory contents

- System Information:
  - Display disk usage
  - Show block allocation
  - View file system status

## Usage

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Command line terminal/prompt
- Git (optional, for cloning the repository)

### Setup Instructions

1. **Install Java Development Kit (JDK)**:
   - Download and install JDK from Oracle's website or use your system's package manager
   - Verify installation by running:
     ```bash
     java -version
     javac -version
     ```

2. **Get the Source Code**:

   Clone the repository using Git:
   ```bash
   git clone https://github.com/DamiansPortfolio/FileManagementSystem_Project.git
   cd FileManagementSystem_Project
   ```

   Alternatively, you can download the ZIP file:
   - Visit https://github.com/DamiansPortfolio/FileManagementSystem_Project
   - Click the green "Code" button
   - Select "Download ZIP"
   - Extract the downloaded ZIP file
   - Navigate to the extracted directory

3. **Compile the Project**:
   ```bash
   # Compile all Java files
   javac *.java
   ```
   This will create the corresponding .class files.

4. **Run the Simulator**:
   ```bash
   java FileSystemCLI
   ```
   You should see the welcome message:
   ```
   Welcome to the Filesystem Simulator. Type 'help' for a list of commands.
   Enter command:
   ```

### Troubleshooting

Common issues and solutions:

1. **Java Not Found**:
   ```bash
   'java' is not recognized as an internal or external command
   ```
   Solution: Add Java to your system's PATH environment variable

2. **Compilation Errors**:
   - Ensure all source files are in the same directory
   - Check that your JDK version is compatible
   - Verify all files have correct case-sensitive names

3. **Runtime Errors**:
   - Make sure you run the simulator from the directory containing the .class files
   - Verify all files were compiled successfully (look for .class files)

### Available Commands

- `ls` - List directory contents
- `cd <directoryName>` - Change directory
- `pwd` - Print working directory
- `mkfile <name> <size>` - Create a new file
- `rmfile <name>` - Remove a file
- `mkdir <name>` - Create a new directory
- `rmdir <name>` - Remove a directory
- `cpfile <source> <destination>` - Copy a file
- `mvfile <source> <destDir> <newName>` - Move/rename a file
- `diskinfo` - Display disk information
- `fileinfo <file>` - Show file details
- `showsystem` - Display block allocation
- `writefile <-a|-r> <filename> <size>` - Modify file size
- `exit` - Exit simulator

### Example Usage

Here are some common operations with their expected outputs:

```bash
# Starting the simulator
$ java FileSystemCLI
Welcome to the Filesystem Simulator. Type 'help' for a list of commands.
Enter command: 

# Create and navigate directories
Enter command: mkdir documents
Directory 'documents' created.

Enter command: cd documents
Enter command: pwd
/root/documents

# Create files
Enter command: mkfile report 2
File 'report' created with size 2KB.

Enter command: ls
Contents of directory 'documents':
Name: report, Size: 2KB, Last Modified: 2024/12/22 10:30:15

# Display disk information
Enter command: diskinfo
Disk Information:
Total Disk Space: 131072 Bytes
Used Disk Space: 2048 Bytes
Remaining Disk Space: 129024 Bytes
Total Blocks: 128
Free Blocks: 126
Used Blocks: 2
Total Inodes: 16
Used Inodes: 1
Free Inodes: 15

# Copy a file
Enter command: cpfile report /root
File 'report' copied to 'report(1)' in directory '/root'.

# Move a file
Enter command: mvfile report /root newreport
File 'report' moved to 'newreport' in directory '/root'.

# Show detailed file information
Enter command: fileinfo report(1)
File Information for 'report(1)':
File size: 2048 bytes
Blocks used: [0, 1]
Last Modified Time: 2024/12/22 10:30:15
Used: Yes

# Modify file size
Enter command: writefile -a report(1) 2
File 'report(1)' updated: 2 KB allocated.

# Show file system block allocation
Enter command: showsystem
[1 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]

# Navigate back to parent directory
Enter command: cd ..
Enter command: pwd
/root

# Remove files and directories
Enter command: rmfile report(1)
File 'report(1)' deleted.

Enter command: rmdir documents
Directory 'documents' deleted.
```

The block allocation display (`showsystem`) shows a representation of disk blocks where:
- `1` indicates an allocated block
- `0` indicates a free block
- Output is arranged in 16x8 grid for readability

## Technical Details

### Storage Limits
- Total Disk Size: 128KB
- Block Size: 1KB
- Maximum Files: 16
- Maximum File Size: 8KB
- Maximum Filename Length: 10 characters

### Implementation Notes
- Uses contiguous allocation for file storage
- Implements a hierarchical directory structure
- Maintains file metadata using inodes
- Tracks block usage with a bitmap
- Supports basic file system operations

## Project Structure

```
src/
├── Block.java         # Block management
├── Directory.java     # Directory operations
├── Disk.java         # Disk management
├── FileSystemCLI.java # Command line interface
├── Inode.java        # File metadata
└── SuperBlock.java   # File system management
```

## Limitations

- Fixed disk size (128KB)
- Maximum of 16 files
- No file content storage (simulation only)
- No file permissions or user management
- Limited error recovery
- No journaling or crash recovery

## Future Improvements

- Implement file content storage
- Add file permissions
- Support for larger file systems
- Implement file fragmentation handling
- Add support for symbolic links
- Implement file system recovery
- Add multi-user support

## Contributing

This project is a simulation for educational purposes. Contributions to improve the simulation or add new features are welcome.
