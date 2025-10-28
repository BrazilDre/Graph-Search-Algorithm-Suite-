package edu.asu.cse464.graphparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project Part 1
 * Feature 1 parse DOT
 * Feature 2 add nodes
 * Feature 3 add edges
 * Feature 4 output text, DOT, and png
 */
public class GraphParser {
    private final Set<String> nodes = new LinkedHashSet<>();
    private final Set<Edge> edges = new LinkedHashSet<>();

    // simple rule for lines like A -> B;
    private static final Pattern EDGE_LINE =
            Pattern.compile("^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*->\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*;.*$");

    /** Feature 1 parse DOT from a file */
    public void parseGraph(String filepath) throws IOException {
        Objects.requireNonNull(filepath, "filepath");
        Path p = Path.of(filepath);
        if (!Files.exists(p)) {
            throw new IOException("File not found: " + filepath);
        }
        nodes.clear();
        edges.clear();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = stripInlineComment(line).trim();
                if (line.isEmpty()) continue;
                Matcher m = EDGE_LINE.matcher(line);
                if (m.matches()) {
                    String src = m.group(1);
                    String dst = m.group(2);
                    addEdge(src, dst);
                } else {
                    // ignore other DOT lines for Part 1 scope
                }
            }
        }
    }

    private static String stripInlineComment(String s) {
        int i = s.indexOf("//");
        if (i >= 0) return s.substring(0, i);
        return s;
    }

    /** Feature 2 add a single node, return false on duplicate */
    public boolean addNode(String label) {
        Objects.requireNonNull(label, "label");
        return nodes.add(label);
    }

    /** Feature 2 add many nodes, return true if any new node was added */
    public boolean addNodes(String[] labels) {
        Objects.requireNonNull(labels, "labels");
        boolean any = false;
        for (String s : labels) {
            if (s != null && !s.isBlank()) {
                any |= addNode(s);
            }
        }
        return any;
    }

    /** Feature 3 add an edge, return false on duplicate */
    public boolean addEdge(String src, String dst) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(dst, "dst");
        nodes.add(src);
        nodes.add(dst);
        return edges.add(new Edge(src, dst));
    }

    /** Feature 4 write a readable summary */
    public void outputGraph(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        Files.writeString(Path.of(path), toString(), StandardCharsets.UTF_8);
    }

    /** Feature 4 write DOT text */
    public void outputDOTGraph(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        Files.writeString(Path.of(path), toDotString(), StandardCharsets.UTF_8);
    }

    /** Feature 4 write a png using the dot command */
    public void outputGraphics(String path, String format) throws IOException {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(format, "format");
        String fmt = format.toLowerCase(Locale.ROOT);
        if (!fmt.equals("png")) {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Use png");
        }

        Path pngOut = Path.of(path);
        Path tmpDot = Files.createTempFile("gp-", ".dot");
        try {
            Files.writeString(tmpDot, toDotString(), StandardCharsets.UTF_8);

            Process p = new ProcessBuilder("dot", "-Tpng", tmpDot.toString(), "-o", pngOut.toString())
                    .redirectErrorStream(true)
                    .start();

            try (InputStream is = p.getInputStream();
                 OutputStream os = p.getOutputStream()) {
                // drain output so the process will not block
                try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    while (r.readLine() != null) {
                        // discard
                    }
                }
            }

            int code;
            try {
                code = p.waitFor();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IOException("dot was interrupted", ie);
            }
            if (code != 0) {
                throw new IOException("dot exit code " + code);
            }
            if (!Files.exists(pngOut) || Files.size(pngOut) == 0) {
                throw new IOException("png not created");
            }
        } finally {
            try { Files.deleteIfExists(tmpDot); } catch (IOException ignore) {}
        }
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public String toDotString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        // include nodes in case there are isolated ones
        for (String n : nodes) {
            sb.append("  ").append(n).append(";\n");
        }
        for (Edge e : edges) {
            sb.append("  ").append(e.src).append(" -> ").append(e.dst).append(";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Directed graph\n");
        sb.append("Nodes: ").append(getNodeCount()).append("\n");
        sb.append("Edges: ").append(getEdgeCount()).append("\n");
        int shown = 0;
        for (Edge e : edges) {
            if (shown >= 10) break;
            sb.append(e.src).append(" -> ").append(e.dst).append("\n");
            shown++;
        }
        return sb.toString();
    }

    private static final class Edge {
        final String src;
        final String dst;

        Edge(String s, String d) {
            this.src = s;
            this.dst = d;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge other = (Edge) o;
            return src.equals(other.src) && dst.equals(other.dst);
        }

        @Override
        public int hashCode() {
            return 31 * src.hashCode() + dst.hashCode();
        }
    }
}
