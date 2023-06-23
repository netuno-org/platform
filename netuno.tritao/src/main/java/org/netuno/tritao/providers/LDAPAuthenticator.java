package org.netuno.tritao.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Auth;
import org.netuno.tritao.resource.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LDAPAuthenticator {
    private static Logger logger = LogManager.getLogger(LDAPAuthenticator.class);

    private Proteu proteu;
    private Hili hili;
    private String domain;
    private String url;
    private String search;

    public LDAPAuthenticator(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    public Values authenticate(String username, String password) {
        Auth auth = hili.resource().get(Auth.class);
        if (auth.isProviderLDAPEnabled()) {
            Values config = auth.providerLDAPConfig();
            this.domain = config.getString("domain");
            this.url = config.getString("url");
            this.search = config.getString("search");
        } else {
            return null;
        }
        User user = hili.resource().get(User.class);
        Values dbUser = user.get(username);
        if (!user.hasProviderLDAP(dbUser.getInt("id"))) {
            return null;
        }
        String returnedAtts[] ={ "sn", "givenName", "mail" };
        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + username + "))";

        //Create the search controls
        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(returnedAtts);

        //Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username + "@" + domain);
        env.put(Context.SECURITY_CREDENTIALS, password);

        LdapContext ctxGC = null;
        try {
            ctxGC = new InitialLdapContext(env, null);
            //Search objects in GC using filters
            NamingEnumeration answer = ctxGC.search(search, searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attrs = sr.getAttributes();
                Values data = null;
                if (attrs != null) {
                    data = new Values();
                    NamingEnumeration ne = attrs.getAll();
                    while (ne.hasMore()) {
                        Attribute attr = (Attribute) ne.next();
                        data.set(attr.getID(), attr.get());
                    }
                    ne.close();
                }
                return data;
            }
        } catch (CommunicationException ex) {
            logger.fatal("LDAP URL "+ url +" failed: "+ ex.getMessage());
        } catch (NamingException ex) {
            logger.debug(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " # LDAP Authenticator - User: "+ username, ex.getMessage());
        }
        return null;
    }
}
