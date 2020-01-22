/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.tsdr;


import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;

import com.nytlabs.corpus.*;
import org.jetbrains.annotations.NotNull;


class FileHandler {

    private Path dataDirectory;
    private Path expDirectory;
    private Path expSubdirectory;
    private String pythonScriptFile;

    FileHandler(String dataDirectoryName, String expDirectory, String pythonScriptFile) {
        this.dataDirectory = Paths.get(dataDirectoryName);
        this.expDirectory = Paths.get(expDirectory);
        this.pythonScriptFile = pythonScriptFile;
    }

    void processFiles(DataHandler dh) throws IOException {

        Map<Integer, DocumentModel> documentMap = dh.getDocumentMap();
        Iterator<File> fileIterator;

        // Filter all documents that already have been processed
        Collection<Integer> docIDsUnfiltered = dh.getAllExpDocIDs(); // Keep unfiltered list for overwrite KPE
        Collection<Integer> docIDs = new HashSet<>(docIDsUnfiltered);
        docIDs.removeIf(docID -> documentMap.containsKey(docID));


        // Create list of document XML file names
        List<String> fileNames = new ArrayList<>(docIDs.size());
        for (int docID: docIDs) {
            fileNames.add(String.format("%07d.xml", docID));
        }

        // Parse all matching files in directory
        fileIterator = FileUtils.iterateFiles(dataDirectory.toFile(), new NameFileFilter(fileNames), null);

        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            DocumentModel docModel = parseDocumentFromXML(file);
            documentMap.put(docModel.getDocID(), docModel);
        }


        // Create list of TXT files containing
        fileNames = new ArrayList<>(docIDs.size());
        for (int docID: docIDs) {   // Use unfiltered list in case of overwrite KPE
            fileNames.add(String.format("%07d_text.txt", docID));
        }

        // Save list of given docIDs to TXT file
        File docIDListFile = saveDocIDListToTXT(fileNames, dh.getExpName());

        // Save text body of documents to TXT file
        for (int docID: docIDs) {
            saveDocumentTextToTXT(documentMap.get(docID));
        }

        // Extract keyphrases from document text files in working directory
        extractKeyphrases(docIDListFile);

        // Create list of keyphrase file names
        fileNames = new ArrayList<>(docIDs.size());
        for (int docID: docIDs) {
            fileNames.add(String.format("%07d_keyphrases.csv", docID));
        }

        // Read keyphrases and their respective scores from matching CSV files
        fileIterator = FileUtils.iterateFiles(dataDirectory.toFile(), new NameFileFilter(fileNames), null);

        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            String filename = file.getName();

            // Reads the document ID corresponding to the specified file
            int docID = Integer.parseInt(filename.split("_", 2)[0]);

