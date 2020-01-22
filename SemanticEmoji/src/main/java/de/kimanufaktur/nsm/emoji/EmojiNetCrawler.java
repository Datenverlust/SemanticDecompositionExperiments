package de.kimanufaktur.nsm.emoji;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.WordType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.io.FileReader;

import org.neo4j.ogm.json.JSONArray;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;


public class EmojiNetCrawler {
    List<Emoji> emojiNetDictionary = new ArrayList<Emoji>();

    public void init() {
        //String path = new File("Experiments/SemanticEmoji2/data/emojis.json").getAbsolutePath();
        String path = new File("Experiments/SemanticEmoji2/data/emojis_selection_updated_keywords.json").getAbsolutePath();

        try {
            System.out.println("Initialize EmojiNet Dictionary ...");
            String content = readFile(path, StandardCharsets.UTF_8);
            JSONArray jsonarray = new JSONArray(content);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String unicode = jsonobject.getString("unicode");
                // String category = jsonobject.getString("category");
                //String definition = jsonobject.getString("definition");
                //String name = jsonobject.getString("name");
                List<String> keywords = parseJsonArrayToList(jsonobject.getJSONArray("keywords"));

                Emoji emoji = new Emoji(unicode, keywords);
                this.emojiNetDictionary.add(emoji);
            }
            System.out.println("Finished. Loaded " + jsonarray.length() + " Emojis to Dictionary.");

            //Decomposition decomposition = new Decomposition();
            //decomposition.init();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static List<String> parseJsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        //JSONArray jArray = (JSONArray) jsonobject.get("keywords");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public List<Concept> decompose(String unicode) {

        Emoji emoji = this.findEmoji(unicode);
        List<Concept> concepts = new ArrayList<>();
        Decomposition decomposition = new Decomposition();
        decomposition.init();
        //String definition = emoji.getDefinition();
        List<String> keywords = emoji.getKeywords();
        //String definition = emoji.getName();

        //definition = definition.toLowerCase().replaceAll("http://emojipedia.org/[A-Za-z0-9_-]*/", "");

        //for (String s : definition.split("\\W")) {
        //  Concept concept = decomposition.multiThreadedDecompose(s, WordType.UNKNOWN, 2);
        //    concepts.add(concept);
        //}

        for(int i=0; i<keywords.size(); i++){
            String keyword = keywords.get(i);
            System.out.println("New Keyword " + keyword);
            //Concept concept = decomposition.multiThreadedDecompose(keyword, WordType.UNKNOWN, 2);
            Concept concept = decomposition.decompose(keyword, WordType.UNKNOWN, 1);
            concepts.add(concept);
        }
        System.out.println("Finished EmojiNetCrawler");
        return concepts;
    }

    String getDefinition(String unicode){
        Emoji emoji = this.findEmoji(unicode);
        return emoji.getDefinition().toLowerCase().replaceAll("http://emojipedia.org/[A-Za-z0-9_-]*/", "");
    }

    private Emoji findEmoji(String unicode) {
        if (this.emojiNetDictionary == null) {
            this.init();
        }
        for (Emoji e : this.emojiNetDictionary) {
            /**
            if (e.getUnicode().contains(unicode)) {
                return e;
            }
             **/
            if (e.getUnicode().equals(unicode)) {
                return e;
            }
        }
        System.out.println("Error. Emoji " + unicode + " not found.");
        return null;
    }
}
