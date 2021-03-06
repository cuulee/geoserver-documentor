package de.geops.geoserver.documentor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.util.logging.Logging;

public class ExtensionInfo {

	private static final Logger LOGGER = Logging.getLogger(ExtensionInfo.class);
	
	public ExtensionInfo() {
	}
	
    public String getGitVersion() {
        String gitVersion = getResourceContents("/geoserver-documentor.gitversion");
        if ((gitVersion==null) || (gitVersion=="")) {
            gitVersion = "<No git version information available>";
        }
        return gitVersion;
	}

    
	public String getReadme() {
        String readmeText = getResourceContents("/README.documentor.md");
        if ((readmeText==null) || (readmeText=="")) {
        	readmeText = "Readme is not available";
        }
        return readmeText;
	}
	
	
	private String getResourceContents(String resourceName) {
        InputStream rs = getClass().getResourceAsStream(resourceName);
        if (rs==null) {
            return "";
        }
        try {
	        Scanner scanner = new Scanner(rs,"UTF-8");
	        try {
	        	return scanner.useDelimiter("\\A").next().trim();
	        } finally {
	        	scanner.close();
	        }
        } finally {
        	try {
				rs.close();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Could not close inputstream to "+resourceName, e);
			}
        }
    }
	
	public String getVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "unknown - not packaged";
        }
        return version;
	}
}
