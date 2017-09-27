package com.lavidatec.template.entity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hibernate user type to persist JSONObject.
 *
 * @author "Jan Jonas <mail@janjonas.net>"
 * @see http://www.json.org/javadoc/org/json/JSONObject.html
 * @see http://docs.jboss.org/hibernate/stable/annotations/api
 * /org/hibernate/usertype/UserType.html
 */
public class JSONObjectUserType implements UserType {

    /**
     * SQL_TYPES.
     */
    private static final int[] SQL_TYPES = {Types.LONGVARCHAR};

    /**
     * assemble.
     */
    @Override
    public Object assemble(final Serializable cached, final Object owner)
            throws HibernateException {
        return deepCopy(cached);
    }

    /**
     * deepCopy.
     */
    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        try {
            return new JSONObject(((JSONObject) value).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * disassemble.
     */
    @Override
    public Serializable disassemble(final Object value)
            throws HibernateException {
        return ((JSONObject) value).toString();
    }

    /**
     * equals.
     */
    @Override
    public boolean equals(final Object x, final Object y)
            throws HibernateException {
        if (x == null) {
            return (y != null);
        }
        return (x.equals(y));
    }

    /**
     * hashCode.
     */
    @Override
    public int hashCode(final Object x) throws HibernateException {
        return ((JSONObject) x).toString().hashCode();
    }

    /**
     * isMutable.
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * replace.
     */
    @Override
    public Object replace(final Object original, final Object target,
                          final Object owner) throws HibernateException {
        return deepCopy(original);
    }

    /**
     * returnedClass.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class returnedClass() {
        return JSONObject.class;
    }

    /**
     * sqlTypes.
     */
    @Override
    public int[] sqlTypes() {
        return SQL_TYPES.clone();
    }

    /**
     * nullSafeGet.
     */
    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                              final SharedSessionContractImplementor session,
                              final Object owner)
            throws HibernateException, SQLException {
        if (!rs.wasNull()) {
            try {
                return new JSONObject(rs.getString(names[0]));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * nullSafeSet.
     */
    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value,
                            final int index,
                            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, SQL_TYPES[0]);
        } else {
            st.setString(index, ((JSONObject) value).toString());
        }
    }
}


