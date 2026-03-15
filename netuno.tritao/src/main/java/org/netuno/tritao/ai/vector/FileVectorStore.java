package org.netuno.tritao.ai.vector;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.hili.Hili;

public class FileVectorStore extends VectorStore {

    public FileVectorStore(Proteu proteu, Hili hili, String provider) {
        super(proteu, hili, provider);
    }

    public FileVectorStore(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
}
