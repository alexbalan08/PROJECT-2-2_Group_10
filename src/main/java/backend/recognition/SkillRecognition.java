package backend.recognition;

import java.io.IOException;

/**
 *
 * This interface can be implemented by a class that performs skill recognition, which is the task of identifying the skills required to solve a particular task or problem.
 * The implementation of determineSkill may involve analyzing the input text to identify key phrases or keywords that indicate the required skill,
 * or it may involve querying a knowledge base or other data source to determine the skill required for a particular task or problem.
 *
 * */
public interface SkillRecognition {

    /**
     *
     * Takes a single argument input, which is a string containing natural language text that describes a task or problem.
     * The method returns a string representing the skill required to solve the task or problem.
     *
     * The method throws an IOException, which is a checked exception that indicates an error occurred while reading or writing data.
     *
     * */
    String determineSkill(String input) throws IOException;

}
