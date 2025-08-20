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

import org.eclipse.lyo.oslc.domains.auto.AutomationRequest;
import org.eclipse.lyo.oslc.domains.auto.AutomationResult;

public interface IAutomationRequestHandler {

    /**
     * Handle the Automation Request assigned to an Automation Adapter and return an Automation Response.
     *
     * @param request
     * @param adapter
     * @return An Automation Response, or null if there is no response to be returned.
     * @throws AutomationException
     */
    AutomationResult handleAutomationRequest(AutomationRequest request, AutomationAdapter adapter)
            throws AutomationException;
}
