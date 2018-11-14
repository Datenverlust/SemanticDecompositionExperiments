/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.data.DataSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Hannes on 07.02.2017.
 */
public class WinogradSchemaDataSetReader implements WinogradSchemaSet, DataSet {

    @Override
    public Collection<WinogradSchemaData> readPDPChallangeDataset() throws IOException {
        return readExampleDataSet("/PDPChallenge.xml");
    }

    @Override
    public Collection<WinogradSchemaData> readWSChallangeDataset() throws IOException {
        return readExampleDataSet("/WSCollection.xml");
    }

    @Override
    public Collection<WinogradSchemaData> readExampleDataSet(String path2DataSet) {


        List<WinogradSchemaData> result = new ArrayList<>();

        String read = null;
        String trueAnswer = null;
        String text = null;
        String quote1 = null;
        String quote2 = null;
        String quote = null;
        String txt1 = null;
        String txt2 = null;
        String pron = null;

        String u = this.getClass().getResource(path2DataSet).getPath();

        //String u = this.getClass().getResource("/PDPChallengeSwapped.xml").getPath();
        //String u = this.getClass().getResource("/sets_with_no_activation.xml").getPath();
        //String u = this.getClass().getResource("/lonesome.xml").getPath();
        //String u = this.getClass().getResource("/only_wrongs_after_deg.xml").getPath();
        ArrayList<String> answers = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();

        try {


            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(u));


            while (reader.hasNext()) {
                int Event = reader.next();

                switch (Event) {
                    case XMLStreamConstants.START_ELEMENT: {
                        if ("schema".equals(reader.getLocalName())) {
                            answers = null;
                        }
                        if ("collection".equals(reader.getLocalName()))
                            result = new ArrayList<>();

                        if ("text".equals(reader.getLocalName()))
                            text = null;

                        if ("answers".equals(reader.getLocalName())) {
                            answers = new ArrayList<>();
                        }
                        if ("quote".equals((reader.getLocalName()))) {
                            quote = null;
                            quote1 = null;
                            quote2 = null;
                        }
                        break;
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        read = reader.getText().trim();
                        if (text != null && (text.startsWith("\n") || text.startsWith("\r\n") || text.startsWith("\r"))) {
                            read = "";
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        switch (reader.getLocalName()) {
                            case "schema": {
                                WinogradSchemaData schema = new WinogradSchemaData(text, answers, pron, quote, trueAnswer);
                                result.add(schema);
                                break;
                            }
                            case "txt1": {
                                txt1 = read;
                                break;
                            }

                            case "txt2": {
                                txt2 = read;
                                break;
                            }

                            case "pron": {
                                pron = read;
                                break;
                            }

                            //merge sentence
                            case "text": {
                                if (txt1 == null) {
                                    text = pron + " " + txt2;
                                } else {
                                    text = txt1 + " " + pron + " " + txt2;
                                }
                                break;
                            }

                            case "quote1": {
                                quote1 = read;
                                break;
                            }

                            case "quote2": {
                                quote2 = read;
                                break;
                            }

                            //merge quote
                            case "quote": {
                                if (quote1 == null) {
                                    quote = pron + " " + quote2;
                                } else if (quote2 == null) {
                                    quote = quote1 + " " + pron;
                                } else {
                                    quote = quote1 + " " + pron + " " + quote2;
                                }
                                break;
                            }

                            case "answer": {
                                answers.add(read);
                                break;
                            }
                            case "correctAnswer": {
                                if (read.contains("A")) {
                                    trueAnswer = answers.get(0);
                                }
                                if (read.contains("B")) {
                                    trueAnswer = answers.get(1);
                                }
                                if (read.contains("C")) {
                                    trueAnswer = answers.get(2);
                                }
                                if (read.contains("D")) {
                                    trueAnswer = answers.get(3);
                                }
                                if (read.contains("E")) {
                                    trueAnswer = answers.get(4);
                                }
                                if (read.contains("F")) {
                                    trueAnswer = answers.get(5);
                                }
                                if (read.contains("G")) {
                                    trueAnswer = answers.get(6);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Collection<WinogradSchemaData> getNYUDataSet() throws IOException {
        return readExampleDataSet("WSCollection.xml");
    }

    @Override
    public Collection<WinogradSchemaData> getRahmanDataSet() throws IOException {
        return getTxtSchemaData("Rahman2012.txt");

    }

    @Override
    public Collection<WinogradSchemaData> getLevesqueDataSet() throws IOException {
        return readExampleDataSet("Levesque.xml");

    }


    public Collection<WinogradSchemaData> getWSCDataSet() throws IOException {
        return getTxtSchemaData("new5.txt");

    }


    private Collection<WinogradSchemaData> getTxtSchemaData(String dataset) throws IOException {
        //File file = new File("D:/semantic-decomposition/Reasoning/WinogradSchema/src/main/resources/new5.txt");
        Collection<WinogradSchemaData> result = new ArrayList<WinogradSchemaData>();
        String u = this.getClass().getResource("/" + dataset).getPath();
        File file = new File(u);

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer fileContents = new StringBuffer();

        String line = br.readLine();
        while (line != null) {
            if (line.equals("")) {
                line = br.readLine();
                continue;
            }
            //Read text
            String text = line;
            //read pronoun
            String pron = br.readLine();
            //read Answer possibilities
            line = br.readLine();
            String[] answersArray = line.trim().split(",");
            List<String> answers = new ArrayList<>();
            for (String answer: answersArray) {
                answers.add(answer);
            }
            //read correct answer
            String correctAnswer = br.readLine();
            WinogradSchemaData schema = new WinogradSchemaData(text, answers, pron, "", correctAnswer);
            result.add(schema);
            line = br.readLine();
        }

        br.close();


        return result;
    }


    public List<DataExample> ReadExampleDataSet() {

        List<DataExample> result = new ArrayList<DataExample>();

        try {
            //result.addAll(getNYUDataSet());
            result.addAll(getWSCDataSet());
            //result.addAll(getLevesqueDataSet());
            //result.addAll(getRahmanDataSet());
//            result.addAll(readWSChallangeDataset());
            //result.addAll(readPDPChallangeDataset());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
