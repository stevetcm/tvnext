package com.orangemuffin.tvnext.thetvdb;

import com.uwetrottmann.thetvdb.TheTvdb;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/* Created by OrangeMuffin on 7/28/2017 */
public class TheTvdbServiceBase {
    protected static final String API_KEY = "THETVDB_API_KEY_HERE";

    private static final TheTvdb theTvdb = new TheTvdb(API_KEY);

    protected static final TheTvdb getTheTvdb() {
        return theTvdb;
    }

    public static <T> T executeCall(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }
}
