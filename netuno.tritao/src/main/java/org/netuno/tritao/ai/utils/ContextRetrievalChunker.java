package org.netuno.tritao.ai.utils;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextRetrievalChunker {
    private static final int DEFAULT_CHUNK_SIZE = 512;
    private static final int DEFAULT_OVERLAP = 50;

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");

    public Values markdown(String markdown) {
        return markdown(markdown, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public Values markdown(String markdown, int chunkSize, int overlap) {
        String text = preprocessMarkdown(markdown);
        List<int[]> codeBlocks = extractRanges(CODE_BLOCK_PATTERN, text);
        Values headings = extractHeadings(text);
        return chunkText(text, chunkSize, overlap, codeBlocks, headings);
    }

    private String preprocessMarkdown(String markdown) {
        return markdown
                .replaceAll("\\r\\n", "\n")
                .replaceAll(" +\n", "\n")
                .replaceAll("\n{3,}", "\n\n");
    }

    private List<int[]> extractRanges(Pattern pattern, String text) {
        List<int[]> ranges = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            ranges.add(new int[]{matcher.start(), matcher.end()});
        }
        return ranges;
    }

    private Values extractHeadings(String text) {
        Values headings = Values.newList();
        Matcher m = HEADING_PATTERN.matcher(text);
        while (m.find()) {
            int level = m.group(1).length();
            Values h = Values.newMap();
            h.set("position", m.start());
            h.set("level", level);
            h.set("text", m.group(2).trim());
            h.set("raw", "#".repeat(level) + " " + m.group(2).trim());
            headings.add(h);
        }
        return headings;
    }

    private Values chunkText(String text, int chunkSize, int overlap, List<int[]> codeBlocks, Values headings) {
        Values chunks = Values.newList();
        if (text.length() <= chunkSize) {
            chunks.add(buildChunk(text, 0, headings, 0));
            return chunks;
        }

        int step = Math.max(1, chunkSize - overlap);
        int index = 0;
        for (int start = 0; start < text.length(); start += step) {
            int end = Math.min(start + chunkSize, text.length());

            end = findSafeBreak(text, start, end, step, codeBlocks, headings);

            String chunkText = text.substring(start, end).stripLeading();
            if (!chunkText.isBlank()) {
                chunks.add(buildChunk(chunkText, start, headings, index++));
            }
            if (end >= text.length()) break;
        }
        return chunks;
    }


    private String nearestHeading(Values headings, int pos) {
        String nearest = "";
        for (int i = 0; i < headings.size(); i++) {
            Values h = headings.getValues(i);
            if (h.getInt("position") <= pos) nearest = h.getString("raw");
            else break;
        }
        return nearest;
    }

    private Values buildChunk(String chunkText, int start, Values headings, int index) {
        String heading = nearestHeading(headings, start);
        String textWithContext = !heading.isEmpty() && !chunkText.startsWith(heading)
                ? heading + "\n\n" + chunkText
                : chunkText;

        Values chunk = Values.newMap();
        chunk.set("index", index);
        chunk.set("start", start);
        chunk.set("heading", heading);
        chunk.set("text", textWithContext);
        return chunk;
    }

    private int findSafeBreak(String text, int start, int end, int maxBacktrack, List<int[]> codeBlocks, Values headings) {
        int searchStart = Math.max(start, end - maxBacktrack);

        for (int i = headings.size() - 1; i >= 0; i--) {
            int hPos = headings.getValues(i).getInt("position");
            if (hPos <= end && hPos > searchStart && !insideCodeBlock(codeBlocks, hPos)) return hPos;
        }

        for (String sep : new String[]{"\n\n", "\n", " "}) {
            int pos = text.lastIndexOf(sep, end);
            if (pos > searchStart && !insideCodeBlock(codeBlocks, pos)) return pos + sep.length();
        }

        return end;
    }
    private boolean insideCodeBlock(List<int[]> ranges, int pos) {
        for (int[] r : ranges) if (pos >= r[0] && pos <= r[1]) return true;
        return false;
    }

}