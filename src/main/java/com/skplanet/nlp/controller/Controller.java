package com.skplanet.nlp.controller;

import java.util.List;

/**
 * Controller Interface
 * Created by Donghun Shin
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * Date: 11/1/13
 */
public interface Controller<T> {
    /**
     * Initialize the controller
     */
    public void init();

    /**
     * load meta data
     * @return list of Meta data
     */
    public List<T> load();

}
