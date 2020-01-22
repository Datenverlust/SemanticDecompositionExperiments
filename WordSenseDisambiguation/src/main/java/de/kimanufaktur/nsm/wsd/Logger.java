/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by root on 27.03.16.
 */
public class Logger {


    private static Logger instance = null;
    private static String path = System.getProperty("user.home").toString() + File.separator + ".decomposition" + File.separator + "WSD";//"logging//";//"/home/jkz/Documents/Uni/Bachelor/calculations";

    private Logger() {

    }

    public static Logger log() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    public static synchronized void logMeanGeneration(int generation, double mean, double best) {
        try {
            String str = generation + ":" + mean + ":" + best + "\n";
            Files.write(Paths.get(path + "/log.txt"), str.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
        }
    }

    public static synchronized void logBestCandidate(int generation, TypedMarkerPassingConfig config) {
        try (ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(path + File.separator + "gen_" + generation + ".wsd"))) {
            write.writeObject(config);
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
        } catch (IOException eio) {
            eio.printStackTrace();
        }
    }

    public static void logGeneration(int generation, double mean, double best) {
        try {
            String output = generation + ":" + mean + ":" + best + "\n";
            Files.write(Paths.get(path + File.separator + "log.txt"), output.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println("There was an error logging a generation!");
        }
    }

    public static void logSolution(int sentence, double fitness, String name) {
        String f = null;
        String output = null;
        try {
            System.out.println("[Solution]: " + sentence + " : " + fitness);
            output = sentence + ":" + fitness + "\n";
            f = path + File.separator + "solution_" + name + ".wsd";
            Files.write(Paths.get(f), output.getBytes(), StandardOpenOption.APPEND);
        } catch (NoSuchFileException noFile) {
            try {
                Files.write(Paths.get(f), output.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("There was an error logging a solution!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was an error logging a solution!");
        }
    }

    public static void logConfig(double generation, TypedMarkerPassingConfig conf, double fitness) {
        try {
            String output = "Generation = " + generation;
            output += "\nFitness = " + fitness + "\n";
            output += "Config: \n";
            output += conf.toString();
            Files.write(Paths.get(path + File.separator + "gen_" + generation + ".wsd"), output.getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            System.out.println("Exception in saving Config!");
        }
    }

}
