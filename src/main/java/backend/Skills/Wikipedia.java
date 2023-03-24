package backend.Skills;

import backend.SkillWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import org.json.JSONObject;

public class Wikipedia extends SkillWrapper {

    @Override
    public void start(List<String> slots) {
        try {
            String search = slots.get(0);
            URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exsentences=2&exlimit=1&format=json&titles=" + search);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JSONObject response = new JSONObject(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
            JSONObject page = response.getJSONObject("query").getJSONObject("pages").getJSONObject(response.getJSONObject("query").getJSONObject("pages").keys().next());
            String summary = page.getString("extract").replaceAll("\\<.*?\\>", "").replaceAll("[\\r\\n]+", " ");
            if(!summary.startsWith("<!--")) {
                outputs.add(summary);
            } else {
                outputs.add("I'm sorry, I have a problem with this subject \"" + slots.get(0).replace("%20", " ") + "\". Try another one !");
            }
        } catch (Exception e) {
            outputs.add("I'm sorry, I can't find a Wikipedia page for \"" + slots.get(0) + "\".");
        }
    }
}
