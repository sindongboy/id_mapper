package com.skplanet.nlp.map;

import com.skplanet.nlp.data.UnifiedMeta;

import java.util.List;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/21/14.
 */
public interface Mapper {

    /**
     * Initialize Mapper
     */
    public void init();

    /**
     * map and unify the given meta
     * @return list of {@link com.skplanet.nlp.data.UnifiedMeta}
     */
    public List<UnifiedMeta> map();

}
