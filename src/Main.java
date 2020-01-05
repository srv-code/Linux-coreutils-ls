import linux.util.fs.List;


public class Main {
    public static void main(final String[] args) {
        try {
            setOptions(args);
            List ls = new List();
            try {
                ls.showList();
            } catch(Exception e) {
                throw new Exception(e);
            }
        } catch(IllegalArgumentException e) {
            System.err.printf("Error: Invalid argument: %s %n", e.getMessage());
            System.exit( ExitValue.ERROR );
        } catch(Throwable t) {
            Throwable errorToDisplay = t;
            Throwable causeError = t.getCause();
            if(causeError != null)
                errorToDisplay = causeError;
            System.err.println("Fatal Error: Unknown application error");
            System.err.println("!Contact developers!");
            System.err.printf("Error detail: %s (%s) %n", errorToDisplay.getClass().getSimpleName(), errorToDisplay.getMessage());
            System.err.println("Full error stacktrace:");
            errorToDisplay.printStackTrace();
            System.exit( ExitValue.FATAL );
        }
    }
}
