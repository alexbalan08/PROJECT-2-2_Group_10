package backend.Skills;

import backend.SkillWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONObject;

public class Wikipedia extends SkillWrapper {

    @Override
    public void start(String matchedTemplate) {
        try {
            String searchTerm = "The_Cats";
            URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exsentences=2&exlimit=1&format=json&titles=" + searchTerm);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JSONObject response = new JSONObject(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
            JSONObject page = response.getJSONObject("query").getJSONObject("pages").getJSONObject(response.getJSONObject("query").getJSONObject("pages").keys().next());
            String summary = page.getString("extract");
            System.out.println(summary);
            outputs.add(summary);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
