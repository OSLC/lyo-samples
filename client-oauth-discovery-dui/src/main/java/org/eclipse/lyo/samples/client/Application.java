package org.eclipse.lyo.samples.client;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;

public class Application extends jakarta.ws.rs.core.Application {

    private static final Set<Class<?>> RESOURCE_CLASSES = new HashSet<Class<?>>();

    static {
        RESOURCE_CLASSES.addAll(JenaProvidersRegistry.getProviders());
        RESOURCE_CLASSES.add(OslcCorsFilter.class);

        RESOURCE_CLASSES.add(DiscoveryServices.class);
    }

    public Application() throws OslcCoreApplicationException, URISyntaxException {}

    @Override
    public Set<Class<?>> getClasses() {
        return RESOURCE_CLASSES;
    }
}
