/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.reasoning;
import de.kimanufaktur.markerpassing.Link;
import de.kimanufaktur.markerpassing.Marker;
import de.kimanufaktur.markerpassing.Node;
import de.kimanufaktur.markerpassing.SpreadingStep;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.graph.entities.links.AntonymLink;
import de.kimanufaktur.nsm.graph.entities.links.HypernymLink;


import java.util.*;

import static java.lang.Thread.sleep;

public class PathNode implements Node {

    public List<Marker> activationHistory = null;
    Collection<Link> links = null;
    Collection<Marker> markers = null;
    Map<Concept, Double> threshold = null;
    Concept concept = null;
    private boolean terminated = false;
    //Elements for the distance measure
    Map<Concept, Double> activation = null;
    List<InferenceCollision> inferenceCollisions = null;
    boolean containsWhiteListLink;
    List<PathMarker> tmpMarkers=null;
    PathMarker tmpMarker;

    public boolean isQuestionNode() {
        return isQuestionNode;
    }

    public void setQuestionNode(boolean quest) {
        this.isQuestionNode = quest;
    }

    private boolean isQuestionNode;

    public PathNode(Concept concept) {
        this.links = new ArrayList<>();
        this.markers = new ArrayList<>();
        this.activation = new HashMap<>();
        this.threshold = new HashMap<>();
        this.activationHistory = new ArrayList<>();
        this.inferenceCollisions = new ArrayList<>();
        this.setConcept(concept);
        this.terminated = false;
        isQuestionNode = false;
        tmpMarkers=new ArrayList<>();
    }

    public List<Marker> getActivationHistory() {
        return activationHistory;
    }

    public void setActivationHistory(List<Marker> activationHistory) {
        this.activationHistory = activationHistory;
    }

    public Map<Concept, Double> getActivation() {
        return activation;
    }

    public void setActivation(Map<Concept, Double> activation) {
        this.activation = activation;
    }

    public double getActivation(Concept originToCheck) {
        return activation.get(originToCheck);
    }

    public void addActivation(Concept origin, double activation) {
        Double activation4Concept = this.activation.get(origin);
        if (null == activation4Concept) {
            this.activation.put(origin, activation);
        } else {
            this.activation.put(origin, this.activation.get(origin) + activation);
        }
    }

    public void addActivation_v2(Concept origin, double markerActivation) {
        Double activation4Concept = this.activation.get(origin);
        if (null == activation4Concept) {
            this.activation.put(origin, markerActivation);
        } else {
            this.activation.put(origin, Double.max(markerActivation, this.activation.get(origin)));
        }
    }

