package org.intermine.objectstore.query;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * An element that can appear in the SELECT, ORDER BY or GROUP BY clause of a query
 *
 * @author Mark Woodbridge
 * @author Richard Smith
 * @author Matthew Wakeling
 */
public interface QueryNode
{
    /**
     * Get Java type represented by this evaluable item
     *
     * @return class describing the type
     */
    public Class getType();
}
