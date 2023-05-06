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

package org.netuno.psamata;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graalvm.polyglot.Value;

import static org.junit.jupiter.api.Assertions.*;

import org.netuno.psamata.net.Remote;
import org.netuno.psamata.script.GraalRunner;

public class ValuesTest {

    public ValuesTest() {

    }

    @Test
    public void remote() {
        Remote remote = new Remote()
            .setURLPrefix("http://localhost:9000/services");
        new Values()
                .add("Olá do Java!!!")
                .list(String.class)
                .stream()
                .forEach((s) ->
                        remote.post(
                                "dictionary",
                                new Values()
                                        .set("language_id", 1)
                                        .set("entry_id", 1)
                                        .set("value", s)
                        )
                );

        var response = Values.fromJSON(
                remote.get("dictionary/list")
        );

        response.list(Values.class)
                .stream()
                .map((v) -> {
                    var item = v.map();
                    return item.get("language_id")
                            +"::"+ item.get("entry_id")
                            +"::"+ item.get("value");
                })
                .forEach(System.out::println);
    }
    @Test
    public void list() {
        Values list = new Values();
        for (int i = 0; i < 3; i++) {
            Values item = new Values();
            item.put("index", i);
            list.add(item);
        }
        System.out.println(list.toJSON());
        assertTrue(list.isList());
        assertTrue(list.size() == 3);
    }

    @Test
    public void map() {
        Values map = new Values();
        Values list = new Values();
        for (int i = 0; i < 3; i++) {
            Values item = new Values();
            item.put("index", i);
            list.add(item);
        }
        map.put("name", "test");
        map.put("list", list);
        System.out.println(map.toJSON());
        assertTrue(map.isMap());
        assertTrue(map.getValues("list").isList());
        assertTrue(map.getValues("list").size() == 3);
    }

    @Test
    public void listMapList() {
        Values baseList = new Values();
        for (int l = 0; l < 1; l++) {
            Values map = new Values();
            Values list = new Values();
            for (int i = 0; i < 3; i++) {
                Values item = new Values();
                item.put("index", i);
                list.add(item);
            }
            map.put("name", "test"+ l);
            map.put("list", list);
            baseList.add(map);
        }
        System.out.println(baseList.toJSON());
        assertTrue(baseList.isList());
        assertTrue(baseList.getValues(0).getValues("list").isList());
        assertTrue(baseList.getValues(0).getValues("list").size() == 3);
    }

    @Test
    public void listListMap() {
        Values baseList = new Values();
        for (int l = 0; l < 1; l++) {
            Values list = new Values();
            for (int i = 0; i < 1; i++) {
                Values item = new Values();
                item.put("count", i + 1);
                list.add(item);
            }
            baseList.add(list);
        }
        System.out.println(baseList.toJSON());
        assertTrue(baseList.isList());
        assertTrue(baseList.getValues(0).isList());
        assertTrue(baseList.getValues(0).getValues(0).isMap());
        assertEquals(baseList.getValues(0).getValues(0).getInt("count"), 1);
    }

    @Test
    public void mergeListListMap() {
        List _baseList = new ArrayList();
        for (int l = 0; l < 1; l++) {
            List list = new ArrayList();
            for (int i = 0; i < 1; i++) {
                Map item = new HashMap();
                item.put("count", i + 1);
                list.add(item);
            }
            _baseList.add(list);
        }
        Values baseList = new Values(_baseList);
        System.out.println(baseList.toJSON());
        assertTrue(baseList.isList());
        assertTrue(baseList.getValues(0).isList());
        assertTrue(baseList.getValues(0).getValues(0).isMap());
        assertEquals(baseList.getValues(0).getValues(0).getInt("count"), 1);
        Values data = new Values(_baseList);
        if (data.isList()) {
            for (Values values : data.listOfValues()) {
                System.out.println("Ok");
                if (values.isList()) {
                    System.out.println("Ok");
                }
            }
        }
    }

    @Test
    public void contains() {
        Values values = new Values()
                .push(
                        new Values()
                                .set("id", 1)
                                .set("code", "abc")
                ).push(
                        new Values()
                                .set("id", 2)
                                .set("code", "lkj")
                );
        assertTrue(values.contains("code", "lkj"));
        assertTrue(values.contains("id", 1));
        assertFalse(values.contains("code", "uio"));
        assertFalse(values.contains("id", 5));
        assertTrue(values.containsValue(1));
        assertTrue(values.containsKey("code"));
        assertFalse(values.containsValue(10));
        assertFalse(values.containsKey("name"));
    }

    @Test
    public void find() {
        Values values = new Values()
                .push(
                        new Values()
                                .set("id", 1)
                                .set("code", "abc")
                ).push(
                        new Values()
                                .set("id", 2)
                                .set("code", "lkj")
                );
        assertTrue(values.find("code", "lkj") != null);
    }

