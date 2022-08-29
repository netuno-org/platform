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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.net.Remote;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.netuno.tritao.Service;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * Firebase - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "firebase")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Firebase",
                introduction = "Recurso de comunicação com o Firebase.",
                howToUse = { }
        )
})
public class Firebase extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Firebase.class);

    private static Values APPS_LOADING = new Values();
    private static Values APPS = new Values();
    private static Values AUTHS = new Values();
    private static Values DATABASES = new Values();

    public Firebase(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values firebaseConfig = getProteu().getConfig().getValues("_app:config").getValues("firebase");
        if (firebaseConfig != null) {
            getProteu().getConfig().set("_firebase:app_name", firebaseConfig.get("app_name"));
            getProteu().getConfig().set("_firebase:database_url", firebaseConfig.get("database_url"));
            getProteu().getConfig().set("_firebase:key_file", firebaseConfig.get("key_file"));
            getProteu().getConfig().set("_firebase:listener_secret", firebaseConfig.get("listener_secret"));
        }
    }
    
    @AppEvent(type=AppEventType.BeforeInitialization)
    private void beforeInitialization() {
        config();
    }
    
    @AppEvent(type=AppEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        Service service = Service.getInstance(getProteu());
        if (service.path.startsWith("firebase/listener/")
                && getProteu().getConfig().getString("_firebase:listener_secret") == getProteu().getRequestAll().getString("secret")) {
            service.allow();
        }
    }

    public void config() {
        if (APPS_LOADING.hasKey(Config.getApp(getProteu()))) {
            return;
        }
        if (APPS.hasKey(Config.getApp(getProteu()))) {
            getProteu().getConfig().set("_firebase:app", APPS.get(Config.getApp(getProteu())));
            getProteu().getConfig().set("_firebase:auth", AUTHS.get(Config.getApp(getProteu())));
            getProteu().getConfig().set("_firebase:database", DATABASES.get(Config.getApp(getProteu())));
            return;
        }
        if (active()) {
            APPS_LOADING.set(Config.getApp(getProteu()), true);
            try {
                File file = new File(
                        getProteu().getConfig().getString("_firebase:key_file"),
                        Config.getPathAppBaseConfig(getProteu())
                );
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(file.getInputStream()))
                        .setDatabaseUrl(getProteu().getConfig().getString("_firebase:database_url"))
                        .build();
                if (options != null) {
                    FirebaseApp app = FirebaseApp.initializeApp(
                            options,
                            getProteu().getConfig().getString("_firebase:app_name")
                    );
                    FirebaseAuth auth = FirebaseAuth.getInstance(app);
                    FirebaseDatabase database = FirebaseDatabase.getInstance(app);
                    APPS.set(Config.getApp(getProteu()), app);
                    AUTHS.set(Config.getApp(getProteu()), auth);
                    DATABASES.set(Config.getApp(getProteu()), database);
                    getProteu().getConfig().set("_firebase:app", app);
                    getProteu().getConfig().set("_firebase:auth", auth);
                    getProteu().getConfig().set("_firebase:database", database);
                    Builder builder = Config.getDataBaseBuilder(getProteu());
                    Header _header = new Header(getProteu(), getHili());
                    for (Values _table : builder.selectTable()) {
                        final Values table = _table;
                        final Hili hili = getHili();
                        final Proteu proteu = getProteu();
                        if (!table.getString("firebase").isEmpty()) {
                            String _firebaseTableName = table.getString("firebase").trim();
                            if (_firebaseTableName.equals("#")) {
                                _firebaseTableName = table.getString("name");
                            }
                            listener(
                                    _firebaseTableName,
                                    _header.baseURL() +
                                            "/Sync"+ org.netuno.proteu.Config.getExtension() +
                                            "?mode=firebase"
                            );
                        }
                    }
                }
            } catch (IOException e)  {
                logger.warn("Firebase configuration failed.", e);
            } finally {
                APPS_LOADING.remove(Config.getApp(getProteu()));
            }
        }
    }

    public boolean active() {
        return getProteu().getConfig().hasKey("_firebase:app_name")
                && getProteu().getConfig().hasKey("_firebase:key_file")
                && getProteu().getConfig().hasKey("_firebase:database_url")
                && !getProteu().getConfig().getString("_firebase:app_name").isEmpty()
                && !getProteu().getConfig().getString("_firebase:key_file").isEmpty()
                && !getProteu().getConfig().getString("_firebase:database_url").isEmpty();
    }

    public FirebaseApp app() {
        return (FirebaseApp)APPS.get(Config.getApp(getProteu()));
    }
    public FirebaseAuth auth() {
        return (FirebaseAuth)AUTHS.get(Config.getApp(getProteu()));
    }
    public FirebaseDatabase database() {
        return (FirebaseDatabase)DATABASES.get(Config.getApp(getProteu()));
    }

    public DatabaseReference databaseReference(String path, String uid) {
        return database().getReference(path + "/"+ uid);
    }

    public DatabaseReference databaseReference(String path) {
        return database().getReference(path);
    }

    public void listener(String path, String url) {
        listener(databaseReference(path), url);
    }

    public void listener(DatabaseReference ref, String url) {
        String secret = getProteu().getConfig().getString("_firebase:listener_secret");
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                new Remote().post(
                        url,
                        new Values()
                                .set("secret", secret)
                                .set("action", "added")
                                .set("path", ref.getPath().toString())
                                .set("previous", previousChildName)
                                .set("key", dataSnapshot.getKey())
                                .set("value", new Values(dataSnapshot.getValue()))
                );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                new Remote().post(
                        url,
                        new Values()
                                .set("secret", secret)
                                .set("action", "changed")
                                .set("path", ref.getPath().toString())
                                .set("previous", previousChildName)
                                .set("key", dataSnapshot.getKey())
                                .set("value", new Values(dataSnapshot.getValue()))
                );
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                new Remote().post(
                        url,
                        new Values()
                                .set("secret", secret)
                                .set("action", "removed")
                                .set("path", ref.getPath().toString())
                                .set("key", dataSnapshot.getKey())
                                .set("value", new Values(dataSnapshot.getValue()))
                );
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                new Remote().post(
                        url,
                        new Values()
                                .set("secret", secret)
                                .set("action", "moved")
                                .set("path", ref.getPath().toString())
                                .set("previous", previousChildName)
                                .set("key", dataSnapshot.getKey())
                                .set("value", new Values(dataSnapshot.getValue()))
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("The read failed: "+ databaseError.getMessage() +"#"+ ref.getPath(), databaseError);
            }
        });
    }

    public void setValue(String path, Map data) {
        setValue(databaseReference(path), new Values(data));
    }

    public void setValue(String path, List data) {
        setValue(databaseReference(path), new Values(data));
    }

    public void setValue(String path, Values data) {
        setValue(databaseReference(path), data);
    }

    public void setValue(String path, String uid, Map data) {
        setValue(databaseReference(path, uid), new Values(data));
    }

    public void setValue(String path, String uid, List data) {
        setValue(databaseReference(path, uid), new Values(data));
    }

    public void setValue(String path, String uid, Values data) {
        setValue(databaseReference(path, uid), data);
    }

    public void setValue(DatabaseReference ref, List data) {
        setValue(ref, new Values(data));
    }

    public void setValue(DatabaseReference ref, Values data) {
        ref.setValue(data, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Data could not be saved: "+ databaseError.getMessage() +" # "+ ref.getPath() +" # "+ data.toJSON());
            } else {
                logger.debug("Data saved successfully # "+ ref.getPath() +" # "+ data.toJSON());
            }
        });
    }

    public void removeValue(String path) {
        removeValue(databaseReference(path));
    }

    public void removeValue(String path, String uid) {
        removeValue(databaseReference(path, uid));
    }

    public void removeValue(DatabaseReference ref) {
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                logger.error("Data could not be removed: "+ databaseError.getMessage() +" # "+ ref.getPath());
            } else {
                logger.debug("Data removed successfully: "+ ref.getPath());
            }
        });
    }

}
