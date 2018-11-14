/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by faehndrich on 30.09.16.
 * This example Winograd data set is taken from: http://www.cs.nyu.edu/faculty/davise/papers/WinogradSchemas/WS.html
 * @ARTICLE{2016arXiv160801884D,
author = {{Davis}, E.},
title = "{Winograd Schemas and Machine Translation}",
journal = {ArXiv e-prints},
archivePrefix = "arXiv",
eprint = {1608.01884},
primaryClass = "cs.AI",
keywords = {Computer Science - Artificial Intelligence, Computer Science - Computation and Language},
year = 2016,
month = aug,
adsurl = {http://adsabs.harvard.edu/abs/2016arXiv160801884D},
adsnote = {Provided by the SAO/NASA Astrophysics Data System}
}

 */
public class WinogradSchemaTrainDataSet implements WinogradSchemaDataSet {
    @Override
    public Collection<WinogradSchemaQuestion> ReadExampleDataSet() {
        String u = this.getClass().getResource("/WinogradSchema_Train.csv").getPath();
        List<WinogradSchemaQuestion> result = new ArrayList<>(70);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(u));
            String[] nextLine;
            while((nextLine=reader.readNext())!=null)
            {
                String sentence = nextLine[0];
                nextLine=reader.readNext();
                String conceptOfInterest = nextLine[0];
                nextLine=reader.readNext();
                Map<String, Boolean> answers = new HashMap<>(2);
                String[] answersList = nextLine;
                nextLine=reader.readNext();
                String trueAnswer = nextLine[0];
                for (String answer : answersList) {
                    if(answer.equals(trueAnswer)){
                        answers.put(answer,true);
                    }else {
                        answers.put(answer,false);
                    }

                }
                WinogradSchemaQuestion winogradSchemaQuestion = new WinogradSchemaQuestion(sentence, conceptOfInterest, answers);
                result.add(winogradSchemaQuestion);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
