/*
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.deployment.annotation.handlers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.glassfish.api.naming.SimpleJndiName;

import static com.sun.enterprise.universal.JavaLangUtils.nonNull;

/**
 * @author David Matejcek
 */
public class ContextServiceDefinitionData implements Serializable {

    private static final long serialVersionUID = -6964391431010485710L;

    private SimpleJndiName name;
    private Set<String> cleared = new HashSet<>();
    private Set<String> propagated = new HashSet<>();
    private Set<String> unchanged = new HashSet<>();
    private Properties properties = new Properties();

    public SimpleJndiName getName() {
        return name;
    }


    public void setName(SimpleJndiName name) {
        this.name = name;
    }


    public Set<String> getCleared() {
        return cleared;
    }


    public void setCleared(Set<String> cleared) {
        this.cleared = nonNull(cleared, HashSet::new);
    }


    public void addCleared(String clearedItem) {
        this.cleared.add(clearedItem);
    }


    public Set<String> getPropagated() {
        return propagated;
    }


    public void setPropagated(Set<String> propagated) {
        this.propagated = nonNull(propagated, HashSet::new);
    }


    public void addPropagated(String propagatedItem) {
        this.propagated.add(propagatedItem);
    }


    public Set<String> getUnchanged() {
        return unchanged;
    }


    public void setUnchanged(Set<String> unchanged) {
        this.unchanged = nonNull(unchanged, HashSet::new);
    }


    public void addUnchanged(String unchangedItem) {
        this.unchanged.add(unchangedItem);
    }


    public Properties getProperties() {
        return properties;
    }


    public void setProperties(Properties properties) {
        this.properties = properties;
    }


    public void addContextServiceExecutorDescriptor(String name, String value) {
        properties.put(name, value);
    }


    @Override
    public String toString() {
        return super.toString() + "[name=" + name + ", cleared=" + cleared + ", propagated=" + propagated
            + ", unchanged=" + unchanged + ']';
    }
}
