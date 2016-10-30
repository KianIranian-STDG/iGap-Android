package com.iGap.module;

import java.util.Comparator;

/**
 * Created by Erfan on 8/10/2016.
 */
public class CountryListComparator implements Comparator<StructCountry> {
    @Override public int compare(StructCountry structCountry, StructCountry t1)

    {
        return structCountry.getName().compareToIgnoreCase(t1.getName());
    }
}
