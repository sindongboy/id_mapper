package com.skplanet.nlp.driver;

import com.skplanet.nlp.data.UnifiedMeta;
import com.skplanet.nlp.map.HoppinMapper;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * ID Mapping for Hoppin Driver
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 4/2/14
 * <br>
 */
@SuppressWarnings("unused")
public final class HoppinMapperDriver {
    private static final Logger LOGGER = Logger.getLogger(HoppinMapperDriver.class.getName());
    public static void main(String[] args) {
        long btime, etime;
        LOGGER.info("Hoppin Mapping ....");
        btime = System.currentTimeMillis();
        // create mapper instance
        HoppinMapper mapper = new HoppinMapper();
        // initialize it
        mapper.init();
        // mapping
        List<UnifiedMeta> result = mapper.map();

        // print out the mapping result
        for (UnifiedMeta m : result) {
            System.out.println(m.toString());
        }
        etime = System.currentTimeMillis();
        LOGGER.info("Hoppin Mapping done : " + (etime - btime) / 1000 + " sec.");
    }

    private HoppinMapperDriver() {
    }

}
