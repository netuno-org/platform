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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.resource.util.ResourceException;

import java.util.Date;
import java.util.List;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * JWT (JSON Web Token) - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "jwt")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Jwt",
                introduction = "Recurso de geração de JSON Web Tokens.",
                howToUse = { }
        )
})
public class JWT extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(JWT.class);
    private boolean enabled = false;
    private int accessExpires = 60;
    private int refreshExpires = 1440;
    private String secret = "";
    private String algorithm = null;

    public JWT(Proteu proteu, Hili hili) {
        super(proteu, hili);
        if (!Config.isAppConfigLoaded(proteu)) {
            return;
        }
        try {
            init();
        } catch (Exception e) {
            logger.fatal("Initializing JWT...", e);
        }
        isEnabled();
    }

    public JWT(Proteu proteu, Hili hili, String secret, String algorithm) {
        super(proteu, hili);
        this.secret = secret;
        this.algorithm = algorithm;
        isEnabled();
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_jwt", getProteu().getConfig().getValues("_app:config").getValues("jwt"));
    }

    public JWT init() throws ResourceException {
        Values config = getProteu().getConfig().asValues("_jwt");
        if (config == null) {
            return null;
        }
        if (!isEnabled()) {
            return null;
        }
        this.secret = config.getString("secret");
        this.algorithm = config.getString("algorithm");
        if (secret.isEmpty() || secret.length() < 16) {
            throw new ResourceException("JWT secret is weak! Please choose a more secure secret with at least 16 characters.");
        }
        if (algorithm.isEmpty() || signatureAlgorithm(algorithm) == null) {
            throw new ResourceException("JWT algorithm is not defined! Please choose one and define in the environment configuration:\n"+
                    " - ES256, ES384, ES512\n" +
                    " - HS256, HS384, HS512\n" +
                    " - PS256, PS384, PS512\n" +
                    " - RS256, RS384, RS512\n"
            );
        }
        return new JWT(getProteu(), getHili(), secret, algorithm);
    }

    public JWT init(String secret, String algorithm) throws ResourceException {
        return new JWT(getProteu(), getHili(), secret, algorithm);
    }

    public boolean isEnabled() {
        Values config = getProteu().getConfig().asValues("_jwt");
        if (config != null) {
            enabled = config.getBoolean("enabled");
            getProteu().getConfig().set("_jwt:enabled", enabled);
        }
        return enabled;
    }

    public int accessExpires() {
        Values config = getProteu().getConfig().asValues("_jwt");
        if (config != null) {
            accessExpires = config.getInt("access_expires");
            getProteu().getConfig().set("_jwt:expire", accessExpires);
        }
        return accessExpires;
    }

    public int refreshExpires() {
        Values config = getProteu().getConfig().asValues("_jwt");
        if (config != null) {
            refreshExpires = config.getInt("refresh_expires");
            getProteu().getConfig().set("_jwt:expire", refreshExpires);
        }
        return refreshExpires;
    }

    public String token() {
        if (getProteu().getConfig().hasKey("_jwt:token")
                && !getProteu().getConfig().getString("_jwt:token").isEmpty()) {
            return getProteu().getConfig().getString("_jwt:token");
        }
        if (getProteu().getRequestHeader().has("Authorization")) {
            String authorization = getProteu().getRequestHeader().getString("Authorization");
            if (authorization.startsWith("Bearer ")) {
                String token = authorization.substring("Bearer ".length()).trim();
                getProteu().getConfig().set("_jwt:token", token);
                return token;
            }
        }
        return "";
    }

    public String token(Values data) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.claim("token_type", "Bearer");
        /*jwtBuilder.claim("expires_in", 86400);
        JWT refreshToken = init();
        refreshToken.token(
                new Values()
                        .set("expires", System.currentTimeMillis() + 600000)
                        .set("expires_in", System.currentTimeMillis() + 600000)
        );*/
        jwtBuilder.claim("uid", new UID(getProteu(), getHili()).generate());
        for (String key : data.keys()) {
            jwtBuilder.claim(key, data.get(key));
        }
        return jwtBuilder.signWith(signatureAlgorithm(algorithm), secret).compact();
    }
    
    public String encode(Values body) {
        return encode(new Values(), body);
    }
    
    public String encode(Values header, Values body) {
        JwtBuilder jwtBuilder = Jwts.builder();
        header.keys().forEach(key -> {
            jwtBuilder.setHeaderParam(key, header.get(key));
        });
        body.keys().forEach(key -> {
            jwtBuilder.claim(key, body.get(key));
        });
        return jwtBuilder.signWith(signatureAlgorithm(algorithm), secret).compact();
    }
    
    public Values decode(String token) {
        Jws<Claims> jwtParts = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        JwsHeader jwtHeader = jwtParts.getHeader();
        Claims jwtBody = jwtParts.getBody();
        Values header = new Values();
        jwtHeader.keySet().forEach(key -> {
            header.set(key.toString(), jwtHeader.get(key));
        });
        Values body = new Values();
        jwtBody.keySet().forEach(key -> {
            body.set(key, jwtBody.get(key));
        });
        return new Values().set("header", header)
                .set("body", body)
                .set("signature", jwtParts.getSignature());
    }

    public Values data() {
        String token = token();
        return !token.isEmpty() ? data(token()) : null;
    }

    public Values data(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            Values data = new Values();
            for (String key : claims.keySet()) {
                data.set(key, claims.get(key));
            }
            return data;
        } catch (Exception e) {
            logger.fatal(e);
            return null;
        }
    }

    public boolean check() {
        JWT jwt = new JWT(getProteu(), getHili());
        if (jwt.token() == null || jwt.token().isEmpty()) {
        	return false;
        }
        return check(jwt.token());
    }
    
    public boolean check(String token) {
    	Time time = new Time(getProteu(), getHili());
        Data dbManagerData = new Data(getProteu(), getHili());
        List<Values> dbTokens = dbManagerData.find(
                "netuno_auth_jwt_token",
                new Values().set("where",
                        new Values()
                                .set("access_token", new Values()
                                		.set("type", "text")
                                		.set("value", token)
                                ).set("active", true)
                )
        );
        if (dbTokens.size() == 1) {
            Values dbToken = dbTokens.get(0);
            Date expires = dbToken.getDate("access_expires");
            if (expires.getTime() > time.instant().toEpochMilli()) {
                return true;
            }
        }
        return false;
    }

    public Values accessToken(Values contextData) {
        return accessToken(0, contextData);
    }

    public Values accessToken(int userId, Values contextData) {
        DB db = new DB(getProteu(), getHili());
        Time time = new Time(getProteu(), getHili());
        String accessToken = this.token(contextData);
        Values tokenData = this.data(accessToken);
        Data dbManagerData = new Data(getProteu(), getHili());
        int tokenId = dbManagerData.insert(
                "netuno_auth_jwt_token",
                new Values()
                        .set("uid", "'"+ tokenData.getString("uid") +"'")
                        .set("user_id", userId)
                        .set("access_token", "'"+ db.sanitize(accessToken) +"'")
                        .set("created", "'"+ db.sanitize(db.timestamp().toString()) +"'")
                        .set("access_expires", "'"+ db.sanitize(db.timestamp(time.localDateTime().plusMinutes(accessExpires())).toString()) +"'")
                        .set("active", true)
        );
        Values dataToken = dbManagerData.get(
                "netuno_auth_jwt_token",
                tokenId
        );
        String refreshToken = this.token(
                new Values()
                        .set("token_uid", dataToken.getString("uid"))
                        .set("expires_in", this.refreshExpires() * 60000)
        );
        dbManagerData.update(
                "netuno_auth_jwt_token",
                tokenId,
                new Values()
                        .set("refresh_token", "'"+ db.sanitize(refreshToken) +"'")
                        .set("refresh_expires", "'"+ db.sanitize(db.timestamp(time.localDateTime().plusMinutes(refreshExpires())).toString()) +"'")
        );
        getProteu().getConfig().set(
                "_jwt:auth:data",
                new Values()
                        .set("result", true)
                        .set("access_token", accessToken)
                        .set("refresh_token", refreshToken)
                        .set("expires_in", accessExpires() * 60000)
                        .set("refresh_expires_in", refreshExpires() * 60000)
                        .set("token_type", "Bearer")
        );
        return getProteu().getConfig().getValues("_jwt:auth:data");
    }

    public Values refreshToken(String refreshToken) {
        Data dbManagerData = new Data(getProteu(), getHili());
        List<Values> dbOldTokens = dbManagerData.find(
                "netuno_auth_jwt_token",
                new Values().set("where",
                        new Values().set("refresh_token", refreshToken)
                                .set("active", true)
                )
        );
        if (dbOldTokens.size() == 1) {
            Time time = resource(Time.class);
            Values dbOldToken = dbOldTokens.get(0);
            if (dbOldToken.getDate("refresh_expires").getTime() > time.instant().toEpochMilli()) {
                Values values = this.data(refreshToken);
                if (values.getString("token_uid").equals(dbOldToken.getString("uid"))) {
                    Values data = data(dbOldToken.getString("access_token"));
                    data.unset("uid");
                    Values token = accessToken(dbOldToken.getInt("id"), data);
                    dbManagerData.update("netuno_auth_jwt_token", dbOldToken.getInt("id"), new Values().set("active", false));
                    return token;
                }
            }
        }
        return null;
    }

    public Values searchApp(Values data) {
        if (data.hasKey("key") && data.hasKey("secret")) {

        } else if (data.hasKey("code")) {

        } else if (data.hasKey("name")) {

        }
        return new Values();
    }

    public Values registerApp(Values data) {
        return new Values();
    }

    public String createAppCode(String code) {
        return "";
    }

    public boolean isAppCode(String code) {
        return false;
    }

    public Values searchWebOrigin(Values data) {
        return new Values();
    }

    public Values registerWebOrigin(Values data) {
        return new Values();
    }

    private SignatureAlgorithm signatureAlgorithm(String algorithm) {
        try {
            return (SignatureAlgorithm)SignatureAlgorithm.class.getDeclaredField(algorithm.replace("-", "_").toUpperCase()).get(SignatureAlgorithm.class);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }
}
