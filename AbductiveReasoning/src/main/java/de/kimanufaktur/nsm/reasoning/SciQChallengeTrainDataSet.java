/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.reasoning;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by klempnow on 07.12.17
 * This SciQ data set is taken from: http://data.allenai.org/sciq/
 * @inproceedings{welbl,
title={Crowdsourcing Multiple Choice Science Questions},
author={Johannes Welbl, Nelson F. Liu, and Matt Gardner},
booktitle={Workshop on Noisy User-generated Text},
location={Copenhagen, Denmark},
year={2017}
}
 */
public class SciQChallengeTrainDataSet implements SciQChallengeDataSet {
    @Override
    public Collection<SciQChallengeQuestion> ReadExampleDataSet() {
        List<SciQChallengeQuestion> result = new ArrayList<>();
        Reader reader = null;
        try {
            reader = new InputStreamReader(SciQChallengeTrainDataSet.class.getResourceAsStream("/AllenAI-SciQ/train.json"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Type fooType = new TypeToken<List<SciQChallengeQuestion>>(){}.getType();
        List<SciQChallengeQuestion> emp = new ArrayList<>();

        // Get Gson object
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // parse json string to object
        result = gson.fromJson(reader, fooType);
        return result;
    }
}
