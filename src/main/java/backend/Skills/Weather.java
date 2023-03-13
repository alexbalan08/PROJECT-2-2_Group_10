package backend.Skills;

import backend.SkillWrapper;

import backend.Skills.WeatherData.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather extends SkillWrapper {

    public final String APIkey = "450b386258d6eca1fe2eda0820dfa7a6";
    public String city;

    @Override
    public void start(String[] slots) {
        try {
            if(slots.length == 1) {
                city = slots[0];
            } else {
                city = slots[0];
                String time = slots[1];
            }

            String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + APIkey + "&units=metric";

            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper om = new ObjectMapper();
            WeatherData WD = om.readValue(response.toString(), WeatherData.class);

            outputs.add("At the moment, in " + city + ", it's " + WD.getMain().getTemp() + "°C.\nFeels like: " + WD.getMain().getFeels_like() + "°C.");
        } catch (Exception e) {
            outputs.add("I can't find the weather for " + city.trim().replace("\n", "") + ". This city exist ?");
        }
    }
}





