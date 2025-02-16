/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.main.admin.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

import static org.glassfish.main.admin.test.tool.asadmin.GlassFishTestEnvironment.openConnection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestITest {

    @Test
    public void testManagementEndpoint() throws Exception {
        HttpURLConnection connection = openConnection("/management/domain.xml");
        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void testMonitoringEndpoint() throws Exception {
        HttpURLConnection connection = openConnection("/monitoring/domain.xml");
        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void testAdminCommandEndpoint() throws Exception {
        HttpURLConnection connection = openConnection("/management/domain/version.xml");
        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void testChildConfigBeanEndpoint() throws Exception {
        HttpURLConnection connection = openConnection("/management/domain/applications.xml");
        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void testPostGetDelete() throws Exception {
        // FIXME: causes HTTP 500 without any log
        assertThat(deleteNode(), greaterThanOrEqualTo(400));
        assertEquals(200, createNode());
        assertEquals(200, getNode());
        assertEquals(200, deleteNode());
    }

    private int createNode() throws IOException {
        String parameters = "name=myConfigNode";
        HttpURLConnection connection = openConnection("/management/domain/nodes/create-node-config");
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Language", "en-US");
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(parameters);
            }
            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }

    private int getNode() throws IOException {
        HttpURLConnection connection = openConnection("/management/domain/nodes/node/myConfigNode");
        try {
            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }

    private int deleteNode() throws IOException {
        HttpURLConnection connection = openConnection("/management/domain/nodes/delete-node-config?id=myConfigNode");
        try {
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }
}
