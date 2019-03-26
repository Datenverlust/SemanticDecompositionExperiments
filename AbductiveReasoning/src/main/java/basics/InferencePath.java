/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package basics;
import de.dailab.nsm.decomposition.graph.entities.links.*;
import de.tuberlin.spreadalgo.*;
import java.util.*;

public class InferencePath {
    private static Double hypernymSpec=0.176;
    private static Double synonymSpec=0.296;
    private static Double meronymSpec=0.500;
    private static Double hyponymSpec=0.333;
    private static Double definitionSpec=0.297;

    private List<Link> pathType;
    public List<Node> visitedNodes;
    public boolean containsHypernymLink=false;
    public boolean containsHyponymLink=false;
    public boolean containsDefintionLink=false;
    public boolean containsAntonymLink=false;
    public boolean containsArbitraryRelationsLink=false;
    public boolean containsSynonymLink=false;
    public boolean containsMeronymLink=false;
    private int nabductiveLinkSize=0;

    public InferencePath(){
        pathType=new ArrayList<>();
        visitedNodes=new ArrayList<>();
    }

    public InferencePath(InferencePath path){
        this.pathType=new ArrayList<>(path.pathType);
        this.visitedNodes=new ArrayList<>(path.visitedNodes);
        nabductiveLinkSize=0;
    }

    public InferencePath(Link link){
        pathType=new ArrayList<>();
        pathType.add(link);
        setContainLinkBoolean(link);
        visitedNodes=new ArrayList<>();
        visitedNodes.add(link.getSource());
        visitedNodes.add(link.getTarget());
        nabductiveLinkSize=0;
    }

    public void addLinkToPath(Link link){
        visitedNodes.add(link.getTarget());
//        if( pathType.size() == 0 || ! link.getClass().equals(pathType.get(pathType.size()-1).getClass()))
        pathType.add(link);
        setContainLinkBoolean(link);
    }

    private void setContainLinkBoolean(Link link) {
    if(link instanceof HyponymLink)
        containsHyponymLink=true;
    else if (link instanceof HypernymLink)
        containsHypernymLink=true;
    else if(link instanceof SynonymLink)
        containsSynonymLink=true;
    else if(link instanceof ArbitraryRelationLink)
        containsArbitraryRelationsLink=true;
    else if(link instanceof DefinitionLink)
        containsDefintionLink=true;
    else if(link instanceof AntonymLink)
        containsAntonymLink=true;
    else if(link instanceof MeronymLink)
        containsMeronymLink=true;
    }

    /*
    //todo-semantic : equality refers to semantically identity
     */
    public boolean equals(InferencePath path2){
        return this.pathType.equals(path2.pathType) && this.visitedNodes.equals(path2.visitedNodes);
    }

    public List<Link> getInferencePath(){return pathType;}

    public boolean isNodeOnPath(Node node){
        return visitedNodes.contains(node);
    }

    /*
    //todo-refactoring : part of what experiment
     */
    public static boolean pathMatchesWhiteListWithAdditionalLink_v001(InferencePath path, Link link){
        if (link instanceof AntonymLink )
            return false;
        if (((PathNode) link.getTarget()).isQuestionNode())
            return matchesWhiteList_v001(path);
        else
            return true;
    }

    public static boolean matchesWhiteList_v001(InferencePath path){
        return  (path.containsSynonymLink
                ||
                path.containsHypernymLink
                ||
                path.containsMeronymLink);
    }

    /*
    //todo-refactoring : part of what experiment
     */
    public static boolean pathMatchesWhiteListWithAdditionalLink(InferencePath path, Link link){
        if (link instanceof AntonymLink )
            return false;
        if (((PathNode) link.getTarget()).isQuestionNode() && PathMarkerPassingConfig.bAbductiveInference)
            return matchesWhiteList(path) ||
                    ( link instanceof SynonymLink || link instanceof HypernymLink || link instanceof MeronymLink);
        else
            return true;
    }

    /*
    //todo-refactoring : part of what experiment
     */
    public static boolean matchesWhiteList(InferencePath path){
        if (PathMarkerPassingConfig.bAbductiveInference) {
            return (path.containsSynonymLink
                    ||
                    path.containsHypernymLink
                    ||
                    path.containsMeronymLink);
        }else{
            return !path.containsAntonymLink;
        }
    }

    public static boolean matchesWhiteListFromQuestion(InferencePath path){
        if (PathMarkerPassingConfig.bAbductiveInference) {
            return (!path.containsHypernymLink
                    &&
                    !path.containsAntonymLink);
        }else{
            return !path.containsAntonymLink;
        }
    }


    public void setAbductiveLinkNumber(Link newLink, boolean isAnswer) {
        if(!isAnswer){
            if(newLink instanceof HyponymLink)
                nabductiveLinkSize++;
        }else{
            if(newLink instanceof SynonymLink
                    || newLink instanceof HypernymLink
                    || newLink instanceof MeronymLink)
                nabductiveLinkSize++;
        }
    }

    public int getAbductiveLinkSize() {
        return nabductiveLinkSize;
    }

    /*
    //todo: verify abduction is shown that way
     */
    public double getAbductiveValue(){
        return nabductiveLinkSize/getPathSize();
    }

    public int getPathSize(){
        return visitedNodes.size();
    }

    public double getPathSpecifity(){
        double res=0D;
        for(Link link : pathType){
           res+= InferencePath.getLinkSpecifiy(link);
        }
        return res/pathType.size();
    }

    private static double getLinkSpecifiy(Link link) {
        if(link instanceof HypernymLink)
            return hypernymSpec;
        if(link instanceof HyponymLink)
            return hyponymSpec;
        if(link instanceof SynonymLink)
            return synonymSpec;
        if(link instanceof MeronymLink)
            return meronymSpec;
        if(link instanceof DefinitionLink)
            return definitionSpec;
        return 0D;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Pathtype: ");
        pathType.forEach(link->{str.append(link.getClass().toString() + "; ");});
        str.append("visitedNodes: ");
        visitedNodes.forEach(link->{str.append(link.getClass().toString() + "; ");});
        str.append("\n");
        return new String(str);
    }

    public String printHistory() {
        StringBuilder res = new StringBuilder();
        res.append(" [ ");
        visitedNodes.forEach(node -> {
            res.append(((PathNode)node).getConcept() +" -> ");
        });
        res.append("]");
        return res.toString();
    }

}
