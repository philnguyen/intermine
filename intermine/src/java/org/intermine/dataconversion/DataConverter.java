package org.intermine.dataconversion;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Map;
import java.util.HashMap;
 
import org.apache.log4j.Logger;

/**
 * Abstract parent class of all DataConverters
 * @author Mark Woodbridge
 */
 public abstract class DataConverter
{
    protected static final Logger LOG = Logger.getLogger(DataConverter.class);

    protected ItemWriter writer;
    protected Map aliases = new HashMap();
    protected int nextClsId = 0;

    /**
    * Constructor that should be called by children
    * @param writer an ItemWriter used to handle the resultant Items
    */
    public DataConverter(ItemWriter writer) {
        this.writer = writer;
    }

    /**
    * Perform the data conversion
    * @throws Exception if an error occurs during processing
    */
    public abstract void process() throws Exception;

    /**
     * Uniquely alias a className
     * @param className the class name
     * @return the alias
     */
    protected String alias(String className) {
        String alias = (String) aliases.get(className);
        if (alias != null) {
            return alias;
        }
        String nextIndex = "" + (nextClsId++);
        aliases.put(className, nextIndex);
        LOG.error("Aliasing className " + className + " to index " + nextIndex);
        return nextIndex;
    }
}
