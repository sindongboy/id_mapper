package com.skplanet.nlp.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.data.UnifiedMeta;
import com.skplanet.nlp.map.KMDBMapper;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * KMDB Mapping Driver
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 4/3/14
 * <br>
 */
public final class KMDBMapperDriver {
    private static final Logger LOGGER = Logger.getLogger(KMDBMapperDriver.class.getName());
    public static void main(String[] args) throws IOException {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("u", "u", true, "unified meta file output", true);
        cli.addOption("m", "m", true, "id mapping file output", true);
        cli.parseOptions(args);

        BufferedWriter uWriter;
        BufferedWriter mWriter;

        long btime, etime;
        LOGGER.info("KMDB Mapping ....");
        btime = System.currentTimeMillis();
        KMDBMapper mapper = new KMDBMapper();
        mapper.init();

        List<UnifiedMeta> unifiedMetaList = mapper.map();
        uWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("u"))));
        for (UnifiedMeta m : unifiedMetaList) {
            uWriter.write(m.toString());
            uWriter.newLine();
        }
        uWriter.close();

        Map<String, String> map = mapper.getIdMappingTable();
        mWriter = new BufferedWriter(new FileWriter(new File(cli.getOption("m"))));
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry m = (Map.Entry) iter.next();
            mWriter.write(m.getKey() + "\t" + m.getValue());
            mWriter.newLine();
        }
        mWriter.close();

        etime = System.currentTimeMillis();
        LOGGER.info("KMDB Mapping done : " + (etime - btime) / 1000 + " sec.");
    }

    private KMDBMapperDriver() {
    }
}
