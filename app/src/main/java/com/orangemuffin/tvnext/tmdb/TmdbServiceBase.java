package com.orangemuffin.tvnext.tmdb;

import com.uwetrottmann.tmdb2.Tmdb;

/* Created by OrangeMuffin on 7/30/2017 */
public class TmdbServiceBase {
    protected static final String API_KEY = "TMDB_API_KEY_HERE";

    private static final Tmdb tmdb = new Tmdb(API_KEY);

    protected static final Tmdb getTmdb() { return tmdb; }
}
