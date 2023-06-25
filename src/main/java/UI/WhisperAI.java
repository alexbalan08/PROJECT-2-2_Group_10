package UI;

import javax.sound.sampled.*;
import java.io.*;

public class WhisperAI {

        public static void main(String[] args) throws IOException {
            WhisperAI test = new WhisperAI();
            test.record();
            test.startASR();
        }

        public void record (){
            int SAMPLE_RATE = 16000;
            try {
                // Open the audio input line

                AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);

                // Start capturing the audio
                line.start();

                System.out.println("Recording started. Press Enter to stop.");

                // Create a byte array to store the captured audio
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Read the captured audio and write it to the output stream
                while (HelloApplication.getInstance().counter == 1) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Stop capturing the audio
                line.stop();
                line.close();

                System.out.println("Recording stopped.");

                // Save the captured audio to a file
                byte[] audioData = outputStream.toByteArray();
                AudioInputStream audioStream = new AudioInputStream(
                        new ByteArrayInputStream(audioData), format, audioData.length);
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File("C:\\Users\\simon\\OneDrive\\Documents\\Sound Recordings\\Recording.m4a"));

                System.out.println("Recording saved to output.wav");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public String startASR() throws IOException {
            try {
                // Specify the directory for the cmd process
                File directory = new File("C:/Users/simon/OneDrive/Documents/Sound Recordings");

                // Create the ProcessBuilder with the command you want to execute
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "whisper", "Recording.m4a", "--language", "English", "--model", "base.en","--output_format", "txt");

                // Set the directory for the process
                processBuilder.directory(directory);

                // Redirect the output of the command to the Java process
                processBuilder.redirectErrorStream(true);

                // Start the process
                Process process = processBuilder.start();

                // Read the output of the process
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            BufferedReader br = new BufferedReader(new FileReader("C:/Users/simon/OneDrive/Documents/Sound Recordings/Recording.txt"));
            System.out.println("Extracted text: ");
            String line;
            while ((line = br.readLine()) != null) {
                return line;
            }
            return "Sorry, couldn't understand you...";
        }
    }

