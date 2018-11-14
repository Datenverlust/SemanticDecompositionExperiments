/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by Johannes Fähndrich on 05.07.18 as part of his dissertation.
 */
public class WinogradSchemaDataTest {

    @Test
    public void notEqual(){
        WinogradSchemaData data = new WinogradSchemaData("aa", Arrays.asList("a","b"),"a", "b","a");
        WinogradSchemaData data2 = new WinogradSchemaData("aa", Arrays.asList("a","b"),"a", "b","b");
        Assert.assertNotEquals(data,data2);
        Assert.assertNotEquals(data.hashCode(),data2.hashCode());
    }
    @Test
    public void equal(){
        WinogradSchemaData data = new WinogradSchemaData("aa", Arrays.asList("a","b"),"a", "b","a");
        WinogradSchemaData data2 = new WinogradSchemaData("aa", Arrays.asList("a","b"),"a", "b","a");
        Assert.assertEquals(data,data2);
        Assert.assertEquals(data.hashCode(),data2.hashCode());
    }

}
