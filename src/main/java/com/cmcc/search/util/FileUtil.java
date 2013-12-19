package com.cmcc.search.util;

import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class FileUtil {
	public static String getFileEncoding(String fileName) throws IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
		byte[] buf = new byte[4096];
		// (1)
	    UniversalDetector detector = new UniversalDetector(null);
	    // (2)
	    int nread;
	    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
	      detector.handleData(buf, 0, nread);
	    }
	    // (3)
	    detector.dataEnd();
	    // (4)
	    String enc = detector.getDetectedCharset();
	    detector.reset();
	    fis.close();
	    return enc;
	}
	
}
