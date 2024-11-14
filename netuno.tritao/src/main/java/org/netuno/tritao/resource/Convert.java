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

package org.netuno.tritao.resource;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.netuno.psamata.io.File;

/**
 * Convert - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "convert")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Convert",
                introduction = "Conversor de tipos de dados da aplicação.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Convert",
                introduction = "Application data type converter.",
                howToUse = { }
        )
})
public class Convert extends ResourceBase {

    public Convert(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte os bytes em **hexadecimal** com letras minúsculas.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Convert bytes to **hexadecimal** with lowercase letters.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "bytes", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "bytes",
									description = "Bytes para serem convertidos."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Bytes to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna os bytes recebidos em **hexadecimal** em minúsculas."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Return bytes received in **hexadecimal** in lowercase."
					)
			}
	)
	public String toHex(byte[] bytes) {
		return Hex.encodeHexString(bytes);
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte os bytes em **hexadecimal** com letras maiúsculas.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Convert bytes to **hexadecimal** with uppercase letters.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "bytes", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "bytes",
									description = "Bytes para serem convertidos."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Bytes to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna os bytes recebidos em **hexadecimal** em minúsculas."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Return bytes received in **hexadecimal** in capital letters."
					)
			}
	)
	public String toHEX(byte[] bytes) {
		return Hex.encodeHexString(bytes, false);
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo em Base64 para o array de bytes original descodificado.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts the Base64 content to the original decoded byte array.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna os bytes descodificados que estavam em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns decoded bytes that were in Base64."
					)
			}
	)
	public byte[] fromBase64AsBytes(byte[] content) {
		return Base64.getDecoder().decode(content);
	}

	public byte[] fromBase64AsBytes(String content) {
		return Base64.getDecoder().decode(content);
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo com Base64 em uma string descodificada.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts Base64 content to a decoded string.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna a string descodificada que estava em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the decoded string that was in Base64."
					)
			}
	)
	public String fromBase64(byte[] content) {
		return new String(Base64.getDecoder().decode(content));
	}

	public String fromBase64(String content) {
		return new String(Base64.getDecoder().decode(content));
	}


	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo em uma string codificada com Base64.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts the content to a Base64 encoded string.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna a string codificada em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the encoded string in Base64."
					)
			}
	)
	public byte[] toBase64AsBytes(byte[] content) {
		return Base64.getEncoder().encode(content);
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo em codificação Base64.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Convert the content into Base64 encoding.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language =LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna os bytes codificados em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns Base64 encoded bytes."
					)
			}
	)
	public byte[] toBase64AsBytes(String content) {
		return Base64.getEncoder().encode(content.getBytes());
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo em uma string codificada com Base64.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts the content to a Base64 encoded string.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna a string codificada em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the encoded string in Base64."
					)
			}
	)
	public String toBase64(byte[] content) {
		return Base64.getEncoder().encodeToString(content);
	}
	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte o conteúdo em uma string codificada com Base64.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts the content to a Base64 encoded string.",
							howToUse = { }),
			},
			parameters = {
					@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna a string codificada em Base64."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the encoded string in Base64."
					)
			}
	)

	public String toBase64(String content) {
		return Base64.getEncoder().encodeToString(content.getBytes());
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte um array de bytes em text.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts a byte array to text.",
							howToUse = { })
			},
			parameters = {
					@ParameterDoc(
							name = "bytes", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									description = "Array de bytes."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Byte array."
							)
					}),
					@ParameterDoc(
							name = "charset", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									description = "Código de codificação de caracteres."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Character encoding code."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna o texto obtido do array de bytes."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the text obtained from the byte array."
					)
			}
	)
	public String textFromBytes(byte[] bytes, String charset) throws UnsupportedEncodingException {
		return new String(bytes, charset);
	}

	public String textFromBytes(byte[] bytes) {
		return new String(bytes);
	}

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte um text em array de bytes.",
							howToUse = { }),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts a text to byte array.",
							howToUse = { })
			},
			parameters = {
					@ParameterDoc(
							name = "text", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									description = "Conteúdo em texto."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Text content."
							)
					}),
					@ParameterDoc(
							name = "charset", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									description = "Código de codificação de caracteres."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Character encoding code."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna o array de bytes obtido do texto."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns the byte array obtained from text."
					)
			}
	)
	public byte[] bytesFromText(String text, String charset) throws UnsupportedEncodingException {
		return text.getBytes(charset);
	}

	public byte[] bytesFromText(String text) {
		return text.getBytes();
	}

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **byte**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toByte(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **byte**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toByte(3456)"
		                            )
		                    })
		    },
    		parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o valor recebido em **byte**."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the value received in **byte**."
    	            )
    	    }
    )
    public byte toByte(int value) {
        return (byte)value;
    }

    public byte toByte(long value) {
        return (byte)value;
    }

    public byte toByte(short value) {
        return (byte)value;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **short**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toShort(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **short**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toShort(3456)"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o valor recebido em **short**."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the value received in **short**."
    	            )
    	    }
    )
    public short toShort(int value) {
        return (short)value;
    }

    public short toShort(long value) {
        return (short)value;
    }

    public short toShort(byte value) {
        return (short)value;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **int**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toInt(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **int**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toInt(3456)"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o valor recebido em **int**."
    	            )
    	    }
    )
    public int toInt(byte value) {
        return (int)value;
    }

    public int toInt(long value) {
        return (int)value;
    }

    public int toInt(short value) {
        return (int)value;
    }

    public int toInt(float value) {
        return (int)value;
    }

    public int toInt(double value) {
        return (int)value;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **long**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toLong(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **long**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toLong(3456)"
		                            )
		                    })
    		},
			parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
            returns = {
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Retorna o valor recebido em **long**."
		            )
		    }
    )
    public long toLong(byte value) {
        return (long)value;
    }

    public long toLong(int value) {
        return (long)value;
    }

    public long toLong(short value) {
        return (long)value;
    }

    public long toLong(float value) {
        return (long)value;
    }

    public long toLong(double value) {
        return (long)value;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **float**",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toFloat(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **float**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toFloat(3456)"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
    		returns = {
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Retorna o valor recebido em **float**."
		            )
		    }
    )
    public float toFloat(double value) {
        return (float)value;
    }

    public float toFloat(int value) {
        return (float)value;
    }

    public float toFloat(long value) {
        return (float)value;
    }

    public float toFloat(short value) {
        return (float)value;
    }

    public float toFloat(byte value) {
        return (float)value;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o valor recebido para **double**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toDouble(3456)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received value to **double**.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "_convert.toDouble(3456)"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "value", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "valor",
									description = "Valor para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Value to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o valor recebido em **double**."
    	            )
    	    }
    )
    public double toDouble(float value) {
        return (double)value;
    }

    public double toDouble(int value) {
        return (double)value;
    }

    public double toDouble(long value) {
        return (double)value;
    }

    public double toDouble(short value) {
        return (double)value;
    }

    public double toDouble(byte value) {
        return (double)value;
    }
    
    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o objecto recebido em um tipo de objeto genérico.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const objetoGenerico = _convert.toObject(\"Texto...\")\n"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received object to a generic object type.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const genericObject = _convert.toObject(\"Text...\")\n"
		                            )
		                    })
    		},
			parameters = {
    				@ParameterDoc(
							name = "object", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "objeto",
									description = "Objeto para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Object to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna como tipo de objeto genérico."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns as type of generic object."
    	            )
    	    }
    )
    public Object toObject(Object o) {
        return (Object)o;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o objecto recebido em um objeto de erro do tipo Throwable.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const erro = _error.createError(\"Falha grave.\")\n"
		                                    		+ "const comoThrowable = _convert.toThrowable(erro)\n"
		                                    		+ "_error.rise(comoThrowable)"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received object into a Throwable type error.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const error = _error.createError(\"Serious failure.\")\n"
		                                    		+ "const asThrowable = _convert.toThrowable(erro)\n"
		                                    		+ "_error.rise(asThrowable)"
		                            )
		                    })
            },
			parameters = {
    				@ParameterDoc(
							name = "object", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "objeto",
									description = "Objeto para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Object to be converted."
							)
					})
			},
    		returns = {
	            @ReturnTranslationDoc(
	                    language = LanguageDoc.PT,
	                    description = "Retorna o objeto transformado em objeto de erro do tipo Throwable."
	            ),
	            @ReturnTranslationDoc(
	                    language = LanguageDoc.EN,
	                    description = "Returns the Throwable type error."
	            )
	    }
    )
    public Throwable toThrowable(Object o) {
        return (Throwable)o;
    }
    
    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o objecto recebido em um objeto de Error.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const erroOriginal = _error.createError(\"Falha grave.\")\n"
		                                    		+ "const generico = _convert.toObject(erroOriginal)\n"
		                                    		+ "const deVoltaComoErro = _convert.toError(generico)\n"
		                                    		+ "if (_error.isError(deVoltaComoErro)) {\n"
		                                    		+ "    _error.info(\"É realmente uma falha grave...\")\n"
		                                    		+ "}"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received object to an Error object.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const originalError = _error.createError(\"Serious failure.\")\n"
		                                    		+ "const generic = _convert.toObject(originalError)\n"
		                                    		+ "const backAsError = _convert.toError(generic)\n"
		                                    		+ "if (_error.isError(backAsError)) {\n"
		                                    		+ "    _error.info(\"It is really a serious failure...\")\n"
		                                    		+ "}"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "object", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "objeto",
									description = "Objeto para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Object to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o objeto convertido em Error."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the object converted as Error."
    	            )
    	    }
    )
    public java.lang.Error toError(Object o) {
        return (java.lang.Error)o;
    }
    
    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte o objecto recebido em um objeto de Exception.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const excecaoOriginal = _error.createError(\"Falha grave.\")\n"
		                                    		+ "const generico = _convert.toObject(excecaoOriginal)\n"
		                                    		+ "const deVoltaComoExcecao = _convert.toException(generico)\n"
		                                    		+ "if (_error.isException(deVoltaComoExcecao)) {\n"
		                                    		+ "    _error.info(\"É realmente uma falha grave...\")\n"
		                                    		+ "}"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts the received object to an Exception object.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const originalException = _error.createException(\"Serious failure.\")\n"
		                                    		+ "const generic = _convert.toObject(originalException)\n"
		                                    		+ "const backAsException = _convert.toException(generic)\n"
		                                    		+ "if (_error.isException(backAsException)) {\n"
		                                    		+ "    _error.info(\"It is really a serious failure...\")\n"
		                                    		+ "}"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "object", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "objeto",
									description = "Objeto para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Object to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o objeto convertido em Exception."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the object converted as Exception."
    	            )
    	    }
    )
    public Exception toException(Object o) {
        return (java.lang.Exception)o;
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte os caracteres especiais no texto para garantir que são válidos em HTML.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Vai imprimir: &euro;s A&ccedil;&atilde;o\n"
		                                    		+ "_out.print(_convert.toHTML(\"€s Ação\"))"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts special characters in the text to ensure that they are valid in HTML.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Will print: &euro;s A&ccedil;&atilde;o\n"
		                                    		+ "_out.print(_convert.toHTML(\"€s Ação\"))"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdp para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Texto formatado com códificação dos caracteres especiais em HTML."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Text formatted with encoding of special characters in HTML."
    	            )
    	    }
    )
    public String toHTML(String content) {
        return StringEscapeUtils.escapeHtml4(content);
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Transforma a codificação de caracteres especiais de HTML para texto normal.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Vai imprimir: €s Ação\n"
		                                    		+ "_out.print(_convert.fromHTML(\"&euro;s A&ccedil;&atilde;o\"))"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Transforms the encoding of special HTML characters to plain text.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Will print: €s Ação\n"
		                                    		+ "_out.print(_convert.fromHTML(\"&euro;s A&ccedil;&atilde;o\"))"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
    		returns = {
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Texto com códificação dos caracteres especiais em HTML tranformado para texto normal."
		            ),
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Text with encoding of special characters in HTML transformed to normal text."
		            )
    		}
    )
    public String fromHTML(String content) {
        return StringEscapeUtils.unescapeHtml4(content);
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Converte os caracteres especiais no texto para garantir que são válidos em JSON.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Vai imprimir: \\u20ACs A\\u00E7\\u00E3o\n"
		                                    		+ "_out.print(_convert.toJSON(\"€s Ação\"))"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Converts special characters in the text to ensure that they are valid in JSON.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Will print: \\u20ACs A\\u00E7\\u00E3o\n"
		                                    		+ "_out.print(_convert.toJSON(\"€s Ação\"))"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Texto formatado com códificação dos caracteres especiais em JSON."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Text formatted with encoding of special characters in JSON."
    	            )
    	    }
    )
    public String toJSON(String content) {
        return StringEscapeUtils.escapeJson(content);
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Transforma a codificação de caracteres especiais de JSON para texto normal.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Vai imprimir: €s Ação\n"
		                                    		+ "_out.print(_convert.toJSON(\"\\u20ACs A\\u00E7\\u00E3o\"))"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Transforms the encoding of special characters from JSON to normal text.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Will print: €s Ação\n"
		                                    		+ "_out.print(_convert.toJSON(\"\\u20ACs A\\u00E7\\u00E3o\"))"
		                            )
		                    })
		    },
			parameters = {
    				@ParameterDoc(
							name = "content", translations = {
							@ParameterTranslationDoc(
									language = LanguageDoc.PT,
									name = "conteúdo",
									description = "Conteúdo para ser convertido."
							),
							@ParameterTranslationDoc(
									language = LanguageDoc.EN,
									description = "Content to be converted."
							)
					})
			},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Texto com codificação dos caracteres especiais em JSON tranformado para texto normal."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Text with encoding of special characters in JSON transformed to normal text."
    	            )
    	    }
    )
    public String fromJSON(String content) {
        return StringEscapeUtils.unescapeJson(content);
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um objeto de UUID com o conteúdo recebido.",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Inicia um objeto de UUID com o conteúdo recebido.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(
                name = "value",
                translations = {
                    @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "valor",
                        description = "String com conteúdo no formato de UUID."
                    ),
                    @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "String with content in the UUID format."
                    )
                }
            )
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto do UUID iniciado."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object of the started UUID."
            )
        }
    )
    public UUID toUUID(String value) {
    	try{
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um objeto de ficheiro em memória com o conteúdo dos bytes recebidos.",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a file object in memory with the contents of the received bytes.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(
                name = "fileName",
                translations = {	@ParameterTranslationDoc(
						language = LanguageDoc.PT,
						name = "nome",
						description = "Nome do ficheiro."
				),
						@ParameterTranslationDoc(
								language = LanguageDoc.EN,
								description = "Name of the file."
						)}
            ),
            @ParameterDoc(
                name = "contentType",
                translations = {
						@ParameterTranslationDoc(
								language = LanguageDoc.PT,
								name = "conteúdo",
								description = "Tipo de conteúdo."
						),
						@ParameterTranslationDoc(
								language = LanguageDoc.EN,
								description = "Type of content."
						)
				}
            ),
            @ParameterDoc(
					name = "bytes", translations = {
					@ParameterTranslationDoc(
							language = LanguageDoc.PT,
							name = "bytes",
							description = "Bytes que representam o conteúdo do ficheiro."
					),
					@ParameterTranslationDoc(
							language = LanguageDoc.EN,
							description = "Bytes that represent the file content."
					)
			})
		},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto do ficheiro iniciado que permite a manipulação dos bytes como ficheiro."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object of the started file that allows the manipulation of bytes as a file."
            )
        }
    )
    public File toFile(String fileName, String contentType, byte[] bytes) {
        return new File(fileName, contentType, new java.io.ByteArrayInputStream(bytes))
                .ensureJail(Config.getPathAppBase(getProteu()));
    }

	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Converte um array de tipos primitivos para uma lista (coleção).",
							howToUse = {}),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Converts an array of primitive types to a list (collection).",
							howToUse = {})
			},
			parameters = {
					@ParameterDoc(
							name = "array",
							translations = {
									@ParameterTranslationDoc(
											language = LanguageDoc.PT,
											description = "Array que será convertido numa lista (coleção)."
									),
									@ParameterTranslationDoc(
											language = LanguageDoc.EN,
											description = "Array that will be converted into a list (collection)."
									)
							}
					)
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Nova lista com todos os elementos do array."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "New list with all array elements."
					)
			}
	)
	public List arrayToList(Object array) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < Array.getLength(array); i++) {
			list.add(Array.get(array, i));
		}
		return list;
	}
}
