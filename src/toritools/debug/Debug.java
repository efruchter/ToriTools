package toritools.debug;

/**
 * Generic mechanism for displaying log info.
 * 
 * @author toriscope
 * 
 */
public class Debug {

    private Debug() {
    }

    /**
     * Flip this to true if you want to display all the printouts.
     */
    public static boolean showDebugPrintouts = false;

    /**
     * Print a line to console if showDebugPrintouts is true.
     * 
     * @param string
     *            to print.
     */
    public static void print(final String s) {
        if (showDebugPrintouts) {
            System.out.println(s);
        }
    }
}
