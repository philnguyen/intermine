package org.intermine.objectstore.webservice;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import junit.framework.*;

import org.intermine.objectstore.ObjectStoreAbstractImpl;
import org.intermine.objectstore.ObjectStoreAbstractImplTestCase;
import org.intermine.objectstore.ObjectStoreFactory;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.fql.FqlQuery;
import org.intermine.objectstore.safe.ObjectStoreSafeImpl;

public class ObjectStoreClientTest extends ObjectStoreAbstractImplTestCase
{
    public static void oneTimeSetUp() throws Exception {
        osai = (ObjectStoreClient) ObjectStoreFactory.getObjectStore("os.unittest-client");
        os = new ObjectStoreSafeImpl(osai);
        ObjectStoreAbstractImplTestCase.oneTimeSetUp();
    }

    public ObjectStoreClientTest(String arg) {
        super(arg);
    }

    public static Test suite() {
        return buildSuite(ObjectStoreClientTest.class);
    }

    public void testQueryId() throws Exception {
        Query q1 = new FqlQuery("select a1_ from Company as a1_", "org.intermine.model.testmodel").toQuery();
        Query q2 = new FqlQuery("select a1_ from Company as a1_", "org.intermine.model.testmodel").toQuery();

        assertEquals(((ObjectStoreClient) osai).getQueryId(q1), ((ObjectStoreClient) osai).getQueryId(q1));
        assertEquals(((ObjectStoreClient) osai).getQueryId(q2), ((ObjectStoreClient) osai).getQueryId(q2));

        assertTrue(((ObjectStoreClient) osai).getQueryId(q1) != ((ObjectStoreClient) osai).getQueryId(q2));
    }

}
