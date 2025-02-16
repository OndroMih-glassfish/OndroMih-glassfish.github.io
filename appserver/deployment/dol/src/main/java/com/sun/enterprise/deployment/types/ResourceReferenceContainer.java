/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.deployment.types;

import com.sun.enterprise.deployment.ResourceReferenceDescriptor;

import java.util.Set;

/**
 * This class defines the behaviour of a descriptor containing resource references
 *
 * @author  Jerome Dochez
 * @version
 */
public interface ResourceReferenceContainer {

    /**
     * Add a resource reference to the J2EEE component
     *
     * @param the reference descriptor to add
     */
    void addResourceReferenceDescriptor(ResourceReferenceDescriptor ejbReference);

    /**
     * Looks up a reference to a resource by its name (getName()). Throws an IllegalArgumentException
     * if no such descriptor is found.
     *
     * @param the name of the resource reference
     */
    ResourceReferenceDescriptor getResourceReferenceByName(String name);

    /**
    * Return the set of references to resources that I have.
    */
    Set<ResourceReferenceDescriptor> getResourceReferenceDescriptors();
}

