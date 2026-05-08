/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.ai.utils;

import org.netuno.library.doc.*;
import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ContextRetrievalChunker - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI ContextRetrievalChunker",
                introduction = "Utilitário de divisão de texto em blocos (chunks) para recuperação de contexto em pipelines RAG (Retrieval-Augmented Generation).\n\n"
                        + "Permite dividir documentos Markdown em blocos de tamanho controlado, com sobreposição configurável, "
                        + "preservando a estrutura de cabeçalhos e blocos de código para melhor qualidade de recuperação semântica.\n\n"
                        + "**Características principais:**\n"
                        + "- Preserva a hierarquia de cabeçalhos (H1-H6)\n"
                        + "- Protege blocos de código contra quebras\n"
                        + "- Tamanho de chunk padrão: 512 caracteres\n"
                        + "- Sobreposição padrão: 50 caracteres\n"
                        + "- Quebras inteligentes em limites semânticos (cabeçalhos, parágrafos)",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo básico\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "const chunks = chunker.markdown(documentoMD)\n"
                                        + "\n"
                                        + "for (const chunk of chunks) {\n"
                                        + "    _log.info(`Chunk ${chunk.get('index')}: ${chunk.get('heading')}`)\n"
                                        + "    _log.info(`Texto: ${chunk.get('text')}`)\n"
                                        + "}\n"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "AI ContextRetrievalChunker",
                introduction = "Text chunking utility for context retrieval in RAG (Retrieval-Augmented Generation) pipelines.\n\n"
                        + "Splits Markdown documents into controlled-size chunks with configurable overlap, "
                        + "preserving heading structure and code blocks for better semantic retrieval quality.\n\n"
                        + "**Key features:**\n"
                        + "- Preserves heading hierarchy (H1-H6)\n"
                        + "- Protects code blocks from being broken\n"
                        + "- Default chunk size: 512 characters\n"
                        + "- Default overlap: 50 characters\n"
                        + "- Smart breaks at semantic boundaries (headings, paragraphs)",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Basic example\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "const chunks = chunker.markdown(markdownDocument)\n"
                                        + "\n"
                                        + "for (const chunk of chunks) {\n"
                                        + "    _log.info(`Chunk ${chunk.get('index')}: ${chunk.get('heading')}`)\n"
                                        + "    _log.info(`Text: ${chunk.get('text')}`)\n"
                                        + "}\n"
                        )
                }
        )
})
public class ContextRetrievalChunker {
    private static final int DEFAULT_CHUNK_SIZE = 512;
    private static final int DEFAULT_OVERLAP = 50;

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[\\s\\S]*?```");


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento Markdown em blocos de texto utilizando os valores predefinidos de tamanho de bloco ("
                            + DEFAULT_CHUNK_SIZE + " caracteres) e sobreposição (" + DEFAULT_OVERLAP + " caracteres). "
                            + "Cada bloco preserva o cabeçalho Markdown mais próximo como contexto e respeita os limites de blocos de código.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown('# Título\\n\\nConteúdo do documento...')\n"
                                            + "\n"
                                            + "for (const chunk of chunks) {\n"
                                            + "    _log.info(chunk.get('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a Markdown document into text chunks using the default chunk size ("
                            + DEFAULT_CHUNK_SIZE + " characters) and overlap (" + DEFAULT_OVERLAP + " characters). "
                            + "Each chunk preserves the nearest Markdown heading as context and respects code block boundaries.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown('# Title\\n\\nDocument content...')\n"
                                            + "\n"
                                            + "for (const chunk of chunks) {\n"
                                            + "    _log.info(chunk.get('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "markdown", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto em formato Markdown a dividir em blocos."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text in Markdown format to split into chunks."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de blocos, cada um com os campos: `index` (posição na sequência), `start` (posição inicial no texto original), `heading` (cabeçalho Markdown mais próximo) e `text` (conteúdo do bloco, com cabeçalho de contexto prefixado se necessário)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of chunks, each with the fields: `index` (position in the sequence), `start` (starting position in the original text), `heading` (nearest Markdown heading) and `text` (chunk content, with context heading prepended if needed)."
            )
    })
    public Values markdown(String markdown) {
        return markdown(markdown, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento Markdown em blocos de texto com tamanho de bloco e sobreposição configuráveis. "
                            + "Os cortes são feitos preferencialmente em cabeçalhos Markdown, parágrafos ou espaços, evitando sempre cortar dentro de blocos de código. "
                            + "Quando um bloco não começa por um cabeçalho, o cabeçalho mais próximo é automaticamente prefixado para preservar o contexto semântico.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Blocos de 1024 caracteres com sobreposição de 100\n"
                                            + "const chunks = chunker.markdown(markdown, 1024, 100)\n"
                                            + "\n"
                                            + "for (const chunk of chunks) {\n"
                                            + "    _log.info('--- Chunk ' + chunk.get('index'))\n"
                                            + "    _log.info('Heading: ' + chunk.get('heading'))\n"
                                            + "    _log.info(chunk.get('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a Markdown document into text chunks with configurable chunk size and overlap. "
                            + "Cuts are made preferably at Markdown headings, paragraphs or spaces, always avoiding splitting inside code blocks. "
                            + "When a chunk does not start with a heading, the nearest heading is automatically prepended to preserve semantic context.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code =  "// Chunks of 1024 characters with overlap of 100\n"
                                            + "const chunks = chunker.markdown(markdown, 1024, 100)\n"
                                            + "\n"
                                            + "for (const chunk of chunks) {\n"
                                            + "    _log.info('--- Chunk ' + chunk.get('index'))\n"
                                            + "    _log.info('Heading: ' + chunk.get('heading'))\n"
                                            + "    _log.info(chunk.get('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "markdown", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto em formato Markdown a dividir em blocos."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text in Markdown format to split into chunks."
                    )
            }),
            @ParameterDoc(name = "chunkSize", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "tamanhodoBloco",
                            description = "Número máximo de caracteres por bloco. Valor predefinido: " + DEFAULT_CHUNK_SIZE + "."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum number of characters per chunk. Default value: " + DEFAULT_CHUNK_SIZE + "."
                    )
            }),
            @ParameterDoc(name = "overlap", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "sobreposicao",
                            description = "Número de caracteres de sobreposição entre blocos consecutivos, para preservar continuidade de contexto. Valor predefinido: " + DEFAULT_OVERLAP + "."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Number of overlapping characters between consecutive chunks, to preserve context continuity. Default value: " + DEFAULT_OVERLAP + "."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de blocos, cada um com os campos: `index` (posição na sequência), `start` (posição inicial no texto original), `heading` (cabeçalho Markdown mais próximo) e `text` (conteúdo do bloco, com cabeçalho de contexto prefixado se necessário)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of chunks, each with the fields: `index` (position in the sequence), `start` (starting position in the original text), `heading` (nearest Markdown heading) and `text` (chunk content, with context heading prepended if needed)."
            )
    })
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

    public Values pdf(String pdfText) {
        return pdf(pdfText, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public Values pdf(String pdfText, int chunkSize, int overlapSize) {
        if (pdfText == null || pdfText.isBlank()) return Values.newList();
        String normalizedText = normalizePdfText(pdfText);
        List<Integer> sentenceOffsets   = new ArrayList<>();
        List<String>  sentenceTexts     = new ArrayList<>();
        List<Boolean> isParagraphOpener = new ArrayList<>();
        tokenizeIntoSentences(normalizedText, sentenceOffsets, sentenceTexts, isParagraphOpener);
        return buildPdfChunks(sentenceOffsets, sentenceTexts, isParagraphOpener, chunkSize, overlapSize);
    }

    private String normalizePdfText(String rawText) {
        return rawText
                .replaceAll("\\r\\n|\\r", "\n")
                .replaceAll("-\\n([a-z])", "$1")
                .replaceAll("[ \\t]+(?=\\n)", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private void tokenizeIntoSentences(String text,
                                       List<Integer> sentenceOffsets,
                                       List<String>  sentenceTexts,
                                       List<Boolean> isParagraphOpener) {
        Pattern sentenceBoundary = Pattern.compile("(?<=[.!?])[ \\t]+(?=[\\p{Lu}\\p{N}\"'(\\[])");
        int cursor = 0;
        while (cursor < text.length()) {
            int paragraphStart = cursor;
            while (paragraphStart < text.length() && text.charAt(paragraphStart) == '\n') {
                paragraphStart++;
            }
            if (paragraphStart >= text.length()) break;

            int paragraphEnd = text.indexOf("\n\n", paragraphStart);
            if (paragraphEnd < 0) paragraphEnd = text.length();

            String paragraph = text.substring(paragraphStart, paragraphEnd);
            if (!paragraph.isBlank()) {
                String[] sentences        = sentenceBoundary.split(paragraph);
                boolean  opensNewParagraph = true;
                int      searchCursor     = paragraphStart;
                for (String raw : sentences) {
                    String sentence = raw.trim();
                    if (sentence.isBlank()) continue;
                    int offset = text.indexOf(sentence, searchCursor);
                    if (offset < 0 || offset >= paragraphEnd) offset = searchCursor;
                    searchCursor = offset + sentence.length();
                    sentenceOffsets.add(offset);
                    sentenceTexts.add(sentence);
                    isParagraphOpener.add(opensNewParagraph);
                    opensNewParagraph = false;
                }
            }
            cursor = paragraphEnd + 2;
        }
    }

    private Values buildPdfChunks(List<Integer> sentenceOffsets,
                                  List<String>  sentenceTexts,
                                  List<Boolean> isParagraphOpener,
                                  int chunkSize,
                                  int overlapSize) {
        Values result = Values.newList();
        if (sentenceTexts.isEmpty()) return result;

        final int totalSentences = sentenceTexts.size();
        final int minChunkLength = Math.max(1, chunkSize / 4);
        int chunkIndex  = 0;
        int windowStart = 0;

        while (windowStart < totalSentences) {
            int           chunkStartOffset = sentenceOffsets.get(windowStart);
            StringBuilder chunkContent     = new StringBuilder();
            int           windowEnd        = windowStart;

            while (windowEnd < totalSentences) {
                String  sentence           = sentenceTexts.get(windowEnd);
                boolean startsNewParagraph = isParagraphOpener.get(windowEnd);
                String  separator          = chunkContent.isEmpty()
                        ? ""
                        : (startsNewParagraph ? "\n\n" : " ");
                int proposedLength = chunkContent.length() + separator.length() + sentence.length();
                if (proposedLength > chunkSize) {
                    if (chunkContent.isEmpty()) { chunkContent.append(sentence); windowEnd++; }
                    break;
                }
                chunkContent.append(separator).append(sentence);
                windowEnd++;
            }

            // Absorb tiny trailing tail to avoid orphan micro-chunks
            if (windowEnd < totalSentences) {
                int remainingLength = 0;
                for (int k = windowEnd; k < totalSentences; k++) {
                    remainingLength += sentenceTexts.get(k).length() + 2;
                }
                if (remainingLength > 0 && remainingLength < minChunkLength) {
                    while (windowEnd < totalSentences) {
                        boolean startsNewParagraph = isParagraphOpener.get(windowEnd);
                        chunkContent.append(startsNewParagraph ? "\n\n" : " ")
                                .append(sentenceTexts.get(windowEnd++));
                    }
                }
            }

            String chunkText = chunkContent.toString().strip();
            if (!chunkText.isBlank()) {
                Values chunk = Values.newMap();
                chunk.set("index", chunkIndex++);
                chunk.set("start", chunkStartOffset);
                chunk.set("text",  chunkText);
                result.add(chunk);
            }

            if (windowEnd >= totalSentences) break;

            // Overlap: rewind to repeat ≈ overlapSize chars in the next chunk
            int accumulatedOverlap = 0;
            int nextWindowStart    = windowEnd;
            for (int k = windowEnd - 1; k > windowStart; k--) {
                int sentenceLength = sentenceTexts.get(k).length() + (isParagraphOpener.get(k) ? 2 : 1);
                if (accumulatedOverlap + sentenceLength > overlapSize) break;
                accumulatedOverlap += sentenceLength;
                nextWindowStart = k;
            }
            windowStart = Math.max(nextWindowStart, windowStart + 1);
        }

        return result;
    }
}