/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.appclient.server.connector;

import org.glassfish.api.deployment.archive.ArchiveType;

import org.jvnet.hk2.annotations.Service;

/**
 * {@link ArchiveType} .
 *
 * @author sanjeeb.sahoo@oracle.com
 */
@Service(name = CarType.ARCHIVE_TYPE)
@jakarta.inject.Singleton
public class CarType extends ArchiveType {

    public static final String ARCHIVE_TYPE = "car";

    public static final String ARCHIVE_EXTENSION = ".car";

    public CarType() {
        super(ARCHIVE_TYPE, ARCHIVE_EXTENSION);
    }
}
