/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SemCorXMLReader;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.*;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

/**
 * Created by root on 28.12.15.
 */
public class SensevalData {

    private static SensevalData instance = null;
    private HashMap<Integer, SVSentence> data;
    private Random rng = new Random();
    private int numberOfEntries = 0;

    private SensevalData() {
        this.data = new HashMap<>();
    }

    public static SensevalData get() {
        if (instance == null) {
            instance = new SensevalData();
            try {
                instance.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public int getSize() {
        return data.size();
    }

    private void load() throws Exception {

        final String semCorDirectory = getClass().getClassLoader().getResource("senseval2/").getPath();
        CollectionReader reader = createReader(
                SemCorXMLReader.class,
                SemCorXMLReader.PARAM_SOURCE_LOCATION, semCorDirectory,
                SemCorXMLReader.PARAM_PATTERNS, new String[]{
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.xml"}
        );
        System.out.println("Loading reader complete...");

        //TEST
        HashSet<String> postList = new HashSet<String>();

        JCasIterator iterator = new JCasIterator(reader);
        int dataIndex = 0;
        while (iterator.hasNext()) {
            JCas i = iterator.next();
            Iterator<Sentence> it = JCasUtil.iterator(i, Sentence.class);
            while (it.hasNext()) {
                Sentence s = it.next();

                // First we create a Sentence
                SVSentence sentence = new SVSentence(s.getCoveredText());

                List<WSDItem> wsdlist = JCasUtil.selectCovered(i, WSDItem.class, s);
                for (WSDItem wsdi : wsdlist) {
                    // Then we create a word
                    SVWord word = new SVWord();
                    word.setCoveredText(wsdi.getCoveredText());
                    word.setDisambiguationSubject(wsdi.getSubjectOfDisambiguation());
                    word.setPOS(wsdi.getPos());
                    //TEST
                    if (!postList.contains(wsdi.getPos()))
                        postList.add(wsdi.getPos());

                    List<WSDResult> resultlist = JCasUtil.selectCovered(i, WSDResult.class, wsdi);
                    for (WSDResult resulti : resultlist) {
                        // Then we add the sense to the word
                        int senseNum = resulti.getSenses().size();
                        for (int senseIndex = 0; senseIndex < senseNum; senseIndex++) {
                            Sense sense = resulti.getSenses(senseIndex);
                            // Create new SVSense
                            SVSense sv = new SVSense();
                            sv.setID(sense.getId());
                            sv.setConfidence(sense.getConfidence());
                            // Add Sense to the word
                            word.addSense(sv);
                        }
                    }

                    // Lastly we add the fully assembled word to the sentence
                    sentence.addWord(word);
                }

                // We now add the Sentence to our data set
                this.data.put(new Integer(dataIndex), sentence);
                dataIndex++;
            }
        }
        this.numberOfEntries = dataIndex;

        //TEST
        System.out.println("POS LIST:");
        Iterator<String> it = postList.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public SVSentence getRandomSentence() {
        int rnd = rng.nextInt(this.numberOfEntries);
        System.out.println("Random Sentence with number:" + rnd);
        return data.get(new Integer(rnd));
    }

    public SVSentence getSentence(int id) {
        if (id >= 0 && id < numberOfEntries)
            return data.get(id);
        else
            return null;
    }
}