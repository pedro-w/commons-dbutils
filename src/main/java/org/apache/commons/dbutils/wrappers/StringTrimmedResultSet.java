/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils.wrappers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.apache.commons.dbutils.ProxyFactory;

/**
 * Wraps a {@code ResultSet} to trim strings returned by the
 * {@code getString()} and {@code getObject()} methods.
 *
 * <p>
 * Usage Example:
 * This example shows how to decorate ResultSets so processing continues as
 * normal but all Strings are trimmed before being returned from the
 * {@code ResultSet}.
 * </p>
 *
 * <pre>
 * ResultSet rs = // somehow get a ResultSet;
 *
 * // Substitute wrapped ResultSet with additional behavior for real ResultSet
 * rs = StringTrimmedResultSet.wrap(rs);
 *
 * // Pass wrapped ResultSet to processor
 * List list = new BasicRowProcessor().toBeanList(rs);
 * </pre>
 */
public class StringTrimmedResultSet implements InvocationHandler {

    /**
     * The factory to create proxies with.
     */
    private static final ProxyFactory factory = ProxyFactory.instance();

    /**
     * Wraps the {@code ResultSet} in an instance of this class.  This is
     * equivalent to:
     * <pre>
     * ProxyFactory.instance().createResultSet(new StringTrimmedResultSet(rs));
     * </pre>
     *
     * @param rs The {@code ResultSet} to wrap.
     * @return wrapped ResultSet
     */
    public static ResultSet wrap(final ResultSet rs) {
        return factory.createResultSet(new StringTrimmedResultSet(rs));
    }

    /**
     * The wrapped result.
     */
    private final ResultSet rs;

    /**
     * Constructs a new instance of {@code StringTrimmedResultSet}
     * to wrap the specified {@code ResultSet}.
     * @param rs ResultSet to wrap
     */
    public StringTrimmedResultSet(final ResultSet rs) {
        this.rs = rs;
    }

    /**
     * Intercept calls to the {@code getString()} and
     * {@code getObject()} methods and trim any Strings before they're
     * returned.
     *
     * @see java.lang.reflect.InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])
     * @param proxy Not used; all method calls go to the internal result set
     * @param method The method to invoke on the result set
     * @param args The arguments to pass to the result set
     * @return string trimmed result
     * @throws Throwable error
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
        throws Throwable {

        Object result = method.invoke(this.rs, args);

        if (result instanceof String
                && (method.getName().equals("getObject")
                || method.getName().equals("getString"))) {
            result = ((String) result).trim();
        }

        return result;
    }

}
