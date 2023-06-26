package UI;

import javax.sound.sampled.*;
import java.io.*;

public class WhisperAI {
    public void record() {
        byte[] b = new byte[1024];
        ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
        try {
            AudioFormat audioSettings = new AudioFormat(16000, 16, 1, true, true);
            TargetDataLine audio = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, audioSettings));

            audio.open(audioSettings);
            audio.start();

            while (HelloApplication.getInstance().counter == 1) {
                int bytesRead = audio.read(b, 0, b.length);
                outputArray.write(b, 0, bytesRead);
            }

            audio.stop();
            audio.close();

            byte[] data = outputArray.toByteArray();
            AudioInputStream finalStream = new AudioInputStream(new ByteArrayInputStream(data), audioSettings, data.length);
            AudioSystem.write(finalStream, AudioFileFormat.Type.WAVE, new File("C:\\Users\\simon\\OneDrive\\Documents\\Sound Recordings\\Recording.m4a"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("End of the recording.");
        }
    }

    public String startASR() throws IOException {
        try {
            // Preparing a call to cmd
            ProcessBuilder cmd = new ProcessBuilder("cmd", "/c", "whisper", "Recording.m4a", "--language", "English", "--model", "base.en", "--output_format", "txt");
            cmd.directory(new File("C:/Users/simon/OneDrive/Documents/Sound Recordings"));
            cmd.redirectErrorStream(true);
            Process process = cmd.start();

            // Extracted text from command line.
            BufferedReader brCMD = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String textFromCMD;
            while ((textFromCMD = brCMD.readLine()) != null) {
                System.out.println(textFromCMD);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Reading out the transcript of the recording.
        BufferedReader brTXT = new BufferedReader(new FileReader("C:/Users/simon/OneDrive/Documents/Sound Recordings/Recording.txt"));
        System.out.println("Extracted text: ");
        String textFromTXT = "";
        String reading;
        while ((reading = brTXT.readLine()) != null) {
            textFromTXT += reading;
        }
        if (textFromTXT == "") {
            return "Sorry, ASR could not understand you!";
        }
        return textFromTXT;

    }
}

