/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sabine Weber on 08.09.15.
 */
public class ReadWriteTextFile {

    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void cleanCorpus(String fileName, String outputFileName) throws IOException {
        ReadWriteTextFile text = new ReadWriteTextFile();

        //treat as a large file - use some buffering
        //text.readLargerTextFile(fileName);
        List<String> lines = (text.readLargerTextFile(fileName));
        //System.out.print(lines);
        text.writeLargerTextFile(outputFileName, lines);
    }

    private static void log(Object aMsg) {
        System.out.println(String.valueOf(aMsg));
    }

    /**
     * Parsing a copus file line by line and preparing its content for the use in Deeplearning4j. Here all punctuations
     * are removed except pots. Dots are seperated by spaces before and after the dot.
     * @param aFileName The path to the corpus.
     * @return A list containing each line of the corpus.
     * @throws IOException if corpus was not readable.
     */
    List<String> readLargerTextFile(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        List<String> words = new ArrayList<String>();
        String line = new String();
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(path, ENCODING.name())) {
            while (scanner.hasNextLine()) {
                //process each line in some way
                // You may want to check for a non-line character before blindly
                // performing a replacement
                // It may also be necessary to adjust the character class
                line = scanner.nextLine();
                if(line.equals("")){
                    continue;
                }
                line = line.replaceAll("[\\p{Digit}]", "");
                line = line.replaceAll("(?!\\.)\\p{Punct}", "");
                line = line.replaceAll("([0-9]+(?:\\.[0-9]*)*)", "");
                line = line.replaceAll("\\.", "");
                line = line.replaceAll("\\, ", " , ");
                line = line.replaceAll("\\: ", " : ");
                line = line.replaceAll("\\; ", " ; ");
                line = line.replaceAll("\\'", " '");
                line = line.replaceAll("  ", "");
                line = line.replaceAll("   ", "");
                line = line.replaceAll("    ", "");
                line = line.replaceAll("\t ", "");
                line = line.replaceAll("\t", "");
                line = line.replaceAll("-", "");
                //line = line.replaceAll("\\p{Punct}+", " .");
                if (line.equals(".") || line.equals("")){
                    continue;
                }
                //line = line.replaceAll("\\.", " . ");
                words.add(line +  " . ");
                //stringBuilder.append(line);
                //stringBuilder.append(" ");
                //System.out.print(stringBuilder.toString());
                //System.out.println();
                //Concept line = Decomposition.createConcept(words[i], null);
                //definitions.add(line);


                //log(scanner.next());

            }
        }
        //words.add(line);
        return words;
    }

    void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
        Path path = Paths.get(aFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
            for (String line : aLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }


}
