package org.eclipse.lyo.samples.client.jazz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordedFixturesLoader {

    private static final Logger log = LoggerFactory.getLogger(RecordedFixturesLoader.class);
    private static final Path FIXTURES_DIR = Paths.get("src/test/resources/fixtures/recorded");
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(\\d{3})_([A-Z]+)_(.*)\\.xml");

    public static void loadFixtures(
            MockServerClient mockServer, String mockBaseUri, Map<String, String> loadedFixtures) {
        if (!Files.exists(FIXTURES_DIR)) {
            log.warn("Recorded fixtures directory not found: {}", FIXTURES_DIR);
            return;
        }

        try (Stream<Path> paths = Files.walk(FIXTURES_DIR)) {
            List<Path> files = paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .sorted(Comparator.comparing(Path::getFileName))
                    .collect(Collectors.toList());

            String detectedOriginalBaseUri = null;

            // First pass: detect original base URI from rootservices or similar
            for (Path file : files) {
                if (file.toString().contains("rootservices")) {
                    String url = getRequestUrl(file);
                    if (url != null && url.endsWith("/rootservices")) {
                        detectedOriginalBaseUri = url.substring(0, url.length() - "/rootservices".length());
                        break;
                    }
                }
            }

            if (detectedOriginalBaseUri == null) {
                // Fallback: try to guess from the first file with a URL
                for (Path file : files) {
                    String url = getRequestUrl(file);
                    if (url != null) {
                        // Assume the context is the first path segment
                        java.net.URI uri = java.net.URI.create(url);
                        String path = uri.getPath();
                        // e.g. /sandbox01-rm/oslc/... -> base is scheme://host/sandbox01-rm
                        String[] segments = path.split("/");
                        if (segments.length > 1) {
                            detectedOriginalBaseUri = uri.getScheme() + "://" + uri.getAuthority() + "/" + segments[1];
                            break;
                        }
                    }
                }
            }

            log.info("Detected original base URI: {}", detectedOriginalBaseUri);

            for (Path file : files) {
                Matcher matcher = FILENAME_PATTERN.matcher(file.getFileName().toString());
                if (matcher.matches()) {
                    String method = matcher.group(2);
                    String sanitizedPath = matcher.group(3);

                    String requestPath = getRequestPath(file, sanitizedPath);
                    String content = Files.readString(file);

                    // Replace URIs if we detected the original base
                    String modifiedContent = content;
                    if (detectedOriginalBaseUri != null) {
                        modifiedContent = content.replace(detectedOriginalBaseUri, mockBaseUri);
                    }

                    // Populate map for snapshot
                    loadedFixtures.put(file.getFileName().toString(), modifiedContent);

                    // Map the request path
                    String effectivePath = requestPath;
                    if (detectedOriginalBaseUri != null) {
                        java.net.URI originalUri = java.net.URI.create(detectedOriginalBaseUri);
                        java.net.URI mockUri = java.net.URI.create(mockBaseUri);

                        if (requestPath.startsWith(originalUri.getPath())) {
                            // Replace /sandbox01-rm with /rm (or whatever mockBaseUri path is, which is usually
                            // empty/root or just port)
                            // Actually mockBaseUri is http://localhost:port. It has no path.
                            // But the test expects /rm.
                            // Wait, JazzSnapshotTest uses /rm, /ccm, /qm.
                            // If original is /sandbox01-rm, we map to /rm.
                            // We need to know the target context.

                            // Heuristic: map /sandbox01-X to /X ?
                            // Or just rely on the test using the SAME context structure?
                            // JazzSnapshotTest uses /rm.
                            // If the recording is /sandbox01-rm, we must map /sandbox01-rm -> /rm.

                            String originalContext = originalUri.getPath(); // /sandbox01-rm
                            // How do we know the target is /rm?
                            // We can try to infer from the filename or content?
                            // Or just strict replacement?

                            // For now, let's assume we map the detected context to the one in the test?
                            // But we don't know the test context here easily without passing it.

                            // Let's just strip the original context and prepend the one from the request?
                            // No, easier: Replace originalBaseUri string in content is fine.
                            // For the mock expectation path:
                            // If request was /sandbox01-rm/foo
                            // We want to match /rm/foo ?
                            // Only if the test client is configured to use /rm.

                            // If we use recorded fixtures, we should probably update the test to use the recorded
                            // context!
                            // But the test hardcodes "/rm".

                            // Let's try to map dynamically.
                            // If original context contains "-rm", map to "/rm".
                            if (originalContext.endsWith("-rm")) {
                                effectivePath = "/rm" + requestPath.substring(originalContext.length());
                            } else if (originalContext.endsWith("-ccm")) {
                                effectivePath = "/ccm" + requestPath.substring(originalContext.length());
                            } else if (originalContext.endsWith("-qm")) {
                                effectivePath = "/qm" + requestPath.substring(originalContext.length());
                            }
                        }
                    }

                    mockServer
                            .when(HttpRequest.request().withMethod(method).withPath(effectivePath))
                            .respond(HttpResponse.response()
                                    .withStatusCode(200)
                                    .withContentType(MediaType.APPLICATION_XML)
                                    .withBody(modifiedContent));

                    log.info("Loaded fixture: {} -> {} {}", file.getFileName(), method, effectivePath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixtures", e);
        }
    }

    private static String getRequestUrl(Path xmlFile) throws IOException {
        Path metaFile = xmlFile.resolveSibling(xmlFile.getFileName().toString() + ".meta");
        if (Files.exists(metaFile)) {
            List<String> lines = Files.readAllLines(metaFile);
            for (String line : lines) {
                if (line.startsWith("URL: ")) {
                    return line.substring(5).trim();
                }
            }
        }
        return null;
    }

    private static String getRequestPath(Path xmlFile, String sanitizedPath) throws IOException {
        String url = getRequestUrl(xmlFile);
        if (url != null) {
            return java.net.URI.create(url).getPath();
        }
        return "/" + sanitizedPath.replace("_", "/");
    }
}
