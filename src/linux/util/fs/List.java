/* Modelled after linux command: ls */
package linux.util.fs;


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;


public class List {
    private final float appVersion = 0.03f;
    
    private ArrayList<Path> filesToDisplay = new ArrayList<>();
    /**
     * <p> Show in long format </br>
     * <p> Shows in the following orders:
     * <p> - file permissions
     * <p> - file owner
     * <p> - file type
     * <p> - file size
     * <p> - file modification date and time
     * <p> - file name
     * */
    private boolean displayInLongFormat = false; /* default value set */
    private boolean displayInHumanUnderstandableFormat = false; /* default value set */
    private boolean displayHiddenFiles = false; /* default value set */
    private boolean showRecursively = false; /* default value set */
    /** Values:
     * <p> - n = name = file name
     * <p> - s = size = file size
     * <p> - m = mtime = modification time
     * <p> - t = type = file type
    */
    private char sortBy = 'n';  /* default value set */
    private boolean sortInAsc = true; /* default value set */
    
    public List() {}
    
    private void setOptions(final String[] args)
            throws  IllegalArgumentException, /* For any error in provided argument(s) */
                    AssertionError { /* For programming error */
        char requireArgumentForOption = 0;
        for(String arg: args) {
//          System.out.printf("    [arg: %s, requireArgumentForOption=%c (%<d)]\n", arg, (int)requireArgumentForOption); // DEBUG
            if(requireArgumentForOption != 0) {
                switch(requireArgumentForOption) {
                    case 's': /* argument for sortBy */
                        switch (arg) {
                            case "name":
                            case "n":
                                sortBy = 'n';
                                break;
                            case "size":
                            case "s":
                                sortBy = 's';
                                break;
                            case "mtime":
                            case "m":
                                sortBy = 'm';
                                break;
                            case "type":
                            case "t":
                                sortBy = 't';
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid value "+arg+" for option --sort-by / -s");
                        }
                        requireArgumentForOption = 0; /* Reset after each use */
                        break;
                    default:
                        throw new AssertionError("Should not get here: requireArgumentForOption = " + requireArgumentForOption);
                }
            } else {
                switch(arg) {
                    case "--help":
                    case "-h":
                        showHelpAndExit();
                    case "--human":
                    case "-u":
                        displayInHumanUnderstandableFormat = true;
                        break;
                    case "--long":
                    case "-l":
                        displayInLongFormat = true;
                        break;
                    case "--show-hidden":
                    case "-a":
                        displayHiddenFiles = true;
                        break;
                    case "--recursive":
                    case "-R":
                        showRecursively = true;
                        break;
                    case "--sort-by":
                    case "-s":
                        requireArgumentForOption = 's';
                        break;
                    case "--reverse":
                    case "-r":
                        sortInAsc = false;
                        break;
                    default:
                        if(arg.startsWith("-"))
                            throw new IllegalArgumentException(arg);
                        Path path = Paths.get(arg);
                        if(Files.notExists(path))
                            throw new IllegalArgumentException("File cannot be located: " + arg);
                        filesToDisplay.add(path);
                }
            }
        }

        if(requireArgumentForOption != 0) {
            throw new IllegalArgumentException("Argument not specified for provided option " + requireArgumentForOption);
        }

        if(filesToDisplay.size() == 0) {
            filesToDisplay.add(Paths.get( System.getProperty("user.dir") )); /* default value set */
        }
    }
    
