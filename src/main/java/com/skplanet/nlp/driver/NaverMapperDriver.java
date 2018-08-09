package com.skplanet.nlp.driver;

import com.google.common.collect.ArrayListMultimap;
import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.data.UnifiedMeta;
import com.skplanet.nlp.map.NaverMapper;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Naver Mapping Driver
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 10/29/13
 * <br>
 */
public final class NaverMapperDriver {
    private static final Logger LOGGER = Logger.getLogger(NaverMapperDriver.class.getName());

    // main
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("m", "meta", true, "meta output file", true);
        cli.addOption("p", "pair", true, "pair output file", true);
        cli.parseOptions(args);

        BufferedWriter mWriter;
        BufferedWriter pWriter;
        long btime, etime;
        LOGGER.info("Naver Mapping ....");
        btime = System.currentTimeMillis();
        NaverMapper mapper = new NaverMapper();
        mapper.init();
        List<UnifiedMeta> unifiedMetaList = mapper.map();
        ArrayListMultimap<String, String> result = mapper.getIDPair();

        try {
            mWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("m"))));
            // print updated unified meta list
            for (UnifiedMeta um : unifiedMetaList) {
                //System.out.println(um.toString());
                mWriter.write(um.toString());
                mWriter.newLine();
            }
            mWriter.close();
        } catch (IOException e) {
            LOGGER.error("Failed to write unified meta : " + cli.getOption("m"), e);
        }

        // print out id pair
        try {
            pWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("p"))));
            Set<String> keys = result.keySet();
            for (String k : keys) {
                for (String n : result.get(k)) {
                    //System.out.print(n);
                    //System.out.println("\t" + k);
                    pWriter.write(n);
                    pWriter.write("\t" + k);
                    pWriter.newLine();
                }
            }
            pWriter.close();
        } catch (IOException e) {
            LOGGER.error("Failed to write naver id to unified id map : " + cli.getOption("p"), e);
        }
        etime = System.currentTimeMillis();
        LOGGER.info("Naver Mapping done : " + (etime - btime) / 1000 + " sec.");
    }

    private NaverMapperDriver() {
    }
}
