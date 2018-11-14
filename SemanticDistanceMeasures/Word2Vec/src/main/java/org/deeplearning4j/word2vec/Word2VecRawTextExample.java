/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package org.deeplearning4j.word2vec;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.semanticDistanceMeasures.ReadWriteTextFile;
import de.dailab.nsm.semanticDistanceMeasures.Word2VecCosineSimilarityMeasure;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by agibsonccc on 10/9/14.
 */
public class Word2VecRawTextExample {

    private static Logger log = LoggerFactory.getLogger(Word2VecRawTextExample.class);
    static String pathtoCorpus = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec" + File.separator + "eng_news_2013_1M-sentences.txt";
    static String path2CurrentCorpus = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec" + File.separator + "currentCorpus.txt";

    public static void main(String[] args) throws Exception {
        Concept word1 = new Concept("day");
        word1.setWordType(WordType.NN);
        Concept word2 = new Concept("night");
        word2.setWordType(WordType.NN);
        //String filePath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        // SentenceIterator iter = UimaSentenceIterator.createWithPath(filePath);

        //some corpus cleaning, add own corpus here
        ReadWriteTextFile.cleanCorpus(pathtoCorpus,path2CurrentCorpus);


        SentenceIterator iter = new LineSentenceIterator(new File(path2CurrentCorpus));
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
                .vectorLength(100)
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
        String path2writeMatrix = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Word2Vec" + File.separator + word1 + "_" + word1.getWordType() + ".matrix";
        File file = new File(path2writeMatrix);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        WordVectorSerializer.writeWordVectors(vec, file.getPath());

        log.info("Save vectors....");
        WordVectorSerializer.writeWordVectors(vec, "words.txt");

        log.info("Reload vectors....");
        WordVectors wordVectors = WordVectorSerializer.loadTxtVectors(new File("words.txt"));

        //WeightLookupTable weightLookupTable = wordVectors.lookupTable();
        //Iterator<INDArray> vectors = weightLookupTable.vectors();
        //INDArray wordVector = vectors.getWordVectorMatrix("myword");
        //double[] wordVector = vectors.getWordVector("myword");


        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest(word1.getLitheral(), 10);
        System.out.println(lst);
        double cosSim = wordVectors.similarity(word1.getLitheral(), word2.getLitheral());
        System.out.println(cosSim);
        double cosSim2 = vec.similarity("day", "night");
        System.out.println(cosSim2);
    }
}
