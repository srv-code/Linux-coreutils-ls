package tester;

import linux.util.fs.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main {
    private static boolean displayInLongFormat = false; /* default value set */
    private static boolean displayInHumanUnderstandableFormat = false; /* default value set */
    private static boolean displayHiddenFiles = false; /* default value set */
    private static boolean showRecursively = false; /* default value set */
    /** Values:
     * <p> - n = name = file name
     * <p> - s = size = file size
     * <p> - m = mtime = modification time
     * <p> - t = type = file type
     */
    private static char sortBy = 'n';  /* default value set */
    private static boolean sortInAsc = true; /* default value set */    
    private final static ArrayList<Path> filesToDisplay = new ArrayList<>();
    
    
    public static void main(final String[] args) {
        try {
            setOptions(args);
            List ls = new List( sortBy,             sortInAsc, 
                                displayHiddenFiles, displayInLongFormat, 
                                displayInHumanUnderstandableFormat,
                                showRecursively,    filesToDisplay);
            try {
                ls.showList();
            } catch(Exception e) {
                throw new Exception(e);
            }
        } catch(IllegalArgumentException e) {
            System.err.printf("Error: Invalid argument: %s %n", e.getMessage());
            System.exit( StandardExitCodes.ERROR );
        } catch(Throwable t) {
            Throwable errorToDisplay = t;
            Throwable causeError = t.getCause();
            if(causeError != null)
                errorToDisplay = causeError;
            System.err.println("Fatal Error: Unknown application error");
            System.err.println("!Contact developers!");
            System.err.printf("Error detail: %s (%s) %n", errorToDisplay.getClass().getSimpleName(), errorToDisplay.getMessage());
            System.err.println("Full error stacktrace:");
            errorToDisplay.printStackTrace(System.err);
            System.exit( StandardExitCodes.FATAL );
        }
    }

    /**
     * @throws IllegalArgumentException For any error in provided argument(s).
     * @throws AssertionError For programming error.
     */
    private static void setOptions(final String[] args) throws  IllegalArgumentException, AssertionError {
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

        if(filesToDisplay.isEmpty()) {
            filesToDisplay.add(Paths.get( System.getProperty("user.dir") )); /* default value set */
        }
    }
    
    private static void showHelpAndExit() {
        System.out.println("Purpose:   Lists directory contents");
        System.out.println("Usage:     ls [-<option1> [-<option2>...]] [file-path1 [file-path2 ...]]");
        System.out.printf ("Version:   %.2f %n", List.APP_VERSION);
        System.out.println("Options:");
        System.out.println("    --help,        -h          Display this help menu and exit");
        System.out.println("    --human,       -u          Display in human understandable format");
        System.out.println("    --long,        -l          Show list in long format in the following order: ");
        System.out.println("        file permissions");
        System.out.println("        file owner");
        System.out.println("        file type");
        System.out.println("        file size");
        System.out.println("        file modification date and time");
        System.out.println("        file name");
        System.out.println("    --show-hidden, -a          Don't ignore hidden files");
        System.out.println("    --recursive,   -R          Show directory contents recursively");
        System.out.println("    --reverse,     -r          Show list in descending order on the specified sorting attribute column [Default order: Ascending]");
        System.out.println("    --sort-by,     -s <value>  Sorts by the specified attribute column [Default column: file name]");
        System.out.println("        Valid values:");
        System.out.println("            n, name    On file name");
        System.out.println("            s, size    On file size");
        System.out.println("            m, mtime   On file modification date and time");
        System.out.println("            t, type    On file type");
        StandardExitCodes.showMessage();
        
        System.exit( StandardExitCodes.NORMAL );
    }
}
