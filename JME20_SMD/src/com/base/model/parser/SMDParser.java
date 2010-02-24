/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.base.model.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class that parses a .smd file, file types are : .qc files and
 * .smd files.
 * @author serser
 */
public class SMDParser {

    protected static final String SPLITCHAR = " ";
    protected static final String GROUP_IN = "{";
    protected static final String GROUP_OUT = "}";
    protected ArrayList<String> lines;
    private static final Logger logger = Logger.getLogger(
            SMDParser.class.getName());

    /**
     * Method that parses a file, returns the file lines.
     * @param file : URL of the file to read the lines.
     * @return : ArrayList<String> with the recovered lines.
     */
    public static ArrayList<String> readFile(URL file) {
        ArrayList<String> lines = null;
        BufferedReader reader;
        try {
            lines = new ArrayList<String>();
            reader = new BufferedReader(new InputStreamReader(file.openStream()));
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                if (line != null && !line.equals("")) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error reading file : " +
                    file.toString(),Ex);
        } finally {
            reader = null;
        }
        return lines;
    }

    /**
     * Method that cleans line white-spaces and removes special char.
     * @param original : String array with line tokens.
     * @param chartodel : String with the char to delete in the token.
     * @return String[] with cleaned line tokens.
     */
    protected String[] clean(String[] original, String chartodel) {
        ArrayList<String> cleaned = null;
        try {
            cleaned = new ArrayList<String>();
            int j = 0;
            boolean sequence = false;
            for (int i = 0; i < original.length; i++) {
                if (!original[i].equals("") && !original[i].equals(" ")) {
                    if (original[i].startsWith("\"") && !original[i].endsWith("\"")) {
                        cleaned.add(original[i]);
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        sequence = true;
                    } else if (!original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned.add(j, cleaned.get(j).concat(original[i]));
                        cleaned.remove(j + 1);
                        sequence = false;
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        j++;
                    } else if (original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned.add(original[i]);
                        if (chartodel != null) {
                            cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                            cleaned.remove(j + 1);
                        }
                        j++;
                    } else {
                        if (sequence) {
                            cleaned.add(j, cleaned.get(j).concat(original[i]));
                            cleaned.remove(j + 1);
                            if (chartodel != null) {
                                cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                                cleaned.remove(j + 1);
                            }
                        } else {
                            cleaned.add(original[i]);
                            if (chartodel != null) {
                                cleaned.add(j, cleaned.get(j).replaceAll(chartodel, ""));
                                cleaned.remove(j + 1);
                            }
                            j++;
                        }
                    }
                }
            }
            original = new String[cleaned.size()];
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error cleaning tokens.",Ex);
        }
        return cleaned.toArray(original);
    }

    /**
     * Method that cleans line white-spaces.
     * @param original : String array with line tokens.
     * @return String[] with cleaned line tokens.
     */
    protected String[] clean(String[] original, int positions) {
        String[] cleaned = null;
        try {
            cleaned = new String[positions];
            int j = 0;
            boolean sequence = false;
            for (int i = 0; i < original.length; i++) {
                if (!original[i].equals("")) {
                    if (original[i].startsWith("\"") && !original[i].endsWith("\"")) {
                        cleaned[j] = original[i];
                        sequence = true;
                    } else if (!original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned[j] = cleaned[j].concat(original[i]);
                        sequence = false;
                        j++;
                    } else if (original[i].startsWith("\"") && original[i].endsWith("\"")) {
                        cleaned[j] = original[i];
                        j++;
                    } else {
                        if (sequence) {
                            cleaned[j] = cleaned[j].concat(original[i]);
                        } else {
                            cleaned[j] = original[i];
                            j++;
                        }
                    }
                }
            }
        } catch (Exception Ex) {
            logger.log(Level.SEVERE, "Error cleaning tokens.",Ex);
        }
        return cleaned;
    }
}
