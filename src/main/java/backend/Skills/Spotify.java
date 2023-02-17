package backend.Skills;

import backend.SkillWrapper;
import com.google.gson.JsonParser;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// I = "18942c9e84434efbb15ad08c82cd1ee3";
// S = "6e25829074f54e10a4811b5cb39e5622";
// TODO: REFRESH TOKENS OR AVAILABILITY FOR ALL DEVICES
// Getting tokens: https://alecchen.dev/spotify-refresh-token/
public class Spotify extends SkillWrapper {

    // TODO: CFG
    @Override
    public void start(String matchedTemplate) {

        playSong(getSongID("Корабль в бутылке"));
        outputs.add("Playing Корабль в бутылке.");
    }

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            // .setRefreshToken("refreshtokenhere")
            .setAccessToken("BQBQMbLo3dh6MzESpW9UdmEujszrSJx8qlgxuK95uv-Rp5zCwP0B8F0Zctx3D997Y--j_CXRQjWNVWdKAcqYSyCR4DysoM0pjBJYz23m_8BJ6LoDl5Cx8l1j-ymXtezL6fsr3Aw9TOc2xz4aCttYY5-A0UV4SxyjGSccaakiuPeVzDq8Yicd6ZJIwlzE-Sz0ectK-FAmXjhnJhVVJITHyMkPd79S78T371VckJwgsVnpLXPnsHmCcEndppLid2VJJGx1haTfm1Td5CTYYv1z06WZejz0cuEzgp1LuC1EdsbtgYtmxDEJHskAM6jfswWZ4lU4WROg6Ole1gnCKmTGBg")
            .build();
    public void startResumeUsersPlayback_Sync(StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest) {
        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return;
    }
    public String getSongID(String song){
        SearchTracksRequest request = spotifyApi.searchTracks(song)
                .build();

        try {
            // Execute the request and get the first page of results
            Paging<Track> trackPaging = request.execute();
            Track[] tracks = trackPaging.getItems();

            if (tracks.length > 0) {
                // Print the name and ID of the first matching track to the console
                Track track = tracks[0];
//                System.out.println("Track name: " + track.getName());
//                System.out.println("Track ID: " + track.getId());

                return track.getId();
            } else {
                System.out.println("No tracks found with that name.");
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
    public void pause() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback()
                .build();
        try {
            pauseUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return;
    }
    private void playSong(String s) {
        // Set up the request to get a specific track by ID
        GetTrackRequest request = spotifyApi.getTrack(s).build();
        try {
            // Execute the request and get the track object
            Future<Track> future = request.executeAsync();
            Track track = future.get();

            // Print the name of the track to the console
            System.out.println("Now playing: " + track.getName());
            //System.out.println(track.getUri());
            StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                    .startResumeUsersPlayback()
                    .uris(JsonParser.parseString("[\""+ track.getUri()+"\"]").getAsJsonArray())
                    .build();
            startResumeUsersPlayback_Sync(startResumeUsersPlaybackRequest);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        }
        return;
    }

    public static void main(String[] args) {
        Spotify s = new Spotify();
        s.start("TODO");
    }


}