    @Test
    public void valuesClone() {
        Values configRaw = Values.fromJSON("{\n" +
                "    \"name\": \"demo\",\n" +
                "    \"language\": \"pt_PT\",\n" +
                "    \"locale\": \"pt_PT\",\n" +
                "    \"db\": {\n" +
                "        \"password\": \"\",\n" +
                "        \"engine\": \"h2\",\n" +
                "        \"port\": \"\",\n" +
                "        \"host\": \"\",\n" +
                "        \"name\": \"demo\",\n" +
                "        \"username\": \"\"\n" +
                "    },\n" +
                "    \"firebase\": {\n" +
                "        \"app_name\": \"\",\n" +
                "        \"database_url\": \"\",\n" +
                "        \"key_file\": \"\",\n" +
                "        \"listener_secret\": \"\"\n" +
                "    },\n" +
                "    \"cron\": {\n" +
                "        \"jobs\": [\n" +
                "            {\n" +
                "                \"key\": \"sample\",\n" +
                "                \"config\": \"0 * * * * ?\",\n" +
                "                \"url\": \"/services/jobs/sample.netuno\",\n" +
                "                \"params\": {\n" +
                "                    \"secret\": \"Cron$ample713\",\n" +
                "                    \"id\": 1\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        Values config = new Values();
        config.set("_app:config", configRaw.fromJSON(configRaw.toJSON()));
        String json = ((Values)config.get("_app:config")).toJSON();
        Values configMerge = new Values(configRaw);
        assertTrue(configMerge.getString("name").equals(configRaw.getString("name")));
        System.out.println(json);
    }

    @Test
    public void toJSON() {
        Values content = new Values();
        content.add("<span style=\"display: none\">ação</span>3");
        System.out.println(content.toJSON());
    }

    @Test
    public void replaceAllFromJavaScript() {
        Values list = new Values().add("a").add("b").add("c");
        GraalRunner graalRunner = new GraalRunner("js");
        graalRunner.set("js", "list", list);
        graalRunner.eval("js", "list.replaceAll((a) => a + '-ok')");
        assertEquals(list.getString(0), "a-ok");
        assertEquals(list.getString(1), "b-ok");
        assertEquals(list.getString(2), "c-ok");
        list = new Values()
                .add(new Values().set("key", 1))
                .add(new Values().set("key", 2))
                .add(new Values().set("key", 3));
        graalRunner.set("js", "list", list);
        graalRunner.eval("js", "list.replaceAll((a) => a.set('key', a.getInt('key') * 10))");
        assertEquals(list.getValues(0).getInt("key"), 10);
        assertEquals(list.getValues(1).getInt("key"), 20);
        assertEquals(list.getValues(2).getInt("key"), 30);
    }

    @Test
    public void sortFromJavaScript() {
        Values list = new Values().add("c").add("a").add("b");
        GraalRunner graalRunner = new GraalRunner("js");
        graalRunner.set("js", "list", list);
        graalRunner.eval("js", "list.sort((a, b) => a > b ? 1 : a < b ? -1 : 0)");
        assertEquals(list.getString(0), "a");
        assertEquals(list.getString(1), "b");
        assertEquals(list.getString(2), "c");
        list = new Values()
                .add(new Values().set("key", 2))
                .add(new Values().set("key", 3))
                .add(new Values().set("key", 1));
        graalRunner.set("js", "list", list);
        graalRunner.eval("js", "list.sort((a, b) => a.getInt('key') - b.getInt('key'))");
        assertEquals(list.getValues(0).getInt("key"), 1);
        assertEquals(list.getValues(1).getInt("key"), 2);
        assertEquals(list.getValues(2).getInt("key"), 3);
    }
    
    @Test
    public void toFormMap() {
        Values data = new Values()
                .set("simple", 123)
                .set("parentList", new Values()
                        .add("text")
                        .add(new Values()
                                .set("key", "text")
                                .set("childMap", new Values()
                                        .set("childKey", "childValue")
                                ).set("childList", new Values()
                                        .add("childItem")
                                )
                        )
                ).set("parentMap", new Values()
                        .set("key", 123.21)
                        .set("middleList", new Values()
                                .add("text")
                                .add(new Values()
                                        .set("key", "text")
                                        .set("childMap", new Values()
                                                .set("childKey", "childValue")
                                        ).set("childList", new Values()
                                                .add("childItem")
                                        )
                                )
                        ).set("middleMap", new Values()
                                .set("middleKey", "middleMapValue")
                                .set("middleObject", new Values()
                                        .set("key", "text")
                                        .set("childMap", new Values()
                                                .set("childKey", "childValue")
                                        ).set("childList", new Values()
                                                .add("childItem")
                                        )
                                )
                        )
                );
        System.out.println("JSON: "+ data.toJSON());
        data = data.toFormMap();
        for (String key : data.keySet()) {
            System.out.println(key);
        }
        System.out.println("RESULT: "+ data.toString("&", "="));
    }
}
