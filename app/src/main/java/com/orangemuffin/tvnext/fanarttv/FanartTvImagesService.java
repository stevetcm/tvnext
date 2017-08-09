package com.orangemuffin.tvnext.fanarttv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 7/30/2017 */
public class FanartTvImagesService extends FanartTvServiceBase {
    public static String getImage(String id, String type) {
        try {
            InputStream stream = null;

            try {
                stream = downloadUrl(id);

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON Parser
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray banners = jsonObject.getJSONArray(type);
                    for (int i = 0; i < banners.length(); i++) {
                        if (banners.getJSONObject(i).getString("lang").equals("en")) {
                            String imageUrl = banners.getJSONObject(i).getString("url");
                            if (type.equals("tvposter")) {
                                imageUrl.replace("fanart/", "preview/");
                            }
                            return imageUrl;
                        }
                    }
                } catch (Exception e) { }

                return null;
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }  catch (Exception e) { }
        return null;
    }

    public static List<String> getBackgrounds(String id) {
        try {
            InputStream stream = null;

            try {
                stream = downloadUrl(id);

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                List<String> backgrounds = new ArrayList<>();

                //JSON Parser
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray banners = jsonObject.getJSONArray("showbackground");
                    for (int i = 0; i < 7; i++) {
                        backgrounds.add(banners.getJSONObject(i).getString("url"));
                    }
                } catch (Exception e) { }

                return backgrounds;
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }  catch (Exception e) { }
        return null;
    }
}
