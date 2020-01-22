/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.markerpassing.Link;
import de.kimanufaktur.markerpassing.Marker;
import de.kimanufaktur.markerpassing.SpreadingStep;
import de.kimanufaktur.nsm.decomposition.Concept;

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.graph.entities.links.*;
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.graph.entities.relations.Hyponym;
import links.NerLink;
import links.RoleLink;
import links.SyntaxLink;

import java.util.*;

/**
 * Created by Hannes on 03.04.2017.
 */
public class WinogradDoubleNode extends DoubleNodeWithMultipleThresholds {
        public List<Marker> activationHistory = null;
        Collection<Link> links = null;
        Collection<Marker> markers = null;

        static Double threshold = 0.3;
        static Double negativeThreshold = -0.3;
        Concept concept = null;

        //------------
        //Elements for the distance measure
        public List<MarkerInformation> markerInformation = null;

    public static String getLinkType(Link l){
        String returnString = "other";
        if (l instanceof SyntaxLink) {
            //System.out.println(((SyntaxLink) l).getName());
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "sytax: "+((SyntaxLink) l).getName();
        }
        if (l instanceof ArbitraryRelationLink) {
            //System.out.println(((ArbitraryRelationLink) l).getRelationName());
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "arbitrary: "+((ArbitraryRelationLink) l).getRelationName();
        }
        if (l instanceof SynonymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString= "synonym";
        }
        if (l instanceof AntonymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "antonym";
        }
        if (l instanceof HypernymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString="hyernym";
        }
        if (l instanceof Hyponym) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "hyonym";
        }
        if (l instanceof NerLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString="ner";
        }
        if (l instanceof RoleLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString="role";
        }
        if (l instanceof DefinitionLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString= "definition";
        }
        return returnString;

    }



    public WinogradDoubleNode(Concept concept) {
            this.links = new ArrayList<>();
            this.markers = new ArrayList<>();
            this.markerInformation = new LinkedList<>();
            this.activationHistory = new ArrayList<>();
            this.setConcept(concept);
        }
        public WinogradDoubleNode() {
            this.links = new ArrayList<>();
            this.markers = new ArrayList<>();
            this.markerInformation = new LinkedList<>();
            this.activationHistory = new ArrayList<>();

        }

        //-----------
        public List<Marker> getActivationHistory() {
            return activationHistory;
        }

        public void setActivationHistory(List<Marker> activationHistory) {
            this.activationHistory = activationHistory;
        }

        public List<MarkerInformation> getMarkerInformation() {return markerInformation;}

        public void setMarkerInformation(List<MarkerInformation> markerInformation) {this.markerInformation = markerInformation;}

        public void addActivation(Concept origin, double activation, List<Concept> visitedConcepts, List<Link> visitedLinks, List<Concept> answers) {
            if (this.markerInformation.isEmpty()){
                MarkerInformation markerInformation = new MarkerInformation(origin,activation,visitedConcepts, visitedLinks, answers);
                this.markerInformation.add(markerInformation);
            }
            else {
                for (MarkerInformation markerInformation : this.markerInformation) {
                    Double currentActivation = markerInformation.getActivation();
                    List<Concept> currentVisitedConcepts = markerInformation.getVisitedConcepts();
                    if (currentActivation.equals(0.0) && currentVisitedConcepts == null) {
                        markerInformation.setVisitedConcepts(visitedConcepts);
                        markerInformation.setVisitedLinks(visitedLinks);
                        markerInformation.setActivation(activation);
                        markerInformation.setAnswers(answers);
                    } else {
                        Double newActivation = currentActivation + activation;
                        markerInformation.setActivation(newActivation);
                    }
                }
            }

        }

        public Concept getConcept() {
            return concept;
        }

        public void setConcept(Concept concept) {
            this.concept = concept;
        }


        public Double getFixThreshold() {
            return threshold;
        }

        public void setFixThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public Double getNegativeThreshold() {
            return negativeThreshold;
        }

        public void setNegativeThreshold(Double negativeThreshold) {
            WinogradDoubleNode.negativeThreshold = negativeThreshold;
        }

    @Override
        public void addLink(Link link) {
            if (link != null) {
                links.add(link);
            }
        }

        @Override
        public void removeLink(Link link) {
            if (link != null && links.contains(link)) {
                links.remove(link);
            }
        }

        @Override
        public Collection<Link> getLinks() {
            return links;
        }

        public void setLinks(Collection<Link> links) {
            this.links = links;
        }

        @Override
        public Collection<Marker> getMarkers() {
            return this.markers;
        }

        public void setMarkers(Collection<Marker> markers) {
            this.markers = markers;
        }

        @Override
        public void addMarker(Marker marker) {
            if (marker != null) {
                markers.add(marker);
            }
        }

        @Override
        public void removeMarker(Marker marker) {
            if (marker != null) {
                markers.remove(marker);
            }
        }

        @Override
        public boolean checkThresholds(Object originConcept) {
            //collect all activation from the not yet processed markers.
            List<Marker> marker2remove = new ArrayList<>();
            for (Marker marker : this.getMarkers()) {
                //TODO: we do not check for special markers  yet.
                Concept origin = ((DoubleMarkerWithOrigin) marker).getOrigin();
                Double activation = ((DoubleMarkerWithOrigin) marker).getActivation();
                List<Concept> visitedConcepts = ((DoubleMarkerWithOrigin) marker).getVisitedConcepts();
                List<Link> visitedLinks = ((DoubleMarkerWithOrigin) marker).getVisitedLinks();
                List<Concept> answers = ((DoubleMarkerWithOrigin) marker).getAnswers();
                addActivation(origin, activation, visitedConcepts, visitedLinks, answers);
                activationHistory.add(marker);
                marker2remove.add(marker);
            }

            //we remove the markers we have allready looked at.
            this.getMarkers().removeAll(marker2remove);
            //check threshold for each origin marker separately
            /*for (Concept activeConcepts : this.activation.keySet()) {
                Map<Double, List<Concept>> activeConceptActivationMap = this.activation.get(activeConcepts);
                Map.Entry<Double, List<Concept>> entry = activeConceptActivationMap.entrySet().iterator().next();
                Double currentActivation = entry.getKey();
                //if (currentActivation >= this.getThreshold().get(activeConcepts)) {
                if (currentActivation >= this.getFixThreshold() || currentActivation<= this.getNegativeThreshold()){
                    return true;
                }
            }*/
            for (MarkerInformation markerInformation : this.markerInformation){
            Double currentActivation = markerInformation.getActivation();
                if (currentActivation >= this.getFixThreshold() || currentActivation<= this.getNegativeThreshold()){
                return true;
                }
            }
            return false;
        }

