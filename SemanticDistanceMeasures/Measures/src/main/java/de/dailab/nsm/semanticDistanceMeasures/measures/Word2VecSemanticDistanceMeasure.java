//package de.dailab.nsm.semanticDistanceMeasures.measures;
//
//import de.dailab.nsm.decomposition.Concept;
//import de.dailab.nsm.semanticDistanceMeasures.Word2VecCosineSimilarityMeasure;
//import de.dailab.nsm.semanticDistanceMeasures.SemanticDistanceMeasureInterface;
//
///**
// * Created by faehndrich on 10.09.15.
// */
//
//
///**
// * This is an implementation of a semantic distance Mearure which uses Word to vector neuronals networks trained on a corpus.
// * The neuronal network can be configured (layers, activation, corpus, training...) in the implementation.
// */
//public class Word2VecSemanticDistanceMeasure implements SemanticDistanceMeasureInterface {
//    Word2VecCosineSimilarityMeasure w2v =null;
//
//   public Word2VecSemanticDistanceMeasure(){
//       w2v = new Word2VecCosineSimilarityMeasure();
//   }
//    @Override
//    public double compareConcepts(Concept c1, Concept c2) {
//        try {
//            return w2v.findSim(c1,c2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Double.NaN;
//    }
//}
