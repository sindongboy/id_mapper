package com.skplanet.nlp.driver;


import com.skplanet.nlp.cli.CommandLineInterface;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 4/8/14
 * <br>
 */
public final class SplitNaverCommentsByUnifiedID {
    private static final Logger LOGGER = Logger.getLogger(SplitNaverCommentsByUnifiedID.class.getName());

    private SplitNaverCommentsByUnifiedID() {
    }

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("m", "map", true, "nid-uid mapper", true);
        cli.addOption("c", "comment", true, "comments file", true);
        cli.addOption("o", "output", true, "output directory", true);
        cli.parseOptions(args);

        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> comments = new HashMap<String, String>();
        BufferedReader reader;
        BufferedWriter writer;

        String line;
        try {
            reader = new BufferedReader(new FileReader(new File(cli.getOption("m"))));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                String[] fields = line.split("\t");
                if (fields.length != 2) {
                    LOGGER.info("wrong format: " + line);
                    continue;
                }

                map.put(fields[0], fields[1]);
            }
            reader.close();
            LOGGER.info("mapping loaded : " + map.size());

            reader = new BufferedReader(new FileReader(new File(cli.getOption("c"))));
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if ((count % 100) == 0) {
                    LOGGER.debug("line: " + count);
                }
                if (line.trim().length() == 0) {
                    continue;
                }

                String[] fields = line.split("\\t");
                if (fields.length != 2) {
                    LOGGER.info("wrong format: " + line);
                    continue;
                }

                if (map.containsKey(fields[0])) {
                    if (comments.containsKey(map.get(fields[0]))) {
                        String tmpStr = comments.get(map.get(fields[0]));
                        tmpStr = tmpStr + "\n" + fields[1];
                        comments.remove(map.get(fields[0]));
                        comments.put(map.get(fields[0]), tmpStr);
                    } else {
                        comments.put(map.get(fields[0]), fields[1]);
                    }
                }
                count++;
            }
            reader.close();

            Set<String> keys = comments.keySet();
            for (String key : keys) {
                writer = new BufferedWriter(new FileWriter(new File(cli.getOption("o") + "/" + key), true));
                writer.write(comments.get(key));
                writer.newLine();
                writer.close();
            }

        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (IOException e) {
            LOGGER.error("Failed to read file", e);
        }


    }
}
