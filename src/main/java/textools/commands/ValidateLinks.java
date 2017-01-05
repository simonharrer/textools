package textools.commands;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

import textools.Command;
import textools.commands.latex.Latex;
import textools.tasks.FileSystemTasks;

/**
 * Find acronyms defined in the acronym package but that are not yet included.
 */
public class ValidateLinks implements Command {

    @Override
    public String getName() {
        return "validate-links";
    }

    @Override
    public String getDescription() {
        return "detects malformed and unreachable urls";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        Set<Link> links = new HashSet<>();
        Latex.with(texFiles, (line, lineNumber, file) -> links.addAll(Link.find(line, lineNumber, file)));

        links.stream()
                .parallel()
                .filter(link -> {
                    try {
                        link.validateUrl();
                        return true;
                    } catch (MalformedURLException e) {
                        System.out.format("%s#%4d URL %s is malformed %s%n", link.file, link.lineNumber, link.url, e.getMessage());
                        return false;
                    }
                })
                .forEach(link -> {
                    try {
                        final int statusCode = link.getStatusCode();
                        if (statusCode < 0) {
                            System.out.format("%s#%4d URL %s returned no response%n", link.file, link.lineNumber, link.url);
                        } else if (statusCode != 200) {
                            System.out.format("%s#%4d URL %s return http status code %d%n", link.file, link.lineNumber, link.url, statusCode);
                        }
                    } catch (IOException e) {
                        System.out.format("%s#%4d URL %s %s%n", link.file, link.lineNumber, link.url, e.getMessage());
                    }
                });
    }

    public static class Link {

        private static final Pattern DEFINITION_DETECTION = Pattern.compile("\\\\url\\{(?<url>[^\\}]*)\\}");

        public static List<Link> find(String line, int lineNumber, Path file) {
            final Matcher matcher = DEFINITION_DETECTION.matcher(line);

            List<Link> result = new ArrayList<>();
            while (matcher.find()) {
                result.add(new Link(matcher.group("url"), lineNumber, file));
            }

            return result;
        }

        private final String url;
        private final int lineNumber;
        private final Path file;

        public Link(String url, int lineNumber, Path file) {
            this.url = url.replace("\\#","#"); // replace some things
            this.lineNumber = lineNumber;
            this.file = file;
        }

        public void validateUrl() throws MalformedURLException {
            new URL(url);
        }

        public int getStatusCode() throws IOException {
            String finalUrl = url;
            try {
                finalUrl = getFinalURL(url);
            } catch (IOException e) {

            }

            try {
                final URL url = new URL(finalUrl);
                HttpURLConnection http = null;
                try {
                    http = (HttpURLConnection) url.openConnection();
                    http.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17");
                    int statusCode = http.getResponseCode();
                    return statusCode;
                } finally {
                    if (http != null) {
                        http.disconnect();
                    }
                }
            } catch (MalformedURLException e) {
                return -1;
            }
        }

        private static String getFinalURL(String url) throws IOException {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(false);
                con.connect();
                con.getInputStream();

                if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String redirectUrl = con.getHeaderField("Location");
                    return getFinalURL(redirectUrl);
                }
                return url;
            } catch (IOException e) {
                return url;
            }
        }
    }

}
