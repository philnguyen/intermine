package org.intermine.sql;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Properties;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.intermine.util.StringUtil;


/**
 * Class that represents a physical SQL database
 *
 * @author Andrew Varley
 */

public class Database
{
    protected DataSource datasource;
    protected String platform;
    protected String driver;

    // Store all the properties this Database was configured with
    protected Properties settings;

    /**
     * No argument constructor for testing purposes
     *
     */
    protected Database() {
    }

    /**
     * Constructs a Database object from a set of properties
     *
     * @param props the properties by which this Database is configured
     * @throws ClassNotFoundException if there is a class in props that cannot be found
     */
    protected Database(Properties props) throws ClassNotFoundException {
        settings = props;
        configure(props);
    }

    /**
     * Gets the DataSource object for this Database
     *
     * @return the datasource for this Database
     */
    public DataSource getDataSource() {
        return datasource;
    }

    /**
     * Gets a Connection to this Database
     *
     * @return a Connection to this Database
     * @throws SQLException if there is a problem in the underlying database
     */
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    /**
     * Gets the platform of this Database
     *
     * @return the datasource for this Database
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Gets the driver this Database
     *
     * @return the driver for this Database
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Gets the username for this Database
     *
     * @return the username for this Database
     */
    public String getUser() {
        return (String) settings.get("datasource.user");
    }

    /**
     * Gets the password for this Database
     *
     * @return the password for this Database
     */
    public String getPassword() {
        return (String) settings.get("datasource.password");
    }

    /**
     * Gets the URL from this database
     *
     * @return the URL for this database
     */
    public String getURL() {
        return "jdbc:" + platform.toLowerCase() + "://"
            + (String) settings.get("datasource.serverName")
            + "/" + (String) settings.get("datasource.databaseName");
    }

    /**
     * Configures a datasource from a Properties object
     *
     * @param props the properties for configuring the Database
     * @throws ClassNotFoundException if the class given in the properties file cannot be found
     * @throws IllegalArgumentException if the configuration properties are empty
     * @throws NullPointerException if props is null
     */
    protected void configure(Properties props) throws ClassNotFoundException {
        if (props == null) {
            throw new NullPointerException("Props cannot be null");
        }

        if (props.size() == 0) {
            throw new IllegalArgumentException("No configuration details");
        }

        Properties subProps = new Properties();

        Enumeration enum = props.keys();
        while (enum.hasMoreElements()) {
            String propertyName = (String) enum.nextElement();
            Object propertyValue = props.get(propertyName);
            String configName = propertyName.substring(propertyName.lastIndexOf(".") + 1);
            Field field = null;

            // Get the first part of the string - this is the attribute we are taking about
            String attribute = propertyName;
            String subAttribute = "";
            int index;
            if ((index = propertyName.indexOf(".")) != -1) {
                attribute = propertyName.substring(0, index);
                subAttribute = propertyName.substring(index + 1);
            }

            try {
                field = Database.class.getDeclaredField(attribute);
            } catch (Exception e) {
                // Ignore this property - no such field
                continue;
            }

            if (subAttribute.equals("class")) {
                // make a new instance of this class for this attribute
                Class clazz;
                Object obj;

                clazz = Class.forName(propertyValue.toString());
                try {
                    obj = clazz.newInstance();
                } catch (Exception e) {
                    throw new ClassNotFoundException("Cannot instantiate class "
                                                     + clazz.getName() + " " + e.getMessage());
                }
                // Set the field to this newly instantiated class
                try {
                    field.set(this, obj);
                } catch (Exception e) {
                    continue;
                }
            } else if (subAttribute.equals("")) {
                // Set this attribute directly
                try {
                    field.set(this, propertyValue);
                } catch (Exception e) {
                    continue;
                }
            } else {
                // Set parameters on the attribute
                Method m = null;
                // Set this configuration parameter on the DataSource;
                try {
                    // Strings first
                    Object o = field.get(this);
                    // Sometimes the class will not have been instantiated yet
                    if (o == null) {
                        subProps.put(propertyName, propertyValue);
                        continue;
                    }
                    Class clazz = o.getClass();
                    m = clazz.getMethod("set" + StringUtil.capitalise(subAttribute),
                                        new Class[] {String.class});
                    if (m != null) {
                        m.invoke(field.get(this), new Object [] {propertyValue});
                    }
                    // now integers
                } catch (Exception e) {
                    // Don't do anything - either the method not found or cannot be invoked
                }
                try {
                    if (m == null) {
                        m = field.get(this).getClass().
                            getMethod("set" + StringUtil.capitalise(subAttribute),
                                      new Class[] {int.class});
                        if (m != null) {
                            m.invoke(field.get(this),
                                     new Object [] {Integer.valueOf(propertyValue.toString())});
                        }
                    }
                } catch (Exception e) {
                // Don't do anything - either the method not found or cannot be invoked
                }
            }
            if (subProps.size() > 0) {
                configure(subProps);
            }
        }

    }


}
