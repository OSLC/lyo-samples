package org.eclipse.lyo.samples.client.jazz;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FixtureRecorderFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final Path OUTPUT_DIR = Paths.get("src/test/resources/fixtures/recorded");

    static {
        if (Boolean.getBoolean("lyo.record.fixtures")) {
            try {
                Files.createDirectories(OUTPUT_DIR);
            } catch (IOException e) {
                log.error("Failed to create fixture recording directory", e);
            }
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // No-op for requests in this simple version, or we could record requests too
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (!Boolean.getBoolean("lyo.record.fixtures")) {
            return;
        }

        int id = counter.incrementAndGet();
        String method = requestContext.getMethod();
        String path = requestContext.getUri().getPath();
        String filename = String.format("%03d_%s_%s.xml", id, method, sanitize(path));
        Path filePath = OUTPUT_DIR.resolve(filename);

        if (responseContext.hasEntity()) {
            InputStream entityStream = responseContext.getEntityStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = entityStream.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            byte[] bytes = baos.toByteArray();

            // Write to disk
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Restore stream for the client
            responseContext.setEntityStream(new ByteArrayInputStream(bytes));

            // Also write metadata
            Path metaPath = OUTPUT_DIR.resolve(filename + ".meta");
            String meta = "URL: " + requestContext.getUri().toString() + "\n" + "Status: " + responseContext.getStatus()
                    + "\n";
            Files.writeString(metaPath, meta);

            log.info("Recorded fixture: {}", filePath);
        }
    }

    private String sanitize(String path) {
        return path.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
