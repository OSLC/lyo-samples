/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  SPDX-License-Identifier: EPL-1.0 OR BSD-3-Clause
 */
package org.eclipse.lyo.samples.client;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class SnapshotUtils {

    /**
     * Stabilizes an RDF string by converting it to N-TRIPLES, canonicalizing blank node IDs, and sorting triples.
     *
     * <p>This implementation uses a rudimentary canonicalization algorithm: 1. Compute a "signature" for each blank
     * node based on its non-blank neighbors (properties/values). 2. Sort blank nodes by this signature. 3. Assign
     * sequential IDs (_:b1, _:b2, ...) to the sorted nodes.
     *
     * @param rdfXml the RDF content to stabilize (RDF/XML, Turtle, or N-TRIPLES)
     * @return the stabilized N-TRIPLES string
     */
    public static String stabilizeRdf(String rdfXml) {
        try {
            Model model = ModelFactory.createDefaultModel();
            if (!tryRead(model, rdfXml, "RDF/XML")) {
                model = ModelFactory.createDefaultModel();
                if (!tryRead(model, rdfXml, "TURTLE")) {
                    model = ModelFactory.createDefaultModel();
                    if (!tryRead(model, rdfXml, "N-TRIPLES")) {
                        return rdfXml.lines().sorted().collect(Collectors.joining("\n"));
                    }
                }
            }

            // 1. Identify all blank nodes
            List<Resource> blankNodes = new ArrayList<>();
            model.listSubjects().filterKeep(Resource::isAnon).forEachRemaining(blankNodes::add);
            model.listObjects()
                    .filterKeep(RDFNode::isAnon)
                    .mapWith(RDFNode::asResource)
                    .forEachRemaining(node -> {
                        if (!blankNodes.contains(node)) {
                            blankNodes.add(node);
                        }
                    });

            // 2. Compute signatures for sorting
            // Signature = sorted list of strings representing (Property, Value) for outgoing
            //             and (Subject, Property) for incoming, IGNORING other blank nodes initially.
            Map<Resource, String> signatures = new HashMap<>();
            for (Resource bnode : blankNodes) {
                List<String> sigParts = new ArrayList<>();

                // Outgoing
                bnode.listProperties().forEachRemaining(stmt -> {
                    if (!stmt.getObject().isAnon()) {
                        sigParts.add("OUT:" + stmt.getPredicate().getURI() + "|"
                                + stmt.getObject().toString());
                    } else {
                        sigParts.add("OUT:" + stmt.getPredicate().getURI() + "|_BLANK_");
                    }
                });

                // Incoming
                model.listStatements(null, null, bnode).forEachRemaining(stmt -> {
                    if (!stmt.getSubject().isAnon()) {
                        sigParts.add("IN:" + stmt.getSubject().getURI() + "|"
                                + stmt.getPredicate().getURI());
                    } else {
                        sigParts.add("IN:_BLANK_|" + stmt.getPredicate().getURI());
                    }
                });

                Collections.sort(sigParts);
                signatures.put(bnode, String.join(";;", sigParts));
            }

            // 3. Sort blank nodes by signature
            blankNodes.sort(Comparator.comparing(signatures::get));

            // 4. Create a mapping to new IDs
            Map<Resource, Resource> remap = new HashMap<>();
            AtomicInteger counter = new AtomicInteger(1);
            for (Resource bnode : blankNodes) {
                // Check if we already mapped it (duplicates in list?) -> logic above handles uniqueness, but standard
                // set check is good
                if (!remap.containsKey(bnode)) {
                    Resource newBNode = model.createResource(new AnonId("b" + counter.getAndIncrement()));
                    remap.put(bnode, newBNode);
                }
            }

            // 5. Create new model with renamed blank nodes
            Model newModel = ModelFactory.createDefaultModel();
            model.listStatements().forEachRemaining(stmt -> {
                Resource s = stmt.getSubject();
                Property p = stmt.getPredicate();
                RDFNode o = stmt.getObject();

                if (s.isAnon() && remap.containsKey(s)) {
                    s = remap.get(s);
                }
                if (o.isAnon() && remap.containsKey(o.asResource())) {
                    o = remap.get(o.asResource());
                }

                newModel.add(s, p, o);
            });

            StringWriter out = new StringWriter();
            newModel.write(out, "N-TRIPLES");

            return out.toString()
                    .lines()
                    .filter(line -> !line.isBlank())
                    .sorted()
                    .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            // Fallback to primitive clean if parsing fails, or return original
            return rdfXml.lines().sorted().collect(Collectors.joining("\n"));
        }
    }

    private static boolean tryRead(Model model, String rdfContent, String lang) {
        try {
            model.read(new StringReader(rdfContent), null, lang);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
