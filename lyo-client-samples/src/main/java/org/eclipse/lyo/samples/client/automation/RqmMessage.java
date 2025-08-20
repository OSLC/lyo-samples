/*
 * Copyright (c) 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  Contributors:
 *
 *     Paul McMahan <pmcmahan@us.ibm.com>     - initial implementation
 */
package org.eclipse.lyo.samples.client.automation;

import lombok.Setter;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcResourceShape(title = "Message Resource Shape", describes = IConstants.TYPE_MESSAGE)
@OslcNamespace(IConstants.NAMESPACE_URI_JAZZ_AUTO_RQM)
public class RqmMessage extends AbstractResource implements IConstants {

    @Setter
    private String name;

    @Setter
    private String value;

    /**
     * Default Constructor
     *
     * @param name Name or ID of the message. Cannot be null.
     * @param value The actual message content. Cannot be null.
     */
    public RqmMessage(String name, String value) {
        assert name != null;
        assert value != null;

        this.name = name;
        this.value = value;
    }

    @OslcDescription("The name of the message")
    @OslcPropertyDefinition(NAMESPACE_URI_JAZZ_AUTO_RQM + "name")
    @OslcTitle("Name")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.String)
    public String getName() {
        return name;
    }

    @OslcDescription("The actual message content")
    @OslcPropertyDefinition(NAMESPACE_URI_JAZZ_AUTO_RQM + "value")
    @OslcTitle("Value")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.XMLLiteral)
    public String getValue() {
        return value;
    }
}
