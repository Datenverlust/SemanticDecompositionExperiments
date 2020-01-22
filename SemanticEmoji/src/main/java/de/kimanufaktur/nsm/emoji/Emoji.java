package de.kimanufaktur.nsm.emoji;

import java.util.List;

public class Emoji {

    private String unicode;
    private List<String> keywords;
    private String category;
    private String definition;
    private String name;
    private List<String> adjectives;
    private List<String> verbs;

    public Emoji(String unicode, String name, String definition, List<String> keywords){
        this.unicode = unicode;
        this.keywords = keywords;
        this.definition = definition;
        this.name = name;
    }

    public Emoji(String unicode, List<String> keywords){
        this.unicode = unicode;
        this.keywords = keywords;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAdjectives() {
        return adjectives;
    }

    public void setAdjectives(List<String> adjectives) {
        this.adjectives = adjectives;
    }

    public List<String> getVerbs() {
        return verbs;
    }

    public void setVerbs(List<String> verbs) {
        this.verbs = verbs;
    }

    public List<String> getNouns() {
        return nouns;
    }

    public void setNouns(List<String> nouns) {
        this.nouns = nouns;
    }

    private List<String> nouns;

}
