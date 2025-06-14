package org.eclipse.lyo.samples.client;

/**
 * Record to hold structured error details for display in the UI
 */
public record ErrorDetails(
        Integer httpStatus,
        String httpStatusDescription,
        String oauthProblem,
        String oauthSignature,
        String oauthSignatureBaseString,
        String oauthSignatureMethod,
        String generalErrorMessage) {

    /**
     * Create ErrorDetails from an HTTP response
     */
    public static ErrorDetails fromHttpResponse(int status, String statusDescription) {
        return new ErrorDetails(status, statusDescription, null, null, null, null, null);
    }

    /**
     * Create ErrorDetails from an HTTP Response object
     */
    public static ErrorDetails fromHttpResponse(jakarta.ws.rs.core.Response response) {
        if (response == null) {
            return new ErrorDetails(null, null, null, null, null, null, "No response received");
        }

        int status = response.getStatus();
        String statusDescription = getHttpStatusDescription(status);

        // Try to read response body for OAuth error details if it's a 401
        String oauthProblem = null;
        String oauthSignature = null;
        String oauthSignatureBaseString = null;
        String oauthSignatureMethod = null;
        String generalMessage = null;

        if (status == 401) {
            try {
                // Check WWW-Authenticate header for OAuth details
                String wwwAuthHeader = response.getHeaderString("WWW-Authenticate");
                if (wwwAuthHeader != null) {
                    oauthProblem = extractParameter(wwwAuthHeader, "oauth_problem");
                    oauthSignature = extractParameter(wwwAuthHeader, "oauth_signature");
                    oauthSignatureBaseString =
                            extractParameter(wwwAuthHeader, "oauth_signature_base_string");
                    oauthSignatureMethod =
                            extractParameter(wwwAuthHeader, "oauth_signature_method");
                }

                // Try to read response body for additional OAuth details
                if (response.hasEntity()) {
                    String responseBody = response.readEntity(String.class);
                    if (responseBody != null && !responseBody.trim().isEmpty()) {
                        if (oauthProblem == null) {
                            oauthProblem = extractParameter(responseBody, "oauth_problem");
                        }
                        if (oauthSignature == null) {
                            oauthSignature = extractParameter(responseBody, "oauth_signature");
                        }
                        if (oauthSignatureBaseString == null) {
                            oauthSignatureBaseString =
                                    extractParameter(responseBody, "oauth_signature_base_string");
                        }
                        if (oauthSignatureMethod == null) {
                            oauthSignatureMethod =
                                    extractParameter(responseBody, "oauth_signature_method");
                        }
                        generalMessage = responseBody;
                    }
                }
            } catch (Exception e) {
                // If we can't read the response, just use the status
                generalMessage = "HTTP " + status + " " + statusDescription;
            }
        }

        if (generalMessage == null) {
            generalMessage = "HTTP " + status + " " + statusDescription;
        }

        return new ErrorDetails(
                status,
                statusDescription,
                oauthProblem,
                oauthSignature,
                oauthSignatureBaseString,
                oauthSignatureMethod,
                generalMessage);
    }

    /**
     * Create ErrorDetails from an OAuth exception, attempting to extract HTTP status
     */
    public static ErrorDetails fromOAuthException(Exception e) {
        String oauthProblem = null;
        String oauthSignature = null;
        String oauthSignatureBaseString = null;
        String oauthSignatureMethod = null;
        Integer httpStatus = null;
        String httpStatusDescription = null;

        // Debug logging to understand what we're getting
        System.out.println("DEBUG ErrorDetails.fromOAuthException:");
        System.out.println("  Exception class: " + e.getClass().getSimpleName());
        System.out.println("  Exception message: " + e.getMessage());
        if (e.getCause() != null) {
            System.out.println("  Cause class: " + e.getCause().getClass().getSimpleName());
            System.out.println("  Cause message: " + e.getCause().getMessage());
        }

        // Try to extract HTTP status from exception message
        if (e.getMessage() != null) {
            httpStatus = extractHttpStatus(e.getMessage());
            if (httpStatus != null) {
                httpStatusDescription = getHttpStatusDescription(httpStatus);
            }
        }

        // First try to get details from the cause if it's an OAuthProblemException
        if (e.getCause() != null
                && e.getCause().getClass().getSimpleName().equals("OAuthProblemException")) {
            String message = e.getCause().getMessage();
            oauthProblem = extractParameter(message, "oauth_problem");
            oauthSignature = extractParameter(message, "oauth_signature");
            oauthSignatureBaseString = extractParameter(message, "oauth_signature_base_string");
            oauthSignatureMethod = extractParameter(message, "oauth_signature_method");
        }

        // If no details found in cause, try the main exception message
        if (oauthProblem == null && e.getMessage() != null) {
            String message = e.getMessage();
            oauthProblem = extractParameter(message, "oauth_problem");
            if (oauthSignature == null) {
                oauthSignature = extractParameter(message, "oauth_signature");
            }
            if (oauthSignatureBaseString == null) {
                oauthSignatureBaseString = extractParameter(message, "oauth_signature_base_string");
            }
            if (oauthSignatureMethod == null) {
                oauthSignatureMethod = extractParameter(message, "oauth_signature_method");
            }

            // If still no oauth_problem found, check if the message contains common OAuth error
            // terms
            if (oauthProblem == null) {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("signature_invalid")
                        || lowerMessage.contains("signature invalid")) {
                    oauthProblem = "signature_invalid";
                } else if (lowerMessage.contains("consumer_key_unknown")
                        || lowerMessage.contains("consumer key unknown")) {
                    oauthProblem = "consumer_key_unknown";
                } else if (lowerMessage.contains("timestamp_refused")
                        || lowerMessage.contains("timestamp refused")) {
                    oauthProblem = "timestamp_refused";
                } else if (lowerMessage.contains("token_rejected")
                        || lowerMessage.contains("token rejected")) {
                    oauthProblem = "token_rejected";
                }
            }
        }

        // For OAuth signature errors, try to extract signature base string from exception toString
        if (oauthSignatureBaseString == null && "signature_invalid".equals(oauthProblem)) {
            String exceptionString = e.toString();

            // Look for "oauth_signature base string:" pattern in the exception string
            String baseStringPattern = "oauth_signature base string: ";
            int baseStringIndex = exceptionString.indexOf(baseStringPattern);
            if (baseStringIndex >= 0) {
                int startIndex = baseStringIndex + baseStringPattern.length();
                int endIndex = exceptionString.indexOf('\n', startIndex);
                if (endIndex < 0) {
                    endIndex = exceptionString.length();
                }
                oauthSignatureBaseString = exceptionString.substring(startIndex, endIndex).trim();
            }
        }

        // If we found OAuth authentication-related problems but no HTTP status,
        // these typically indicate HTTP 401 responses
        if (httpStatus == null && oauthProblem != null) {
            if (oauthProblem.equals("signature_invalid")
                    || oauthProblem.equals("consumer_key_unknown")
                    || oauthProblem.equals("consumer_key_rejected")
                    || oauthProblem.equals("timestamp_refused")
                    || oauthProblem.equals("token_rejected")) {
                httpStatus = 401;
                httpStatusDescription = getHttpStatusDescription(401);
            }
        }

        // Debug logging to understand what we're getting
        System.out.println("DEBUG ErrorDetails.fromOAuthException:");
        System.out.println("  Exception class: " + e.getClass().getSimpleName());
        System.out.println("  Exception message: " + e.getMessage());
        System.out.println("  Exception toString: " + e.toString());
        if (e.getCause() != null) {
            System.out.println("  Cause class: " + e.getCause().getClass().getSimpleName());
            System.out.println("  Cause message: " + e.getCause().getMessage());
        }
        System.out.println("  Final oauthProblem: " + oauthProblem);
        System.out.println("  Final oauthSignature: " + oauthSignature);
        System.out.println("  Final oauthSignatureBaseString: " + oauthSignatureBaseString);
        System.out.println("  Final oauthSignatureMethod: " + oauthSignatureMethod);
        System.out.println("  Final httpStatus: " + httpStatus);
        System.out.println("  Final httpStatusDescription: " + httpStatusDescription);

        return new ErrorDetails(
                httpStatus,
                httpStatusDescription,
                oauthProblem,
                oauthSignature,
                oauthSignatureBaseString,
                oauthSignatureMethod,
                e.getMessage());
    }

    /**
     * Create ErrorDetails from a general exception
     */
    public static ErrorDetails fromException(Exception e) {
        return new ErrorDetails(null, null, null, null, null, null, e.getMessage());
    }

    /**
     * Create ErrorDetails combining HTTP and OAuth information
     */
    public static ErrorDetails fromHttpAndOAuth(
            int httpStatus, String statusDescription, Exception oauthException) {
        ErrorDetails oauthDetails = fromOAuthException(oauthException);
        return new ErrorDetails(
                httpStatus,
                statusDescription,
                oauthDetails.oauthProblem(),
                oauthDetails.oauthSignature(),
                oauthDetails.oauthSignatureBaseString(),
                oauthDetails.oauthSignatureMethod(),
                oauthException.getMessage());
    }

    /**
     * Extract a specific parameter value from an OAuth error message
     */
    private static String extractParameter(String message, String paramName) {
        if (message == null || paramName == null) return null;

        String searchPattern = paramName + "=";
        int startIndex = message.indexOf(searchPattern);
        if (startIndex < 0) return null;

        startIndex += searchPattern.length();

        // Handle quoted values
        if (startIndex < message.length() && message.charAt(startIndex) == '"') {
            startIndex++; // Skip opening quote
            int endIndex = message.indexOf('"', startIndex);
            if (endIndex > startIndex) {
                return message.substring(startIndex, endIndex);
            }
        }

        // Handle unquoted values (ending with comma, semicolon, or end of string)
        int endIndex = startIndex;
        while (endIndex < message.length()) {
            char c = message.charAt(endIndex);
            if (c == ',' || c == ';' || c == '&' || c == '\n' || c == '\r') {
                break;
            }
            endIndex++;
        }

        if (endIndex > startIndex) {
            return message.substring(startIndex, endIndex).trim();
        }

        return null;
    }

    /**
     * Extract HTTP status code from exception message
     */
    private static Integer extractHttpStatus(String message) {
        if (message == null) return null;

        // Look for various HTTP status patterns:
        // "HTTP 401", "401 Unauthorized", "HTTP/1.1 401", "Response status: 401", etc.
        java.util.regex.Pattern[] patterns = {
            java.util.regex.Pattern.compile("(?:HTTP(?:/\\d\\.\\d)?\\s+)(\\d{3})"),
            java.util.regex.Pattern.compile("(?:Response\\s+status:?\\s*)(\\d{3})"),
            java.util.regex.Pattern.compile("(?:Status:?\\s*)(\\d{3})"),
            java.util.regex.Pattern.compile(
                    "(\\d{3})\\s+(?:Unauthorized|Forbidden|Bad Request|Not Found)"),
            java.util.regex.Pattern.compile("(?:^|\\s)(\\d{3})(?:\\s|$)")
        };

        for (java.util.regex.Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                try {
                    int status = Integer.parseInt(matcher.group(1));
                    // Only return valid HTTP status codes (100-599)
                    if (status >= 100 && status < 600) {
                        return status;
                    }
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }
        return null;
    }

    /**
     * Get human-readable description for HTTP status codes
     */
    private static String getHttpStatusDescription(Integer statusCode) {
        if (statusCode == null) return null;

        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized - Authentication required";
            case 403:
                return "Forbidden - Access denied";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            default:
                return "HTTP Error";
        }
    }

    /**
     * Format the error details as a structured string for JSP display
     */
    public String toFormattedString() {
        StringBuilder details = new StringBuilder();

        // HTTP status
        if (httpStatus != null) {
            details.append("• HTTP Status: ").append(httpStatus);
            if (httpStatusDescription != null) {
                details.append(" (").append(httpStatusDescription).append(")");
            }
            details.append("\n");
        }

        // OAuth problem
        if (oauthProblem != null && !oauthProblem.equals("unknown_error")) {
            details.append("• OAuth Problem: ").append(oauthProblem).append("\n");
        }

        // OAuth signature base string
        if (oauthSignatureBaseString != null && !oauthSignatureBaseString.trim().isEmpty()) {
            String baseString = oauthSignatureBaseString;
            // Truncate very long base strings for readability
            if (baseString.length() > 200) {
                baseString = baseString.substring(0, 200) + "...";
            }
            details.append("• OAuth Signature Base String: ").append(baseString).append("\n");
        }

        // If no specific details found, include the general error message
        if (details.length() == 0 && generalErrorMessage != null) {
            details.append("• Error: ").append(generalErrorMessage).append("\n");
        }

        return details.toString();
    }
}
