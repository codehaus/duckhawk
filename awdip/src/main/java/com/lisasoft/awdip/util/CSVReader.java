package com.lisasoft.awdip.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a simple ,-separated CSV file and puts all fields/lines in a list
 * Follows RFC 4180 with the following exceptions:
 *  - Each file must have a header with the name of the fields.
 *  - The line ending may also be LF instead of CRLF
 *  - lines with a hash (#) at the beginning are ignored 
 * 
 * @author shansen, vmische
 *
 */
public class CSVReader {
    File file;
    List<String[]> lines = new ArrayList<String[]>();
    String[] fields;
    
    public CSVReader(String filename) throws IOException {
        this.file = new File(filename);
        this.read();
    }

    public CSVReader(File f) throws IOException {
        this.file = f;
        this.read();
    }

    private void read() throws IOException {
        BufferedReader br;

        br = new BufferedReader(new FileReader(this.file));
        String line = br.readLine();

        while (line != null) {
            if (line.trim().equals("") || line.startsWith("#")) {
                line = br.readLine();
                continue;
            }
            
            // split, but not within quotes
            fields = line.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
            
            // remove quotes
            for (int i=0; i<fields.length; i++) {
                fields[i] = fields[i].replaceAll("\"$", "");
                fields[i] = fields[i].replaceAll("^\"", "");
            }

            this.lines.add(fields);
            line = br.readLine();
        }
    }

    public List<String[]> getLines() {
        return this.lines;
    }
}

