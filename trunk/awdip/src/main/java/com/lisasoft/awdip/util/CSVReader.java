package com.lisasoft.awdip.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Reads a simple ;-separated CSV file and puts all tokens/lines in a list
 * 
 * 
 * 
 * @author shansen
 *
 */
public class CSVReader {

	File file;
	List<List<String>> lines = new ArrayList<List<String>>();;
	List<String> allTokens = new ArrayList<String>();

	public CSVReader(String filename) throws IOException {
		this.file = new File(filename);
		this.read();
	}

	public CSVReader(File f) throws IOException {
		this.file = f;
		this.read();
	}

	private void read() throws IOException{

		BufferedReader br;


		br = new BufferedReader(new FileReader(this.file));
		String line = br.readLine();
		StringTokenizer tokenizer;
		List<String> tokens;

		while (line != null) {

			tokenizer = new StringTokenizer(line, ";");
			tokens = new ArrayList<String>();

			while (tokenizer.hasMoreTokens()) {
				tokens.add(tokenizer.nextToken());
			}

			this.lines.add(tokens);
			this.allTokens.addAll(tokens);

			line = br.readLine();

		}

	}
	
	public List<String> getAllTokens() {
		return this.allTokens;
	}
	
	public List<List<String>> getLines() {
		return this.lines;
	}

}
