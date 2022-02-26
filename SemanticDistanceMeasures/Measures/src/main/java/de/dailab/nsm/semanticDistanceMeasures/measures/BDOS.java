/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.measures;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.Dictionaries.BaseDictionary;
import de.dailab.nsm.decomposition.IConcept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.exceptions.DictionaryDoesNotContainConceptException;
import de.dailab.nsm.semanticDistanceMeasures.SemanticDistanceMeasureInterface;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by faehndrich on 14.08.15.
 * Chen, D., Jianzhuo, Y., Liying, F., & Bin, S. (2009).
 * Measure semantic distance in wordnet based on directed graph search.
 * 2009 International Conference on E-Learning, E-Business,
 * Enterprise Information Systems, and E-Government,
 * EEEE 2009, 57–60. doi:10.1109/EEEE.2009.16
 */
public class BDOS implements SemanticDistanceMeasureInterface {
    static  final Logger logger = Logger.getLogger(BDOS.class);
    boolean found = false;
    BaseDictionary wordnet = null;
    public BDOS(){
        init();
    }
    public void init(){
        Decomposition.init();
        wordnet = Decomposition.getDictionaries().get(0);
    }
    int maxPath = 16;

    @Override
    public double compareConcepts(IConcept c1, IConcept c2) {
        found = false;
        double shortestPath = Double.NaN;
        int numberOfSteps = 1;

        Set<Concept> stepSet1 = new HashSet<>();
        Set<Concept> stepSet2 = new HashSet<>();
        WordType wordType1 = ((Concept)c1).getWordType();
        WordType wordType2 = ((Concept)c2).getWordType();


        //Concept root = decomposition.createConcept("entity");
        try {
            c1 = wordnet.fillConcept(((Concept)c1), wordType1);
            c2 = wordnet.fillConcept(((Concept)c2), wordType2);
            //root = wordnet.fillConcept(root, WordType.NN);
        } catch (DictionaryDoesNotContainConceptException e) {
            logger.warn(e.getMessage());
        }

        stepSet1.add(((Concept)c1));
        stepSet2.add(((Concept)c1));
        //stepSet1.addAll(this.dictionaries.get(0).getMeronyms(c1).values());
        //System.out.println(stepSet1);
        if(stepSet1.size()<1 || stepSet2.size()<1){
            return maxPath;
        }
        if (c1.equals(c2)) {
            return 0;
        } else {
            shortestPath = compareSetsRecursivly(stepSet1, wordType1, stepSet2, wordType2, numberOfSteps, shortestPath);
        }
        return (1.0-(shortestPath/maxPath));
    }



    double compareSetsRecursivly(Set<Concept> stepSet1, WordType wordType1, Set<Concept> stepSet2, WordType wordType2, double numberOfSteps, double shortestPath) {

        double newShortestPath = 0;

        while (!found && (numberOfSteps <= maxPath)) {
            Set<Concept> nextSet1 = new HashSet<>();
            Set<Concept> nextSet2 = new HashSet<>();

            nextSet1 = stepSet2;
            nextSet2 = fillSet(stepSet1, WordType.UNKNOWN);
            if(nextSet2.size()<1){
              return maxPath;//TODO: check if this is rightnumberOfSteps;
            }

            Set<Concept> tmpSet1 = new HashSet<>();
            tmpSet1.addAll(nextSet1);

            nextSet1.retainAll(nextSet2);
            if (nextSet1.isEmpty()) {
                numberOfSteps++;
                newShortestPath = compareSetsRecursivly(tmpSet1, wordType1, nextSet2, wordType2, numberOfSteps, newShortestPath);
            } else {
                found = true;
                newShortestPath = numberOfSteps;
            }


        }
        //System.out.println("shortest path in the end=" + newShortestPath);
        return newShortestPath;
    }

