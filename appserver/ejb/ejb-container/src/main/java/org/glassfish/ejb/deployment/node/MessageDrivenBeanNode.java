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

package org.glassfish.ejb.deployment.node;

import com.sun.enterprise.deployment.node.DataSourceDefinitionNode;
import com.sun.enterprise.deployment.node.LifecycleCallbackNode;
import com.sun.enterprise.deployment.node.MailSessionNode;
import com.sun.enterprise.deployment.node.MethodNode;
import com.sun.enterprise.deployment.node.SecurityRoleRefNode;
import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.xml.TagNames;

import java.util.Map;

import org.glassfish.ejb.deployment.EjbTagNames;
import org.glassfish.ejb.deployment.descriptor.EjbBundleDescriptorImpl;
import org.glassfish.ejb.deployment.descriptor.EjbMessageBeanDescriptor;
import org.glassfish.ejb.deployment.descriptor.ScheduledTimerDescriptor;
import org.w3c.dom.Node;

/**
 * This class handles message-driven related xml information
 *
 * @author Jerome Dochez
 */
public class MessageDrivenBeanNode extends EjbNode<EjbMessageBeanDescriptor> {

    private EjbMessageBeanDescriptor descriptor;

    public MessageDrivenBeanNode() {
        registerElementHandler(new XMLElement(EjbTagNames.ACTIVATION_CONFIG), ActivationConfigNode.class, "setActivationConfigDescriptor");
        registerElementHandler(new XMLElement(EjbTagNames.AROUND_INVOKE_METHOD), AroundInvokeNode.class, "addAroundInvokeDescriptor");
        registerElementHandler(new XMLElement(EjbTagNames.AROUND_TIMEOUT_METHOD), AroundTimeoutNode.class, "addAroundTimeoutDescriptor");
        registerElementHandler(new XMLElement(TagNames.POST_CONSTRUCT), LifecycleCallbackNode.class, "addPostConstructDescriptor");
        registerElementHandler(new XMLElement(TagNames.PRE_DESTROY), LifecycleCallbackNode.class, "addPreDestroyDescriptor");
        registerElementHandler(new XMLElement(TagNames.DATA_SOURCE), DataSourceDefinitionNode.class, "addResourceDescriptor");
        registerElementHandler(new XMLElement(EjbTagNames.TIMEOUT_METHOD), MethodNode.class, "setEjbTimeoutMethod");
        registerElementHandler(new XMLElement(TagNames.MAIL_SESSION), MailSessionNode.class, "addResourceDescriptor");
        registerElementHandler(new XMLElement(TagNames.ROLE_REFERENCE), SecurityRoleRefNode.class, "addRoleReference");
    }


    @Override
    public EjbMessageBeanDescriptor getEjbDescriptor() {
        if (descriptor == null) {
            descriptor = new EjbMessageBeanDescriptor();
            descriptor.setEjbBundleDescriptor((EjbBundleDescriptorImpl) getParentNode().getDescriptor());
        }
        return descriptor;
    }


    @Override
    protected Map<String, String> getDispatchTable() {
        // no need to be synchronized for now
        Map<String, String> table = super.getDispatchTable();

        table.put(EjbTagNames.MESSAGING_TYPE, "setMessageListenerType");
        table.put(EjbTagNames.TRANSACTION_TYPE, "setTransactionType");
        table.put(TagNames.MESSAGE_DESTINATION_TYPE, "setDestinationType");
        table.put(TagNames.MESSAGE_DESTINATION_LINK, "setMessageDestinationLinkName");

        // These are the EJB 2.0 elements that no longer exist in the EJB 2.1
        // schema. We still set them on the descriptor but they will be
        // written out as activation config properties.
        table.put(EjbTagNames.MSG_SELECTOR, "setJmsMessageSelector");
        table.put(EjbTagNames.JMS_ACKNOWLEDGE_MODE, "setJmsAcknowledgeMode");
        table.put(EjbTagNames.JMS_DEST_TYPE, "setDestinationType");
        table.put(EjbTagNames.JMS_SUBSCRIPTION_DURABILITY, "setSubscriptionDurability");
        return table;
    }


