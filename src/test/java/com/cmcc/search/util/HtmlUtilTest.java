package com.cmcc.search.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class HtmlUtilTest {

	public File[] findFilesBySuffix(final String dirName, final String suffix) {
		File dir = new File(dirName);
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(suffix);
			}
		});
	}

	
	
	@Test
	public void testParseHtmlFile() {
		final File testDir = new File("/Users/louis36/Downloads/mobile/10-数据流量专区");
		final String destFile = "knowledge.txt";
		Iterator<File> iter = FileUtils.iterateFiles(testDir,
				new String[] { "htm" }, true);

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(destFile));
			while (iter.hasNext()) {
				try {
					File file = iter.next();
					String filteredText = HtmlUtil.filterHtmlTags(file
							.getAbsolutePath());
					
					bw.append(filteredText);
					bw.newLine();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
				}
		}
	}
}