    /**
     * fill the set of currently looked at nodes.
     * @param set the current set, where for each node the set is extendet.
     * @param wordType the POS to consider
     * @return
     */
    Set<Concept> fillSet(Set<Concept> set, WordType wordType) {
        Set<Concept> tmpset = new HashSet<>();
        for (Concept c : set) {
            c.setWordType(wordType);
            tmpset.addAll(Decomposition.getDictionaries().get(0).getMeronyms(c));
            tmpset.addAll(Decomposition.getDictionaries().get(0).getHypernyms(c));
            tmpset.addAll(Decomposition.getDictionaries().get(0).getHyponyms(c));
        }
        //TODO: check if we can leave out concept we allready know.
        tmpset.removeAll(set);
        return tmpset;
    }

   /* public double compareConcepts(Concept c1, Concept c2) {
        double result = Double.NaN;
        int numberOfSteps = 1;
        int maxPath = 1000;

        IDictionary wordnet = dictionaries.get(0);
        Concept root = Decomposition.createConcept("entity");
        try {
            c1 = wordnet.fillConcept(c1, c1.wordType);
            c2 = wordnet.fillConcept(c2, c2.wordType);
            root = wordnet.fillConcept(root, WordType.NN);
        } catch (DictionaryDoesNotContainConceptException e) {
            e.printStackTrace();
        }
        if (c1.equals(c2)) {
            result=0;
        } else {
        //concept 1
        Set<Concept> stepSet1 = new HashSet<>();
        //stepSet1.add(c1);
        //stepSet1.addAll(c1.getMeronyms().values());
        //stepSet1.addAll(c1.getHypernyms().values());
        //stepSet1.addAll(c1.getHyponyms().values());
        //System.out.println(stepSet1);
            fillSet(stepSet1, c1.wordType);
        if (stepSet1.isEmpty()) {
            System.out.println("set1 not filled");
        }

        //concept2
        Set<Concept> stepSet2 = new HashSet<>();
        stepSet2.add(c2);
        stepSet2.addAll(c2.getMeronyms().values());
        stepSet2.addAll(c2.getHypernyms().values());
        stepSet2.addAll(c2.getHyponyms().values());
        //System.out.println(stepSet2);
        if (stepSet2.isEmpty()) {
            System.out.println("set2 not filled");
        }



        result = compareSetsRecursivly(stepSet1,c1.wordType, stepSet2, c2.wordType, numberOfSteps, maxPath);}

        return result;
    }

    private Set<Concept> fillSet(Set<Concept> stepSet, WordType wordType) {

        Set<Concept> tmpset = new HashSet<>();

        for (Concept c : stepSet) {
            c.setWordType(wordType);
            tmpset.addAll(this.dictionaries.get(0).getMeronyms(c).values());
            tmpset.addAll(this.dictionaries.get(0).getHypernyms(c).values());
            tmpset.addAll(this.dictionaries.get(0).getHyponyms(c).values());

        }
        return tmpset;
    }
    boolean found =false;
    private int compareSetsRecursivly(Set<Concept> stepSet1, WordType wordType1, Set<Concept> stepSet2, WordType wordType2,  int numberOfSteps, int shortestPath) {
        if(found){
            return shortestPath;
        }else{
        Set<Concept> tmpset1 = new HashSet<>();
        tmpset1.addAll(stepSet1);
        while (shortestPath >= numberOfSteps) {
            if(found){
                return shortestPath;
            }
            else {
                stepSet1.retainAll(stepSet2);
                if (stepSet1.isEmpty()) {
                    //System.out.println("in if-case");
                    Set<Concept> newStepSet1 = fillSet(tmpset1, wordType1);
                    Set<Concept> newStepSet2 = fillSet(stepSet2, wordType2);
                    numberOfSteps++;
                    compareSetsRecursivly(newStepSet1, wordType1, newStepSet2, wordType2, numberOfSteps, shortestPath);


                } else {
                    found = true;
                    //System.out.println("in else-case");
                    shortestPath = numberOfSteps;
                    return shortestPath;

                }
            }
        }
        return shortestPath;
        }
    }*/




}