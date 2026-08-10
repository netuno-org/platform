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

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.*;
import org.netuno.psamata.Values;
import org.netuno.tritao.ai.client.Client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ContextRetrievalChunker - Resource
 *
 * Structure-aware text chunker for RAG pipelines.
 *
 * The document is processed in a single linear pass through six stages:
 * fence-aware normalization, segmentation into atomic blocks, budget
 * measurement (characters or real BPE tokens), splitting of oversized blocks,
 * greedy packing with semantic overlap and a final merge of residual chunks.
 *
 * Two invariants hold by construction and everything else is built on them:
 * every block of the document belongs to exactly one chunk, so no text is ever
 * dropped; and the same input always produces the same chunks with the same
 * identifiers, so re-ingestion into a vector store is idempotent.
 *
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI ContextRetrievalChunker",
                introduction = "Utilitário de divisão de texto em blocos (chunks) para recuperação de contexto em pipelines RAG (Retrieval-Augmented Generation).\n\n"
                        + "Divide documentos Markdown, texto simples ou texto extraído de PDF em blocos de tamanho controlado, "
                        + "com sobreposição configurável, preservando a estrutura do documento para melhor qualidade de recuperação semântica.\n\n"
                        + "**Características principais:**\n"
                        + "- Segmentação estrutural: cabeçalhos, parágrafos, blocos de código, tabelas, listas e citações são reconhecidos e nunca partidos ao acaso\n"
                        + "- Contexto hierárquico: cada bloco recebe a árvore completa de cabeçalhos (`# Guia > ## Instalação > ### Windows`)\n"
                        + "- Orçamento em caracteres ou em tokens reais (BPE `cl100k_base` / `o200k_base`)\n"
                        + "- Blocos de código maiores que o limite são partidos por linhas com a marcação reaberta em cada parte\n"
                        + "- Tabelas maiores que o limite repetem o cabeçalho em cada parte\n"
                        + "- Sobreposição semântica por frases, nunca a meio de uma palavra\n"
                        + "- Sem perda de texto: todos os blocos do documento pertencem exatamente a um chunk\n"
                        + "- Determinístico: a mesma entrada produz sempre os mesmos chunks e os mesmos `id`, o que permite reingestão idempotente",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo básico\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "const chunks = chunker.markdown(documentoMD)\n"
                                        + "\n"
                                        + "for (const chunk of chunks.listOfValues()) {\n"
                                        + "    _log.info(`Chunk ${chunk.getInt('index')}: ${chunk.getString('breadcrumb')}`)\n"
                                        + "    _log.info(`Texto: ${chunk.getString('text')}`)\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Orçamento em tokens reais e ingestão no vector store\n"
                                        + "const client = _ai.client()\n"
                                        + "const vector = _ai.vector('default')\n"
                                        + "\n"
                                        + "const blocos = _ai.contextRetrievalChunker()\n"
                                        + "    .unit('tokens')\n"
                                        + "    .chunkSize(320)\n"
                                        + "    .overlap(48)\n"
                                        + "    .source('manual-v1')\n"
                                        + "    .markdown(documentoMD)\n"
                                        + "\n"
                                        + "for (const bloco of blocos.listOfValues()) {\n"
                                        + "    const resposta = client.embeddings('embeddinggemma:latest', bloco.getString('text'))\n"
                                        + "    const embedding = resposta.getValues('data').getValues(0).getValues('embedding')\n"
                                        + "    vector.add('netuno', bloco.getString('id'), embedding, bloco.getString('text'), bloco.getValues('metadata'))\n"
                                        + "}\n"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "AI ContextRetrievalChunker",
                introduction = "Text chunking utility for context retrieval in RAG (Retrieval-Augmented Generation) pipelines.\n\n"
                        + "Splits Markdown documents, plain text or text extracted from PDF into controlled-size chunks "
                        + "with configurable overlap, preserving the document structure for better semantic retrieval quality.\n\n"
                        + "**Key features:**\n"
                        + "- Structural segmentation: headings, paragraphs, code blocks, tables, lists and quotes are recognised and never split arbitrarily\n"
                        + "- Hierarchical context: every block carries the full heading tree (`# Guide > ## Install > ### Windows`)\n"
                        + "- Budget in characters or in real tokens (BPE `cl100k_base` / `o200k_base`)\n"
                        + "- Code blocks larger than the budget are split by lines with the fence reopened in each part\n"
                        + "- Tables larger than the budget repeat the header row in each part\n"
                        + "- Sentence-aware overlap, never in the middle of a word\n"
                        + "- No text loss: every block of the document belongs to exactly one chunk\n"
                        + "- Deterministic: the same input always produces the same chunks and the same `id` values, which makes re-ingestion idempotent",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Basic example\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "const chunks = chunker.markdown(markdownDocument)\n"
                                        + "\n"
                                        + "for (const chunk of chunks.listOfValues()) {\n"
                                        + "    _log.info(`Chunk ${chunk.getInt('index')}: ${chunk.getString('breadcrumb')}`)\n"
                                        + "    _log.info(`Text: ${chunk.getString('text')}`)\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Budget in real tokens and ingestion into the vector store\n"
                                        + "const client = _ai.client()\n"
                                        + "const vector = _ai.vector('default')\n"
                                        + "\n"
                                        + "const blocks = _ai.contextRetrievalChunker()\n"
                                        + "    .unit('tokens')\n"
                                        + "    .chunkSize(320)\n"
                                        + "    .overlap(48)\n"
                                        + "    .source('manual-v1')\n"
                                        + "    .markdown(markdownDocument)\n"
                                        + "\n"
                                        + "for (const block of blocks.listOfValues()) {\n"
                                        + "    const response = client.embeddings('embeddinggemma:latest', block.getString('text'))\n"
                                        + "    const embedding = response.getValues('data').getValues(0).getValues('embedding')\n"
                                        + "    vector.add('netuno', block.getString('id'), embedding, block.getString('text'), block.getValues('metadata'))\n"
                                        + "}\n"
                        )
                }
        )
})
public class ContextRetrievalChunker {

    private static final Logger LOGGER = LogManager.getLogger(ContextRetrievalChunker.class);

    /**
     * Default budget when measuring in characters.
     */
    public static final int DEFAULT_CHUNK_SIZE = 1024;
    /**
     * Default overlap when measuring in characters.
     */
    public static final int DEFAULT_OVERLAP = 128;
    /**
     * Default budget when measuring in tokens, applied only when the chunk size was never set explicitly.
     */
    public static final int DEFAULT_CHUNK_SIZE_TOKENS = 256;
    /**
     * Default overlap when measuring in tokens, applied only when the overlap was never set explicitly.
     */
    public static final int DEFAULT_OVERLAP_TOKENS = 32;

    public static final String UNIT_CHARS = "chars";
    public static final String UNIT_TOKENS = "tokens";

    /**
     * What goes into the `text` field, which is always the string meant to be embedded.
     * `content` is always the real chunk, untouched, and is what a search should give back.
     */
    public static final String EMBED_FULL = "full";
    public static final String EMBED_CONTEXT = "context";
    public static final String EMBED_CONTENT = "content";

    private static final String DEFAULT_ENCODING = "cl100k_base";

    private static final int MIN_CHUNK_SIZE_LIMIT = 32;
    private static final int MAX_CHUNK_SIZE_LIMIT = 200_000;

    /**
     * Blocks are joined by a blank line, so packing has to account for it.
     */
    private static final int BLOCK_SEPARATOR_COST = 2;

    /**
     * The context header may never eat more than this fraction of the budget.
     */
    private static final double MAX_HEADER_RATIO = 0.4d;

    /**
     * Tolerance applied when absorbing a residual chunk into its neighbour.
     */
    private static final double MERGE_TOLERANCE = 1.15d;

    private static final Pattern ZERO_WIDTH = Pattern.compile("[\\u200B-\\u200D\\u2060\\uFEFF]");

    private static final Pattern DATA_URI = Pattern.compile(
            "!\\[([^\\]]*)\\]\\(\\s*data:([A-Za-z0-9.+/-]+)(?:;[^,]*)?,[^)\\s]*\\)"
    );

    private static final Pattern ATX_HEADING = Pattern.compile("^ {0,3}(#{1,6})(?:[ \\t]+(.*?))?[ \\t]*#*[ \\t]*$");

    private static final Pattern LIST_ITEM = Pattern.compile("^ {0,3}(?:[-*+]|\\d{1,9}[.)])(?:[ \\t]+|$)");

    private static final Pattern SETEXT_UNDERLINE = Pattern.compile("^ {0,3}(=+|-+)[ \\t]*$");

    private static final Pattern FENCE = Pattern.compile("^ {0,3}(`{3,}|~{3,})[ \\t]*(.*)$");


    private static final Pattern NUMBERED_SECTION = Pattern.compile(
            "^ {0,3}(\\d{1,2}(?:\\.\\d{1,2}){0,4})\\.?[ \\t]+(\\p{Lu}[^\\n]{0,90})$"
    );

    private static final Pattern PAGE_NUMBER_LINE = Pattern.compile("^[ \\t]*[-–—]?[ \\t]*\\d{1,4}[ \\t]*[-–—]?[ \\t]*$");

    private static final Pattern DIGITS = Pattern.compile("\\d+");


    private static final Pattern SENTENCE_BOUNDARY = Pattern.compile(
            "(?<=[.!?\\u2026][\"'\\u201D\\u2019)\\]]{0,2})"
                    + "(?<!\\b\\p{Lu}\\.)"
                    + "(?<!\\b(?:Sr|Sra|Dr|Dra|Prof|Eng|Exmo|Exma|etc|ex|vs|pág|pag|fig|art|cap|min|max|aprox|approx"
                    + "|Mr|Mrs|Ms|St|Jr|Inc|Ltd|Co|vol|ed|cf|al|no|nr|pp)\\.)"
                    + "[ \\t]+(?=[\\p{Lu}\\p{Nd}\"'\\u201C(\\[])"
    );


    private static final Pattern PDF_HYPHEN_LOWER = Pattern.compile("(\\p{L})-\\n[ \\t]*(\\p{Ll})");
    private static final Pattern PDF_HYPHEN_UPPER = Pattern.compile("(\\p{L})-\\n[ \\t]*(\\p{Lu})");

    private static final String CONTEXTUALIZE_TEMPLATE =
            "<document>\n{document}\n</document>\n\n"
                    + "<fragment breadcrumb=\"{breadcrumb}\" heading=\"{heading}\">\n{chunk}\n</fragment>\n\n"
                    + "Write a retrieval note for the fragment, in two parts separated by a blank line, with no "
                    + "labels and no titles.\n\n"
                    + "First part, two or three sentences that could stand as the opening lines of the fragment: "
                    + "plain factual statements about its subject, supplying what the fragment leaves implicit. "
                    + "State, when the fragment does not already:\n"
                    + "- the product, component, module, service, platform, version, file, endpoint or command it "
                    + "concerns, by name, taken from the document\n"
                    + "- what every dangling pronoun or vague reference points to\n"
                    + "- the task it serves and the problem it solves, in the ordinary words someone would use to "
                    + "ask about it\n"
                    + "- the conditions it holds under: platform, version, mode, role, prerequisite, limit or "
                    + "default value\n\n"
                    + "Second part, a single line of terms separated by commas, most specific first, every one of "
                    + "them present in or directly implied by the document. Include whichever exist:\n"
                    + "- names of products, components, modules, libraries, services, standards, formats and "
                    + "protocols\n"
                    + "- identifiers exactly as written: functions, classes, methods, parameters, configuration "
                    + "keys, environment variables, commands and flags, endpoints, file names and paths, tables "
                    + "and fields, error codes and error messages, status codes\n"
                    + "- acronyms together with what they stand for, both forms\n"
                    + "- other wording for the same thing: synonyms, the everyday word for internal jargon, the "
                    + "words someone would actually type into a search box\n"
                    + "- the numbers that identify this material: versions, limits, defaults, sizes, ports, dates\n"
                    + "- when the document is not in English, the English term for each domain concept, next to "
                    + "the original\n\n"
                    + "Hard rules:\n"
                    + "- Only what the document supports. Never invent a name, a number or a capability, and "
                    + "never add a term the document does not back.\n"
                    + "- Write about the subject matter and nothing else. Never mention the document, its "
                    + "sections, this fragment, the reader, or the act of searching. Openings like \"Esta "
                    + "seccao\", \"Este trecho\", \"O documento\", \"This section\" are forbidden.\n"
                    + "- Never copy a whole sentence from the fragment.\n"
                    + "- Keep identifiers exactly as they appear, case included, and never translate them.\n"
                    + "- Sentences in the language of the document.\n"
                    + "- Plain text: no markdown, no backticks, no bullet points, no headings, no quotes.\n"
                    + "- Three sentences at most, and at most thirty terms.\n\n"
                    + "Answer with the note and nothing else.";

