package backend.Skills;

import backend.SkillWrapper;
import com.google.gson.JsonParser;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// I = "18942c9e84434efbb15ad08c82cd1ee3";
// S = "6e25829074f54e10a4811b5cb39e5622";
// TODO: REFRESH TOKENS OR AVAILABILITY FOR ALL DEVICES
// Getting tokens: https://alecchen.dev/spotify-refresh-token/

public class Spotify extends SkillWrapper {

    private String actualMusic = "";
    private final String accessToken = "BQBQMbLo3dh6MzESpW9UdmEujszrSJx8qlgxuK95uv-Rp5zCwP0B8F0Zctx3D997Y--j_CXRQjWNVWdKAcqYSyCR4DysoM0pjBJYz23m_8BJ6LoDl5Cx8l1j-ymXtezL6fsr3Aw9TOc2xz4aCttYY5-A0UV4SxyjGSccaakiuPeVzDq8Yicd6ZJIwlzE-Sz0ectK-FAmXjhnJhVVJITHyMkPd79S78T371VckJwgsVnpLXPnsHmCcEndppLid2VJJGx1haTfm1Td5CTYYv1z06WZejz0cuEzgp1LuC1EdsbtgYtmxDEJHskAM6jfswWZ4lU4WROg6Ole1gnCKmTGBg";
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()/*.setRefreshToken("refreshtokenhere")*/.setAccessToken(this.accessToken).build();

    // slots : [<ACTION>, optional <TITLE>]
    @Override
    public void start(String[] slots) {
        String action = slots[0];
        if (slots.length == 2) {
            this.actualMusic = slots[1];
        }

        if (Objects.equals(action, "play")) {
            outputs.add(playSong(getSongID(this.actualMusic)));
        } else if (Objects.equals(action, "resume") || Objects.equals(action, "replay")) {
            outputs.add(resumeMusic());
        } else if (Objects.equals(action, "pause") || Objects.equals(action, "stop")) {
            outputs.add(pause());
        } else {
            outputs.add(getActualMusicInformation());
        }
    }

    private String getSongID(String song) {
        try {
            SearchTracksRequest request = spotifyApi.searchTracks(song).build();
            
            // Execute the request and get the first page of results
            Paging<Track> trackPaging = request.execute();
            Track[] tracks = trackPaging.getItems();

            if (tracks.length > 0) {
                // Print the name and ID of the first matching track to the console
                Track track = tracks[0];
                return track.getId();
            } else {
                System.out.println("No tracks found with that name.");
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private String playSong(String s) {
        try {
            // Set up the request to get a specific track by ID
            GetTrackRequest request = spotifyApi.getTrack(s).build();

            // Execute the request and get the track object
            Future<Track> future = request.executeAsync();
            Track track = future.get();

            // Print the name of the track to the console
            System.out.println("Now playing: " + track.getName());
            this.actualMusic = track.getName();

            //System.out.println(track.getUri());
            StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                    .startResumeUsersPlayback()
                    .uris(JsonParser.parseString("[\"" + track.getUri() + "\"]").getAsJsonArray())
                    .build();
            startResumeUsersPlayback_Sync(startResumeUsersPlaybackRequest);
            return "Now playing \"" + this.actualMusic + "\", by " + getFormattedArtists(track.getArtists()) + ".";
        } catch (InterruptedException | ExecutionException e) {
            return "I can't find the song ...";
        }
    }

    private String resumeMusic() {
       try {
           startResumeUsersPlayback_Sync(spotifyApi.startResumeUsersPlayback().build());
           return "The actual music is resumed";
       } catch (Exception e) {
           return "I can't resume the music. It's maybe because there isn't a music at the moment.";
       }
    }

    private String pause() {
        try {
            PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback().build();
            pauseUsersPlaybackRequest.execute();
            return "The music is paused.";
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return "I can't pause the music. It's maybe because there isn't a music playing at the moment.";
        }
    }

    private String getActualMusicInformation() {
        try {
            GetTrackRequest request = spotifyApi.getTrack(getSongID(this.actualMusic)).build();
            Future<Track> future = request.executeAsync();
            Track track = future.get();
            return "The actual music is \"" + track.getName() + "\", by " + getFormattedArtists(track.getArtists()) + ".";
        } catch (InterruptedException | ExecutionException e) {
            return "There is no music at the moment.";
        }
    }

    private void startResumeUsersPlayback_Sync(StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest) {
        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getFormattedArtists(ArtistSimplified[] artists) {
        StringBuilder result = new StringBuilder();
        for (ArtistSimplified artist : artists) {
            result.append(artist.getName()).append(", ");
        }
        return result.substring(0, result.toString().lastIndexOf(", "));
    }


}