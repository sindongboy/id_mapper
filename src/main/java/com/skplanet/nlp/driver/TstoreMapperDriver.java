package com.skplanet.nlp.driver;

import com.skplanet.nlp.data.UnifiedMeta;
import com.skplanet.nlp.map.TstoreMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/24/14.
 */
public final class TstoreMapperDriver {
    private static final Logger LOGGER = Logger.getLogger(TstoreMapperDriver.class.getName());

    public static void main(String[] args) throws IOException {
        long btime, etime;
        LOGGER.info("Tstore Mapping ....");
        btime = System.currentTimeMillis();
        TstoreMapper mapper = new TstoreMapper();
        mapper.init();
        List<UnifiedMeta> result = mapper.map();
        for (UnifiedMeta m : result) {
            System.out.println(m.toString());
        }
        etime = System.currentTimeMillis();
        LOGGER.info("Tstore Mapping done : " + (etime - btime) / 1000 + " sec.");
    }

    private TstoreMapperDriver() {
    }
}
