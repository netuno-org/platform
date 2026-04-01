package org.netuno.tritao.ai.utils;

import org.netuno.psamata.Values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextRetrievalChunker {

    private static final int DEFAULT_CHUNK_SIZE = 512;
    private static final int DEFAULT_OVERLAP = 50;
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]\\(([^)]+)\\)");

    public Values markdown(String markdown, int chunkSize, int overlap) {
        Values chunks = Values.newList();

        Values headingStructure = extractHeadingStructure(markdown);

        return headingStructure;

    }

    private static Values extractHeadingStructure(String markdown) {
        Values structure = Values.newMap();

        Matcher matcher = HEADING_PATTERN.matcher(markdown);
        while (matcher.find()) {
            int level = matcher.group(1).length();
            int position = matcher.start();
            String heading = matcher.group(2);

            String levelKey = "level_" + level;
            if (!structure.hasKey(levelKey)) {
                structure.set(levelKey, Values.newList());
            }

            Values headingInfo = Values.newMap();
            headingInfo.set("position", position);
            headingInfo.set("level", level);
            headingInfo.set("text", heading);

            structure.getValues(levelKey).add(headingInfo);
        }

        return structure;
    }
}
