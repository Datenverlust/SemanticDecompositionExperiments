/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.tsdr;

public class Logger {

    static boolean enabled = false;

    static void out(String msg) {
        if (enabled) {
            System.out.print(msg);
        }
    }

    static void outLn(String msg) {
        if (enabled) {
            System.out.println(msg);
        }
    }
}
