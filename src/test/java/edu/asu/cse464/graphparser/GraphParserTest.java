package edu.asu.cse464.graphparser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Project Part 1 features:
 * parse
 * add nodes
 * add edges
 * output to DOT and png
 */
public class GraphParserTest {

    private static Path writeDot(Path dir, String fileName, String dot) throws IOException {
        Path p = dir.resolve(fileName);
        Files.writeString(p, dot, StandardCharsets.UTF_8);
        return p;
    }

    private static String simpleDot() {
        return "digraph G {\n" +
                "  A -> B;\n" +
                "  B -> C;\n" +
                "}\n";
    }

    private static String complexDot() {
        return "digraph X {\n" +
                "  A -> B;\n" +
                "  B -> C;\n" +
                "  A -> C;\n" +
                "  C -> E;\n" +
                "  E -> A;\n" +
                "}\n";
    }

    @Test
    void testParseGraph_SimpleGraph(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test1.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String out = gp.toString();
        assertNotNull(out);
        assertTrue(out.contains("A"));
        assertTrue(out.contains("B"));
        assertTrue(out.contains("C"));
        assertTrue(out.contains("A -> B") || out.contains("A->B"));
        assertTrue(out.contains("B -> C") || out.contains("B->C"));
    }

    @Test
    void testParseGraph_ComplexGraph(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test2.dot", complexDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String out = gp.toString();
        assertTrue(out.contains("A"));
        assertTrue(out.contains("B"));
        assertTrue(out.contains("C"));
        assertTrue(out.contains("E"));
        assertTrue(out.contains("A -> B") || out.contains("A->B"));
        assertTrue(out.contains("B -> C") || out.contains("B->C"));
        assertTrue(out.contains("A -> C") || out.contains("A->C"));
        assertTrue(out.contains("C -> E") || out.contains("C->E"));
        assertTrue(out.contains("E -> A") || out.contains("E->A"));
    }

    @Test
    void testGetNodeLabels(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test3.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String out = gp.toString();
        assertTrue(out.contains("A"));
        assertTrue(out.contains("B"));
        assertTrue(out.contains("C"));
    }

    @Test
    void testGetEdges(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test4.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String out = gp.toString();
        assertTrue(out.contains("A -> B") || out.contains("A->B"));
        assertTrue(out.contains("B -> C") || out.contains("B->C"));
    }

    @Test
    void testToString(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test5.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String out = gp.toString();
        assertNotNull(out);
        assertFalse(out.isBlank());
        assertTrue(out.toLowerCase(Locale.ROOT).contains("node") ||
                out.toLowerCase(Locale.ROOT).contains("edge"));
    }

    @Test
    void testOutputGraph(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test6.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        Path outFile = temp.resolve("out.txt");
        gp.outputGraph(outFile.toString());

        assertTrue(Files.exists(outFile));
        String written = Files.readString(outFile);
        assertFalse(written.isBlank());
        assertTrue(written.contains("A"));
    }

    @Test
    void testAddNode_Duplicate(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test7.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        boolean added = gp.addNode("A");
        assertFalse(added, "Adding A again should return false");
    }

    @Test
    void testAddNodes_WithDuplicates(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test8.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        String[] add = {"C", "D", "E", "A"};
        gp.addNodes(add);
        String out = gp.toString();

        assertTrue(out.contains("D"));
        assertTrue(out.contains("E"));
    }

    @Test
    void testAddEdge_DuplicateEdge(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test9.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        boolean added = gp.addEdge("A", "B");
        assertFalse(added, "Duplicate edge should return false");
    }

    @Test
    void testOutputDOTGraph_Success(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test10.dot", complexDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        Path dotOut = temp.resolve("roundtrip.dot");
        gp.outputDOTGraph(dotOut.toString());

        assertTrue(Files.exists(dotOut));
        String roundtrip = Files.readString(dotOut);
        assertTrue(roundtrip.contains("digraph"));
        assertTrue(roundtrip.contains("A"));
        assertTrue(roundtrip.contains("E"));
    }

    @Test
    void testParseGraph_FileNotFound(@TempDir Path temp) {
        GraphParser gp = new GraphParser();
        IOException ex = assertThrows(IOException.class, () -> gp.parseGraph("nonexistent.dot"));
        assertTrue(ex.getMessage().toLowerCase(Locale.ROOT).contains("file"));
    }

    @Test
    void testOutputGraphics_UnsupportedFormat(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test11.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> gp.outputGraphics(temp.resolve("x.xyz").toString(), "xyz"));
        assertTrue(ex.getMessage().toLowerCase(Locale.ROOT).contains("png"));
    }

    @Test
    void testOutputGraphics_Png(@TempDir Path temp) throws Exception {
        Path in = writeDot(temp, "test12.dot", simpleDot());
        GraphParser gp = new GraphParser();
        gp.parseGraph(in.toString());

        Path png = temp.resolve("g.png");
        gp.outputGraphics(png.toString(), "png");
        assertTrue(Files.exists(png), "png should be created");
        assertTrue(Files.size(png) > 0, "png should not be empty");
    }
}
