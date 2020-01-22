/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures;


import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


/**
 * Created by faehndrich on 13.09.15.
 */

/**
 * Class to get the cosine similarity of the deeplearning4J Word2Vec approach.
 */
public class Word2VecCosineSimilarityMeasure {

    private static Logger log = LoggerFactory.getLogger(Word2Vec.class);

    String path2writeModel = null;
    String path2writeMatrix = null;
    WordVectors wordVectors = null;

    public Word2VecCosineSimilarityMeasure() {
        this.init();
    }

    private void init() {

        path2writeModel = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec";
        path2writeMatrix = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec" + File.separator + "test" + ".matrix";
        String source = "http://dainas.dai-labor.de/~faehndrich@dai/Dictionaries/Corpora/";
        String corpusName = "eng_news_2013_1M-sentences.txt";
        String currentCorpus = "currentCorpus.txt";
        Word2Vec vec = null;
        //add corpus here

        try {
            wordVectors = WordVectorSerializer.loadStaticModel(new File(path2writeModel + File.separator + currentCorpus));
        } catch (Exception e) {
            e.printStackTrace();
            vec = buildModell(source, corpusName, currentCorpus);
        }
    }

    private Word2Vec buildModell(String source, String corpusName, String currentCorpus) {
        Word2Vec vec = new Word2Vec();
        try {
            log.info("Load & Vectorize Sentences Model for Word2Vec....");
            // Strip white space before and after for each line
            SentenceIterator iter = new BasicLineIterator(path2writeModel + File.separator + corpusName);


            //ReadWriteTextFile.cleanCorpus(path2writeModel + File.separator + currentCorpus);
            //wordVectors = WordVectorSerializer.loadTxtVectors(new File(path2writeModel + File.separator + currentCorpus));

            log.info("Load data....");
            iter.setPreProcessor(new SentencePreProcessor() {
                @Override
                public String preProcess(String sentence) {
                    return sentence.toLowerCase();
                }
            });
            // Split on white spaces in the line to get words
            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());

            log.info("Building model....");
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            log.info("Fitting Word2Vec model....");
            vec.fit();

            File file = new File(path2writeModel + File.separator + currentCorpus);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
            }
            // Write word vectors
            log.info("Save vectors....");
            WordVectorSerializer.writeWord2VecModel(vec, path2writeModel + File.separator + currentCorpus);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                //DictUtil.deleteFile(path2writeModel + File.separator + currentCorpus);
                File f = new File(path2writeModel + File.separator + corpusName);
                if (f.exists() && !f.isDirectory()) {
                    // do nothing
                } else {
                    DictUtil.downloadFileParalell(source + corpusName, path2writeModel + File.separator + corpusName);
                }
                ReadWriteTextFile.cleanCorpus(path2writeModel + File.separator + corpusName, path2writeModel + File.separator + currentCorpus);
                wordVectors = WordVectorSerializer.loadStaticModel(new File(path2writeModel + File.separator + currentCorpus)); // loadTxtVectors(new File(path2writeModel + File.separator + currentCorpus));
                //DictUtil.deleteFile(path2writeModel + File.separator + corpusName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return vec;
    }

    /**
     * Find the similarity as cosine similarity with the deeplearning4j framework.
     *
     * @param c1 the concept which should be
     * @param c2
     * @return
     * @throws Exception
     */
    public double findSim(Concept c1, Concept c2) throws NullPointerException {
        double cosSim = wordVectors.similarity(c1.getLitheral(), c2.getLitheral());
        return cosSim;
    }
}