            readKeyphrasesFromCSV(file, documentMap.get(docID));
        }
    }


    /**
     *  Simple CSV file parser. Will throw exception if file does not match excel dialect CSV format.
     *  Expects ';' as delimiter and '\r\n' at end of line.
     *
     * @param file
     * @param docModel
     * @return
     * @throws FileNotFoundException
     */

    private void readKeyphrasesFromCSV(File file, DocumentModel docModel) throws FileNotFoundException {

        Scanner scanner = new Scanner (file);
        scanner.useDelimiter(";|\\r\\n");
        int index = 0;

        String keyphrase = null;
        Double score = null;


        while (scanner.hasNext()) {
            try {
                if (index == 0) {
                    keyphrase = scanner.next().replaceAll("\"", "");
                    index++;
                }
                if (index == 1) {
                    score = scanner.nextDouble();
                    index = 0;
                }
                docModel.addKeyphraseScorePair(keyphrase, score);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DocumentModel parseDocumentFromXML(File file) {
        NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser();

        NYTCorpusDocument nytCorpusDocument = parser.parseNYTCorpusDocumentFromFile(file, false);

        DocumentModel docModel = new DocumentModel(nytCorpusDocument.getGuid());

        docModel.setTextBody(nytCorpusDocument.getBody());
        docModel.setHeadline(nytCorpusDocument.getHeadline());
        docModel.setOnlineHeadline(nytCorpusDocument.getOnlineHeadline());

        return docModel;
    }

    private void saveDocumentTextToTXT(@NotNull DocumentModel docModel) throws IOException {

        // create Path object for txt File corresponding to the original xml file (with zero padding to match name scheme)
        Path docTextFilename = Paths.get(String.format("%07d_text.txt", docModel.getDocID()));

        File docTextFile = dataDirectory.resolve(docTextFilename).toFile();

        FileUtils.writeStringToFile(docTextFile, docModel.getTextBody(), "UTF-8");
    }


    private void extractKeyphrases(@NotNull File docIDListFile) {

        // TODO: include parameters in command
        String docIDList = docIDListFile.getAbsolutePath();
        String command = "python " + this.pythonScriptFile + " " + dataDirectory.toString() + " " + docIDList;
        String s;

        try {
            Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            Logger.outLn("Here is the standard output of the keyphrase extraction python script (if any):\n");
            while ((s = stdInput.readLine()) != null) {
                Logger.outLn(s);
            }

            // read any errors from the attempted command
            Logger.outLn("Here is the standard error of the keyphrase extraction python script (if any):\n");
            while ((s = stdError.readLine()) != null) {
                Logger.outLn(s);
            }

//            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Map<Integer,TopicDocumentAssessment> parseQrelsTXT(File file) throws FileNotFoundException{

        Map<Integer, TopicDocumentAssessment> topicDocumentAssessmentMap = new HashMap<>();

        Scanner scanner = new Scanner (file);
        scanner.useDelimiter("\\s|\\n");
        int index = 0;

        int topicID = -1;
        int docID = -1;
        int relevance = -1;

        while (scanner.hasNextInt()) {

            try {
                if (index == 0) {
                    topicID = scanner.nextInt();
                    index++;
                }
                if (index == 1) {
                    scanner.next();
                    index++;
                }
                if (index == 2) {
                    docID = scanner.nextInt();
                    index++;
                }
                if (index == 3) {
                    relevance = scanner.nextInt();
                    index = 0;
                }

                if (topicID > 0 && !topicDocumentAssessmentMap.containsKey(topicID)) {
                    topicDocumentAssessmentMap.put(topicID, new TopicDocumentAssessment(topicID));
                }

                TopicDocumentAssessment topicDocumentAssessment = topicDocumentAssessmentMap.get(topicID);
                if (topicDocumentAssessment != null && docID > 0) {
                    if (relevance == 0) topicDocumentAssessment.addIrrelevantDoc(docID);
                    if (relevance == 1) topicDocumentAssessment.addRelevantDoc(docID);
                    if (relevance == 2) topicDocumentAssessment.addHighlyRelevantDoc(docID);
                }

//                Logger.outLn(String.format("topic: %d, docID: %d, relevance: %d", topicID, docID, relevance));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return topicDocumentAssessmentMap;
    }

    private File saveDocIDListToTXT(Collection<String> fileNames, String expName) throws IOException{
        Path docIDListFilename = Paths.get(expName + "_docIDList.txt");
        File docIDListFile = expSubdirectory.resolve(docIDListFilename).toFile();

        FileUtils.writeLines(docIDListFile, "UTF-8", fileNames, "\n");

        return docIDListFile;
    }

    void initDataHandler(DataHandler dh, String qrelsTXTFile) throws FileNotFoundException {
        File qrelsTXT = new File(qrelsTXTFile);
        dh.init(parseQrelsTXT(qrelsTXT));

        this.expSubdirectory = this.expDirectory.resolve(Paths.get(dh.getExpName() + "/"));

    }

    void writeExpStatistics(DataHandler dh) throws IOException {
        Logger.outLn("--- Number of statistic lines: " + dh.getExpStatistics().size());

        StringBuilder sb = new StringBuilder();

        Path expStatisticsFilename = Paths.get(dh.getExpName() + "_expStatistics.csv");
        File expStatisticsFile = expSubdirectory.resolve(expStatisticsFilename).toFile();


        for (List<String> line: dh.getExpStatistics()) {
            for (String entry: line) {
                sb.append(entry);

                if (line.indexOf(entry) == line.size() - 1) {
                    sb.append("\n");
                }
                else {
                    sb.append(";");
                }

            }
        }

        // append=true if multiple experiments should be collect in one file
        FileUtils.writeStringToFile(expStatisticsFile, sb.toString(), "UTF-8", false);
    }


}