    private void showHelpAndExit() {
        System.out.println("Purpose: Lists directory contents");
        System.out.println("Usage: ls [-<option1> [-<option2>...]] [filename]");
        System.out.printf ("Version: %.2f %n", appVersion);
        System.out.println("Options:");
        System.out.println("  --help, -h  : Display this help menu and exit");
        System.out.println("  --human, -u : Display in human understandable format");
        System.out.println("  --long, -l  : Show list in long format in the following order: ");
            System.out.println("      file permissions");
            System.out.println("      file owner");
            System.out.println("      file type");
            System.out.println("      file size");
            System.out.println("      file modification date and time");
            System.out.println("      file name");
        System.out.println("  --show-hidden, -a : Don't ignore hidden files");
        System.out.println("  --recursive, -R   : Show directory contents recursively");
        System.out.println("  --reverse, -r     : Show list in descending order on the specified sorting attribute column [Default order: Ascending]");
        System.out.println("  --sort-by, -s <value> : Sorts by the specified attribute column [Default column: file name]");
            System.out.println("      Valid values:");
            System.out.println("        n, name: On file name");
            System.out.println("        s, size: On file size");
            System.out.println("        m, mtime: On file modification date and time");
            System.out.println("        t, type: On file type");
        ExitValue.showMessage();
        
        System.exit( ExitValue.NORMAL );
    }
    
    /**
     * Main method of command
     * */
    private void showList() throws Exception { /* For any programming error : Fatal Error */
        for(Path path : filesToDisplay) {
            if(Files.isDirectory(path)) {
                if(showRecursively) {
                    try {
                        Files.walkFileTree(path, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                showList(dir);
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                                if(e != null)
                                    System.err.printf("Error: Cannot list: %s (%s) %n", dir, e.getClass().getSimpleName());
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                // showList(file); /* Not required as already listed inside preVisitDirectory(...) */
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
                                if(e != null)
                                    System.err.printf("Error: Cannot list: %s (%s) %n", file, e.getClass().getSimpleName());
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch(Exception e) {
                        System.err.printf("Error: Cannot list directory recursively: %s (%s) %n", path, (e==null?"unknown":e.getClass().getSimpleName()));
                        // e.printStackTrace(); /* As stack trace will also be printed in the main method's exception handles */
                        throw e; /* throw exception as its a programming error and should be reported to the Developers! */
                    }
                } else {
                    showList(path);
                }
            } else {
                showList(path);
            }
        }
    }
    
    
    private void showList(final Path path) {
//      $showOptions(); /* for diagnostics only */
        
        System.out.printf("%n%s: %n", path);
        if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            long totalFileCount = 0L;
            ArrayList<FileAttributes> list = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for(Path entry : stream) {
                    if(Files.isHidden(entry)) {
                        if(displayHiddenFiles) {
                            list.add( new FileAttributes(entry) );
                            totalFileCount++;
                        }
                    } else {
                        list.add( new FileAttributes(entry) );
                        totalFileCount++;
                    }
                }
            } catch(Exception e) {
                System.err.printf("Error: Cannot access file: %s (%s) %n", path, e.getClass().getSimpleName());
            }
            Collections.sort(list);
            for(FileAttributes attrs : list) {
                System.out.println("  " + attrs);
            }
            
            System.out.printf("Total: %d %n", totalFileCount);
        } else {
            System.out.println("  " + new FileAttributes(path));
        }
    }
    
    private class FileAttributes implements Comparable<FileAttributes> {
        final Path file;
        final boolean isDirectory, isSymLink;
        final String owner, type, name;
        final String permissions;
        final long size;
        final FileTime mtime;
        
        FileAttributes(final Path file) {
            this.file = file;
            this.isDirectory = Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS);
            this.isSymLink = Files.isSymbolicLink(file);
            
            char[] perms = new char[4];
            perms[0] = (this.isDirectory?'d':(this.isSymLink?'l':'-'));
            perms[1] = (Files.isReadable(file)?'r':'-');
            perms[2] = (Files.isWritable(file)?'w':'-');
            perms[3] = (Files.isExecutable(file)?'x':'-');
            this.permissions = new String(perms);
            
            String tmpOwner;
            try {
                tmpOwner = Files.getOwner(file, LinkOption.NOFOLLOW_LINKS).getName();
            } catch(IOException e) {
                tmpOwner = "?";
            }
            this.owner = tmpOwner;
            
