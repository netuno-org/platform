package org.netuno.tritao.ai.vector;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.hili.Hili;

public class PostgreVectorStore extends VectorStore {

    public PostgreVectorStore(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public PostgreVectorStore(Proteu proteu, Hili hili, String provider) {
        super(proteu, hili, provider);
    }
}
