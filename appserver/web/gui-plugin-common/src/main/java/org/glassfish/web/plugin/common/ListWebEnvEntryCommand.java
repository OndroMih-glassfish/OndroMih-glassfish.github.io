/*
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

package org.glassfish.web.plugin.common;

import org.glassfish.web.config.serverbeans.EnvEntry;
import org.glassfish.web.config.serverbeans.WebModuleConfig;
import com.sun.enterprise.config.serverbeans.Application;
import java.text.MessageFormat;
import org.glassfish.api.ActionReport;
import org.glassfish.api.I18n;
import org.glassfish.api.Param;
import org.glassfish.api.admin.*;

import org.jvnet.hk2.annotations.Service;
import org.glassfish.hk2.api.PerLookup;

/**
 *
 * @author tjquinn
 */
@Service(name="list-web-env-entry")
@I18n("listWebEnvEntry.command")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@RestEndpoints({
    @RestEndpoint(configBean=Application.class,
        opType=RestEndpoint.OpType.GET,
        path="list-web-env-entry",
        description="list-web-env-entry",
        params={
            @RestParam(name="id", value="$parent")
        })
})
public class ListWebEnvEntryCommand extends WebModuleConfigCommand {

    @Param(name="name",optional=true)
    private String name;

    public void execute(AdminCommandContext context) {
        ActionReport report = context.getActionReport();

        WebModuleConfig config = webModuleConfig(report);
        if (config == null) {
            return;
        }

        ActionReport.MessagePart part = report.getTopMessagePart();
        final String format = localStrings.getLocalString(
                "listWebEnvEntryFormat", "{0} ({1}) = {2} ignoreDescriptorItem={3} //{4}");
        int reported = 0;
        for (EnvEntry entry : config.envEntriesMatching(name)) {
            ActionReport.MessagePart childPart = part.addChild();
            childPart.setMessage(MessageFormat.format(format,
                    entry.getEnvEntryName(),
                    entry.getEnvEntryType(),
                    entry.getEnvEntryValue(),
                    entry.getIgnoreDescriptorItem(),
                    descriptionValueOrNotSpecified(entry.getDescription())));
            reported++;
        }
        succeed(report, "listSummary",
                "Reported {0,choice,0#no {1} settings|1#one {1} setting|1<{0,number,integer} {1} settings}",
                reported, "env-entry");
    }
}