            if(this.isDirectory)
                this.type = "dir";
            else if(this.isSymLink)
                this.type = "slnk";
            else if(Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS))
                this.type = "file";
            else
                this.type = "?";
            
            long tmpSize;
            try {
                tmpSize = Files.size(file);
            } catch(IOException e) {
                tmpSize = -1;
            }
            this.size = tmpSize;
            
            FileTime tmpMTime;
            try {
                tmpMTime = Files.getLastModifiedTime(file, LinkOption.NOFOLLOW_LINKS);
            } catch(IOException e) {
                tmpMTime = null;
            }
            this.mtime = tmpMTime;
            
            this.name = file.getFileName().toString();
        }
        
        @Override
        public int compareTo(final FileAttributes that) {
            int c;
            switch(sortBy) {
                case 'n': c = this.name.compareTo(that.name); return sortInAsc ? c : -c;
                case 's': c = Long.valueOf(this.size - that.size).intValue(); return sortInAsc ? c : -c;
                case 'm': c = ((this.mtime==null || that.mtime==null)? 0 : this.mtime.compareTo(that.mtime)); return sortInAsc ? c : -c;
                case 't': c = this.type.compareTo(that.type); return sortInAsc ? c : -c;
                default: throw new AssertionError("Should not get here: Invalid sortBy value: " + sortBy);
            }
        }
        
        @Override
        public String toString() {
            if(displayInLongFormat)
                return String.format("%s  %10s  %-4s  %10s  %17s  %s",
                                        permissions,
                                        owner,
                                        type,
                                        getFileSize(),
                                        getModTime(),
                                        getFileName());
            else
                return getFileName();
        }
        
        private String getModTime() {
            return ((mtime==null) ? "?" : String.format("%tT-%<tD", mtime.toMillis()));
        }
        
        private String getFileName() {
            if(displayInHumanUnderstandableFormat) {
                if(isDirectory)
                    return name  + "/";
                if(isSymLink) {
                    try {
                        Path tgtRaw = Files.readSymbolicLink(file);
                        Path tgtResolved = file.resolveSibling(tgtRaw).normalize();
                        String tgtName = (Files.isDirectory(tgtResolved) ? tgtRaw + "/" : tgtRaw.toString());
                        if(Files.notExists(tgtResolved, LinkOption.NOFOLLOW_LINKS))
                            return name + " -> " + tgtName + " (!)";
                        return name + " -> " + tgtName;
                    } catch(IOException e) {
                        return name + " -> " + "?";
                    }
                }
            }
            return name;
        }
        
        private final double KB = Math.pow(1024.0, 1.0);
        private final double MB = Math.pow(1024.0, 2.0);
        private final double GB = Math.pow(1024.0, 3.0);
        
        private String getFileSize() {
            if(size == -1) return "?";
            if(displayInHumanUnderstandableFormat) {
                final double s  = Long.valueOf(size).doubleValue();
                if(s < KB)          return String.format("%8d B", size);
                else if(s < MB)     return String.format("%4.2f K", s/KB);
                else if(s < GB)     return String.format("%4.2f M", s/MB);
                else                return String.format("%4.2f G", s/GB);
            }
            return String.format("%8d", size);
        }
    }
    
    /**
     * For developers only
     * */
    private void $showOptions() {
        System.out.println("------------------------");
        System.out.println("[Disable $showOptions() before final deployment...]");
        System.out.println("  App version: " + appVersion);
        System.out.println("  Options:");
        System.out.println("    filesToDisplay = " + filesToDisplay);
        System.out.println("    displayInLongFormat = " + displayInLongFormat);
        System.out.println("    displayInHumanUnderstandableFormat = " + displayInHumanUnderstandableFormat);
        System.out.println("    displayHiddenFiles = " + displayHiddenFiles);
        System.out.println("    showRecursively = " + showRecursively);
        System.out.println("    sortBy = " + sortBy);
        System.out.println("    sortInAsc = " + sortInAsc);
        System.out.println("------------------------\n");
    }
}
