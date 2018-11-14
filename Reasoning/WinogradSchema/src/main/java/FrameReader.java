/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Hannes on 02.04.2017.
 */
public class FrameReader implements RoleSet{
    @Override

    public Map<String, List<String>> readRoleSet(String verb){

        String nr=null;
        String role;
        String vRole;
        String[] filename=verb.split(("[\\p{Punct}]+"));
        Map<String, List<String>> result=new HashMap<>();
        List<String> roleList=new ArrayList<>();
        String u;
        if(this.getClass().getResource("/frames/" + filename[0] + ".xml")==null){
            return null;
        } else{ u=this.getClass().getResource("/frames/" + filename[0] + ".xml").getPath();}


        Boolean rightSet=false;
        XMLInputFactory factory = XMLInputFactory.newInstance();

        try{
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(u));
            while (reader.hasNext()) {
                int Event = reader.next();
                switch (Event) {
                    case XMLStreamConstants.START_ELEMENT: {
                        switch (reader.getLocalName()) {
                            case "roleset": {

                                int attributes=reader.getAttributeCount();
                                for(int i=0;i<attributes;i++) {
                                    if(reader.getAttributeLocalName(i).equals("id")){
                                        String[] target=reader.getAttributeValue(i).split(("[\\p{Punct}]+"));
                                        if(filename[filename.length-1].equals(target[target.length-1])){
                                            rightSet=true;
                                        }
                                    }
                                }
                                break;
                            }
                            case "role": {
                                if (rightSet){
                                    nr=null;
                                    role=null;
                                    roleList=new ArrayList<>();
                                    int attributes=reader.getAttributeCount();
                                    for(int i=0;i<attributes;i++) {
                                        if (reader.getAttributeLocalName(i).equals("n")) {
                                            nr=reader.getAttributeValue(i);
                                        }
                                        if(reader.getAttributeLocalName(i).equals("descr")){
                                            role=reader.getAttributeValue(i);
                                            roleList.add(role);
                                        }
                                    }

                                }
                                break;
                            }
                            case "vnrole": {
                                if(rightSet){
                                    vRole=null;
                                    int attributes=reader.getAttributeCount();
                                    for(int i=0;i<attributes;i++){
                                        if(reader.getAttributeLocalName(i).equals("vntheta")){
                                            vRole=reader.getAttributeValue(i);
                                            if(!((vRole==null) || roleList.contains(vRole))){
                                                roleList.add(vRole);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        switch (reader.getLocalName()) {
                            case "roleset": {
                                if(rightSet) return result;
                            }
                            case "role": {
                                if(rightSet){
                                    result.put("Arg"+nr,roleList);
                                }
                            }

                        }
                        break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e){
            e.printStackTrace();
        }


        return result;
    }
}