    private static final String CONTEXTUALIZE_SYSTEM =
            "You write retrieval context notes. Your output is never read by a person: it is "
                    + "concatenated to a document fragment and turned into a search embedding, and it is what a "
                    + "keyword search matches against as well. Notes that describe the structure of a document are "
                    + "worthless for that, and so are notes that repeat what the fragment already says. What makes "
                    + "a fragment findable is naming the entities it leaves implicit, spelling out the identifiers "
                    + "and acronyms it takes for granted, and covering the vocabulary a real query arrives in: the "
                    + "words of the user, the words of the manual, and the exact strings of the code.";

    private int chunkSize = DEFAULT_CHUNK_SIZE;
    private int overlap = DEFAULT_OVERLAP;
    private int minChunkSize = -1;
    private boolean chunkSizeExplicit = false;
    private boolean overlapExplicit = false;
    private String unit = UNIT_CHARS;
    private String embed = EMBED_FULL;
    private String encoding = DEFAULT_ENCODING;
    private boolean prependHeading = true;
    private boolean headingPath = true;
    private boolean splitOnHeadings = true;
    private boolean stripDataUri = true;
    private boolean stripHtmlComments = true;
    private String source = "";
    private Values metadata = null;

    // ------------------------------------------------------------------
    // Configuration
    // ------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o tamanho máximo de cada bloco, na unidade escolhida em `unit`. "
                            + "Valor predefinido: " + DEFAULT_CHUNK_SIZE + " caracteres, ou " + DEFAULT_CHUNK_SIZE_TOKENS
                            + " quando a unidade é `tokens` e o tamanho não foi definido explicitamente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunkSize(1500).markdown(documento)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the maximum size of each chunk, in the unit chosen with `unit`. "
                            + "Default value: " + DEFAULT_CHUNK_SIZE + " characters, or " + DEFAULT_CHUNK_SIZE_TOKENS
                            + " when the unit is `tokens` and the size was not set explicitly.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunkSize(1500).markdown(document)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "chunkSize", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "tamanhoDoBloco",
                            description = "Tamanho máximo de cada bloco, limitado a [" + MIN_CHUNK_SIZE_LIMIT + ", " + MAX_CHUNK_SIZE_LIMIT + "]."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum size of each chunk, clamped to [" + MIN_CHUNK_SIZE_LIMIT + ", " + MAX_CHUNK_SIZE_LIMIT + "]."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker chunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        this.chunkSizeExplicit = true;
        return this;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a sobreposição entre blocos consecutivos, na unidade escolhida em `unit`. "
                            + "A sobreposição é construída a partir das últimas frases do bloco anterior, nunca a meio de uma palavra, "
                            + "e é limitada a metade do tamanho do bloco. Valor predefinido: " + DEFAULT_OVERLAP + " caracteres.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunkSize(1500).overlap(200).markdown(documento)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the overlap between consecutive chunks, in the unit chosen with `unit`. "
                            + "The overlap is built from the trailing sentences of the previous chunk, never mid-word, "
                            + "and is clamped to half the chunk size. Default value: " + DEFAULT_OVERLAP + " characters.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunkSize(1500).overlap(200).markdown(document)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "overlap", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "sobreposicao",
                            description = "Sobreposição entre blocos consecutivos, limitada a metade do tamanho do bloco."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Overlap between consecutive chunks, clamped to half the chunk size."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker overlap(int overlap) {
        this.overlap = overlap;
        this.overlapExplicit = true;
        return this;
    }

    public int getOverlap() {
        return overlap;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o tamanho mínimo de um bloco. Blocos abaixo deste valor são absorvidos por um vizinho, "
                            + "para evitar micro-blocos órfãos que poluem o vector store. Valor predefinido: um quarto do tamanho do bloco.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the minimum size of a chunk. Chunks below this value are absorbed by a neighbour, "
                            + "to avoid orphan micro-chunks that pollute the vector store. Default value: a quarter of the chunk size.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "minChunkSize", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "tamanhoMinimo", description = "Tamanho mínimo de um bloco."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Minimum size of a chunk.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker minChunkSize(int minChunkSize) {
        this.minChunkSize = minChunkSize;
        return this;
    }

    public int getMinChunkSize() {
        return minChunkSize;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a unidade de medida do orçamento: `chars` para caracteres ou `tokens` para tokens reais, "
                            + "contados com o mesmo algoritmo BPE dos modelos. Medir em tokens é o que garante que nenhum bloco "
                            + "ultrapassa o limite do modelo de embeddings.\n\n"
                            + "Quando se passa para `tokens` sem ter definido `chunkSize` e `overlap` explicitamente, os valores "
                            + "predefinidos passam a " + DEFAULT_CHUNK_SIZE_TOKENS + " e " + DEFAULT_OVERLAP_TOKENS + " tokens.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.unit('tokens').chunkSize(320).markdown(documento)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the budget unit: `chars` for characters or `tokens` for real tokens, "
                            + "counted with the same BPE algorithm the models use. Measuring in tokens is what guarantees no chunk "
                            + "goes over the embedding model limit.\n\n"
                            + "When switching to `tokens` without having set `chunkSize` and `overlap` explicitly, the defaults "
                            + "become " + DEFAULT_CHUNK_SIZE_TOKENS + " and " + DEFAULT_OVERLAP_TOKENS + " tokens.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.unit('tokens').chunkSize(320).markdown(document)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "unit", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "unidade", description = "`chars` ou `tokens`."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "`chars` or `tokens`.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker unit(String unit) {
        this.unit = UNIT_TOKENS.equalsIgnoreCase(unit) ? UNIT_TOKENS : UNIT_CHARS;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o que entra no campo `text`, que é sempre a cadeia destinada a ser embebida. "
                            + "O campo `content` mantém sempre o bloco real e é o que uma pesquisa deve devolver.\n\n"
                            + "- `full`, predefinido: cabeçalho de contexto, contexto gerado e corpo do bloco\n"
                            + "- `context`: apenas cabeçalho e contexto gerado, deixando o corpo de fora\n"
                            + "- `content`: apenas o corpo, sem cabeçalho nem contexto\n\n"
                            + "O modo `context` indexa a nota gerada em vez do bloco. A nota é prosa densa, enquanto o "
                            + "corpo traz blocos de código, canos de tabelas e marcação que diluem o vetor. Em troca, "
                            + "um termo exato que exista no corpo e não na nota deixa de ser pesquisável, por isso este "
                            + "modo só faz sentido depois de correr `contextualize`. Enquanto o contexto estiver vazio, "
                            + "o corpo é usado na mesma, para não se indexar um cabeçalho sozinho.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Indexar o contexto, devolver o texto real\n"
                                            + "const chunks = chunker.embed('context').markdown(documento)\n"
                                            + "chunker.contextualize(client, documento, chunks)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    const resposta = client.embeddings(modelo, chunk.getString('text'))\n"
                                            + "    const embedding = resposta.getValues('data').getValues(0).getValues('embedding')\n"
                                            + "    vector.add('docs', chunk.getString('id'), embedding, chunk.getString('content'), metadados)\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets what goes into the `text` field, which is always the string meant to be embedded. "
                            + "The `content` field always keeps the real chunk and is what a search should return.\n\n"
                            + "- `full`, the default: context header, generated context and chunk body\n"
                            + "- `context`: header and generated context only, leaving the body out\n"
                            + "- `content`: body only, with no header and no context\n\n"
                            + "The `context` mode indexes the generated note instead of the chunk. The note is dense "
                            + "prose, while the body carries code blocks, table pipes and markup that dilute the vector. "
                            + "In exchange, an exact term that exists in the body but not in the note stops being "
                            + "searchable, so this mode only makes sense after running `contextualize`. While the "
                            + "context is still empty the body is used anyway, so a heading is never indexed on its own.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Index the context, return the real text\n"
                                            + "const chunks = chunker.embed('context').markdown(document)\n"
                                            + "chunker.contextualize(client, document, chunks)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    const response = client.embeddings(model, chunk.getString('text'))\n"
                                            + "    const embedding = response.getValues('data').getValues(0).getValues('embedding')\n"
                                            + "    vector.add('docs', chunk.getString('id'), embedding, chunk.getString('content'), metadata)\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "embed", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "embeber", description = "`full`, `context` ou `content`."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "`full`, `context` or `content`.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker embed(String embed) {
        if (EMBED_CONTEXT.equalsIgnoreCase(embed)) {
            this.embed = EMBED_CONTEXT;
        } else if (EMBED_CONTENT.equalsIgnoreCase(embed)) {
            this.embed = EMBED_CONTENT;
        } else {
            this.embed = EMBED_FULL;
        }
        return this;
    }

    public String getEmbed() {
        return embed;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a codificação usada na contagem de tokens: `cl100k_base` (predefinida), `o200k_base`, "
                            + "`p50k_base`, `r50k_base`, ou o nome de um modelo OpenAI, de onde a codificação é deduzida.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the encoding used for token counting: `cl100k_base` (default), `o200k_base`, "
                            + "`p50k_base`, `r50k_base`, or the name of an OpenAI model, from which the encoding is inferred.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "codificacao", description = "Nome da codificação ou do modelo."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Encoding or model name.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker encoding(String encoding) {
        this.encoding = encoding == null || encoding.isBlank() ? DEFAULT_ENCODING : encoding.trim();
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se o cabeçalho de contexto é prefixado ao campo `text` de cada bloco. Ativo por omissão. "
                            + "O campo `content` mantém sempre o corpo do bloco sem o cabeçalho.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets whether the context header is prepended to the `text` field of each chunk. Enabled by default. "
                            + "The `content` field always keeps the chunk body without the header.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "prependHeading", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "prefixarCabecalho", description = "Prefixar ou não o cabeçalho de contexto."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Whether to prepend the context header.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker prependHeading(boolean prependHeading) {
        this.prependHeading = prependHeading;
        return this;
    }

    public boolean isPrependHeading() {
        return prependHeading;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se o cabeçalho de contexto usa a árvore completa de cabeçalhos, ativo por omissão, "
                            + "ou apenas o cabeçalho mais próximo. A árvore completa dá muito melhor recuperação em documentos "
                            + "com secções aninhadas, porque um bloco sob `### Windows` mantém também `# Guia` e `## Instalação`.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets whether the context header uses the full heading tree, enabled by default, "
                            + "or only the nearest heading. The full tree gives much better retrieval on documents "
                            + "with nested sections, because a chunk under `### Windows` also keeps `# Guide` and `## Install`.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "headingPath", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "arvoreDeCabecalhos", description = "Usar a árvore completa ou apenas o cabeçalho mais próximo."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Use the full tree or only the nearest heading.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker headingPath(boolean headingPath) {
        this.headingPath = headingPath;
        return this;
    }

    public boolean isHeadingPath() {
        return headingPath;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se um novo bloco começa preferencialmente num cabeçalho, ativo por omissão. "
                            + "É isto que alinha os blocos com as secções do documento.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets whether a new chunk preferably starts at a heading, enabled by default. "
                            + "This is what aligns chunks with the document sections.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "splitOnHeadings", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "cortarNosCabecalhos", description = "Cortar ou não preferencialmente nos cabeçalhos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Whether to preferably break at headings.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker splitOnHeadings(boolean splitOnHeadings) {
        this.splitOnHeadings = splitOnHeadings;
        return this;
    }

    public boolean isSplitOnHeadings() {
        return splitOnHeadings;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se as imagens embebidas em `data:` são reduzidas ao tipo de conteúdo, ativo por omissão. "
                            + "Um único PNG em base64 pode ocupar centenas de milhares de caracteres sem qualquer valor semântico.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets whether images embedded as `data:` URIs are reduced to their content type, enabled by default. "
                            + "A single base64 PNG can take hundreds of thousands of characters with no semantic value at all.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "stripDataUri", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "removerDataUri", description = "Reduzir ou não as imagens embebidas."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Whether to reduce embedded images.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker stripDataUri(boolean stripDataUri) {
        this.stripDataUri = stripDataUri;
        return this;
    }

    public boolean isStripDataUri() {
        return stripDataUri;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se os comentários HTML são removidos do Markdown, ativo por omissão. "
                            + "Comentários dentro de blocos de código são sempre preservados.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets whether HTML comments are removed from the Markdown, enabled by default. "
                            + "Comments inside code blocks are always preserved.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "stripHtmlComments", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "removerComentarios", description = "Remover ou não os comentários HTML."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Whether to remove HTML comments.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker stripHtmlComments(boolean stripHtmlComments) {
        this.stripHtmlComments = stripHtmlComments;
        return this;
    }

    public boolean isStripHtmlComments() {
        return stripHtmlComments;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o identificador da origem do documento, usado para construir o `id` de cada bloco. "
                            + "Com a mesma origem e o mesmo conteúdo os `id` são sempre iguais, o que permite reindexar um documento "
                            + "sem duplicar registos no vector store.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.source('manual-v1').markdown(documento)\n"
                                            + "// id -> manual-v1#0-3f2a1c9d"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the document source identifier, used to build the `id` of each chunk. "
                            + "With the same source and the same content the `id` values are always the same, which allows reindexing "
                            + "a document without duplicating records in the vector store.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.source('manual-v1').markdown(document)\n"
                                            + "// id -> manual-v1#0-3f2a1c9d"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "source", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "origem", description = "Identificador da origem do documento."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Document source identifier.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker source(String source) {
        this.source = source == null ? "" : source.trim();
        return this;
    }

    public String getSource() {
        return source;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define metadados aplicados a todos os blocos, copiados para o campo `metadata` e prontos a passar "
                            + "diretamente a `vector.add`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker\n"
                                            + "    .metadata(_val.map().set('origem', 'manual').set('versao', 3))\n"
                                            + "    .markdown(documento)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets metadata applied to every chunk, copied into the `metadata` field and ready to pass "
                            + "straight to `vector.add`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker\n"
                                            + "    .metadata(_val.map().set('source', 'manual').set('version', 3))\n"
                                            + "    .markdown(document)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "metadata", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "metadados", description = "Metadados aplicados a todos os blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Metadata applied to every chunk.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A própria instância, para encadear configurações."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The instance itself, to chain configuration calls.")
    })
    public ContextRetrievalChunker metadata(Values metadata) {
        this.metadata = metadata;
        return this;
    }

    public Values getMetadata() {
        return metadata;
    }

    // ------------------------------------------------------------------
    // Chunking
    // ------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento em blocos, detetando automaticamente se o conteúdo é Markdown ou texto corrido. "
                            + "É o ponto de entrada a usar quando a origem do conteúdo não é conhecida à partida.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunk(conteudo)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a document into chunks, automatically detecting whether the content is Markdown or running text. "
                            + "This is the entry point to use when the content origin is not known upfront.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunk(content)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "conteudo", description = "Texto a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text to split into chunks.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values chunk(String content) {
        return chunk(content, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento em blocos com opções, detetando automaticamente se o conteúdo é Markdown ou texto corrido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunk(conteudo, _val.map()\n"
                                            + "    .set('unit', 'tokens')\n"
                                            + "    .set('chunkSize', 320)\n"
                                            + "    .set('overlap', 48))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a document into chunks with options, automatically detecting whether the content is Markdown or running text.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.chunk(content, _val.map()\n"
                                            + "    .set('unit', 'tokens')\n"
                                            + "    .set('chunkSize', 320)\n"
                                            + "    .set('overlap', 48))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "conteudo", description = "Texto a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text to split into chunks.")
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "opcoes", description = "Opções que sobrepõem a configuração da instância. Ver a lista completa em `markdown`."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Options overriding the instance configuration. See the full list in `markdown`.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values chunk(String content, Values options) {
        return process(content, looksLikeMarkdown(content) ? Kind.MARKDOWN : Kind.TEXT, options);
    }

    public Values chunk(String content, int chunkSize, int overlap) {
        return chunk(content, sizeOptions(chunkSize, overlap));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento Markdown em blocos, usando a configuração da instância.\n\n"
                            + "O documento é primeiro segmentado em blocos atómicos, respeitando a marcação: cabeçalhos, parágrafos, "
                            + "blocos de código, tabelas, listas e citações. Um `# comentário` dentro de um bloco de código nunca é "
                            + "confundido com um cabeçalho, porque a deteção é feita com estado de marcação. Os blocos são depois "
                            + "agrupados até ao orçamento, preferindo começar num cabeçalho.\n\n"
                            + "Cada bloco recebe a árvore de cabeçalhos em vigor, prefixada ao campo `text`, o que melhora "
                            + "substancialmente a recuperação semântica em documentos com secções aninhadas.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown('# Título\\n\\nConteúdo do documento...')\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('breadcrumb') +' -> '+ chunk.getInt('tokens') +' tokens')\n"
                                            + "    _log.info(chunk.getString('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a Markdown document into chunks, using the instance configuration.\n\n"
                            + "The document is first segmented into atomic blocks, respecting the markup: headings, paragraphs, "
                            + "code blocks, tables, lists and quotes. A `# comment` inside a code block is never mistaken for a "
                            + "heading, because detection is done with markup state. The blocks are then packed up to the budget, "
                            + "preferring to start at a heading.\n\n"
                            + "Every chunk carries the heading tree in effect, prepended to the `text` field, which substantially "
                            + "improves semantic retrieval on documents with nested sections.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown('# Title\\n\\nDocument content...')\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('breadcrumb') +' -> '+ chunk.getInt('tokens') +' tokens')\n"
                                            + "    _log.info(chunk.getString('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "markdown", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, description = "Texto em formato Markdown a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text in Markdown format to split into chunks.")
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de blocos, cada um com os campos: `id` (identificador estável, próprio para reindexação idempotente), "
                            + "`hash` (resumo do conteúdo), `index` e `total` (posição e total), `start` e `end` (posições no texto normalizado), "
                            + "`length` e `tokens` (tamanho em caracteres e em tokens), `heading` e `headingLevel` (cabeçalho mais próximo e nível), "
                            + "`path` (lista da árvore de cabeçalhos), `breadcrumb` (a mesma árvore em texto), `sections` (todas as secções que o bloco toca), `header` (cabeçalho de contexto já renderizado), `content` (corpo do bloco), "
                            + "`context` (nota de recuperação, frases de situação mais termos, preenchida por `contextualize`), `text` (a cadeia a embeber, composta segundo `embed`), `embed` (modo usado), "
                            + "`type` (`markdown`, `text` ou `pdf`), `blocks` (tipos de bloco presentes), `overlap` (caracteres repetidos do bloco anterior), "
                            + "`page` (página em que o bloco começa) e `pages` (todas as páginas que o bloco atravessa), ambos apenas em PDF paginado, `metadata` (metadados configurados) e `synthetic` "
                            + "(verdadeiro quando o corpo não é uma fatia literal da origem, por reabertura de marcação de código ou repetição de cabeçalho de tabela)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of chunks, each with the fields: `id` (stable identifier, suitable for idempotent reindexing), "
                            + "`hash` (content digest), `index` and `total` (position and total), `start` and `end` (positions in the normalized text), "
                            + "`length` and `tokens` (size in characters and in tokens), `heading` and `headingLevel` (nearest heading and level), "
                            + "`path` (heading tree as a list), `breadcrumb` (the same tree as text), `sections` (every section the chunk touches), `header` (the rendered context header), `content` (chunk body), "
                            + "`context` (retrieval note, situating sentences plus terms, filled in by `contextualize`), `text` (the string to embed, composed according to `embed`), `embed` (the mode used), "
                            + "`type` (`markdown`, `text` or `pdf`), `blocks` (block types present), `overlap` (characters repeated from the previous chunk), "
                            + "`page` (the page the chunk starts on) and `pages` (every page the chunk spans), both only on paginated PDF, `metadata` (configured metadata) and `synthetic` "
                            + "(true when the body is not a literal slice of the source, because a code fence was reopened or a table header repeated)."
            )
    })
    public Values markdown(String markdown) {
        return process(markdown, Kind.MARKDOWN, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento Markdown em blocos com tamanho e sobreposição explícitos, na unidade configurada em `unit`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Blocos de 1500 caracteres com sobreposição de 200\n"
                                            + "const chunks = chunker.markdown(markdown, 1500, 200)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a Markdown document into chunks with explicit size and overlap, in the unit configured with `unit`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Chunks of 1500 characters with overlap of 200\n"
                                            + "const chunks = chunker.markdown(markdown, 1500, 200)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "markdown", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, description = "Texto em formato Markdown a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text in Markdown format to split into chunks.")
            }),
            @ParameterDoc(name = "chunkSize", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "tamanhoDoBloco", description = "Tamanho máximo de cada bloco."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Maximum size of each chunk.")
            }),
            @ParameterDoc(name = "overlap", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "sobreposicao", description = "Sobreposição entre blocos consecutivos, limitada a metade do tamanho do bloco."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Overlap between consecutive chunks, clamped to half the chunk size.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values markdown(String markdown, int chunkSize, int overlap) {
        return process(markdown, Kind.MARKDOWN, sizeOptions(chunkSize, overlap));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide um documento Markdown em blocos com opções, que sobrepõem a configuração da instância apenas nesta chamada.\n\n"
                            + "Opções aceites: `chunkSize`, `overlap`, `minChunkSize`, `unit` (`chars` ou `tokens`), `embed` (`full`, `context` ou `content`), `encoding`, "
                            + "`prependHeading`, `headingPath`, `splitOnHeadings`, `stripDataUri`, `stripHtmlComments`, `source` e `metadata`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown(markdown, _val.map()\n"
                                            + "    .set('unit', 'tokens')\n"
                                            + "    .set('chunkSize', 320)\n"
                                            + "    .set('overlap', 48)\n"
                                            + "    .set('source', 'manual-v1')\n"
                                            + "    .set('metadata', _val.map().set('idioma', 'pt')))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits a Markdown document into chunks with options, overriding the instance configuration for this call only.\n\n"
                            + "Accepted options: `chunkSize`, `overlap`, `minChunkSize`, `unit` (`chars` or `tokens`), `embed` (`full`, `context` or `content`), `encoding`, "
                            + "`prependHeading`, `headingPath`, `splitOnHeadings`, `stripDataUri`, `stripHtmlComments`, `source` and `metadata`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.markdown(markdown, _val.map()\n"
                                            + "    .set('unit', 'tokens')\n"
                                            + "    .set('chunkSize', 320)\n"
                                            + "    .set('overlap', 48)\n"
                                            + "    .set('source', 'manual-v1')\n"
                                            + "    .set('metadata', _val.map().set('language', 'en')))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "markdown", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, description = "Texto em formato Markdown a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text in Markdown format to split into chunks.")
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "opcoes", description = "Opções que sobrepõem a configuração da instância."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Options overriding the instance configuration.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values markdown(String markdown, Values options) {
        return process(markdown, Kind.MARKDOWN, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide texto corrido em blocos, agrupando parágrafos inteiros e cortando por frases apenas quando "
                            + "um parágrafo ultrapassa o orçamento. Títulos numerados, no formato `3.1 Instalação`, são reconhecidos "
                            + "como cabeçalhos e alimentam a árvore de contexto.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.text(textoSimples)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits running text into chunks, packing whole paragraphs and cutting by sentences only when "
                            + "a paragraph goes over the budget. Numbered titles, in the `3.1 Install` shape, are recognised "
                            + "as headings and feed the context tree.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const chunks = chunker.text(plainText)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "texto", description = "Texto corrido a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Running text to split into chunks.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values text(String text) {
        return process(text, Kind.TEXT, null);
    }

    public Values text(String text, int chunkSize, int overlap) {
        return process(text, Kind.TEXT, sizeOptions(chunkSize, overlap));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide texto corrido em blocos com opções. Ver a lista de opções aceites em `markdown`.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits running text into chunks with options. See the accepted options in `markdown`.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "texto", description = "Texto corrido a dividir em blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Running text to split into chunks.")
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "opcoes", description = "Opções que sobrepõem a configuração da instância."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Options overriding the instance configuration.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values text(String text, Values options) {
        return process(text, Kind.TEXT, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide texto extraído de um PDF em blocos, aplicando antes a limpeza específica desta origem: "
                            + "junção de palavras cortadas por hífen no fim da linha, remoção de cabeçalhos e rodapés repetidos entre "
                            + "páginas, e remoção de linhas que são apenas o número da página. Quando o texto traz separadores de "
                            + "página, cada bloco recebe também o campo `page`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const texto = _pdf.toText(_storage.filesystem('server', 'docs', 'manual.pdf'))\n"
                                            + "const chunks = chunker.source('manual.pdf').pdf(texto)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info('Página '+ chunk.getInt('page') +': '+ chunk.getString('content'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits text extracted from a PDF into chunks, first applying the cleanup specific to this source: "
                            + "joining words hyphenated at the end of a line, removing headers and footers repeated across pages, "
                            + "and removing lines that are just the page number. When the text carries page separators, each chunk "
                            + "also gets the `page` field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const text = _pdf.toText(_storage.filesystem('server', 'docs', 'manual.pdf'))\n"
                                            + "const chunks = chunker.source('manual.pdf').pdf(text)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info('Page '+ chunk.getInt('page') +': '+ chunk.getString('content'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "pdfText", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "textoDoPdf", description = "Texto extraído de um PDF."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text extracted from a PDF.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values pdf(String pdfText) {
        return process(pdfText, Kind.PDF, null);
    }

    public Values pdf(String pdfText, int chunkSize, int overlap) {
        return process(pdfText, Kind.PDF, sizeOptions(chunkSize, overlap));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Divide texto extraído de um PDF em blocos com opções. Ver a lista de opções aceites em `markdown`.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Splits text extracted from a PDF into chunks with options. See the accepted options in `markdown`.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "pdfText", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "textoDoPdf", description = "Texto extraído de um PDF."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text extracted from a PDF.")
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "opcoes", description = "Opções que sobrepõem a configuração da instância."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Options overriding the instance configuration.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Lista de blocos. Ver a descrição dos campos em `markdown`."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "List of chunks. See the field descriptions in `markdown`.")
    })
    public Values pdf(String pdfText, Values options) {
        return process(pdfText, Kind.PDF, options);
    }

    // ------------------------------------------------------------------
    // Token counting
    // ------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta os tokens de um texto com a codificação configurada em `encoding`, usando o mesmo algoritmo BPE "
                            + "dos modelos. Útil para orçamentar prompts e para verificar que um bloco cabe no limite do modelo de embeddings.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_log.info('Tokens: '+ chunker.countTokens(texto))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the tokens of a text with the encoding configured in `encoding`, using the same BPE algorithm "
                            + "the models use. Useful to budget prompts and to check that a chunk fits the embedding model limit.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_log.info('Tokens: '+ chunker.countTokens(text))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "texto", description = "Texto a medir."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text to measure.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Número de tokens do texto."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "Number of tokens in the text.")
    })
    public int countTokens(String text) {
        return countTokens(text, encoding);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta os tokens de um texto com uma codificação específica, ou com a codificação deduzida do nome de um modelo.",
                    howToUse = {}
                ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the tokens of a text with a specific encoding, or with the encoding inferred from a model name.",
                    howToUse = {}
                )
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "texto", description = "Texto a medir."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Text to measure.")
            }),
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "codificacao", description = "Nome da codificação, como `cl100k_base` ou `o200k_base`, ou nome de um modelo."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Encoding name, such as `cl100k_base` or `o200k_base`, or a model name.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "Número de tokens do texto."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "Number of tokens in the text.")
    })
    public int countTokens(String text, String encoding) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        Encoding tokenizer = Tokenizers.get(encoding);
        if (tokenizer == null) {
            Tokenizers.warnOnce();
            return 0;
        }
        return tokenizer.countTokensOrdinary(text);
    }

    // ------------------------------------------------------------------
    // Contextual retrieval
    // ------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Preenche o campo `context` de cada bloco com uma nota de recuperação, gerada pelo modelo a partir do "
                            + "documento completo, e reconstrói o campo `text` com essa nota incluída. É a técnica de *contextual "
                            + "retrieval*: um bloco que diz apenas \"o valor subiu 3%\" passa a dizer também de que empresa e de que "
                            + "trimestre se trata, o que reduz muito as falhas de recuperação.\n\n"
                            + "A nota tem duas partes, porque uma pesquisa chega em duas formas. Duas ou três frases situam o bloco "
                            + "nomeando o produto, a tarefa e as condições que o bloco dá como garantidas, para responder a quem "
                            + "descreve o problema por palavras suas. A seguir, uma linha de termos reúne o vocabulário exato: nomes, "
                            + "identificadores, chaves de configuração, comandos, códigos de erro, siglas com o respetivo significado, "
                            + "sinónimos e, em documentos que não estejam em inglês, o termo inglês ao lado do original. É isto que "
                            + "encontra quem cola uma mensagem de erro ou o nome de um parâmetro.\n\n"
                            + "É uma operação paga: faz uma chamada ao modelo por bloco. Correr apenas na fase de ingestão, nunca por pedido.\n\n"
                            + "As chamadas são sequenciais de propósito, porque o `Client` mantém estado de sessão e de contabilização "
                            + "de tokens que não é seguro partilhar entre chamadas em paralelo. Um erro num bloco não interrompe os "
                            + "restantes: fica registado no log, o `context` desse bloco fica vazio e o processamento continua.\n\n"
                            + "Opções aceites: `model`, `temperature` (0 por omissão, para o resultado ser reproduzível), "
                            + "`documentMaxChars` (trunca documentos muito grandes), `template` (marcadores `{document}`, `{chunk}`, `{breadcrumb}` e `{heading}`), `system` (mensagem de sistema), "
                            + "`skipIfPresent` (não repete blocos que já tenham contexto) e `failFast`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const client = _ai.client()\n"
                                            + "const chunks = chunker.markdown(documento)\n"
                                            + "\n"
                                            + "chunker.contextualize(client, documento, chunks)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('context'))\n"
                                            + "    // text já inclui o contexto, é o que se deve embeber\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Fills the `context` field of each chunk with a retrieval note, generated by the model from the whole "
                            + "document, and rebuilds the `text` field with that note included. This is the "
                            + "*contextual retrieval* technique: a chunk that only says \"the value went up 3%\" also comes to say "
                            + "which company and which quarter it is about, which greatly reduces retrieval misses.\n\n"
                            + "The note has two parts, because a query arrives in two shapes. Two or three sentences situate the chunk "
                            + "by naming the product, the task and the conditions it takes for granted, which answers whoever describes "
                            + "the problem in their own words. Then a line of terms gathers the exact vocabulary: names, identifiers, "
                            + "configuration keys, commands, error codes, acronyms with what they stand for, synonyms and, on documents "
                            + "not written in English, the English term next to the original. That is what finds whoever pastes an error "
                            + "message or the name of a parameter.\n\n"
                            + "It is a paid operation: it makes one model call per chunk. Run it during ingestion only, never per request.\n\n"
                            + "The calls are sequential on purpose, because `Client` keeps session and token accounting state that is "
                            + "not safe to share across parallel calls. An error on one chunk does not stop the rest: it is logged, "
                            + "that chunk's `context` stays empty and processing continues.\n\n"
                            + "Accepted options: `model`, `temperature` (0 by default, so the result is reproducible), "
                            + "`documentMaxChars` (truncates very large documents), `template` (placeholders `{document}`, `{chunk}`, `{breadcrumb}` and `{heading}`), `system` (system message), "
                            + "`skipIfPresent` (does not redo chunks that already have context) and `failFast`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const client = _ai.client()\n"
                                            + "const chunks = chunker.markdown(document)\n"
                                            + "\n"
                                            + "chunker.contextualize(client, document, chunks)\n"
                                            + "\n"
                                            + "for (const chunk of chunks.listOfValues()) {\n"
                                            + "    _log.info(chunk.getString('context'))\n"
                                            + "    // text already includes the context, it is what should be embedded\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "client", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "cliente", description = "Cliente de IA usado para gerar o contexto."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "AI client used to generate the context.")
            }),
            @ParameterDoc(name = "document", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "documento", description = "Documento completo, o mesmo que originou os blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "The whole document, the same one the chunks came from.")
            }),
            @ParameterDoc(name = "chunks", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "blocos", description = "Lista de blocos devolvida por `markdown`, `text`, `pdf` ou `chunk`."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "List of chunks returned by `markdown`, `text`, `pdf` or `chunk`.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A mesma lista de blocos, com `context` e `text` atualizados."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The same list of chunks, with `context` and `text` updated.")
    })
    public Values contextualize(Client client, String document, Values chunks) {
        return contextualize(client, document, chunks, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Preenche o campo `context` de cada bloco com opções. Ver a descrição completa e a lista de opções em `contextualize`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "chunker.contextualize(client, documento, chunks, _val.map()\n"
                                            + "    .set('model', 'gpt-4o-mini')\n"
                                            + "    .set('documentMaxChars', 40000))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Fills the `context` field of each chunk with options. See the full description and option list in `contextualize`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "chunker.contextualize(client, document, chunks, _val.map()\n"
                                            + "    .set('model', 'gpt-4o-mini')\n"
                                            + "    .set('documentMaxChars', 40000))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "client", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "cliente", description = "Cliente de IA usado para gerar o contexto."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "AI client used to generate the context.")
            }),
            @ParameterDoc(name = "document", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "documento", description = "Documento completo, o mesmo que originou os blocos."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "The whole document, the same one the chunks came from.")
            }),
            @ParameterDoc(name = "chunks", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "blocos", description = "Lista de blocos devolvida por `markdown`, `text`, `pdf` ou `chunk`."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "List of chunks returned by `markdown`, `text`, `pdf` or `chunk`.")
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(language = LanguageDoc.PT, name = "opcoes", description = "Opções da geração de contexto."),
                    @ParameterTranslationDoc(language = LanguageDoc.EN, description = "Context generation options.")
            })
    }, returns = {
            @ReturnTranslationDoc(language = LanguageDoc.PT, description = "A mesma lista de blocos, com `context` e `text` atualizados."),
            @ReturnTranslationDoc(language = LanguageDoc.EN, description = "The same list of chunks, with `context` and `text` updated.")
    })
    public Values contextualize(Client client, String document, Values chunks, Values options) {
        if (chunks == null || chunks.isEmpty()) {
            return chunks;
        }
        if (client == null || !client.isInitialized()) {
            LOGGER.warn("Contextualize skipped: the AI client is not initialized.");
            return chunks;
        }

        Values opts = options == null ? Values.newMap() : options;

        String model = opts.getString("model", "");
        double temperature = opts.getDouble("temperature", 0d);
        int documentMaxChars = Math.max(0, opts.getInt("documentMaxChars", 60_000));
        String template = opts.getString("template", CONTEXTUALIZE_TEMPLATE);
        String system = opts.getString("system", CONTEXTUALIZE_SYSTEM);
        boolean skipIfPresent = opts.getBoolean("skipIfPresent", true);
        boolean failFast = opts.getBoolean("failFast", false);

        String reference = document == null ? "" : document;
        if (documentMaxChars > 0 && reference.length() > documentMaxChars) {
            reference = reference.substring(0, documentMaxChars);
        }

        Values chatOptions = Values.newMap().set("temperature", temperature);

        // A provider that answers nothing on the very first chunk is misconfigured, not
        // having a bad day, and every remaining chunk would fail the same way. Stopping
        // with one explanation beats a log full of identical warnings and a bill for calls
        // that were never going to work.
        boolean firstChunk = true;

        // Indexed access on purpose: listOfValues() rebuilds every element with
        // new Values(Map), which copies it, so writing to what it hands back updates a
        // throwaway object. getValues(int) returns the stored instance untouched.
        for (int index = 0; index < chunks.size(); index++) {
            Values chunk = chunks.getValues(index);
            if (chunk == null) {
                continue;
            }
            if (skipIfPresent && !chunk.getString("context", "").isBlank()) {
                continue;
            }

            String body = chunk.getString("content", "");
            if (body.isBlank()) {
                continue;
            }

            String prompt = template
                    .replace("{document}", reference)
                    .replace("{chunk}", body)
                    .replace("{breadcrumb}", chunk.getString("breadcrumb", ""))
                    .replace("{heading}", chunk.getString("heading", ""));

            try {
                Values messages = Values.newList();
                if (!system.isBlank()) {
                    messages.add(Values.newMap().set("role", "system").set("content", system));
                }
                messages.add(Values.newMap().set("role", "user").set("content", prompt));

                Values response = model.isBlank()
                        ? client.chat(messages, chatOptions)
                        : client.chat(model, messages, chatOptions);

                String context = firstChoiceContent(response);
                if (context == null || context.isBlank()) {
                    if (firstChunk) {
                        LOGGER.error("Contextualize stopped: the provider returned no content for the first chunk. "
                                + "The most common cause is no model being set, which the client logs as "
                                + "\"Model cannot be null or empty\": pass the 'model' option, call client.model(...), "
                                + "or set a model in the provider configuration.");
                        return chunks;
                    }
                    LOGGER.warn("Contextualize returned no content for chunk {}.", chunk.getString("id", ""));
                    continue;
                }

                chunk.set("context", context.strip());
                chunk.set("text", composeText(
                        chunk.getString("header", ""),
                        context.strip(),
                        body,
                        // The chunk remembers how it was built, so contextualize rebuilds the
                        // embedding text the same way even on a different chunker instance.
                        opts.getString("embed", chunk.getString("embed", embed))
                ));
                firstChunk = false;
            } catch (Exception e) {
                if (failFast) {
                    throw e;
                }
                if (firstChunk) {
                    LOGGER.error("Contextualize stopped: the provider call failed on the first chunk: {}", e.getMessage());
                    return chunks;
                }
                LOGGER.warn("Contextualize failed for chunk {}: {}", chunk.getString("id", ""), e.getMessage());
            }
        }

        return chunks;
    }

    private String firstChoiceContent(Values response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        Values choices = response.getValues("choices");
        if (choices == null || choices.isEmpty()) {
            return null;
        }
        Values message = choices.getValues(0) == null ? null : choices.getValues(0).getValues("message");
        return message == null ? null : message.getString("content");
    }

    // ==================================================================
    // Internals
    // ==================================================================

    private enum Kind {
        MARKDOWN("markdown"),
        TEXT("text"),
        PDF("pdf");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        String label() {
            return label;
        }
    }

    private enum BlockType {
        FRONT_MATTER, HEADING, PARAGRAPH, CODE, TABLE, LIST, QUOTE, RULE, HTML;

        String label() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * An atomic unit of the document. Blocks are only ever split when a single one
     * does not fit the budget, and the packer never breaks inside one.
     */
    private static final class Block {
        final BlockType type;
        final int start;
        final int end;
        final String text;
        final List<String> path;
        final boolean synthetic;
        int size = -1;

        Block(BlockType type, int start, int end, String text, List<String> path, boolean synthetic) {
            this.type = type;
            this.start = start;
            this.end = end;
            this.text = text;
            this.path = path;
            this.synthetic = synthetic;
        }

        Block derive(BlockType type, int start, int end, String text, boolean synthetic) {
            return new Block(type, start, end, text, this.path, synthetic || this.synthetic);
        }
    }

    /**
     * A contiguous run of blocks that becomes one chunk, plus the overlap taken
     * from the run before it.
     */
    private static final class Pack {
        int from;
        int to;
        String overlapText = "";
        int overlapStart = -1;

        Pack(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    /**
     * The normalized document, with the page boundaries when the source was paginated.
     */
    private static final class Doc {
        final String text;
        final int[] pageStarts;

        Doc(String text, int[] pageStarts) {
            this.text = text;
            this.pageStarts = pageStarts;
        }

        int pageAt(int offset) {
            if (pageStarts == null || pageStarts.length == 0) {
                return 0;
            }
            int low = 0;
            int high = pageStarts.length - 1;
            int page = 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                if (pageStarts[mid] <= offset) {
                    page = mid + 1;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            return page;
        }
    }

    private interface Measure {
        int size(String text);
    }

    private static final class CharMeasure implements Measure {
        @Override
        public int size(String text) {
            return text == null ? 0 : text.length();
        }
    }

    private static final class TokenMeasure implements Measure {
        private final Encoding encoding;

        TokenMeasure(Encoding encoding) {
            this.encoding = encoding;
        }

        @Override
        public int size(String text) {
            if (text == null || text.isEmpty()) {
                return 0;
            }
            return encoding.countTokensOrdinary(text);
        }
    }

    /**
     * Lazily initialized so applications that never measure in tokens pay nothing
     * for loading the encodings.
     */
    private static final class Tokenizers {
        private static final EncodingRegistry REGISTRY = createRegistry();
        private static final Map<String, Encoding> CACHE = new ConcurrentHashMap<>();
        private static boolean warned = false;

        /**
         * jtokkit is only needed to count tokens, so a runtime that does not carry it
         * must still be able to chunk by characters. Swallowing the loading failure
         * here is what keeps a missing jar from taking the whole chunker down.
         */
        private static EncodingRegistry createRegistry() {
            try {
                return Encodings.newLazyEncodingRegistry();
            } catch (Throwable e) {
                return null;
            }
        }

        static boolean available() {
            return REGISTRY != null;
        }

        static void warnOnce() {
            if (!warned) {
                warned = true;
                LOGGER.warn("Token counting is unavailable: jtokkit is missing from the runtime classpath. "
                        + "Chunking by characters still works and the tokens field stays at zero.");
            }
        }

        static Encoding get(String name) {
            if (!available()) {
                return null;
            }
            String key = name == null || name.isBlank() ? DEFAULT_ENCODING : name.trim();
            return CACHE.computeIfAbsent(key, Tokenizers::resolve);
        }

        private static Encoding resolve(String name) {
            Optional<Encoding> byEncoding = REGISTRY.getEncoding(name);
            if (byEncoding.isPresent()) {
                return byEncoding.get();
            }
            Optional<Encoding> byModel = REGISTRY.getEncodingForModel(name);
            if (byModel.isPresent()) {
                return byModel.get();
            }
            LOGGER.warn("Unknown token encoding '{}', falling back to {}.", name, DEFAULT_ENCODING);
            return REGISTRY.getEncoding(EncodingType.CL100K_BASE);
        }
    }

    /**
     * The configuration resolved for a single call: instance fields overridden by
     * the options map, validated and clamped once so the pipeline never has to
     * defend against impossible values.
     */
    private static final class Settings {
        int chunkSize;
        int overlap;
        int minChunkSize;
        Measure measure;
        String encoding;
        String embed;
        boolean prependHeading;
        boolean headingPath;
        boolean splitOnHeadings;
        boolean stripDataUri;
        boolean stripHtmlComments;
        String source;
        Values metadata;
        Kind kind;
    }

    private Values sizeOptions(int chunkSize, int overlap) {
        return Values.newMap().set("chunkSize", chunkSize).set("overlap", overlap);
    }

    private Settings resolve(Values options, Kind kind) {
        Values opts = options == null ? Values.newMap() : options;
        Settings s = new Settings();

        s.kind = kind;
        s.encoding = opts.getString("encoding", encoding);

        String resolvedUnit = opts.getString("unit", unit);
        boolean tokens = UNIT_TOKENS.equalsIgnoreCase(resolvedUnit);

        // Measuring in tokens without the tokenizer would silently produce a budget that
        // means something else entirely, so it fails loudly instead of quietly degrading.
        if (tokens && !Tokenizers.available()) {
            throw new IllegalStateException(
                    "Chunking with unit 'tokens' requires the jtokkit library on the classpath. "
                            + "Add com.knuddels:jtokkit to the runtime libraries, or use unit 'chars'.");
        }

        s.measure = tokens ? new TokenMeasure(Tokenizers.get(s.encoding)) : new CharMeasure();

        int defaultChunkSize = tokens && !chunkSizeExplicit ? DEFAULT_CHUNK_SIZE_TOKENS : chunkSize;
        int defaultOverlap = tokens && !overlapExplicit ? DEFAULT_OVERLAP_TOKENS : overlap;

        s.chunkSize = clamp(opts.getInt("chunkSize", defaultChunkSize), MIN_CHUNK_SIZE_LIMIT, MAX_CHUNK_SIZE_LIMIT);
        s.overlap = clamp(opts.getInt("overlap", defaultOverlap), 0, s.chunkSize / 2);

        int requestedMin = opts.getInt("minChunkSize", minChunkSize);
        s.minChunkSize = requestedMin < 0 ? s.chunkSize / 4 : clamp(requestedMin, 0, s.chunkSize);

        s.prependHeading = opts.getBoolean("prependHeading", prependHeading);
        s.headingPath = opts.getBoolean("headingPath", headingPath);
        s.splitOnHeadings = opts.getBoolean("splitOnHeadings", splitOnHeadings);
        s.stripDataUri = opts.getBoolean("stripDataUri", stripDataUri);
        s.stripHtmlComments = opts.getBoolean("stripHtmlComments", stripHtmlComments);
        s.embed = normalizeEmbed(opts.getString("embed", embed));
        s.source = opts.getString("source", source);
        s.metadata = opts.getValues("metadata") != null ? opts.getValues("metadata") : metadata;

        return s;
    }

    private static String normalizeEmbed(String value) {
        if (EMBED_CONTEXT.equalsIgnoreCase(value)) {
            return EMBED_CONTEXT;
        }
        if (EMBED_CONTENT.equalsIgnoreCase(value)) {
            return EMBED_CONTENT;
        }
        return EMBED_FULL;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // ------------------------------------------------------------------
    // Pipeline
    // ------------------------------------------------------------------

    private Values process(String raw, Kind kind, Values options) {
        if (raw == null || raw.isBlank()) {
            return Values.newList();
        }

        Settings s = resolve(options, kind);
        Doc doc = kind == Kind.PDF ? normalizePdf(raw, s) : new Doc(normalize(raw, s, kind == Kind.MARKDOWN), null);

        if (doc.text.isBlank()) {
            return Values.newList();
        }

        List<Block> blocks = kind == Kind.MARKDOWN ? segmentMarkdown(doc.text) : segmentPlain(doc.text);
        if (blocks.isEmpty()) {
            return Values.newList();
        }

        List<Block> fitted = fitBlocks(blocks, s);
        List<Pack> packs = pack(fitted, s);
        mergeResidual(fitted, packs, s);
        applyOverlap(fitted, packs, s);

        return emit(doc, fitted, packs, s);
    }

    // ------------------------------------------------------------------
    // Normalization
    // ------------------------------------------------------------------

    private String normalize(String raw, Settings s, boolean markdown) {
        String text = stripBom(raw).replace("\r\n", "\n").replace('\r', '\n');

        List<String> out = new ArrayList<>();
        boolean[] inComment = {false};
        char fenceChar = 0;
        int fenceLength = 0;
        boolean inFence = false;

        int cursor = 0;
        int length = text.length();

        while (cursor <= length) {
            int newline = text.indexOf('\n', cursor);
            int lineEnd = newline < 0 ? length : newline;
            String line = text.substring(cursor, lineEnd);

            if (markdown) {
                Matcher fence = FENCE.matcher(line);
                if (fence.matches()) {
                    String marker = fence.group(1);
                    if (!inFence) {
                        inFence = true;
                        fenceChar = marker.charAt(0);
                        fenceLength = marker.length();
                        out.add(line.stripTrailing());
                        cursor = lineEnd + 1;
                        if (newline < 0) {
                            break;
                        }
                        continue;
                    }
                    if (marker.charAt(0) == fenceChar && marker.length() >= fenceLength && fence.group(2).isBlank()) {
                        inFence = false;
                        out.add(line.stripTrailing());
                        cursor = lineEnd + 1;
                        if (newline < 0) {
                            break;
                        }
                        continue;
                    }
                }
            }

            if (inFence) {
                out.add(line);
            } else {
                String cleaned = cleanLine(line, s, inComment);
                if (cleaned.isBlank()) {
                    if (!out.isEmpty() && !out.get(out.size() - 1).isEmpty()) {
                        out.add("");
                    }
                } else {
                    out.add(cleaned);
                }
            }

            cursor = lineEnd + 1;
            if (newline < 0) {
                break;
            }
        }

        while (!out.isEmpty() && out.get(out.size() - 1).isBlank()) {
            out.remove(out.size() - 1);
        }

        return String.join("\n", out);
    }

    private String cleanLine(String line, Settings s, boolean[] inComment) {
        String cleaned = line;

        if (s.stripHtmlComments) {
            cleaned = stripComments(cleaned, inComment);
        }

        cleaned = normalizeSpaces(cleaned);

        if (s.stripDataUri) {
            cleaned = DATA_URI.matcher(cleaned).replaceAll("![$1](data:$2)");
        }

        return cleaned.stripTrailing();
    }

    /**
     * Non breaking and figure spaces are folded into plain spaces and the zero width
     * characters are dropped, so the same word is measured and matched the same way
     * no matter which editor or extractor produced the text.
     */
    private String normalizeSpaces(String line) {
        String cleaned = line
                .replace('\u00A0', ' ')
                .replace('\u2007', ' ')
                .replace('\u202F', ' ')
                .replace('\t', ' ');
        return ZERO_WIDTH.matcher(cleaned).replaceAll("");
    }

    /**
     * Removes HTML comments across lines. The state flag carries an open comment
     * into the following lines.
     */
    private String stripComments(String line, boolean[] inComment) {
        StringBuilder out = new StringBuilder(line.length());
        int cursor = 0;

        while (cursor < line.length()) {
            if (inComment[0]) {
                int close = line.indexOf("-->", cursor);
                if (close < 0) {
                    return out.toString();
                }
                inComment[0] = false;
                cursor = close + 3;
                continue;
            }

            int open = line.indexOf("<!--", cursor);
            if (open < 0) {
                out.append(line, cursor, line.length());
                break;
            }
            out.append(line, cursor, open);
            inComment[0] = true;
            cursor = open + 4;
        }

        return out.toString();
    }

    private Doc normalizePdf(String raw, Settings s) {
        String text = stripBom(raw).replace("\r\n", "\n").replace('\r', '\n');

        text = PDF_HYPHEN_LOWER.matcher(text).replaceAll("$1$2");
        text = PDF_HYPHEN_UPPER.matcher(text).replaceAll("$1-$2");

        List<List<String>> pages = splitPages(text);
        boolean paginated = pages.size() > 1;

        Set<String> repeated = paginated ? repeatedEdgeLines(pages) : Collections.emptySet();

        StringBuilder body = new StringBuilder();
        List<Integer> pageStarts = new ArrayList<>();

        for (List<String> page : pages) {
            List<String> kept = new ArrayList<>();
            for (String line : page) {
                String cleaned = normalizeSpaces(line).stripTrailing();

                if (cleaned.isBlank()) {
                    if (!kept.isEmpty() && !kept.get(kept.size() - 1).isEmpty()) {
                        kept.add("");
                    }
                    continue;
                }
                if (paginated && PAGE_NUMBER_LINE.matcher(cleaned).matches()) {
                    continue;
                }
                if (repeated.contains(fingerprint(cleaned))) {
                    continue;
                }
                kept.add(cleaned);
            }

            while (!kept.isEmpty() && kept.get(kept.size() - 1).isBlank()) {
                kept.remove(kept.size() - 1);
            }
            while (!kept.isEmpty() && kept.get(0).isBlank()) {
                kept.remove(0);
            }
            if (kept.isEmpty()) {
                continue;
            }

            if (body.length() > 0) {
                body.append("\n\n");
            }
            pageStarts.add(body.length());
            body.append(String.join("\n", kept));
        }

        int[] starts = new int[pageStarts.size()];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = pageStarts.get(i);
        }

        return new Doc(body.toString(), paginated ? starts : null);
    }

    private List<List<String>> splitPages(String text) {
        List<List<String>> pages = new ArrayList<>();
        List<String> current = new ArrayList<>();

        int cursor = 0;
        int length = text.length();

        while (cursor <= length) {
            int newline = text.indexOf('\n', cursor);
            int lineEnd = newline < 0 ? length : newline;
            String line = text.substring(cursor, lineEnd);

            int formFeed = line.indexOf('\f');
            if (formFeed < 0) {
                current.add(line);
            } else {
                int from = 0;
                while (formFeed >= 0) {
                    current.add(line.substring(from, formFeed));
                    pages.add(current);
                    current = new ArrayList<>();
                    from = formFeed + 1;
                    formFeed = line.indexOf('\f', from);
                }
                current.add(line.substring(from));
            }

            cursor = lineEnd + 1;
            if (newline < 0) {
                break;
            }
        }

        pages.add(current);
        return pages;
    }

    /**
     * A line that shows up as the first or last line of most pages is a running
     * header or footer, not content.
     */
    private Set<String> repeatedEdgeLines(List<List<String>> pages) {
        if (pages.size() < 3) {
            return Collections.emptySet();
        }

        Map<String, Integer> counts = new ConcurrentHashMap<>();

        for (List<String> page : pages) {
            Set<String> edges = new LinkedHashSet<>();
            String first = firstNonBlank(page, true);
            String last = firstNonBlank(page, false);
            if (first != null) {
                edges.add(fingerprint(first));
            }
            if (last != null) {
                edges.add(fingerprint(last));
            }
            for (String edge : edges) {
                counts.merge(edge, 1, Integer::sum);
            }
        }

        int threshold = Math.max(3, (int) Math.ceil(pages.size() * 0.6d));
        Set<String> repeated = new LinkedHashSet<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() >= threshold && !entry.getKey().isBlank()) {
                repeated.add(entry.getKey());
            }
        }
        return repeated;
    }

    private String firstNonBlank(List<String> lines, boolean fromStart) {
        if (fromStart) {
            for (String line : lines) {
                if (!line.isBlank()) {
                    return line.strip();
                }
            }
            return null;
        }
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (!lines.get(i).isBlank()) {
                return lines.get(i).strip();
            }
        }
        return null;
    }

    /**
     * Digits are masked so "Página 3 de 47" and "Página 4 de 47" collapse to the same footer.
     */
    private String fingerprint(String line) {
        return DIGITS.matcher(line.strip()).replaceAll("#");
    }

    private static String stripBom(String text) {
        return !text.isEmpty() && text.charAt(0) == '\uFEFF' ? text.substring(1) : text;
    }

    // ------------------------------------------------------------------
    // Segmentation
    // ------------------------------------------------------------------

    private static final class Lines {
        final String text;
        final int[] start;
        final int[] end;
        final int count;

        Lines(String text) {
            this.text = text;
            List<Integer> starts = new ArrayList<>();
            List<Integer> ends = new ArrayList<>();

            int cursor = 0;
            int length = text.length();
            while (cursor <= length) {
                int newline = text.indexOf('\n', cursor);
                int lineEnd = newline < 0 ? length : newline;
                starts.add(cursor);
                ends.add(lineEnd);
                cursor = lineEnd + 1;
                if (newline < 0) {
                    break;
                }
            }

            this.count = starts.size();
            this.start = new int[count];
            this.end = new int[count];
            for (int i = 0; i < count; i++) {
                this.start[i] = starts.get(i);
                this.end[i] = ends.get(i);
            }
        }

        String get(int index) {
            return text.substring(start[index], end[index]);
        }

        boolean isBlank(int index) {
            for (int i = start[index]; i < end[index]; i++) {
                if (!Character.isWhitespace(text.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    private List<Block> segmentMarkdown(String text) {
        Lines lines = new Lines(text);
        List<Block> blocks = new ArrayList<>();
        HeadingStack stack = new HeadingStack();

        int i = 0;

        if (lines.count > 1 && lines.get(0).strip().equals("---")) {
            int close = -1;
            for (int j = 1; j < lines.count; j++) {
                if (lines.get(j).strip().equals("---")) {
                    close = j;
                    break;
                }
            }
            if (close > 0) {
                blocks.add(new Block(BlockType.FRONT_MATTER, lines.start[0], lines.end[close],
                        text.substring(lines.start[0], lines.end[close]), List.of(), false));
                i = close + 1;
            }
        }

        while (i < lines.count) {
            if (lines.isBlank(i)) {
                i++;
                continue;
            }

            String line = lines.get(i);

            Matcher fence = FENCE.matcher(line);
            if (fence.matches()) {
                String marker = fence.group(1);
                int close = i;
                for (int j = i + 1; j < lines.count; j++) {
                    Matcher candidate = FENCE.matcher(lines.get(j));
                    if (candidate.matches()
                            && candidate.group(1).charAt(0) == marker.charAt(0)
                            && candidate.group(1).length() >= marker.length()
                            && candidate.group(2).isBlank()) {
                        close = j;
                        break;
                    }
                    close = j;
                }
                blocks.add(new Block(BlockType.CODE, lines.start[i], lines.end[close],
                        text.substring(lines.start[i], lines.end[close]), stack.path(), false));
                i = close + 1;
                continue;
            }

            Matcher heading = ATX_HEADING.matcher(line);
            if (heading.matches()) {
                int level = heading.group(1).length();
                String title = heading.group(2) == null ? "" : heading.group(2).strip();
                stack.push(level, "#".repeat(level) + (title.isEmpty() ? "" : " " + title));
                blocks.add(new Block(BlockType.HEADING, lines.start[i], lines.end[i], line.strip(), stack.path(), false));
                i++;
                continue;
            }

            if (isThematicBreak(line)) {
                blocks.add(new Block(BlockType.RULE, lines.start[i], lines.end[i], line.strip(), stack.path(), false));
                i++;
                continue;
            }

            if (line.indexOf('|') >= 0 && i + 1 < lines.count && isTableDelimiter(lines.get(i + 1))) {
                int last = i + 1;
                for (int j = i + 2; j < lines.count && !lines.isBlank(j) && lines.get(j).indexOf('|') >= 0; j++) {
                    last = j;
                }
                blocks.add(new Block(BlockType.TABLE, lines.start[i], lines.end[last],
                        text.substring(lines.start[i], lines.end[last]), stack.path(), false));
                i = last + 1;
                continue;
            }

            if (LIST_ITEM.matcher(line).find()) {
                int last = i;
                for (int j = i + 1; j < lines.count; j++) {
                    if (lines.isBlank(j)) {
                        if (j + 1 < lines.count && !lines.isBlank(j + 1) && isListContinuation(lines.get(j + 1))) {
                            last = j + 1;
                            continue;
                        }
                        break;
                    }
                    if (!isListContinuation(lines.get(j))) {
                        break;
                    }
                    last = j;
                }
                blocks.add(new Block(BlockType.LIST, lines.start[i], lines.end[last],
                        text.substring(lines.start[i], lines.end[last]), stack.path(), false));
                i = last + 1;
                continue;
            }

            if (line.stripLeading().startsWith(">")) {
                int last = i;
                for (int j = i + 1; j < lines.count && !lines.isBlank(j) && lines.get(j).stripLeading().startsWith(">"); j++) {
                    last = j;
                }
                blocks.add(new Block(BlockType.QUOTE, lines.start[i], lines.end[last],
                        text.substring(lines.start[i], lines.end[last]), stack.path(), false));
                i = last + 1;
                continue;
            }

            int last = i;
            for (int j = i + 1; j < lines.count; j++) {
                if (lines.isBlank(j)) {
                    break;
                }
                String next = lines.get(j);
                if (SETEXT_UNDERLINE.matcher(next).matches() && j == i + 1) {
                    int level = next.strip().charAt(0) == '=' ? 1 : 2;
                    String title = line.strip();
                    stack.push(level, "#".repeat(level) + " " + title);
                    blocks.add(new Block(BlockType.HEADING, lines.start[i], lines.end[j],
                            "#".repeat(level) + " " + title, stack.path(), true));
                    last = -1;
                    i = j + 1;
                    break;
                }
                if (ATX_HEADING.matcher(next).matches() || FENCE.matcher(next).matches() || isThematicBreak(next)) {
                    break;
                }
                last = j;
            }
            if (last < 0) {
                continue;
            }

            blocks.add(new Block(BlockType.PARAGRAPH, lines.start[i], lines.end[last],
                    text.substring(lines.start[i], lines.end[last]), stack.path(), false));
            i = last + 1;
        }

        return blocks;
    }

    private List<Block> segmentPlain(String text) {
        Lines lines = new Lines(text);
        List<Block> blocks = new ArrayList<>();
        HeadingStack stack = new HeadingStack();

        int i = 0;
        while (i < lines.count) {
            if (lines.isBlank(i)) {
                i++;
                continue;
            }

            String line = lines.get(i);

            Matcher section = NUMBERED_SECTION.matcher(line);
            if (section.matches()) {
                int level = Math.min(6, section.group(1).split("\\.").length);
                String title = section.group(1) + " " + section.group(2).strip();
                stack.push(level, "#".repeat(level) + " " + title);
                blocks.add(new Block(BlockType.HEADING, lines.start[i], lines.end[i], line.strip(), stack.path(), false));
                i++;
                continue;
            }

            int last = i;
            for (int j = i + 1; j < lines.count; j++) {
                if (lines.isBlank(j) || NUMBERED_SECTION.matcher(lines.get(j)).matches()) {
                    break;
                }
                last = j;
            }

            blocks.add(new Block(BlockType.PARAGRAPH, lines.start[i], lines.end[last],
                    text.substring(lines.start[i], lines.end[last]), stack.path(), false));
            i = last + 1;
        }

        return blocks;
    }

    /**
     * The heading hierarchy in effect while scanning. The breadcrumb is rebuilt only
     * when a heading is pushed, so every block reuses the same immutable list instead
     * of recomputing it.
     */
    private static final class HeadingStack {
        private final String[] levels = new String[7];
        private List<String> path = List.of();

        void push(int level, String raw) {
            levels[level] = raw;
            for (int i = level + 1; i < levels.length; i++) {
                levels[i] = null;
            }

            List<String> rebuilt = new ArrayList<>(3);
            for (int i = 1; i < levels.length; i++) {
                if (levels[i] != null) {
                    rebuilt.add(levels[i]);
                }
            }
            path = rebuilt.isEmpty() ? List.of() : List.copyOf(rebuilt);
        }

        List<String> path() {
            return path;
        }
    }

    private static boolean isThematicBreak(String line) {
        String trimmed = line.strip();
        if (trimmed.length() < 3) {
            return false;
        }
        char marker = trimmed.charAt(0);
        if (marker != '-' && marker != '*' && marker != '_') {
            return false;
        }
        int count = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char current = trimmed.charAt(i);
            if (current == marker) {
                count++;
            } else if (current != ' ') {
                return false;
            }
        }
        return count >= 3;
    }

    private static boolean isTableDelimiter(String line) {
        String trimmed = line.strip();
        if (trimmed.indexOf('-') < 0 || trimmed.indexOf('|') < 0) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            char current = trimmed.charAt(i);
            if (current != '|' && current != '-' && current != ':' && current != ' ') {
                return false;
            }
        }
        return true;
    }

    private static boolean isListContinuation(String line) {
        if (LIST_ITEM.matcher(line).find()) {
            return true;
        }
        return line.startsWith("  ") || line.startsWith("\t");
    }

    /**
     * Markdown is assumed when the text carries markup that plain prose does not:
     * a heading, a fence, a table delimiter, or a fair number of list items.
     */
    private boolean looksLikeMarkdown(String content) {
        if (content == null || content.isBlank()) {
            return false;
        }

        Lines lines = new Lines(content.replace("\r\n", "\n").replace('\r', '\n'));
        int listItems = 0;

        for (int i = 0; i < lines.count; i++) {
            String line = lines.get(i);
            if (ATX_HEADING.matcher(line).matches() || FENCE.matcher(line).matches()) {
                return true;
            }
            if (isTableDelimiter(line)) {
                return true;
            }
            if (LIST_ITEM.matcher(line).find()) {
                listItems++;
                if (listItems >= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    // ------------------------------------------------------------------
    // Splitting of oversized blocks
    // ------------------------------------------------------------------

    private int size(Block block, Settings s) {
        if (block.size < 0) {
            block.size = s.measure.size(block.text);
        }
        return block.size;
    }

    /**
     * The room a single block has, once the context header and the overlap are paid for.
     */
    private int blockBudget(Block block, Settings s) {
        int budget = s.chunkSize - headerCost(block.path, s) - s.overlap;
        return Math.max(s.chunkSize / 4, budget);
    }

    private List<Block> fitBlocks(List<Block> blocks, Settings s) {
        List<Block> out = new ArrayList<>(blocks.size());
        for (Block block : blocks) {
            int budget = blockBudget(block, s);
            if (size(block, s) <= budget) {
                out.add(block);
                continue;
            }
            splitBlock(block, budget, s, out);
        }
        return out;
    }

    private void splitBlock(Block block, int budget, Settings s, List<Block> out) {
        switch (block.type) {
            case CODE -> splitCode(block, budget, s, out);
            case TABLE -> splitTable(block, budget, s, out);
            case LIST -> splitList(block, budget, s, out);
            default -> splitProse(block, block.text, block.start, budget, s, 0, out);
        }
    }

    /**
     * A code block is cut by lines and each part is wrapped in its own fence, so
     * every chunk still holds valid Markdown instead of a dangling fragment.
     */
    private void splitCode(Block block, int budget, Settings s, List<Block> out) {
        Lines lines = new Lines(block.text);
        if (lines.count <= 2) {
            splitProse(block, block.text, block.start, budget, s, 0, out);
            return;
        }

        String open = lines.get(0).stripTrailing();
        String close = lines.get(lines.count - 1).strip();
        boolean closed = FENCE.matcher(close).matches();
        int lastBody = closed ? lines.count - 2 : lines.count - 1;

        String fenceOnly = open.strip();
        int marker = 0;
        while (marker < fenceOnly.length() && (fenceOnly.charAt(marker) == '`' || fenceOnly.charAt(marker) == '~')) {
            marker++;
        }
        String closing = closed ? close : fenceOnly.substring(0, Math.max(3, marker));

        int wrapper = s.measure.size(open + "\n" + closing);
        int room = Math.max(1, budget - wrapper);

        int from = 1;
        while (from <= lastBody) {
            int used = 0;
            int to = from;
            while (to <= lastBody) {
                int cost = s.measure.size(block.text.substring(lines.start[to], lines.end[to])) + 1;
                if (to > from && used + cost > room) {
                    break;
                }
                used += cost;
                to++;
            }
            if (to == from) {
                to = from + 1;
            }

            String body = block.text.substring(lines.start[from], lines.end[to - 1]);
            boolean whole = from == 1 && to - 1 == lastBody;

            // The first part owns the opening fence line and the last one owns the closing
            // fence, so the parts together still cover the whole block. Without this the
            // fence lines would belong to no chunk at all in the offsets.
            int partStart = from == 1 ? block.start : block.start + lines.start[from];
            int partEnd = to - 1 == lastBody ? block.end : block.start + lines.end[to - 1];

            out.add(block.derive(
                    BlockType.CODE,
                    partStart,
                    partEnd,
                    open + "\n" + body + "\n" + closing,
                    !whole
            ));

            from = to;
        }
    }

    /**
     * A table is cut by rows and the header is repeated in every part, so a chunk
     * that holds the tail of a table still says what each column means.
     */
    private void splitTable(Block block, int budget, Settings s, List<Block> out) {
        Lines lines = new Lines(block.text);
        if (lines.count <= 3) {
            splitProse(block, block.text, block.start, budget, s, 0, out);
            return;
        }

        String header = lines.get(0).stripTrailing() + "\n" + lines.get(1).stripTrailing();
        int headerCost = s.measure.size(header) + 1;
        int room = Math.max(1, budget - headerCost);

        int from = 2;
        while (from < lines.count) {
            int used = 0;
            int to = from;
            while (to < lines.count) {
                int cost = s.measure.size(block.text.substring(lines.start[to], lines.end[to])) + 1;
                if (to > from && used + cost > room) {
                    break;
                }
                used += cost;
                to++;
            }
            if (to == from) {
                to = from + 1;
            }

            String body = block.text.substring(lines.start[from], lines.end[to - 1]);
            boolean whole = from == 2 && to == lines.count;

            // The first part owns the header rows, so the parts together cover the block.
            int partStart = from == 2 ? block.start : block.start + lines.start[from];

            out.add(block.derive(
                    BlockType.TABLE,
                    partStart,
                    block.start + lines.end[to - 1],
                    whole ? block.text : header + "\n" + body,
                    !whole
            ));

            from = to;
        }
    }

    /**
     * A list is cut between items; an item only gets cut when it alone is too large.
     */
    private void splitList(Block block, int budget, Settings s, List<Block> out) {
        Lines lines = new Lines(block.text);

        List<Integer> itemStarts = new ArrayList<>();
        for (int i = 0; i < lines.count; i++) {
            if (LIST_ITEM.matcher(lines.get(i)).find()) {
                itemStarts.add(i);
            }
        }
        if (itemStarts.size() <= 1) {
            splitProse(block, block.text, block.start, budget, s, 0, out);
            return;
        }

        int item = 0;
        while (item < itemStarts.size()) {
            int used = 0;
            int group = item;
            while (group < itemStarts.size()) {
                int lineFrom = itemStarts.get(group);
                int lineTo = group + 1 < itemStarts.size() ? itemStarts.get(group + 1) - 1 : lines.count - 1;
                int cost = s.measure.size(block.text.substring(lines.start[lineFrom], lines.end[lineTo])) + 1;
                if (group > item && used + cost > budget) {
                    break;
                }
                used += cost;
                group++;
            }
            if (group == item) {
                group = item + 1;
            }

            int lineFrom = itemStarts.get(item);
            int lineTo = group < itemStarts.size() ? itemStarts.get(group) - 1 : lines.count - 1;
            String body = block.text.substring(lines.start[lineFrom], lines.end[lineTo]);

            if (s.measure.size(body) > budget) {
                splitProse(block, body, block.start + lines.start[lineFrom], budget, s, 1, out);
            } else {
                out.add(block.derive(BlockType.LIST,
                        block.start + lines.start[lineFrom],
                        block.start + lines.end[lineTo],
                        body, false));
            }

            item = group;
        }
    }

    private static final String[] PROSE_SEPARATORS = {"\n\n", "\n", null, "; ", ", ", " "};

    /**
     * Recursive separator cascade: paragraph, line, sentence, clause, word, and a
     * hard cut as the last resort. Each level is only reached when the level above
     * left a piece that still does not fit.
     */
    private void splitProse(Block block, String text, int offset, int budget, Settings s, int level, List<Block> out) {
        if (text.isEmpty()) {
            return;
        }
        if (s.measure.size(text) <= budget) {
            out.add(block.derive(block.type == BlockType.HEADING ? BlockType.PARAGRAPH : block.type,
                    offset, offset + text.length(), text, false));
            return;
        }
        if (level >= PROSE_SEPARATORS.length) {
            hardCut(block, text, offset, budget, s, out);
            return;
        }

        List<int[]> pieces = level == 2
                ? splitBySentences(text)
                : splitBySeparator(text, PROSE_SEPARATORS[level]);

        if (pieces.size() <= 1) {
            splitProse(block, text, offset, budget, s, level + 1, out);
            return;
        }

        for (int[] piece : pieces) {
            String part = text.substring(piece[0], piece[1]);
            if (part.isBlank()) {
                continue;
            }
            splitProse(block, part, offset + piece[0], budget, s, level + 1, out);
        }
    }

    /**
     * Separators stay attached to the piece before them, so joining the pieces back
     * gives the original text unchanged.
     */
    private List<int[]> splitBySeparator(String text, String separator) {
        List<int[]> pieces = new ArrayList<>();
        int from = 0;
        int cursor = text.indexOf(separator);

        while (cursor >= 0) {
            int to = cursor + separator.length();
            pieces.add(new int[]{from, to});
            from = to;
            cursor = text.indexOf(separator, from);
        }
        if (from < text.length()) {
            pieces.add(new int[]{from, text.length()});
        }
        return pieces;
    }

    private List<int[]> splitBySentences(String text) {
        List<int[]> pieces = new ArrayList<>();
        Matcher matcher = SENTENCE_BOUNDARY.matcher(text);
        int from = 0;

        while (matcher.find()) {
            pieces.add(new int[]{from, matcher.end()});
            from = matcher.end();
        }
        if (from < text.length()) {
            pieces.add(new int[]{from, text.length()});
        }
        return pieces;
    }

    /**
     * Last resort for text with no usable separator, a long unpunctuated run for
     * instance. The cut point is searched so the piece fits the budget exactly and
     * never lands in the middle of a surrogate pair.
     */
    private void hardCut(Block block, String text, int offset, int budget, Settings s, List<Block> out) {
        int from = 0;
        while (from < text.length()) {
            int to = Math.min(text.length(), from + Math.max(1, budget));

            while (to > from + 1 && s.measure.size(text.substring(from, to)) > budget) {
                to = from + Math.max(1, (int) ((to - from) * 0.8d));
            }
            while (to < text.length() && s.measure.size(text.substring(from, Math.min(text.length(), to + 8))) <= budget) {
                to = Math.min(text.length(), to + 8);
            }
            if (to < text.length() && Character.isHighSurrogate(text.charAt(to - 1))) {
                to++;
            }
            if (to <= from) {
                to = from + 1;
            }

            out.add(block.derive(block.type, offset + from, offset + to, text.substring(from, to), true));
            from = to;
        }
    }

    // ------------------------------------------------------------------
    // Packing
    // ------------------------------------------------------------------

    private int headerCost(List<String> path, Settings s) {
        if (!s.prependHeading || path.isEmpty()) {
            return 0;
        }
        return s.measure.size(header(path, s, false)) + BLOCK_SEPARATOR_COST;
    }

    private String header(List<String> path, Settings s, boolean dropDeepest) {
        if (!s.prependHeading || path.isEmpty()) {
            return "";
        }

        List<String> lines = s.headingPath ? path : path.subList(path.size() - 1, path.size());
        if (dropDeepest) {
            if (lines.size() <= 1) {
                return "";
            }
            lines = lines.subList(0, lines.size() - 1);
        }

        String rendered = String.join("\n", lines);

        int limit = (int) (s.chunkSize * MAX_HEADER_RATIO);
        while (lines.size() > 1 && s.measure.size(rendered) > limit) {
            lines = lines.subList(1, lines.size());
            rendered = String.join("\n", lines);
        }

        return rendered;
    }

    private List<Pack> pack(List<Block> blocks, Settings s) {
        List<Pack> packs = new ArrayList<>();
        int index = 0;

        while (index < blocks.size()) {
            int from = index;
            int budget = Math.max(s.chunkSize / 4, s.chunkSize - headerCost(blocks.get(from).path, s) - s.overlap);
            int used = 0;

            while (index < blocks.size()) {
                Block block = blocks.get(index);
                int cost = size(block, s) + (index == from ? 0 : BLOCK_SEPARATOR_COST);

                if (index > from) {
                    if (s.splitOnHeadings && block.type == BlockType.HEADING && used >= s.minChunkSize) {
                        break;
                    }
                    if (used + cost > budget) {
                        break;
                    }
                }

                used += cost;
                index++;
            }

            if (index == from) {
                index++;
            }
            packs.add(new Pack(from, index));
        }

        return packs;
    }

    /**
     * Absorbs a chunk that came out too small into a neighbour, so the store does
     * not end up with orphan fragments that never win a similarity search.
     */
    private void mergeResidual(List<Block> blocks, List<Pack> packs, Settings s) {
        if (packs.size() < 2 || s.minChunkSize <= 0) {
            return;
        }

        int tolerance = (int) (s.chunkSize * MERGE_TOLERANCE);
        int index = 0;

        while (index < packs.size()) {
            Pack pack = packs.get(index);
            int packSize = packSize(blocks, pack, s);

            if (packSize >= s.minChunkSize || packs.size() < 2) {
                index++;
                continue;
            }

            Pack previous = index > 0 ? packs.get(index - 1) : null;
            Pack next = index + 1 < packs.size() ? packs.get(index + 1) : null;

            if (previous != null && packSize + packSize(blocks, previous, s) <= tolerance) {
                previous.to = pack.to;
                packs.remove(index);
                continue;
            }
            if (next != null && packSize + packSize(blocks, next, s) <= tolerance) {
                next.from = pack.from;
                packs.remove(index);
                continue;
            }
            index++;
        }
    }

    private int packSize(List<Block> blocks, Pack pack, Settings s) {
        int total = 0;
        for (int i = pack.from; i < pack.to; i++) {
            total += size(blocks.get(i), s) + (i == pack.from ? 0 : BLOCK_SEPARATOR_COST);
        }
        return total;
    }

    /**
     * Overlap is taken as whole blocks from the end of the previous pack and, when
     * no whole block fits, as the trailing sentences of the block right before.
     * Code and tables are never repeated: a fragment of a fence is pure noise.
     */
    private void applyOverlap(List<Block> blocks, List<Pack> packs, Settings s) {
        if (s.overlap <= 0) {
            return;
        }

        for (int i = 1; i < packs.size(); i++) {
            Pack pack = packs.get(i);
            Pack previous = packs.get(i - 1);

            int used = 0;
            int cursor = pack.from;

            while (cursor - 1 >= previous.from) {
                Block candidate = blocks.get(cursor - 1);
                if (!overlappable(candidate)) {
                    break;
                }
                int cost = size(candidate, s) + BLOCK_SEPARATOR_COST;
                if (used + cost > s.overlap) {
                    break;
                }
                used += cost;
                cursor--;
            }

            StringBuilder overlapText = new StringBuilder();
            int overlapStart = -1;

            if (cursor - 1 >= previous.from && used < s.overlap) {
                Block candidate = blocks.get(cursor - 1);
                if (overlappable(candidate)) {
                    int tailStart = sentenceTailStart(candidate.text, s.overlap - used, s);
                    if (tailStart < candidate.text.length()) {
                        overlapText.append(candidate.text, tailStart, candidate.text.length());
                        overlapStart = candidate.start + tailStart;
                    }
                }
            }

            for (int j = cursor; j < pack.from; j++) {
                if (overlapText.length() > 0) {
                    overlapText.append("\n\n");
                }
                if (overlapStart < 0) {
                    overlapStart = blocks.get(j).start;
                }
                overlapText.append(blocks.get(j).text);
            }

            pack.overlapText = overlapText.toString();
            pack.overlapStart = overlapStart;
        }
    }

    private boolean overlappable(Block block) {
        return block.type != BlockType.CODE
                && block.type != BlockType.TABLE
                && block.type != BlockType.HEADING
                && block.type != BlockType.FRONT_MATTER;
    }

    /**
     * Where the largest suffix that still fits the budget starts, always on a sentence
     * boundary. Returns the text length when not even the last sentence fits, which the
     * caller reads as "no overlap". The offset is exact, so the chunk `start` keeps
     * pointing at the real position in the document.
     */
    private int sentenceTailStart(String text, int budget, Settings s) {
        if (budget <= 0 || text.isBlank()) {
            return text.length();
        }

        List<int[]> sentences = splitBySentences(text);
        int start = text.length();

        for (int i = sentences.size() - 1; i >= 0; i--) {
            int candidate = sentences.get(i)[0];
            if (s.measure.size(text.substring(candidate)) > budget) {
                break;
            }
            start = candidate;
        }

        return start;
    }

    // ------------------------------------------------------------------
    // Emission
    // ------------------------------------------------------------------

    private Values emit(Doc doc, List<Block> blocks, List<Pack> packs, Settings s) {
        Values chunks = Values.newList();

        Encoding tokenizer = Tokenizers.get(s.encoding);
        if (tokenizer == null) {
            Tokenizers.warnOnce();
        }

        List<Pack> emitted = new ArrayList<>(packs.size());
        List<String> bodies = new ArrayList<>(packs.size());
        for (Pack pack : packs) {
            String body = body(doc, blocks, pack);
            if (!body.isBlank()) {
                emitted.add(pack);
                bodies.add(body);
            }
        }

        for (int index = 0; index < emitted.size(); index++) {
            Pack pack = emitted.get(index);
            Block first = blocks.get(pack.from);
            Block last = blocks.get(pack.to - 1);

            String body = bodies.get(index);
            String content = pack.overlapText.isEmpty() ? body : pack.overlapText + "\n\n" + body;

            // The deepest heading is dropped from the context header whenever the chunk
            // already carries that heading in its body, overlap or not, so it is never
            // spelled out twice in the text that gets embedded.
            boolean startsWithOwnHeading = first.type == BlockType.HEADING;
            String contextHeader = header(first.path, s, startsWithOwnHeading);

            // The header answers "what context is missing at the start", so it comes from the
            // first block. The reported heading answers "which section is this chunk in", which
            // is the section it ends in: a chunk that opens with a stray line and then holds a
            // whole section belongs to that section. Weighting by size instead would let a
            // section be swallowed by a busier neighbour and never surface as any chunk's
            // heading, which would make it impossible to filter by.
            List<String> path = blocks.get(pack.to - 1).path;

            String hash = hash(content);
            String prefix = s.source.isEmpty() ? "chunk" : s.source;

            Values chunk = Values.newMap();
            chunk.set("id", prefix + "#" + index + "-" + hash.substring(0, 8));
            chunk.set("hash", hash);
            chunk.set("index", index);
            chunk.set("total", emitted.size());
            chunk.set("start", pack.overlapStart >= 0 ? pack.overlapStart : first.start);
            chunk.set("end", last.end);
            chunk.set("length", content.length());
            chunk.set("tokens", tokenizer == null ? 0 : tokenizer.countTokensOrdinary(content));
            chunk.set("heading", path.isEmpty() ? "" : plainTitle(path.get(path.size() - 1)));
            chunk.set("headingLevel", path.isEmpty() ? 0 : level(path.get(path.size() - 1)));
            chunk.set("path", titles(path));
            chunk.set("breadcrumb", breadcrumb(path));
            chunk.set("sections", sections(blocks, pack));
            chunk.set("header", contextHeader);
            chunk.set("content", content);
            chunk.set("context", "");
            chunk.set("text", composeText(contextHeader, "", content, s.embed));
            chunk.set("embed", s.embed);
            chunk.set("type", s.kind.label());
            chunk.set("blocks", blockTypes(blocks, pack));
            chunk.set("overlap", pack.overlapText.isEmpty() ? 0 : pack.overlapText.length());
            chunk.set("page", doc.pageAt(first.start));
            chunk.set("pages", pages(doc, blocks, pack));
            chunk.set("synthetic", synthetic(blocks, pack));
            chunk.set("metadata", copyMetadata(s.metadata));

            chunks.add(chunk);
        }

        return chunks;
    }

    /**
     * When every block of the pack is a literal slice of the document, the body is
     * the slice itself, which keeps the original spacing intact. Only synthetic
     * blocks, a reopened fence or a repeated table header, force a rebuild.
     */
    private String body(Doc doc, List<Block> blocks, Pack pack) {
        boolean literal = true;
        for (int i = pack.from; i < pack.to; i++) {
            if (blocks.get(i).synthetic) {
                literal = false;
                break;
            }
        }

        if (literal) {
            int from = blocks.get(pack.from).start;
            int to = blocks.get(pack.to - 1).end;
            if (from >= 0 && to <= doc.text.length() && from < to) {
                return doc.text.substring(from, to).strip();
            }
        }

        StringBuilder out = new StringBuilder();
        for (int i = pack.from; i < pack.to; i++) {
            if (out.length() > 0) {
                out.append("\n\n");
            }
            out.append(blocks.get(i).text);
        }
        return out.toString().strip();
    }

    /**
     * Builds the string to embed. In `context` mode the chunk body is deliberately left out:
     * the generated note is dense prose, while the body carries fences, table pipes and
     * markup that dilute the vector. The body is never lost, it stays in `content`, which is
     * what a search should return. When there is no context yet, the body is used instead,
     * because embedding a heading on its own would match nothing.
     */
    private String composeText(String header, String context, String content, String mode) {
        StringBuilder out = new StringBuilder();

        if (header != null && !header.isEmpty()) {
            out.append(header).append("\n\n");
        }

        if (EMBED_CONTENT.equals(mode)) {
            return content;
        }

        boolean hasContext = context != null && !context.isBlank();

        if (hasContext) {
            out.append(context);
            if (EMBED_CONTEXT.equals(mode)) {
                return out.toString();
            }
            out.append("\n\n");
        } else if (EMBED_CONTEXT.equals(mode)) {
            out.append(content);
            return out.toString();
        }

        out.append(content);
        return out.toString();
    }

    private boolean synthetic(List<Block> blocks, Pack pack) {
        for (int i = pack.from; i < pack.to; i++) {
            if (blocks.get(i).synthetic) {
                return true;
            }
        }
        return false;
    }

    private Values blockTypes(List<Block> blocks, Pack pack) {
        Set<String> types = new LinkedHashSet<>();
        for (int i = pack.from; i < pack.to; i++) {
            types.add(blocks.get(i).type.label());
        }
        Values list = Values.newList();
        for (String type : types) {
            list.add(type);
        }
        return list;
    }

    /**
     * Every section the chunk touches, as breadcrumbs and in reading order. A chunk that
     * covers two short sections has to pick one for `heading`, so without this a section
     * absorbed into a busier neighbour would never surface anywhere and could not be
     * filtered on. Usually holds a single entry.
     */
    private Values sections(List<Block> blocks, Pack pack) {
        Set<String> seen = new LinkedHashSet<>();
        for (int i = pack.from; i < pack.to; i++) {
            String crumb = breadcrumb(blocks.get(i).path);
            if (!crumb.isEmpty()) {
                seen.add(crumb);
            }
        }

        Values list = Values.newList();
        for (String crumb : seen) {
            list.add(crumb);
        }
        return list;
    }

    /**
     * Every page the chunk spans, ascending. `page` alone would name only the page the
     * chunk opens on, which is misleading to cite when the chunk runs across a page
     * break. Empty when the source was not paginated.
     */
    private Values pages(Doc doc, List<Block> blocks, Pack pack) {
        Values list = Values.newList();
        if (doc.pageStarts == null || doc.pageStarts.length == 0) {
            return list;
        }

        int previous = -1;
        for (int i = pack.from; i < pack.to; i++) {
            int page = doc.pageAt(blocks.get(i).start);
            if (page != previous) {
                list.add(page);
                previous = page;
            }
        }
        return list;
    }

    private Values titles(List<String> path) {
        Values list = Values.newList();
        for (String raw : path) {
            list.add(plainTitle(raw));
        }
        return list;
    }

    private String breadcrumb(List<String> path) {
        StringBuilder out = new StringBuilder();
        for (String raw : path) {
            if (out.length() > 0) {
                out.append(" > ");
            }
            out.append(plainTitle(raw));
        }
        return out.toString();
    }

    private String plainTitle(String raw) {
        int cursor = 0;
        while (cursor < raw.length() && raw.charAt(cursor) == '#') {
            cursor++;
        }
        return raw.substring(cursor).strip();
    }

    private int level(String raw) {
        int cursor = 0;
        while (cursor < raw.length() && raw.charAt(cursor) == '#') {
            cursor++;
        }
        return cursor;
    }

    private Values copyMetadata(Values metadata) {
        Values copy = Values.newMap();
        if (metadata != null) {
            for (String key : metadata.keys()) {
                copy.set(key, metadata.get(key));
            }
        }
        return copy;
    }

    private String hash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(32);
            for (int i = 0; i < 8; i++) {
                out.append(String.format("%02x", bytes[i]));
            }
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("SHA-256 is not available, falling back to the content hash code.");
            return String.format("%016x", (long) content.hashCode() & 0xFFFFFFFFL);
        }
    }
}
