/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.AnalyseUtil;
import de.dailab.nsm.decomposition.Concept;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import se.lth.cs.srl.CompletePipeline;

import java.util.List;
import java.util.Map;

/**
 * Created by Hannes on 02.04.2017.
 */
public class SRLTest {

    public static void main(String[] args){

        String text= "Peter knows he eats a lot. Martin envies Peter because he is very skinny. Martin likes him because he plays football in a team.";
        StanfordCoreNLP pipeline= AnalyseUtil.tokenizePipeline();
        Annotation doc=AnalyseUtil.getAnnotation(text,pipeline);
        List<List<String>> tokenized=AnalyseUtil.tokenizeText(doc);
        PronConcept pron=WSTest.findPron(tokenized,"he","because he is", pipeline);
        CompletePipeline srlpipe=SemanticRoleLabeler.getPipeline();
        Map<List<Concept>, List<String>> roles=SemanticRoleLabeler.parse(tokenized, pron, srlpipe);

    }
}
