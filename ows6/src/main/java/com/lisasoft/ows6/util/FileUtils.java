/**
 * 
 */
package com.lisasoft.ows6.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that provides static methods for
 * file handling.
 * 
 * Used in OWS6 test for loading tests, configs, etc.
 * 
 * @author shansen
 *
 */
public class FileUtils {
	
	/**
	 * Returns all files with a the given extension (only characters)
	 * in the given directory
	 * 
	 * @param folderName name of the directory
	 * @param extension file extension (accepts only characters)
	 * @return list of the requested files
	 */
	public static List<File> getFilesByExtension (String folderName, String extension) {
		
		
		String regexp = ".\\."+extension;
		return getFilesByPattern(folderName, regexp);
	}
	
	
	/**
	 * Returns all files in a folder that match the given
	 * pattern/regexp.
	 * 
	 * @param folderName name of the directory
	 * @param p pattern to match files
	 * @return list of the requested files
	 */
	public static List<File> getFilesByPattern (String folderName, String regexp) {
		
		Pattern p = Pattern.compile(regexp);
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();		
		Matcher m;

		List<File> ret = new ArrayList<File>();

		for (File file : listOfFiles) {
				
			m = p.matcher(file.getName());
			
			if (m.find() && file.isFile()) {
				ret.add(file);
			} 
    	}	
		
		return ret;
	}

}