//
        public void in(Collection<SpreadingStep> stepts) {
            for (SpreadingStep step : stepts) {
                if (step.getTargetNode().equals(this)) {
                    //the SpreadingStep contains the link the marker has been passed on.
                    for (Marker m : step.getMarkings()) {
                        this.getMarkers().add(m);
                        DoubleMarkerWithOrigin mark = (DoubleMarkerWithOrigin) m;
                        mark.addLink(step.getLink());
                    }
                }
            }
        }

        /**
         * The out function of the double node. Here the activation is splitt amoun the links of the node.
         * The activation is reduced with each firing link. Thus after the out-function has been acitvated,
         * the activation of the node should be 0.0.
         * The links are weighted equally thus the activation of each link is activation/#links
         *
         * @return the activation step for the next node.
         */
        boolean doneThat = false;
        Map<String,String> answersToLinks = new LinkedHashMap<>();
        @Override
        public Collection<SpreadingStep> out() {

            Collection<SpreadingStep> spreadingSteps = new ArrayList<>();
            if (this.markerInformation.size() > 1) { //if this node have been activated from multiple origins
                //TODO: do something if two activation meet. For now we do nothing... thus the node does not continue firing. We should end up with all activation being in such nodes.

                //We tryed sparking and this yiels worst results.
            } else {//this node is activated by only one origin
                for (MarkerInformation markerInformation: this.markerInformation){
                    Double currentActivation = markerInformation.getActivation();
                    List<Concept> currentVisitedConcepts = markerInformation.getVisitedConcepts();
                    List<Link> currentVisitedLinks = markerInformation.getVisitedLinks();
                    List<Concept> answers = markerInformation.getAnswers();

                    //---------Evaluation for the reflux chapter---------------
//                    if (this.getConcept().getLitheral().contains("PDP")&&!doneThat){
//                      for ( Link l : getLinks()){
//                          //System.out.println(getLinkType(l));
//                          //doneThat = true;
//                      }
//                    }

                    //----------Processing of Answers-----------------------
/*                    for (Concept a : answers){
                       // System.out.println(a);
                    };*/
                    List<String> answerNamesList =  new LinkedList<>();
                    for (Concept c : answers){
                        String a = c.getLitheral().replaceAll(".*_", "");
                        answerNamesList.add(a);
                    }
                    Boolean isOnAnswer = false;
                    if (answerNamesList.contains(this.getConcept().getLitheral())){
                        //currentActivation=0.0;
                        String k = "";
                        for ( Link l : getLinks()){
                            k = k +"|"+getLinkType(l);
                            //doneThat = true;
                        }
                        answersToLinks.put(this.getConcept().getLitheral(),k);
                        isOnAnswer = true;
                    }

                    //---------------Processing of Link Names------------------
                    LinkedList<String> linkNames = new LinkedList<>();
                    for (Link l : currentVisitedLinks){
                        linkNames.add(getLinkType(l));
                    }
                    Boolean visitedRole = false;
                    /*if (linkNames.contains("role")){
                        visitedRole = true;
                    }*/
                    Boolean visitedSyntaxNsubj = false;
                    if (linkNames.contains("sytax: nsubj")){
                        visitedSyntaxNsubj = true;
                    }

                    if (!linkNames.isEmpty() && linkNames.getLast().equals("role")){
                        visitedRole = true;
                    }
                    double differentLinks = 1.0;
                    for (int i = 0; i < linkNames.size(); i++) {
                        if ((i!=linkNames.size()-1) && !linkNames.get(i).isEmpty()&& !linkNames.get(i+1).isEmpty()&& !linkNames.get(i).substring(0,3).equals(linkNames.get(i+1).substring(0,3))){
                            differentLinks++;
                        }
                    }

                    //--------------------activation---------------------------------
                    //if (currentActivation >= this.getFixThreshold() || currentActivation<= this.getNegativeThreshold()) {
                        //double activation2Pass = currentActivation / getLinks().size();
                        double activation2Pass = currentActivation*MarkerPassingConfig.getDecay();
                        /*if ((currentVisitedLinks.size()==2 || currentVisitedLinks.size()==6) && isOnAnswer) {
                            activation2Pass = currentActivation*1.5;

                        }*/
                        /*if (!linkNames.isEmpty() && linkNames.getLast().equals("ner")) {
                            activation2Pass = currentActivation*2;

                        }
                        if (!linkNames.isEmpty() && linkNames.getLast().equals("antonym")) {
                            activation2Pass = currentActivation*0.25;

                        }*/
                        /*if (differentLinks>3 && !isOnAnswer) {
                            activation2Pass = currentActivation;
                        }*/
                        for (Link link : getLinks()) {
                            if (link instanceof WeightedLink) {
                                if (getLinkType(link).equals("sytax: nsubj")&&visitedSyntaxNsubj || getLinkType(link).equals("sytax: advcl")&&visitedSyntaxNsubj){
                                    //System.out.println("BINGO!");
                                    SpreadingStep spreadingStep = new SpreadingStep();
                                    spreadingStep.setLink(link);
                                    spreadingStep.setMarkings(getMarkersForLink(link, activation2Pass, markerInformation.getOrigin(), currentVisitedConcepts,currentVisitedLinks, answers));
                                    //spreadingStep.setMarkings(getMarkersForLink(link, activation2Pass, markerInformation.getOrigin(), currentVisitedConcepts));
                                    spreadingStep.setInDirection(true);
                                    spreadingSteps.add(spreadingStep);

                                }else{
                                    SpreadingStep spreadingStep = new SpreadingStep();
                                    spreadingStep.setLink(link);
                                    spreadingStep.setMarkings(getMarkersForLink(link, activation2Pass, markerInformation.getOrigin(), currentVisitedConcepts,currentVisitedLinks, answers));
                                    //spreadingStep.setMarkings(getMarkersForLink(link, activation2Pass, markerInformation.getOrigin(), currentVisitedConcepts));
                                    spreadingStep.setInDirection(true);
                                    spreadingSteps.add(spreadingStep);
                                }

                            }
                        }
                    //}
                }
            }
            return spreadingSteps;
        }

        /**
         * get Markers for the given link. Here we use the activation with activationlvl/#marker
         * TODO: differentiate the link types.
         * TODO: choose a output weight for the different link types.
         *
         * @param link the link to get the marker for
         * @return list of markers to be bast to the given link.
         */
        public Collection<Marker> getMarkersForLink(Link link, double activation2Pass, Concept origin, List<Concept> visitedConcepts, List<Link> visitedLinks, List<Concept> answers) {
            Collection<Marker> markers = new ArrayList<>();
            DoubleMarkerWithOrigin marker4link = new DoubleMarkerWithOrigin();
            //calculate different activation depending on link type
            if (link instanceof WeightedLink) {
                marker4link.setOrigin(origin);
                marker4link.setActivation(((WeightedLink) link).getWeight() * activation2Pass); //Send the double of the normal activation to synonyms
                marker4link.addConcept(this.getConcept());
                for (Concept c : visitedConcepts){marker4link.addConcept(c);}
                for (Link l : visitedLinks){marker4link.addLink(l);}
                marker4link.setAnswers(answers);
                markers.add(marker4link);
                //System.out.println(this.getConcept().getLitheral());
            } else {
                //visitedLinks.add(link);
                marker4link.setOrigin(origin);
                marker4link.setActivation(activation2Pass); //Send the double of the normal activation to synonyms
                marker4link.setLinkType(link.getClass());
                marker4link.addConcept(this.getConcept());
                for (Concept c : visitedConcepts){marker4link.addConcept(c);}
                for (Link l : visitedLinks){marker4link.addLink(l);}
                marker4link.setAnswers(answers);
                markers.add(marker4link);
                //System.out.println(this.getConcept().getLitheral());
            }

            for (MarkerInformation markerInformation: this.markerInformation){
                if (markerInformation.getOrigin().equals(origin)){
                    Double oldActivation = markerInformation.getActivation();
                    markerInformation.setActivation(oldActivation-activation2Pass);
                }
            }

            return markers;
        }

        public double getDoubleActivation() {
            double result = 0;
            Set<Concept> sources = new HashSet<>();
            for (Marker m : this.activationHistory) {
                if (m instanceof DoubleMarkerWithOrigin) {
                    DoubleMarkerWithOrigin markerWithOrigin = (DoubleMarkerWithOrigin) m;
                    sources.add(markerWithOrigin.getOrigin());
                    result += markerWithOrigin.getActivation();
                }
            }
            if (sources.size() < 2) {
                result = 0;
            }
            return result;
        }
    }


