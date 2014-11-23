package com.paolone.dailyselfie;

import java.util.ArrayList;
import java.util.List;

/**
 * The selfies' group class, used to map the data array into groups
 */
public class SelfiesGroup {

    public String string;
    public final List<Integer> children = new ArrayList<Integer>();

    public SelfiesGroup(String string) {
        this.string = string;
    }

    public String toString() {

        return this.string;

    }

}