    @Override
    public Node writeDescriptor(Node parent, String nodeName, EjbMessageBeanDescriptor ejbDesc) {
        Node ejbNode = super.writeDescriptor(parent, nodeName, ejbDesc);
        writeDisplayableComponentInfo(ejbNode, ejbDesc);
        writeCommonHeaderEjbDescriptor(ejbNode, ejbDesc);
        appendTextChild(ejbNode, EjbTagNames.EJB_CLASS, ejbDesc.getEjbClassName());
        appendTextChild(ejbNode, EjbTagNames.MESSAGING_TYPE, ejbDesc.getMessageListenerType());

        MethodNode methodNode = new MethodNode();
        if (ejbDesc.isTimedObject()) {
            if (ejbDesc.getEjbTimeoutMethod() != null) {
                methodNode.writeJavaMethodDescriptor(ejbNode, EjbTagNames.TIMEOUT_METHOD, ejbDesc.getEjbTimeoutMethod());
            }

            for (ScheduledTimerDescriptor timerDesc : ejbDesc.getScheduledTimerDescriptors()) {
                ScheduledTimerNode timerNode = new ScheduledTimerNode();
                timerNode.writeDescriptor(ejbNode, EjbTagNames.TIMER, timerDesc);
            }
        }

        appendTextChild(ejbNode, EjbTagNames.TRANSACTION_TYPE, ejbDesc.getTransactionType());

        // message-destination-type
        appendTextChild(ejbNode, TagNames.MESSAGE_DESTINATION_TYPE, ejbDesc.getDestinationType());

        // message-destination-link
        String link = ejbDesc.getMessageDestinationLinkName();
        appendTextChild(ejbNode, TagNames.MESSAGE_DESTINATION_LINK, link);

        ActivationConfigNode activationConfigNode = new ActivationConfigNode();
        activationConfigNode.writeDescriptor(ejbNode, EjbTagNames.ACTIVATION_CONFIG, ejbDesc.getActivationConfigDescriptor());

        // around-invoke-method
        writeAroundInvokeDescriptors(ejbNode, ejbDesc.getAroundInvokeDescriptors().iterator());

        // around-timeout-method
        writeAroundTimeoutDescriptors(ejbNode, ejbDesc.getAroundTimeoutDescriptors().iterator());

        // env-entry*
        writeEnvEntryDescriptors(ejbNode, ejbDesc.getEnvironmentProperties().iterator());

        // ejb-ref * and ejb-local-ref*
        writeEjbReferenceDescriptors(ejbNode, ejbDesc.getEjbReferenceDescriptors().iterator());

        // service-ref*
        writeServiceReferenceDescriptors(ejbNode, ejbDesc.getServiceReferenceDescriptors().iterator());

        // resource-ref*
        writeResourceRefDescriptors(ejbNode, ejbDesc.getResourceReferenceDescriptors().iterator());

        // resource-env-ref*
        writeResourceEnvRefDescriptors(ejbNode, ejbDesc.getResourceEnvReferenceDescriptors().iterator());

        // message-destination-ref*
        writeMessageDestinationRefDescriptors(ejbNode, ejbDesc.getMessageDestinationReferenceDescriptors().iterator());

        // persistence-context-ref*
        writeEntityManagerReferenceDescriptors(ejbNode, ejbDesc.getEntityManagerReferenceDescriptors().iterator());

        // persistence-unit-ref*
        writeEntityManagerFactoryReferenceDescriptors(ejbNode, ejbDesc.getEntityManagerFactoryReferenceDescriptors().iterator());

        // post-construct
        writeLifeCycleCallbackDescriptors(ejbNode, TagNames.POST_CONSTRUCT, ejbDesc.getPostConstructDescriptors());

        // pre-destroy
        writeLifeCycleCallbackDescriptors(ejbNode, TagNames.PRE_DESTROY, ejbDesc.getPreDestroyDescriptors());

        // all descriptors (includes DSD, MSD, JMSCFD, JMSDD,AOD, CFD)*
        writeResourceDescriptors(ejbNode, ejbDesc.getAllResourcesDescriptors().iterator());

        // security-role-ref*
        writeRoleReferenceDescriptors(ejbNode, ejbDesc.getRoleReferences().iterator());

        // security-identity
        writeSecurityIdentityDescriptor(ejbNode, ejbDesc);

        return ejbNode;
    }
}
