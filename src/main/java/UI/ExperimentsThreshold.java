package UI;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExperimentsThreshold {
    private static FaceRecog faceRecog = FaceRecog.FISHER;
    private static FaceRecognizer faceRecognizer;
    private static String datasetDirectory = "ExpPosLit";
    private static int label;
    private static Map<Integer, String> personLabelName = new HashMap<>();
    private static Map<String, Integer> personNameLabel = new HashMap<>();
    private static List<Mat> faceImages = new ArrayList<>();
    private static List<Integer> faceLabels = new ArrayList<>();
    private static List<String> predictedNames = new ArrayList<>();
    private static List<Double> predictedConfidences = new ArrayList<>();
    private static Size smallestSize;
    private static List<Double> performances = new ArrayList<>();
    private static List<Double> mediumPerformances = new ArrayList<>();
    private static List<Double> lowPerformances = new ArrayList<>();
    private static List<Double> highPerformances = new ArrayList<>();

    public static void main(String[] args) {
        Loader.load(opencv_java.class);

        // Load the face recognizer model
        switch (faceRecog) {
            case LBPH -> faceRecognizer = LBPHFaceRecognizer.create();
            case EIGEN -> faceRecognizer = EigenFaceRecognizer.create();
            case FISHER -> faceRecognizer = FisherFaceRecognizer.create();
        }

        File[] peopleDirectories = (new File("src/main/resources/faceImages")).listFiles();

//        createKGroups(4, 16, shuffledPersonsData);
        String filePath = "src/main/java/UI/FISHERLightFPTPr.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            double[][][] thresholdedFPrs = null;
            double[][][] thresholdedTPrs = null;

            List<Double> bestThresholds = new ArrayList<>();
            List<Double> mediumBestThresholds = new ArrayList<>();
            List<Double> lowBestThresholds = new ArrayList<>();
            List<Double> highBestThresholds = new ArrayList<>();

            int maxTrainData = 30; // before 49
            int numLightIterations = 3;

            int sampleSize = 10;
            for (int sample = 0; sample < sampleSize; sample++) {

                HashMap<String, List<File>> shuffledPersonsData = new HashMap<>();

                personLabelName = new HashMap<>();
                personNameLabel = new HashMap<>();

                assert peopleDirectories != null;
                label = 0;

                for (File personDirectory : peopleDirectories) {
                    if (personDirectory.getName().equals("Valentina") || personDirectory.getName().equals("Expressions"))
                        continue;

                    personLabelName.put(label, personDirectory.getName());
                    personNameLabel.put(personDirectory.getName(), label);

                    List<File> shuffledPersonData = Arrays.asList(personDirectory.listFiles());
                    Collections.shuffle(shuffledPersonData);

                    shuffledPersonsData.put(personDirectory.getName(), shuffledPersonData);

                    label++;
                }

                System.out.println(sample);
                double[][][] tempThresholdedFPrs = null;
                double[][][] tempThresholdedTPrs = null;

                List<Double> tempPerformances = new ArrayList<>();
                List<Double> tempBestThresholds = new ArrayList<>();

                List<Double> tempMediumPerformances = new ArrayList<>();
                List<Double> tempMediumBestThresholds = new ArrayList<>();

                List<Double> tempLowPerformances = new ArrayList<>();
                List<Double> tempLowBestThresholds = new ArrayList<>();

                List<Double> tempHighPerformances = new ArrayList<>();
                List<Double> tempHighBestThresholds = new ArrayList<>();

                for (int numTrainData = 1; numTrainData <= maxTrainData; numTrainData++) { // Originally int numTrainData = 1; numTrainData <= 50
                    double[] thresholdedFPr = null;
                    double[] thresholdedTPr = null;
                    double[] averageLightThresholds = null;

                    for (int lightIteration = 0; lightIteration < numLightIterations; lightIteration++) {
                        faceImages = new ArrayList<>();
                        faceLabels = new ArrayList<>();
                        predictedConfidences = new ArrayList<>();
                        predictedNames = new ArrayList<>();

                        trainModelProcedure(numTrainData, lightIteration, shuffledPersonsData);

//            List<File> personTestDataset = shuffledPersonsData.get(datasetDirectory + 0);
//            personTestDataset = personTestDataset.subList(numTrainData, personTestDataset.size());

                        List<File> filesTestDataset = new ArrayList<>();
                        List<String> namesTestDataset = new ArrayList<>();
                        for (String personName : shuffledPersonsData.keySet()) {
                            List<File> shuffledPersonData = shuffledPersonsData.get(personName);
                            if (personName.contains(datasetDirectory) && !personName.equals(datasetDirectory + lightIteration)) {
                                List<File> testPersonData = shuffledPersonData.subList(0, 20);
                                filesTestDataset.addAll(testPersonData);
                                for (int i = 0; i < testPersonData.size(); i++) {
                                    namesTestDataset.add(personName);
                                }
                            } else if (maxTrainData < shuffledPersonData.size()) {
                                List<File> testPersonData = shuffledPersonData.subList(maxTrainData, shuffledPersonData.size());
                                filesTestDataset.addAll(testPersonData);
                                for (int i = 0; i < (shuffledPersonData.size() - maxTrainData); i++) {
                                    namesTestDataset.add(personName);
                                }
                            }
                        }

                        modelPredictionProcedure(filesTestDataset);

                        // Printing the labels and confidences of each test image
//            for (int i = 0; i < predictedNames.size(); i++) {
//                System.out.println(predictedNames.get(i));
//                System.out.println("True person: " + namesTestDataset.get(i));
//                System.out.println(predictedConfidences.get(i));
////                System.out.println("Confidence for " + datasetDirectory + lightIteration + ": " + predictedWantedConfidences.get(i));
//            }

                        List<Double> sortedPredictedConfidences = new ArrayList<>(predictedConfidences);
                        Collections.sort(sortedPredictedConfidences);

                        List<String> sortedPredictedNames = new ArrayList<>();
                        List<String> sortedTrueNames = new ArrayList<>();

                        for (Double confidence : sortedPredictedConfidences) {
                            int index = predictedConfidences.indexOf(confidence);
                            sortedPredictedNames.add(predictedNames.get(index));
                            sortedTrueNames.add(namesTestDataset.get(index));
                        }

                        // Update the names list with the sorted names
                        predictedConfidences = sortedPredictedConfidences;
                        predictedNames = sortedPredictedNames;
                        namesTestDataset = sortedTrueNames;

                        double[][] tempThresholdedFPTPr = getThresholdedFPTPr(namesTestDataset);

                        double[] tempThresholds = new double[predictedConfidences.size() - 1];
                        for (int threshIndex = 0; threshIndex < tempThresholds.length; threshIndex++) {
                            tempThresholds[threshIndex] = (predictedConfidences.get(threshIndex) + predictedConfidences.get(threshIndex + 1)) / 2;
                        }

                        if (averageLightThresholds == null) {
                            averageLightThresholds = new double[predictedConfidences.size() - 1];
                        }
                        if (thresholdedFPr == null) {
                            thresholdedFPr = new double[predictedConfidences.size() - 1];
                            thresholdedTPr = new double[predictedConfidences.size() - 1];
                        }
                        if (tempThresholdedFPrs == null) {
                            tempThresholdedFPrs = new double[maxTrainData][numLightIterations + 1][thresholdedFPr.length];
                            tempThresholdedTPrs = new double[maxTrainData][numLightIterations + 1][thresholdedFPr.length];
                        }
                        if (thresholdedFPrs == null) {
                            thresholdedFPrs = new double[maxTrainData][numLightIterations + 1][thresholdedFPr.length];
                            thresholdedTPrs = new double[maxTrainData][numLightIterations + 1][thresholdedFPr.length];
                        }

                        for (int threshIndex = 0; threshIndex < tempThresholds.length; threshIndex++) {
                            averageLightThresholds[threshIndex] += tempThresholds[threshIndex];
                        }

                        tempThresholdedFPrs[numTrainData - 1][lightIteration] = tempThresholdedFPTPr[0];
                        tempThresholdedTPrs[numTrainData - 1][lightIteration] = tempThresholdedFPTPr[1];

                        // Perform element-wise addition
                        for (int i = 0; i < thresholdedFPr.length; i++) {
                            thresholdedFPr[i] = thresholdedFPr[i] + tempThresholdedFPTPr[0][i];
                            thresholdedTPr[i] = thresholdedTPr[i] + tempThresholdedFPTPr[1][i];
                        }

                        double[] distances = new double[thresholdedFPr.length];
                        double minDistance = Double.MAX_VALUE;
                        int indexMinDistance = -1;
                        for (int i = 0; i < distances.length; i++) {
                            distances[i] = Math.sqrt(Math.pow(1 - tempThresholdedFPTPr[1][i], 2) + Math.pow(tempThresholdedFPTPr[0][i], 2));

                            if (distances[i] < minDistance) {
                                minDistance = distances[i];
                                indexMinDistance = i;
                            }
                        }

                        double bestLightThreshold = tempThresholds[indexMinDistance];

                        if (lightIteration == 0) {
                            tempMediumPerformances.add(1 - minDistance);
                            tempMediumBestThresholds.add(bestLightThreshold);
                        } else if (lightIteration == 1) {
                            tempHighPerformances.add(1 - minDistance);
                            tempHighBestThresholds.add(bestLightThreshold);
                        } else if (lightIteration == 2) {
                            tempLowPerformances.add(1 - minDistance);
                            tempLowBestThresholds.add(bestLightThreshold);
                        }
                    }

                    for (int threshIndex = 0; threshIndex < averageLightThresholds.length; threshIndex++) {
                        averageLightThresholds[threshIndex] = averageLightThresholds[threshIndex] / numLightIterations;
                    }

                    for (int i = 0; i < thresholdedFPr.length; i++) {
                        thresholdedFPr[i] = thresholdedFPr[i] / numLightIterations;
                        thresholdedTPr[i] = thresholdedTPr[i] / numLightIterations;
                    }

                    tempThresholdedFPrs[numTrainData - 1][numLightIterations] = thresholdedFPr;
                    tempThresholdedTPrs[numTrainData - 1][numLightIterations] = thresholdedTPr;

                    // Calculate distances
                    double[] distances = new double[thresholdedFPr.length];
                    double minDistance = Double.MAX_VALUE;
                    int indexMinDistance = -1;
                    for (int i = 0; i < distances.length; i++) {
                        distances[i] = Math.sqrt(Math.pow(1 - thresholdedTPr[i], 2) + Math.pow(thresholdedFPr[i], 2));

                        if (distances[i] < minDistance) {
                            minDistance = distances[i];
                            indexMinDistance = i;
                        }
                    }

                    double bestAverageLightThreshold = averageLightThresholds[indexMinDistance];

                    tempPerformances.add(1 - minDistance);
//                    System.out.println(minDistance);
                    tempBestThresholds.add(bestAverageLightThreshold);

//            System.out.println(thresholdedFPr[indexMinDistance]);
//            System.out.println(thresholdedTPr[indexMinDistance]);

                }

                if (performances.isEmpty())
                    performances = tempPerformances;
                else {
//                    System.out.println("Size performances and its temp:");
//                    System.out.println(performances.size());
//                    System.out.println(tempPerformances.size());

                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int performanceIndex = 0; performanceIndex < performances.size(); performanceIndex++) {
//                        System.out.println("Size: " + performances.size());
//                        System.out.println(performanceIndex);
                        double sum = performances.get(performanceIndex) + tempPerformances.get(performanceIndex);
                        sumList.add(sum);
                    }
                    performances = sumList;
                }

                if (mediumPerformances.isEmpty())
                    mediumPerformances = tempMediumPerformances;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int performanceIndex = 0; performanceIndex < mediumPerformances.size(); performanceIndex++) {
                        double sum = mediumPerformances.get(performanceIndex) + tempMediumPerformances.get(performanceIndex);
                        sumList.add(sum);
                    }
                    mediumPerformances = sumList;
                }
                if (lowPerformances.isEmpty())
                    lowPerformances = tempLowPerformances;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int performanceIndex = 0; performanceIndex < lowPerformances.size(); performanceIndex++) {
                        double sum = lowPerformances.get(performanceIndex) + tempLowPerformances.get(performanceIndex);
                        sumList.add(sum);
                    }
                    lowPerformances = sumList;
                }
                if (highPerformances.isEmpty())
                    highPerformances = tempHighPerformances;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int performanceIndex = 0; performanceIndex < highPerformances.size(); performanceIndex++) {
                        double sum = highPerformances.get(performanceIndex) + tempHighPerformances.get(performanceIndex);
                        sumList.add(sum);
                    }
                    highPerformances = sumList;
                }

                if (bestThresholds.isEmpty())
                    bestThresholds = tempBestThresholds;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int threshIndex = 0; threshIndex < bestThresholds.size(); threshIndex++) {
//                        System.out.println("Size: " + performances.size());
//                        System.out.println(performanceIndex);
                        double sum = bestThresholds.get(threshIndex) + tempBestThresholds.get(threshIndex);
                        sumList.add(sum);
                    }
                    bestThresholds = sumList;
                }
                if (mediumBestThresholds.isEmpty())
                    mediumBestThresholds = tempMediumBestThresholds;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int threshIndex = 0; threshIndex < mediumBestThresholds.size(); threshIndex++) {
                        double sum = mediumBestThresholds.get(threshIndex) + tempMediumBestThresholds.get(threshIndex);
                        sumList.add(sum);
                    }
                    mediumBestThresholds = sumList;
                }
                if (highBestThresholds.isEmpty())
                    highBestThresholds = tempHighBestThresholds;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int threshIndex = 0; threshIndex < highBestThresholds.size(); threshIndex++) {
                        double sum = highBestThresholds.get(threshIndex) + tempHighBestThresholds.get(threshIndex);
                        sumList.add(sum);
                    }
                    highBestThresholds = sumList;
                }
                if (lowBestThresholds.isEmpty())
                    lowBestThresholds = tempLowBestThresholds;
                else {
                    ArrayList<Double> sumList = new ArrayList<>();
                    for (int threshIndex = 0; threshIndex < lowBestThresholds.size(); threshIndex++) {
                        double sum = lowBestThresholds.get(threshIndex) + tempLowBestThresholds.get(threshIndex);
                        sumList.add(sum);
                    }
                    lowBestThresholds = sumList;
                }

                for (int i = 0; i < thresholdedFPrs.length; i++) {
//                    System.out.println("Length: " + tempThresholdedFPrs.length);
                    for (int j = 0; j < thresholdedFPrs[i].length; j++) {
//                        System.out.println("Length[" + i +"]: " + tempThresholdedFPrs[i].length);
                        for (int k = 0; k < thresholdedFPrs[i][j].length; k++) {
//                            System.out.println("Length[" + i +"][" + j + "]: " + tempThresholdedFPrs[i].length);
//                            System.out.println("i: " + i + ", j: " + j + ", k: " + k);
//                            System.out.println(thresholdedFPrs[i][j].length);
//                            System.out.println(tempThresholdedFPrs[i][j].length);
                            thresholdedFPrs[i][j][k] = thresholdedFPrs[i][j][k] + tempThresholdedFPrs[i][j][k];
                            thresholdedTPrs[i][j][k] = thresholdedTPrs[i][j][k] + tempThresholdedTPrs[i][j][k];
                        }
                    }
                }
            }

            System.out.println("Finished samples iterations");

            for (int i = 0; i < thresholdedFPrs.length; i++) {
                for (int j = 0; j < thresholdedFPrs[i].length; j++) {
                    for (int k = 0; k < thresholdedFPrs[i][j].length; k++) {
                        thresholdedFPrs[i][j][k] = thresholdedFPrs[i][j][k] / sampleSize;
                        thresholdedTPrs[i][j][k] = thresholdedTPrs[i][j][k] / sampleSize;
                    }
                }
            }