    public void resetActivation() {
        activation.keySet().forEach(key -> {
            activation.put(key, 0.0D);
        });
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Map<Concept, Double> getThreshold() {
        return threshold;
    }

    public void setThreshold(Map<Concept, Double> threshold) {
        this.threshold = threshold;
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
    public boolean checkThresholds(Object originConcepts) {
        //collect all activation from the not yet processed markers.
        resetActivation();
        List<Marker> marker2remove = new ArrayList<>();
        for (Marker marker : this.getMarkers()) {
            //TODO: changed activation concept
            addActivation_v2(((PathMarker) marker).getOrigin(), ((PathMarker) marker).getActivation());
            activationHistory.add(marker);
        }

        //check threshold for each origin marker separately
        for (Concept activeConcepts : this.activation.keySet()) {
            if (this.activation.get(activeConcepts) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * in function of the double node.
     * This is an first glance at the implementation where we accept only collision,
     * from markers where one starts at the question and one at the answer
     *
     * @param stepts the processing steps which are to be included into the node.
     */
    public void in(Collection<SpreadingStep> stepts) {
        for (SpreadingStep step : stepts) {
            if (step.getTargetNode().equals(this)) {
                //the SpreadingStep contains the link the marker has been passed on.
                for (Marker m : step.getMarkings()) {
                    this.getMarkers().add(m);
                }
            }
        }
            anlyseInferenceCollisions();
    }

    /**
     * in function of the double node.
     * This is an first glance at the implementation where we accept only collision,
     * from markers where one starts at the question and one at the answer
     *
     * @param stepts the processing steps which are to be included into the node.
     */
    public void in_v06(Collection<SpreadingStep> stepts) {
        for (SpreadingStep step : stepts) {
            if (step.getTargetNode().equals(this)) {
                //the SpreadingStep contains the link the marker has been passed on.
                for (Marker m : step.getMarkings()) {
                    this.getMarkers().add(m);
                }
            }
        }
        setAbduktiveMarkers();

        anlyseInferenceCollisions_v6();
    }

    private void anlyseInferenceCollisions() {
        ArrayList<Marker> reverseMarker=new ArrayList<>();
        reverseMarker.addAll(markers);
        Collections.reverse(reverseMarker);
        for (Marker m : markers) {
            reverseMarker.remove(reverseMarker.size()-1);
            if (m instanceof PathMarker) {
                if (((PathMarker) m).startsAtAnswer()) {
                    if (InferencePath.matchesWhiteList(((PathMarker) m).getInferencePath())) {
                        for (Marker m2 : reverseMarker) {
                            if (m2 instanceof PathMarker && !m.equals(m2)) {
                                if (!((PathMarker) m2).startsAtAnswer()) {
                                    if(InferencePath.matchesWhiteListFromQuestion(((PathMarker) m2).getInferencePath())){
                                        addInferenceCollision(m, m2);
                                }   }
                            }
                        }
                    }
                }
            }
        }
    }

    private void anlyseInferenceCollisions_v2() {
        StringBuilder res= new StringBuilder();
        for ( Marker m : markers){
            if (m instanceof PathMarker){
                if (PathMarkerPassingConfig.bAbductiveInference){
                    if (((PathMarker) m).getInferencePath().containsAntonymLink)
                        break;
                }

                for (Marker m2 : markers){
                    if (PathMarkerPassingConfig.bAbductiveInference){
                        if (((PathMarker) m).getInferencePath().containsAntonymLink)
                            break;
                    }

                    if (m2 instanceof PathMarker && ! m.equals(m2)){
                        if (PathMarkerPassingConfig.bAbductiveInference){
                            if (((PathMarker) m).startsAtAnswer() && (((PathMarker) m2).startsAtAnswer()==false) ){
                                if (((PathMarker) m).getInferencePath().containsHypernymLink ) {
                                    //setNodeTermination(true);
                                    res.append("found hypernym marker collision between: \n"
                                            + "  - " + m.toString() + "\n and: \n  - " + m2.toString() + "\n");
                                    res.append("-----------------------------------------------------------------------------");
                                    res.append("\n");
                                    addInferenceCollision_v2(m,m2);

                                }
                            }else if ((((PathMarker) m).startsAtAnswer()==false) && ((PathMarker) m2).startsAtAnswer()){
                                if (((PathMarker)m).getInferencePath().containsHyponymLink) {
                                    //setNodeTermination(true);
                                    res.append("found hyponym marker collision between: \n"
                                            + "  - " + m.toString() + "\n and: \n  - " + m2.toString() + "\n");
                                    res.append("-----------------------------------------------------------------------------");
                                    res.append("\n");
                                    addInferenceCollision_v2(m2,m);
                                }
                            }
                        }else if(((PathMarker) m).startsAtAnswer()!=((PathMarker) m2).startsAtAnswer()){
                            res.append("found marker collision between: \n"
                                    + "  - " + m.toString() + "\n and: \n  - " + m2.toString() + "\n");
                            res.append("-----------------------------------------------------------------------------");
                            res.append("\n");
                            addInferenceCollision_v2(m,m2);
                        }

                    }
                }
            }
        }
    }

    private void setAbduktiveMarkers() {
        for (Marker m : markers) {
            //m.startsAtQuestion, the path has the length 1 and displays generalisation
            if (m instanceof PathMarker && !((PathMarker) m).startsAtAnswer()){
                if (((PathMarker) m).inferencePath.getAbductiveLinkSize()==1
                        && PathMarkerPassing.matchingWordType(((PathMarker) m).origin.getWordType())
                        && ((PathMarker) m).inferencePath.visitedNodes.size()==2) {
                    ((PathMarker)m).setAbduktiveNode(true);
                    ((PathMarker) m).setActivation(0.0D);
                    setNodeTermination(true);
                }
            }
        }
        resetActivation();
    }

    private void anlyseInferenceCollisions_v6() {
        ArrayList<Marker> reverseMarker=new ArrayList<>();
        reverseMarker.addAll(markers);
        Collections.reverse(reverseMarker);
        for (Marker m : markers) {
            reverseMarker.remove(reverseMarker.size()-1);
            if (m instanceof PathMarker) {
                if (((PathMarker) m).startsAtAnswer()) {
                    if (InferencePath.matchesWhiteList(((PathMarker) m).getInferencePath())) {
                        for (Marker m2 : reverseMarker) {
                            if (m2 instanceof PathMarker && !m.equals(m2)) {
                                if (!((PathMarker) m2).startsAtAnswer() && ((PathMarker) m2).getAbduktiveNode()) {
                                    addInferenceCollision_v6(m, m2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean pathCouldMatchWhitelist_v2(Marker marker, Link link) {

        boolean res=true;

        if (((PathMarker) marker).getInferencePath().isNodeOnPath(link.getTarget()))
            return false;

        if (PathMarkerPassingConfig.bAbductiveInference) {
            if (link instanceof AntonymLink || ((PathMarker) marker).getInferencePath().containsAntonymLink)
                res = false;
            else if (((PathMarker) marker).getInferencePath().containsHyponymLink && ((PathMarker) marker).startsAtAnswer())
                res = false;
            else if (((PathMarker) marker).startsAtAnswer() == false && ((PathMarker) marker).getInferencePath().containsHypernymLink)
                res = false;
        }

        if (res == false)
            ((PathMarker)marker).setActivation(0.0);

        return res;
    }

    private void addInferenceCollision_v2(Marker m, Marker m2) {
        if (PathMarkerPassingConfig.bAbductiveInference){
            containsWhiteListLink =
                    (((PathMarker) m).inferencePath.containsHypernymLink
                        || ((PathMarker) m2).inferencePath.containsHyponymLink);

        }else{
            containsWhiteListLink=true;
        }

        InferenceCollision tmp = new InferenceCollision((PathMarker) m, (PathMarker) m2, containsWhiteListLink);
        for(InferenceCollision ic: inferenceCollisions)
            if(ic.equals(tmp))
                return;
        inferenceCollisions.add(tmp);
    }

    //TODO: more generic way (whitelist pattern should only be configured at one place)
    private void addInferenceCollision(Marker m, Marker m2) {
        if (PathMarkerPassingConfig.bAbductiveInference){
            containsWhiteListLink = (((PathMarker) m).inferencePath.containsSynonymLink
                || ((PathMarker) m).inferencePath.containsHypernymLink
                || ((PathMarker) m).inferencePath.containsMeronymLink
                //path from questionMarker
                || ((PathMarker) m2).inferencePath.containsSynonymLink
                    || ((PathMarker) m2).inferencePath.containsMeronymLink
                    || ((PathMarker) m2).inferencePath.containsHyponymLink);

        }else{
            containsWhiteListLink=true;
        }
        InferenceCollision tmp = new InferenceCollision((PathMarker) m, (PathMarker) m2, containsWhiteListLink);
        for(InferenceCollision ic: inferenceCollisions)
            if(ic.equals(tmp))
                return;
        inferenceCollisions.add(tmp);
    }

    private void addInferenceCollision_v6(Marker m, Marker m2) {
        if (PathMarkerPassingConfig.bAbductiveInference){
            containsWhiteListLink = (((PathMarker) m).inferencePath.containsSynonymLink
                    || ((PathMarker) m).inferencePath.containsHypernymLink
                    || ((PathMarker) m).inferencePath.containsMeronymLink
                    || !((PathMarker)m).inferencePath.containsAntonymLink)
                    && //path from questionMarker
                    ( ((PathMarker) m2).inferencePath.containsSynonymLink
                    || ((PathMarker) m2).inferencePath.containsMeronymLink
                    || ((PathMarker) m2).inferencePath.containsHyponymLink);

        }else{
            containsWhiteListLink=true;
        }
        InferenceCollision tmp = new InferenceCollision((PathMarker) m, (PathMarker) m2, containsWhiteListLink);
        for(InferenceCollision ic: inferenceCollisions)
            if(ic.equals(tmp))
                return;
        inferenceCollisions.add(tmp);
    }

    public List<InferenceCollision> getInferenceCollisions() {
        return inferenceCollisions;
    }

    private void setNodeTermination(boolean state) {
        terminated = state;
    }

    public boolean getNodeTermination() {
        return terminated;
    }

    /**
     * The out function of the pathNode. Evaluates whether the link in question
     * could lead to a meaningful path or not
     *
     * @return the activation step for the next node.
     */
    public Collection<SpreadingStep> out() {
        tmpMarkers=new ArrayList<>();
        Collection<SpreadingStep> spreadingSteps = new ArrayList<>();
        for (Link link : getLinks()) {
            for (Marker marker : getMarkers()) {
                if (marker instanceof PathMarker && ((PathMarker) marker).getActivation() > 0.0D) {
                    if (pathCouldMatchWhitelist(marker, link)
                            && !pathAlreadyExists(marker, link)
                            && spreadingSteps.size() < PathMarkerPassing.maximalSpreadingSteps) {
                        SpreadingStep spreadingStep = new SpreadingStep();
                        spreadingStep.setLink(link);
                        spreadingStep.setMarkings(getMarkersForLink((PathMarker) marker, link));
                        spreadingStep.setInDirection(true);
                        spreadingSteps.add(spreadingStep);
                    }
                }
            }
        }
        for (Marker marker : getMarkers()) {
            ((PathMarker) marker).setActivation(0.0D);
        }
        if (spreadingSteps.size() == 0)
            setNodeTermination(true);

        return spreadingSteps;
    }

    /**
     * @return the static method InferencePath.pathMatchesWithLitsWithAdditionalLink...
     */
    private static boolean pathCouldMatchWhitelist_v001(Marker marker, Link link) {
        if (((PathMarker) marker).getInferencePath().isNodeOnPath(link.getTarget()))
            return false;
        else {
            if (((PathMarker) marker).startsAtAnswer()) {
                return InferencePath.pathMatchesWhiteListWithAdditionalLink_v001(((PathMarker) marker).getInferencePath(), link);
            }
                return ! (link instanceof AntonymLink);
            }

    }

    /**
     * @return the static method InferencePath.pathMatchesWithLitsWithAdditionalLink...
     */
    private static boolean pathCouldMatchWhitelist(Marker marker, Link link) {
        if (((PathMarker) marker).getInferencePath().isNodeOnPath(link.getTarget()))
            return false;
        else {
            if (((PathMarker) marker).startsAtAnswer()) {
                return InferencePath.pathMatchesWhiteListWithAdditionalLink(((PathMarker) marker).getInferencePath(), link);
            } else {
                //marker starts at question
                InferencePath path = ((PathMarker) marker).getInferencePath();
                if (PathMarkerPassingConfig.bAbductiveInference) {
                    return !path.containsAntonymLink && !path.containsHypernymLink
                            && !(link instanceof AntonymLink) && !(link instanceof HypernymLink);
                } else {
                    return !path.containsAntonymLink && !(link instanceof AntonymLink);
                }
            }
        }
    }

    private boolean pathAlreadyExists(Marker marker, Link link) {
        tmpMarker = new PathMarker((PathMarker) marker, link);
        for (Marker marker1 : getMarkers())
            if (marker1 instanceof PathMarker && tmpMarker.equals((PathMarker)marker1))
                return true;
            else
                tmpMarkers.add((PathMarker) tmpMarker);
        return false;
    }

    /**
     * get Markers for the given link.
     *
     * @param link the link to get the marker for
     * @return list of markers to be bast to the given link.
     */
    public Collection<Marker> getMarkersForLink(PathMarker marker, Link link) {
        Collection<Marker> markers = new ArrayList<>();
        PathMarker marker4link = new PathMarker(marker, link);
        marker4link.setActivation(1.0D);
        markers.add(marker4link);
        return markers;
    }

    public void setActivation(Concept origin, double activation) {
        this.activation.put(origin, activation);
    }

    public double getDoubleActivation() {
        double result = 0;
        Set<Concept> sources = new HashSet<>();
        //TODO: this.activationHistory
        for (Marker m : this.markers) {
            if (m instanceof PathMarker) {
                PathMarker markerWithOrigin = (PathMarker) m;
                sources.add(markerWithOrigin.getOrigin());
                result += markerWithOrigin.getActivation();
            }
        }
        if (sources.size() < 2) {
            result = 0;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(concept.getLitheral());
        if (isQuestionNode)
            res.append("         ; QuestionNode");
        return res.toString();
    }

}

