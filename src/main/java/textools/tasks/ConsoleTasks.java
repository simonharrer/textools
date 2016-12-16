package textools.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class ConsoleTasks {

    private ConsoleTasks() {
    }

    /**
     * Ask the user for a string. If no or empty input is provided, the default value is returned.
     *
     * @param question      the question asking the user to input a specific information
     * @param defaultAnswer the value that is returned in case the user provides no or an empty answer
     * @return the answer to the
     */
    public static String askString(String question, String defaultAnswer) {
        System.out.format("%s  (leave blank for [%s]):", question, defaultAnswer);

        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("no console available");
        }

        String inputLine = console.readLine();
        if (inputLine == null) {
            throw new IllegalArgumentException("given input is null");
        }

        String answer = inputLine.trim();

        // use default value when input is blank
        if (answer.isEmpty()) {
            answer = defaultAnswer;
        }

        return answer;
    }

    public static void executeWithLog(String command, String logfile) {
        try {
            System.out.format("\texecuting \"%s\" ... ", command);
            long startTime = System.currentTimeMillis();

            Path logFilePath = Paths.get(logfile);

            Process process = Runtime.getRuntime().exec(command, new String[] {}, new File("."));
            BufferedReader stdio = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            try (BufferedWriter logWriter = Files.newBufferedWriter(logFilePath, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {

                logWriter.write("--------------------------\n");
                logWriter.write("EXECUTING COMMAND [" + command + "] at " + new Date().toString() + "\n");
                logWriter.write("--------------------------\n\n");

                String stdioLine;
                while ((stdioLine = stdio.readLine()) != null) {
                    logWriter.write(stdioLine);
                    logWriter.write("\n");
                    logWriter.flush();
                }
            }

            int result = waitForProcessToExit(process);
            long stopTime = System.currentTimeMillis();
            long diffTime = stopTime - startTime;

            System.out.format(" in %tM.%tL seconds%n", diffTime, diffTime);

            if (result != 0) {
                System.out.println("\nERROR please inspect the log file " + logFilePath);
            }

        } catch (IOException e) {
            throw new IllegalStateException("io error while running " + command, e);
        }
    }

    private static int waitForProcessToExit(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return waitForProcessToExit(process);
        }
    }
}
