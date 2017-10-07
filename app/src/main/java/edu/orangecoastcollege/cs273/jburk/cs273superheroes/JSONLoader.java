package edu.orangecoastcollege.cs273.jburk.cs273superheroes;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class loads MusicEvent data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (MusicEvent) with data.
 */

public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */

    public static List<SuperHeroes> loadJSONFromAsset(Context context) throws IOException {
        List<SuperHeroes> allHeroesList = new ArrayList<>();
        String json = null;
            InputStream is = context.getAssets().open("cs273superheroes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray allHeroesArray = rootObject.getJSONArray("CS273Superheroes");
            int length = allHeroesArray.length();

            for (int i = 0; i < length; ++i) {
                JSONObject heroObject = allHeroesArray.getJSONObject(i);
                String userName = heroObject.getString("Username");
                String name = heroObject.getString("Name");
                String superPower = heroObject.getString("Superpower");
                String oneThing = heroObject.getString("OneThing");
                SuperHeroes hero = new SuperHeroes(userName, name, superPower, oneThing);

                allHeroesList.add(hero);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allHeroesList;
    }
}
