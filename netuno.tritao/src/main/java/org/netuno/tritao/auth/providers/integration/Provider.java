package org.netuno.tritao.auth.providers.integration;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

public interface Provider {
    String getCode();
    String getUrlAuthenticator(Callback callback);
    Values getAccessTokens(Callback callback, String code) throws Exception;
    Values getUserDetails(Values data) throws Exception;
}
