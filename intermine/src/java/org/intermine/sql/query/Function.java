package org.intermine.sql.query;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.*;

/**
 * A representation of a function in an SQL query.
 *
 * @author Matthew wakeling
 * @author Andrew Varley
 */
public class Function extends AbstractValue
{
    protected int operation;
    protected List operands;

    /**
     * COUNT(*) aggregate function - takes no operands.
     */
    public static final int COUNT = 1;
    /**
     * MAX(v) aggregate function - takes one operand.
     */
    public static final int MAX = 2;
    /**
     * MIN(v) aggregate function - takes one operand.
     */
    public static final int MIN = 3;
    /**
     * SUM(v) aggregate function - takes one operand.
     */
    public static final int SUM = 4;
    /**
     * AVG(v) aggregate function - takes one operand.
     */
    public static final int AVG = 5;
    /**
     * PLUS function - takes two or more operands.
     */
    public static final int PLUS = 6;
    /**
     * MINUS function - takes two operands.
     */
    public static final int MINUS = 7;
    /**
     * MULTIPLY function - takes two or more operands.
     */
    public static final int MULTIPLY = 8;
    /**
     * DIVIDE function - takes two operands.
     */
    public static final int DIVIDE = 9;
    /**
     * POWER function - takes two operands.
     */
    public static final int POWER = 10;
    /**
     * MODULO arithmetid function - takes two operands.
     */
    public static final int MODULO = 11;
    
    private static final String REPRESENTATIONS[] = {"", "COUNT(*)", "MAX(", "MIN(",
        "SUM(", "AVG(", " + ", " - ", " * ", " / ", " ^ ", " % "};
    
    /**
     * Constructor for this Function object.
     *
     * @param operation the operation that this Function represents
     * @throws IllegalArgumentException if operation is not valid
     */
    public Function(int operation) {
        if ((operation < 1) || (operation > 11)) {
            throw (new IllegalArgumentException("operation is not valid"));
        }
        this.operation = operation;
        operands = new ArrayList();
    }

    /**
     * Adds an operand to this Function object. Operands are stored in the order they are added.
     *
     * @param obj the AbstractValue to add as an operand
     * @throws IllegalArgumentException if the operation cannot handle that many operands
     */
    public void add(AbstractValue obj) {
        switch (operation) {
            case COUNT:
                throw (new IllegalArgumentException("COUNT does not take any operands"));
            case MAX:
            case MIN:
            case SUM:
            case AVG:
                if (operands.size() >= 1) {
                    throw (new IllegalArgumentException("This function may only take one operand"));
                }
                break;
            case MODULO:
            case MINUS:
            case DIVIDE:
            case POWER:
                if (operands.size() >= 2) {
                    throw (new IllegalArgumentException("This function may only take"
                                + "two operands"));
                }
                break;
        }
        operands.add(obj);
    }

    /**
     * Returns a String representation of this Function object, suitable for forming part of an
     * SQL query.
     *
     * @return the String representation
     * @throws IllegalStateException if there aren't the correct number of operands for the
     * operation yet.
     */
    public String getSQLString() {
        switch (operation) {
            case COUNT:
                return "COUNT(*)";
            case MAX:
            case MIN:
            case SUM:
            case AVG:
                if (operands.size() < 1) {
                    throw (new IllegalStateException("This function needs an operand"));
                }
                return REPRESENTATIONS[operation]
                    + ((AbstractValue) operands.get(0)).getSQLString() + ")";
            case PLUS:
            case MINUS:
            case MULTIPLY:
            case DIVIDE:
            case POWER:
            case MODULO:
                if (operands.size() < 2) {
                    throw (new IllegalStateException("This function needs two operands"));
                }
                Iterator iter = operands.iterator();
                String retval = "(";
                boolean needComma = false;
                while (iter.hasNext()) {
                    AbstractValue v = (AbstractValue) iter.next();
                    if (needComma) {
                        retval += REPRESENTATIONS[operation];
                    }
                    needComma = true;
                    retval += v.getSQLString();
                }
                retval += ")";
                return retval;
            }
        throw (new Error("Unknown operation"));
    }

    /**
     * Overrides Object.equals.
     *
     * @param obj the Object to compare to
     * @return true if they are equal
     */
    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            Function objF = (Function) obj;
            if ((operation == PLUS) || (operation == MULTIPLY)) {
                Map a = new HashMap();
                Iterator opIter = operands.iterator();
                while (opIter.hasNext()) {
                    Object operand = opIter.next();
                    if (!a.containsKey(operand)) {
                        a.put(operand, new Integer(1));
                    } else {
                        Integer i = (Integer) a.get(operand);
                        a.put(operand, new Integer(1 + i.intValue()));
                    }
                }
                Map b = new HashMap();
                opIter = objF.operands.iterator();
                while (opIter.hasNext()) {
                    Object operand = opIter.next();
                    if (!b.containsKey(operand)) {
                        b.put(operand, new Integer(1));
                    } else {
                        Integer i = (Integer) b.get(operand);
                        b.put(operand, new Integer(1 + i.intValue()));
                    }
                }
                return (operation == objF.operation) && (a.equals(b));
            } else {
                return (operation == objF.operation) && (operands.equals(objF.operands));
            }
        }
        return false;
    }

    /**
     * Overrides Object.hashcode.
     *
     * @return an arbitrary integer based on the contents of the Function
     */
    public int hashCode() {
        int multiplier = 5;
        int state = operation * 3;
        Iterator iter = operands.iterator();
        while (iter.hasNext()) {
            state += multiplier * iter.next().hashCode();
            if ((operation != PLUS) && (operation != MULTIPLY)) {
                multiplier += 2;
            }
        }
        return state;
    }

    /**
     * Returns true if this function is an aggregate function.
     *
     * @return true if function is COUNT, MAX, MIN, SUM, or AVG
     */
    public boolean isAggregate() {
        return (operation == COUNT) || (operation == MAX) || (operation == MIN)
            || (operation == SUM) || (operation == AVG);
    }

    /**
     * Returns the operation of the function.
     *
     * @return operation
     */
    public int getOperation() {
        return operation;
    }

    /**
     * Returns the List of operands of this function.
     *
     * @return all operands in a List
     */
    public List getOperands() {
        return operands;
    }
}

