package files;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class FileUtils {
	protected static String executionPath = "";
	
	public static String getExecutionPath(Object object){
        try {
            String thisURIPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String objectURIPath = object.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            boolean discrepancy = !thisURIPath.equals(objectURIPath);
            if(discrepancy) {
                // If there is a discrepancy, then we are executing within Elcipse and need to remove "bin/" from the path. //
            	return objectURIPath.substring(0, objectURIPath.length()-4);
            } else {
            	// If there is no discrepancy, then we are executing the exported .jar and need to trim the filename. //
            	return objectURIPath.substring(0, objectURIPath.lastIndexOf(File.separator)+1);
            }
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Could not find execution path.", "Error", JOptionPane.ERROR_MESSAGE);
        return "";
    }
	
	/**
	 * Set the execution path to the game object to ensure assets are loaded from the correct directory. //
	 * @param object
	 */
	public static void setExecutionPath(Object object) {
		executionPath = getExecutionPath(object);
	}
}