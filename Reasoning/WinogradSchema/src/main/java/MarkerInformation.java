/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.Concept;
import de.tuberlin.spreadalgo.Link;

import java.util.List;

/**
 * Created by Sabine on 24.06.2017.
 */
public class MarkerInformation {
    public Concept origin;
    public Double activation = 0.0;
    public List<Concept> visitedConcepts;
    public List<Link> visitedLinks;


    public List<Concept> answers;


    public MarkerInformation() {

    }

    public MarkerInformation(Concept origin, Double activation, List<Concept> visitedConcepts) {
        this.origin = origin;
        this.activation = activation;
        this.visitedConcepts = visitedConcepts;
    }

    public MarkerInformation(Concept origin, Double activation, List<Concept> visitedConcepts, List<Link> visitedLinks) {
        this.origin = origin;
        this.activation = activation;
        this.visitedConcepts = visitedConcepts;
        this.visitedLinks = visitedLinks;
    }

    public MarkerInformation(Concept origin, Double activation, List<Concept> visitedConcepts, List<Link> visitedLinks, List<Concept> answers) {
        this.origin = origin;
        this.activation = activation;
        this.visitedConcepts = visitedConcepts;
        this.visitedLinks = visitedLinks;
        this.answers = answers;
    }

    public List<Concept> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Concept> answers) {
        this.answers = answers;
    }

    public Concept getOrigin() {
        return origin;
    }

    public void setOrigin(Concept origin) {
        this.origin = origin;
    }

    public Double getActivation() {
        return activation;
    }

    public void setActivation(Double activation) {
        this.activation = activation;
    }

    public List<Concept> getVisitedConcepts() {
        return visitedConcepts;
    }

    public void setVisitedConcepts(List<Concept> visitedConcepts) {
        this.visitedConcepts = visitedConcepts;
    }

    public List<Link> getVisitedLinks() {
        return visitedLinks;
    }

    public void setVisitedLinks(List<Link> visitedLinks) {
        this.visitedLinks = visitedLinks;
    }
}