//            ArrayList<Double> sumList = new ArrayList<>();
//            for (int performanceIndex = 0; performanceIndex < performances.size(); performanceIndex++) {
//                performances.add(performanceIndex, performances.get(performanceIndex) / sampleSize);
//            }
            performances.replaceAll(num -> num / sampleSize);
            mediumPerformances.replaceAll(num -> num / sampleSize);
            highPerformances.replaceAll(num -> num / sampleSize);
            lowPerformances.replaceAll(num -> num / sampleSize);
            bestThresholds.replaceAll(num -> num / sampleSize);
            mediumBestThresholds.replaceAll(num -> num / sampleSize);
            highBestThresholds.replaceAll(num -> num / sampleSize);
            lowBestThresholds.replaceAll(num -> num / sampleSize);

            double[][][][] thresholdedFPTPrs = new double[][][][]{thresholdedFPrs, thresholdedTPrs};

            writer.write("FPTPrs:");
            writer.newLine();
            writer.write("FPrs:");
            writer.newLine();
            for (int i = 0; i < 2; i++) {
                writer.write("[");
                for (int trainDataIndex = 0; trainDataIndex < maxTrainData; trainDataIndex++) {
                    writer.write("[");
                    for (int lightAndTotalIterIndex = 0; lightAndTotalIterIndex < numLightIterations + 1; lightAndTotalIterIndex++) {
                        writer.write("[");
                        for (int fptprIndex = 0; fptprIndex < thresholdedFPrs[trainDataIndex][lightAndTotalIterIndex].length; fptprIndex++) {
//                            System.out.println("fptprIndex: " + fptprIndex);
                            writer.write(String.valueOf(thresholdedFPTPrs[i][trainDataIndex][lightAndTotalIterIndex][fptprIndex]));
                            if (fptprIndex != thresholdedFPrs[trainDataIndex][lightAndTotalIterIndex].length - 1)
                                writer.write(", ");
                        }
                        writer.write("]");
                        if (trainDataIndex != numLightIterations)
                            writer.write(", ");
                    }
                    writer.write("]");
                    if (trainDataIndex != maxTrainData - 1)
                        writer.write(", ");
                }
                writer.write("]");
                writer.newLine();
                if (i == 0) {
                    writer.write("TPrs:");
                    writer.newLine();
                }
            }

            System.out.println("PERFORMANCES:");
            writer.newLine();
            writer.write("PERFORMANCES:");
            writer.newLine();
            writer.write("[");
            for (int performanceIndex = 0; performanceIndex < performances.size(); performanceIndex++) {
                writer.write(String.valueOf(performances.get(performanceIndex)));
                if (performanceIndex != performances.size() - 1)
                    writer.write(", ");
                System.out.println(performances.get(performanceIndex));
            }
            writer.write("]");

            System.out.println("THRESHOLDS:");
            writer.newLine();
            writer.write("THRESHOLDS:");
            writer.newLine();
            writer.write("[");
            for (int threshIndex = 0; threshIndex < bestThresholds.size(); threshIndex++) {
                writer.write(String.valueOf(bestThresholds.get(threshIndex)));
                if (threshIndex != bestThresholds.size() - 1)
                    writer.write(", ");
                System.out.println(bestThresholds.get(threshIndex));
            }
            writer.write("]");

            System.out.println("MEDIUM PERFORMANCES:");
            writer.newLine();
            writer.write("MEDIUM PERFORMANCES:");
            writer.newLine();
            writer.write("[");
            for (int performanceIndex = 0; performanceIndex < mediumPerformances.size(); performanceIndex++) {
                writer.write(String.valueOf(mediumPerformances.get(performanceIndex)));
                if (performanceIndex != mediumPerformances.size() - 1)
                    writer.write(", ");
                System.out.println(mediumPerformances.get(performanceIndex));
            }
            writer.write("]");

            System.out.println("MEDIUM THRESHOLDS:");
            writer.newLine();
            writer.write("MEDIUM THRESHOLDS:");
            writer.newLine();
            writer.write("[");
            for (int threshIndex = 0; threshIndex < mediumBestThresholds.size(); threshIndex++) {
                writer.write(String.valueOf(mediumBestThresholds.get(threshIndex)));
                if (threshIndex != mediumBestThresholds.size() - 1)
                    writer.write(", ");
                System.out.println(mediumBestThresholds.get(threshIndex));
            }
            writer.write("]");

            System.out.println("HIGH PERFORMANCES:");
            writer.newLine();
            writer.write("HIGH PERFORMANCES:");
            writer.newLine();
            writer.write("[");
            for (int performanceIndex = 0; performanceIndex < highPerformances.size(); performanceIndex++) {
                writer.write(String.valueOf(highPerformances.get(performanceIndex)));
                if (performanceIndex != highPerformances.size() - 1)
                    writer.write(", ");
                System.out.println(highPerformances.get(performanceIndex));
            }
            writer.write("]");

            System.out.println("HIGH THRESHOLDS:");
            writer.newLine();
            writer.write("HIGH THRESHOLDS:");
            writer.newLine();
            writer.write("[");
            for (int threshIndex = 0; threshIndex < highBestThresholds.size(); threshIndex++) {
                writer.write(String.valueOf(highBestThresholds.get(threshIndex)));
                if (threshIndex != highBestThresholds.size() - 1)
                    writer.write(", ");
                System.out.println(highBestThresholds.get(threshIndex));
            }
            writer.write("]");

            System.out.println("LOW PERFORMANCES:");
            writer.newLine();
            writer.write("LOW PERFORMANCES:");
            writer.newLine();
            writer.write("[");
            for (int performanceIndex = 0; performanceIndex < lowPerformances.size(); performanceIndex++) {
                writer.write(String.valueOf(lowPerformances.get(performanceIndex)));
                if (performanceIndex != lowPerformances.size() - 1)
                    writer.write(", ");
                System.out.println(lowPerformances.get(performanceIndex));
            }
            writer.write("]");

            System.out.println("LOW THRESHOLDS:");
            writer.newLine();
            writer.write("LOW THRESHOLDS:");
            writer.newLine();
            writer.write("[");
            for (int threshIndex = 0; threshIndex < lowBestThresholds.size(); threshIndex++) {
                writer.write(String.valueOf(lowBestThresholds.get(threshIndex)));
                if (threshIndex != lowBestThresholds.size() - 1)
                    writer.write(", ");
                System.out.println(lowBestThresholds.get(threshIndex));
            }
            writer.write("]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static void createKGroups(int k, int trainPicsPerPerson, HashMap<String, List<File>> shuffledPersonsData) {
//        int picsPerPerson = trainPicsPerPerson + (trainPicsPerPerson / (k - 1));
//
//        for (String personName : shuffledPersonsData.keySet()) {
//            dataSize += shuffledPersonsData.get(personName).size();
//        }
//
//        HashMap<File, String>[] shuffledKGroupsData = new HashMap[k];
//        for (String personName : shuffledPersonsData.keySet()) {
//            if (personName.equals("ExpPosLit")) {
//                int picsPerGroup = picsPerPerson / k;
//                for (int i = 0; i < k; i++) {
//                    System.out.println("i: " + i);
//                    for (int indexSPD = i * picsPerGroup; indexSPD < (i + 1) * picsPerGroup; indexSPD++) {
//                        System.out.println("indexSPD: " + indexSPD);
//                        if (shuffledKGroupsData[i] == null)
//                            shuffledKGroupsData[i] = new HashMap<>();
//                        shuffledKGroupsData[i].put(shuffledPersonsData.get(personName).get(indexSPD), personName);
//                    }
//
//                    if ((i == k - 1) && picsPerPerson % k != 0) {
//                        int j = 0;
//                        for (int indexSPD = (i + 1) * picsPerGroup; indexSPD < picsPerPerson; indexSPD++) {
//                            System.out.println("J leftover: " + j);
//                            System.out.println("indexSPD: " + indexSPD);
//                            if (shuffledKGroupsData[j] == null)
//                                shuffledKGroupsData[j] = new HashMap<>();
//                            shuffledKGroupsData[j].put(shuffledPersonsData.get(personName).get(indexSPD), personName);
//                            j++;
//                        }
//                    }
//                }
//            } else {
//
//            }
//        }
//
//        int index = 0;
//        for (HashMap<File, String> shuffledGroupData : shuffledKGroupsData) {
//            System.out.println("Group " + index + ":");
//            for (File file : shuffledGroupData.keySet()) {
//                System.out.println(file + ", " + shuffledGroupData.get(file));
//            }
//            index++;
//        }
//    }

    // lightIteration must be between 0 and 2 inclusively
    private static void trainModelProcedure(int numTrainData, int lightIteration, HashMap<String, List<File>> shuffledPersonsData) {
        if (numTrainData > 50)
            return;

        for (String personName : shuffledPersonsData.keySet()) {
            if (personName.contains("ExpPosLit") && !personName.equals("ExpPosLit" + lightIteration)) continue;

            List<File> shuffledPersonData = shuffledPersonsData.get(personName);

            List<File> personTrainDataset = shuffledPersonData.subList(0, Math.min(numTrainData, shuffledPersonData.size()));

            int label = personNameLabel.get(personName);

            for (File image : personTrainDataset) {
                Mat imageMat = Imgcodecs.imread(image.getPath());
                Mat grayscaleImage = new Mat();
                Imgproc.cvtColor(imageMat, grayscaleImage, Imgproc.COLOR_BGR2GRAY);

                faceImages.add(grayscaleImage);
                faceLabels.add(label);
            }
        }

        if (!faceImages.isEmpty()) {
            if (faceRecog == FaceRecog.EIGEN || faceRecog == FaceRecog.FISHER) {
                adjustEqualSizeImages();
            }

            MatOfInt labelsMat = new MatOfInt();
            labelsMat.fromList(faceLabels);

            faceRecognizer.train(faceImages, labelsMat);
        }
    }

    private static void modelPredictionProcedure(List<File> personTestDataset) {
        for  (File faceImage : personTestDataset) {
            Mat faceColor = Imgcodecs.imread(faceImage.getAbsolutePath());

            Mat face = new Mat();
            Imgproc.cvtColor(faceColor, face, Imgproc.COLOR_BGR2GRAY);

            // Resize the face image to a fixed size
            Mat resizedFace = new Mat();
            if (faceRecog == FaceRecog.EIGEN || faceRecog == FaceRecog.FISHER)
                Imgproc.resize(face, resizedFace, smallestSize);
            else Imgproc.resize(face, resizedFace, new Size(100, 100));

            // Perform face recognition on the resized face image
            int[] labelBuffer = new int[1];
            double[] confidenceBuffer = new double[1];
            faceRecognizer.predict(resizedFace, labelBuffer, confidenceBuffer);

            predictedNames.add(personLabelName.get(labelBuffer[0]));
            predictedConfidences.add(confidenceBuffer[0]);
        }
    }

    private static double[][] getThresholdedFPTPr(List<String> trueNames) {
        double[] thresholdsFPr = new double[predictedConfidences.size() - 1];
        double[] thresholdsTPr = new double[predictedConfidences.size() - 1];

        for (int i = 0; i < thresholdsFPr.length; i++) {
            double threshold = (predictedConfidences.get(i) + predictedConfidences.get(i + 1)) / 2;

            int truePositives = 0;
            int falsePositives = 0;
            int trueNegatives = 0;
            int falseNegatives = 0;
            for (int tempIndex = 0; tempIndex < predictedNames.size(); tempIndex++) {
                if (predictedConfidences.get(tempIndex) < threshold && predictedNames.get(tempIndex).equals(trueNames.get(tempIndex)))
                    truePositives++;
                else if (predictedConfidences.get(tempIndex) < threshold && !predictedNames.get(tempIndex).equals(trueNames.get(tempIndex)))
                    falsePositives++;
                else if (predictedConfidences.get(tempIndex) > threshold && predictedNames.get(tempIndex).equals(trueNames.get(tempIndex)))
                    falseNegatives++;
                else
                    trueNegatives++;
            }

            thresholdsFPr[i] = (double) falsePositives / (falsePositives + trueNegatives);
            thresholdsTPr[i] = (double) truePositives / (truePositives + falseNegatives);

//            System.out.println("falsePositives: " + falsePositives);
//            System.out.println("trueNegatives: " + trueNegatives);
//            System.out.println("thresholdFPr: " + thresholdsFPr[i]);
//            System.out.println("truePositives: " + truePositives);
//            System.out.println("falseNegatives: " + falseNegatives);
//            System.out.println("thresholdTPr: " + truePositives / (truePositives + falseNegatives));
        }

        return new double[][]{thresholdsFPr, thresholdsTPr};
    }

    public static void adjustEqualSizeImages() {
        List<Mat> resizedImages = new ArrayList<>();

        smallestSize = faceImages.get(0).size();

        // Iterate over the remaining images and update smallestSize if a smaller size is found
        for (int i = 1; i < faceImages.size(); i++) {
            Size imageSize = faceImages.get(i).size();

            if (imageSize.width < smallestSize.width) {
                smallestSize = new Size(imageSize.width, smallestSize.height);
            } else if (imageSize.height < smallestSize.height) {
                smallestSize = new Size(smallestSize.width, imageSize.height);
            }
        }

        // Resize each image in the faceImages list
        for (Mat image : faceImages) {
            Mat resizedImage = new Mat();
            Imgproc.resize(image, resizedImage, smallestSize);
            resizedImages.add(resizedImage);
        }

        faceImages = resizedImages;
    }
}
