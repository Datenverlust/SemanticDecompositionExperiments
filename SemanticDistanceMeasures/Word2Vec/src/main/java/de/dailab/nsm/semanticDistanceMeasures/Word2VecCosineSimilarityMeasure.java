/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures;


import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decompostion.Dictionaries.DictUtil;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by faehndrich on 13.09.15.
 */

/**
 * Class to get the cosine similarity of the deeplearning4J Word2Vec approach.
 */
public class Word2VecCosineSimilarityMeasure {

    private static Logger log = LoggerFactory.getLogger(Word2Vec.class);

    String path2writeVector = null;
    String path2writeMatrix = null;
    WordVectors wordVectors = null;
    public Word2VecCosineSimilarityMeasure(){
        this.init();
    }

    private void init() {

        path2writeVector = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec";
        path2writeMatrix = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec" + File.separator + "test" + ".matrix";
        String source = "http://dainas.dai-labor.de/~faehndrich@dai/Dictionaries/Corpora/";
        String corpusName = "eng_news_2013_1M-sentences.txt";
        String currentCorpus = "currentCorpus.txt";

        //add corpus here
        try {

//            ReadWriteTextFile.cleanCorpus(path2writeVector + File.separator + currentCorpus);
            wordVectors = WordVectorSerializer.loadTxtVectors(new File(path2writeVector + File.separator + currentCorpus));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                DictUtil.deleteFile(path2writeVector+ File.separator + currentCorpus);
                DictUtil.downloadFileParalell(source + corpusName, path2writeVector+ File.separator + corpusName);
                ReadWriteTextFile.cleanCorpus(path2writeVector+ File.separator + corpusName, path2writeVector + File.separator + currentCorpus);
                wordVectors = WordVectorSerializer.loadTxtVectors(new File( path2writeVector + File.separator + currentCorpus));
                DictUtil.deleteFile(path2writeVector+ File.separator + corpusName);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        SentenceIterator iter = new LineSentenceIterator(new File(path2writeVector));
        iter.setPreProcessor(new SentencePreProcessor() {
            @Override
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        InMemoryLookupCache cache = new InMemoryLookupCache();
        WeightLookupTable table = new InMemoryLookupTable.Builder()
                .vectorLength(10)
                .useAdaGrad(false)
                .cache(cache)
                .lr(0.025f).build();

        log.info("Building model....");
        ArrayList<String> stopWords = new ArrayList<>();
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("the");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5).iterations(1)
                .layerSize(100).lookupTable(table)
                .stopWords(stopWords)
                .vocabCache(cache).seed(42)
                .windowSize(5).iterate(iter).tokenizerFactory(t).build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");
        // Write word

        File file = new File(path2writeMatrix);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        try {
            WordVectorSerializer.writeWordVectors(vec, file.getPath());
            log.info("Save vectors....");
            WordVectorSerializer.writeWordVectors(vec, path2writeVector);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("The path to the vector file is wrong...");
            System.exit(-1);
        }

    }

    /**
     * Find the similarity as cosine similarity with the deeplearning4j framework.
     * @param c1 the concept which should be
     * @param c2
     * @return
     * @throws Exception
     */
    public double findSim(Concept c1, Concept c2) throws Exception {
        double cosSim = wordVectors.similarity(c1.getLitheral(), c2.getLitheral());
        return cosSim;
    }
}
