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

package org.netuno.tritao;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Active;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ComponentData.Type;
import org.netuno.tritao.com.DateTime;
import org.netuno.tritao.com.Id;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;
import org.netuno.tritao.util.Translation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Search Form Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Search {

	private static List<String> downloadTokens = new ArrayList<String>();
	
    @SuppressWarnings("unchecked")
	public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        Values rowTritaoTable = null;
        String tableId = proteu.getRequestAll().getString("netuno_table_id");

        if (tableId.isEmpty() && proteu.getRequestAll().hasKey("netuno_table_name")) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", proteu.getRequestAll().getString("netuno_table_name"));
            if (rsTables.size() > 0) {
                rowTritaoTable = rsTables.get(0);
                tableId = rowTritaoTable.getString("id");
            }
        } else if (tableId.isEmpty()) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_table_uid"));
            if (rsTables.size() > 0) {
                rowTritaoTable = rsTables.get(0);
                tableId = rowTritaoTable.getString("id");
            } else {
                return;
            }
        } else {
            List<Values> rsTable = Config.getDBBuilder(proteu).selectTable(tableId);
            if (rsTable.size() == 1) {
                rowTritaoTable = rsTable.get(0);
            }
        }

        if (!Rule.getRule(proteu, hili, tableId).haveAccess()) {
            return;
        }
        proteu.setResponseHeaderNoCache();
        if (rowTritaoTable != null) {

            if (proteu.getRequestAll().getString("netuno_action").equalsIgnoreCase("uid")) {
                proteu.outputJSON(
                        new Values().set("uid", rowTritaoTable.getString("uid"))
                );
                return;
            }

            proteu.getRequestAll().set("netuno_table_id", tableId);

            if (proteu.getRequestAll().hasKey("netuno_relation_table_uid") && proteu.getRequestAll().hasKey("netuno_relation_item_uid")
                    && !proteu.getRequestAll().getString("netuno_relation_table_uid").isEmpty() && !proteu.getRequestAll().getString("netuno_relation_item_uid").isEmpty()) {
                List<Values> rsRelationTables = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_relation_table_uid"));
                if (rsRelationTables.size() == 1) {
                    Values rowRelationTable = rsRelationTables.get(0);
                    proteu.getConfig().set("netuno_relation_table", rowRelationTable);
                    proteu.getConfig().set("_relation_table", rowRelationTable);
                    proteu.getConfig().set("netuno_relation_table_name", rowRelationTable.getString("name"));
                    proteu.getConfig().set("_relation_table_name", rowRelationTable.getString("name"));
                    String relationTableName = rowRelationTable.getString("name");
                    Values item = Config.getDBBuilder(proteu).getItemByUId(relationTableName, proteu.getRequestAll().getString("netuno_relation_item_uid"));
                    if (item != null) {
                        proteu.getConfig().set("netuno_relation_item", item);
                        proteu.getConfig().set("_relation_item", item);
                    }
                } else {
                    return;
                }
            }

            String tableName = rowTritaoTable.getString("name");
            proteu.getConfig().set("netuno_table_name", tableName);
            proteu.getConfig().set("_table_name", tableName);
            proteu.getConfig().set("netuno_form_name", tableName);
            proteu.getConfig().set("_form_name", tableName);
            proteu.getConfig().set("netuno_table_type", "table");
            proteu.getConfig().set("_table_type", "table");
            proteu.getConfig().set("netuno_form_mode", "search");
            proteu.getConfig().set("_form_mode", "search");
            boolean showId = rowTritaoTable.getBoolean("show_id");
            if (proteu.isAcceptJSON()) {
                proteu.setResponseHeader(Proteu.ContentType.JSON);
                
                List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));
                org.netuno.tritao.db.DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch(0, 0, "");
                JSONArray jsonArray = new JSONArray();
                List<Values> rsSearch = dataSelected.getResults();
                for (int i = 0; i < rsSearch.size(); i++) {
                    Values rowSearch = rsSearch.get(i);
                    JSONObject jsonRow = new JSONObject();
                    if (showId) {
                        jsonRow.put("id", rowSearch.getInt(tableName + "_id"));
                    }
                    jsonRow.put("uid", rowSearch.getInt(tableName + "_uid"));
                    for (int j = 0; j < rsDesignXY.size(); j++) {
                        Values rowTritaoDesignXY = rsDesignXY.get(j);
                        if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                            continue;
                        }
                        Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                        com.setProteu(proteu);
                        com.setDesignData(rowTritaoDesignXY);
                        com.setTableData(rowTritaoTable);
                        com.setMode(Component.Mode.View);
                        if (!com.isRenderView()) {
                            continue;
                        }
                        com.setValues(tableName + "_", rowSearch);
                        for (ComponentData data : com.getDataStructure()) {
                            if (data.getType() == Type.Boolean) {
                                jsonRow.put(data.getName(), Boolean.parseBoolean(data.getValue()));
                            } else if (data.getType() == Type.Integer) {
                                jsonRow.put(data.getName(), Integer.parseInt(data.getValue()));
                            } else if (data.getType() == Type.BigInteger) {
                                jsonRow.put(data.getName(), Long.parseLong(data.getValue()));
                            } else if (data.getType() == Type.Float) {
                                jsonRow.put(data.getName(), Float.parseFloat(data.getValue()));
                            } else if (data.getType() == Type.Decimal) {
                                jsonRow.put(data.getName(), Double.parseDouble(data.getValue()));
                            } else {
                                jsonRow.put(data.getName(), data.getValue());
                            }
                        }
                    }
                    jsonArray.put(jsonRow);
                }
                proteu.getOutput().print(jsonArray.toString());
                return;
            }
            if (proteu.getRequestAll().getString("netuno_action").equals("bulk-delete")
                    || proteu.getRequestAll().getString("netuno_action").equals("bulk-inactive")) {
                proteu.setResponseHeader(Proteu.ContentType.JSON);
                List<DataItem> fails = new ArrayList<>();
                proteu.getRequestAll().getValues("netuno_items_uids").list(String.class).forEach((uid) -> {
                    Values item = Config.getDBBuilder(proteu).getItemByUId(tableName, uid);
                    DataItem dataItem = null;
                    if (proteu.getRequestAll().getString("netuno_action").equals("bulk-delete")) {
                        dataItem = Config.getDBBuilder(proteu).delete(tableName, item.getString("id"));
                    } else if (proteu.getRequestAll().getString("netuno_action").equals("bulk-inactive")) {
                        dataItem = Config.getDBBuilder(proteu).update(tableName, item.getString("id"), new Values().set("active", false));
                    }
                    if (dataItem.isStatusTypeAsError()) {
                        fails.add(dataItem);
                    }
                });
                proteu.outputJSON(new Values().set("result", fails.size() == 0).set("fails", fails.size()));
                return;
            }
            if (proteu.getRequestAll().getString("netuno_action").equals("datasource")) {
                proteu.setResponseHeader(Proteu.ContentType.JSON);
                List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));
                String searchOrderBy = "";
                if (proteu.getRequestAll().getInt("order[0][column]") == 0) {
                    if (proteu.getRequestAll().getString("order[0][dir]").equals("asc")) {
                        searchOrderBy = searchOrderBy.concat("[").concat(tableName).concat("].[").concat("id").concat("] ASC, ");
                    } else if (proteu.getRequestAll().getString("order[0][dir]").equals("desc")) {
                        searchOrderBy = searchOrderBy.concat("[").concat(tableName).concat("].[").concat("id").concat("] DESC, ");
                    }
                }
                int searchColumnCount = 1;
                for (int i = 0; i < rsDesignXY.size(); i++) {
                    Values rowTritaoDesignXY = rsDesignXY.get(i);
                    if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                        continue;
                    }
                    if (!rowTritaoDesignXY.getBoolean("whenresult")) {
                        continue;
                    }
                    org.netuno.tritao.com.Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                    com.setProteu(proteu);
                    com.setDesignData(rowTritaoDesignXY);
                    com.setTableData(rowTritaoTable);
                    com.setMode(Component.Mode.SearchResult);
                    if (!com.isRenderSearchResults()) {
                        continue;
                    }
                    if (proteu.getRequestAll().getBoolean("columns["+ searchColumnCount +"][orderable]")) {
                        if (proteu.getRequestAll().getInt("order[0][column]") == searchColumnCount) {
                            for (ComponentData data : com.getDataStructure()) {
                                if (proteu.getRequestAll().getString("order[0][dir]").equals("asc")) {
                                    searchOrderBy = searchOrderBy.concat("[").concat(tableName).concat("].[").concat(data.getName()).concat("] ASC, ");
                                } else if (proteu.getRequestAll().getString("order[0][dir]").equals("desc")) {
                                    searchOrderBy = searchOrderBy.concat("[").concat(tableName).concat("].[").concat(data.getName()).concat("] DESC, ");
                                }
                            }
                        }
                    }
                    searchColumnCount++;
                }
                if (!searchOrderBy.isEmpty()) {
                    searchOrderBy = searchOrderBy.substring(0, searchOrderBy.length() - 2);
                }
                org.netuno.tritao.db.DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch(proteu.getRequestAll().getInt("start") ,proteu.getRequestAll().getInt("length"), searchOrderBy);
                Values jsonObject = new Values();
                jsonObject.set("draw", proteu.getRequestAll().getInt("draw"));
                jsonObject.set("recordsTotal", Integer.toString(dataSelected.getFullTotal()));
                jsonObject.set("recordsFiltered", Integer.toString(dataSelected.getTotal()));
                //jsonObject.set("iTotalDisplayRecords", Integer.toString(dataSelected.getResults().size()));
                Values jsonArray = new Values();
                List<Values> rsSearch = dataSelected.getResults();
                for (int i = 0; i < rsSearch.size(); i++) {
                    Values rowSearch = rsSearch.get(i);
                    Values jsonRow = new Values();
                    String spanUId = "<span style=\"display: none\">"+ rowSearch.getString(tableName + "_uid") +"</span>";
                    if (showId) {
                        jsonRow.add(spanUId + rowSearch.getString(tableName + "_id"));
                        spanUId = null;
                    }
                    for (int j = 0; j < rsDesignXY.size(); j++) {
                        Values rowTritaoDesignXY = rsDesignXY.get(j);
                        if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                            continue;
                        }
                        if (!rowTritaoDesignXY.getBoolean("whenresult")) {
                            continue;
                        }
                        Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                        com.setProteu(proteu);
                        com.setDesignData(rowTritaoDesignXY);
                        com.setTableData(rowTritaoTable);
                        com.setMode(Component.Mode.SearchResult);
                        if (!com.isRenderSearchResults()) {
                            continue;
                        }
                        com.setValues(tableName + "_", rowSearch);
                        if (spanUId != null) {
                            jsonRow.add(spanUId + com.getHtmlValue());
                        } else {
                            jsonRow.add(com.getHtmlValue());
                        }
                    }
                    jsonArray.add(jsonRow);
                }
                jsonObject.set("data", jsonArray);
                proteu.outputJSON(jsonObject);
                return;
            }
            if (!proteu.getRequestAll().getString("downloadToken").isEmpty()) {
                if (proteu.getRequestAll().getBoolean("downloadStatus")) {
                    if (downloadTokens.contains(proteu.getRequestAll().getString("downloadToken"))) {
                        proteu.getOutput().print("waiting");
                    } else {
                        proteu.getOutput().print("done");
                    }
                    return;
                }
                downloadTokens.add(proteu.getRequestAll().getString("downloadToken"));
            }
            try {
                if (proteu.getRequestAll().getString("netuno_action").equals("export_xls")) {
                    exportXLS(proteu, hili, rowTritaoTable);
                    return;
                }
                if (proteu.getRequestAll().getString("netuno_action").equals("export_xml")) {
                    exportXML(proteu, hili, rowTritaoTable);
                    return;
                }
                if (proteu.getRequestAll().getString("netuno_action").equals("export_json")) {
                    exportJSON(proteu, hili, rowTritaoTable);
                    return;
                }
            } finally {
                downloadTokens.remove(proteu.getRequestAll().getString("downloadToken"));
            }
            boolean controlActive = rowTritaoTable.getBoolean("control_active");
            proteu.getRequestPost().set("report", rowTritaoTable.getBoolean("report") ? "true": "false");

            Rule rule = Rule.getRule(proteu, hili, tableId);

            List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));

            if (rule.getWrite() > Rule.NONE) {
                rowTritaoTable.put("button-new", TemplateBuilder.getOutput(proteu, hili, "search/buttons/new", rowTritaoTable));
            }
            rowTritaoTable.set("title", Translation.formTitle(proteu, hili, rowTritaoTable));
            rowTritaoTable.set("description", Translation.formDescription(proteu, hili, rowTritaoTable));
            TemplateBuilder.output(proteu, hili, "search/head", rowTritaoTable);
            TemplateBuilder.outputApp(proteu, hili, "search/".concat(tableName).concat("_head"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "search/form/head");
            TemplateBuilder.outputApp(proteu, hili, "search/form/".concat(tableName).concat("_head"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "form/head");
            TemplateBuilder.outputApp(proteu, hili, "form/".concat(tableName).concat("_head"), rowTritaoTable);
            int rsCount = 0;
            int y = 1;
            int quebra = 0;
            Values valuesIdAndActive = new Values();
            valuesIdAndActive.set("tdwidth", "50%");
            valuesIdAndActive.set("colwidth12", "6");
            if (showId) {
                TemplateBuilder.output(proteu, hili, "form/component_start", valuesIdAndActive);
                Id comId = new Id(proteu, hili);
                comId.setProteu(proteu);
                comId.setDesignData(valuesIdAndActive);
                comId.setTableData(rowTritaoTable);
                comId.setMode(Component.Mode.SearchForm);
                comId.render();
                TemplateBuilder.output(proteu, hili, "form/component_end", valuesIdAndActive);
            }
            if (controlActive) {
                TemplateBuilder.output(proteu, hili, "form/component_start", valuesIdAndActive);
                Active comActive = new Active(proteu, hili);
                comActive.setProteu(proteu);
                comActive.setDesignData(valuesIdAndActive);
                comActive.setTableData(rowTritaoTable);
                comActive.setMode(Component.Mode.SearchForm);
                comActive.setOn();
                comActive.render();
                TemplateBuilder.output(proteu, hili, "form/component_end", valuesIdAndActive);
            }
            TemplateBuilder.output(proteu, hili, "form/break");
            for (int i = 0; i < rsDesignXY.size(); i++) {
                Values rowTritaoDesignXY = rsDesignXY.get(i);
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                    continue;
                }
                if (!rowTritaoDesignXY.getBoolean("whenfilter")) {
                    continue;
                }
                Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowTritaoDesignXY);
                com.setTableData(rowTritaoTable);
                com.setMode(Component.Mode.SearchForm);
                if (!com.isRenderSearchForm()) {
                    continue;
                }
                if (quebra != 0) {
                    if (rowTritaoDesignXY.getInt("y") >= quebra) {
                        TemplateBuilder.output(proteu, hili, "form/break");
                        quebra = 0;
                    }
                }
                if (rsCount == 0) {
                    y = rowTritaoDesignXY.getInt("y");
                }
                if (y < rowTritaoDesignXY.getInt("y")) {
                    TemplateBuilder.output(proteu, hili, "form/line_break");
                    y = rowTritaoDesignXY.getInt("y");
                }
                if (rowTritaoDesignXY.getInt("rowspan") <= 0) {
                    rowTritaoDesignXY.set("rowspan", "");
                } else {
                    quebra = rowTritaoDesignXY.getInt("rowspan") + rowTritaoDesignXY.getInt("y");
                }
                if (rowTritaoDesignXY.getInt("colspan") <= 0) {
                    rowTritaoDesignXY.set("colspan", "");
                }
                if (rowTritaoDesignXY.getString("tdwidth").equals("")) {
                    rowTritaoDesignXY.set("tdwidth", "");
                }
                if (rowTritaoDesignXY.getString("tdheight").equals("")) {
                    rowTritaoDesignXY.set("tdheight", "");
                }

                TemplateBuilder.output(proteu, hili, "form/component_start", rowTritaoDesignXY);
                com.render();
                TemplateBuilder.output(proteu, hili, "form/component_end", rowTritaoDesignXY);
                rsCount++;
            }
            TemplateBuilder.outputApp(proteu, hili, "form/".concat(tableName).concat("_foot"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "form/foot");
            TemplateBuilder.output(proteu, hili, "search/form/foot");
            TemplateBuilder.outputApp(proteu, hili, "search/form/".concat(tableName).concat("_foot"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "search/buttons/head");
            TemplateBuilder.output(proteu, hili, "search/buttons/search");
            TemplateBuilder.outputApp(proteu, hili, "search/buttons/".concat(tableName).concat("_buttons"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "search/buttons/foot");
            TemplateBuilder.output(proteu, hili, "search/table/head");
            if (showId) {
                Values dataColumnId = new Values();
                dataColumnId.set("name", "id");
                dataColumnId.set("title", "Id");
                TemplateBuilder.output(proteu, hili, "search/table/header", dataColumnId);
            }
            for (int i = 0; i < rsDesignXY.size(); i++) {
                Values rowDesignXY = rsDesignXY.get(i);
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowDesignXY)) {
                    continue;
                }
                if (!rowDesignXY.getBoolean("whenresult")) {
                    continue;
                }
                rowDesignXY.set("title", Translation.formFieldTitle(proteu, hili, rowTritaoTable, rowDesignXY));
                Component com = Config.getNewComponent(proteu, hili, rowDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowDesignXY);
                com.setTableData(rowTritaoTable);
                com.setMode(Component.Mode.SearchResult);
                if (!com.isRenderSearchResults()) {
                    continue;
                }
                TemplateBuilder.output(proteu, hili, "search/table/header", rowDesignXY);
            }
            TemplateBuilder.output(proteu, hili, "search/table/foot", rowTritaoTable);
            TemplateBuilder.outputApp(proteu, hili, "search/".concat(tableName).concat("_foot"), rowTritaoTable);
            TemplateBuilder.output(proteu, hili, "search/foot", rowTritaoTable);
        }
    } 
    
    private static void exportXLS(Proteu proteu, Hili hili, Values rowTritaoTable) throws IOException {
    	proteu.getResponseHeader().set("Content-Disposition", "attachment; filename="+ rowTritaoTable.getString("name") +"_"+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) +".xls");
    	proteu.getResponseHeader().set("Content-Type", "application/octet-stream");
        proteu.setResponseHeaderNoCache();
        boolean exportId = rowTritaoTable.getBoolean("export_id");
        boolean exportUid = rowTritaoTable.getBoolean("export_uid");
        boolean exportLastChange = rowTritaoTable.getBoolean("export_lastchange");
    	Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
    	Row row = null;
    	Cell cell = null;
    	CellStyle cellStyleHeader = wb.createCellStyle();
    	cellStyleHeader.setBorderBottom(BorderStyle.MEDIUM);
    	Font fontHeader = wb.createFont();
    	fontHeader.setFontHeightInPoints((short) 12);
    	fontHeader.setColor((short)0xc);
    	fontHeader.setBold(true);
    	cellStyleHeader.setFont(fontHeader);
    	wb.setSheetName(0, WorkbookUtil.createSafeSheetName(
                Translation.formTitle(proteu, hili, rowTritaoTable)
        ));
    	List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));
    	org.netuno.tritao.db.DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch(0, 0, "");
    	List<Values> rsSearch = dataSelected.getResults();
    	row = sheet.createRow(0);
    	row.setHeight((short)0x249);
        if (exportId) {
            cell = row.createCell(0);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue("Id");
        }
        if (exportUid) {
            cell = row.createCell(0);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue("Uid");
        }
        int cellNum = 1;
    	for (Values rowTritaoDesignXY : rsDesignXY) {
    		if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
            	continue;
            }
            if (!rowTritaoDesignXY.getBoolean("whenexport")) {
                continue;
            }
            cell = row.createCell(cellNum);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue(Translation.formFieldTitle(proteu, hili, rowTritaoTable, rowTritaoDesignXY));
            cellNum++;
	}
    	if (exportLastChange) {
            cell = row.createCell(cellNum);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue("Last Change");
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue("User Id");
            cellNum++;
            cell = row.createCell(cellNum);
            cell.setCellStyle(cellStyleHeader);
            cell.setCellValue("User");
            cellNum++;
    	}
    	int rowNum = 1;
    	for (Values rowSearch : rsSearch) {
            row = sheet.createRow(rowNum);
            if (exportId) {
                cell = row.createCell(0);
                cell.setCellValue(rowSearch.getString(rowTritaoTable.getString("name") + "_id"));
            }
            if (exportUid) {
                cell = row.createCell(0);
                cell.setCellValue(rowSearch.getString(rowTritaoTable.getString("name") + "_uid"));
            }
            cellNum = 1;
            for (Values rowTritaoDesignXY : rsDesignXY) {
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                    continue;
                }
                if (!rowTritaoDesignXY.getBoolean("whenexport")) {
                    continue;
                }
                cell = row.createCell(cellNum);
                Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowTritaoDesignXY);
                com.setTableData(rowTritaoTable);
                com.setMode(Component.Mode.SearchResult);
                com.setValues(rowTritaoTable.getString("name") + "_", rowSearch);
                cell.setCellValue(com.getTextValue());
                cellNum++;
            }
            if (exportLastChange) {
                cell = row.createCell(cellNum);
                cell.setCellValue(DateTime.getDateTimeString(rowSearch.getString(rowTritaoTable.getString("name") +"_lastchange_time")));
                cellNum++;
                cell = row.createCell(cellNum);
                cell.setCellValue(rowSearch.getString(rowTritaoTable.getString("name") +"_lastchange_user_id"));
                cellNum++;
                cell = row.createCell(cellNum);
                cell.setCellValue(rowSearch.getString(rowTritaoTable.getString("name") +"_lastchange_user"));
            }
            rowNum++;
    	}
    	wb.write(proteu.getOutput());
    }
    
    private static void exportXML(Proteu proteu, Hili hili, Values rowTritaoTable) throws IOException, ParserConfigurationException, TransformerException {
    	proteu.setResponseHeaderDownloadFile(rowTritaoTable.getString("name") +"_"+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) +".xml");
    	proteu.setResponseHeaderNoCache();
        boolean exportId = rowTritaoTable.getBoolean("export_id");
        boolean exportUid = rowTritaoTable.getBoolean("export_uid");
        boolean exportLastChange = rowTritaoTable.getBoolean("export_lastchange");
    	String tableName = rowTritaoTable.getString("name");
    	Document xmldoc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Element e = null;
        Element child = null;
        xmldoc = impl.createDocument(null, tableName, null);
        Element root = xmldoc.getDocumentElement();
        root.setAttribute("name", Translation.formTitle(proteu, hili, rowTritaoTable));
    	List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));
    	org.netuno.tritao.db.DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch(0, 0, "");
    	List<Values> rsSearch = dataSelected.getResults();
    	for (int i = 0; i < rsSearch.size(); i++) {
        	Values rowSearch = rsSearch.get(i);
        	e = xmldoc.createElementNS(null, "item");
            if (exportId) {
                child = xmldoc.createElementNS(null, "id");
                child.setTextContent(rowSearch.getString(tableName.concat("_id")));
                e.appendChild(child);
            }
            if (exportUid) {
                child = xmldoc.createElementNS(null, "uid");
                child.setTextContent(rowSearch.getString(tableName.concat("_uid")));
                e.appendChild(child);
            }
        	for (int c = 0; c < rsDesignXY.size(); c++) {
                Values rowTritaoDesignXY = rsDesignXY.get(c);
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                	continue;
                }
                if (!rowTritaoDesignXY.getBoolean("whenexport")) {
                    continue;
                }
                Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowTritaoDesignXY);
                com.setTableData(rowTritaoTable);
                com.setMode(Component.Mode.SearchResult);
                com.setValues(tableName.concat("_"), rowSearch);
                child = xmldoc.createElementNS(null, rowTritaoDesignXY.getString("name"));
                child.setAttribute("name", Translation.formFieldTitle(proteu, hili, rowTritaoTable, rowTritaoDesignXY));
                child.setTextContent(com.getTextValue());
                e.appendChild(child);
    	    }
        	if (exportLastChange) {
	        	child = xmldoc.createElementNS(null, "lastchange_time");                
	            child.setTextContent(DateTime.getDateTimeString(rowSearch.getString(tableName.concat("_lastchange_time"))));
	            e.appendChild(child);
	        	child = xmldoc.createElementNS(null, "lastchange_user_id");                
	            child.setTextContent(rowSearch.getString(tableName.concat("_lastchange_user_id")));
	            e.appendChild(child);
	        	child = xmldoc.createElementNS(null, "lastchange_user");                
	            child.setTextContent(rowSearch.getString(tableName.concat("_lastchange_user")));
	            e.appendChild(child);
        	}
        	root.appendChild(e);
    	}
    	StringWriter stringWriter = new StringWriter();
    	DOMSource domSource = new DOMSource(xmldoc);
        StreamResult streamResult = new StreamResult(stringWriter);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.transform(domSource, streamResult); 
        proteu.getOutput().print(stringWriter.toString());
    }
    
    private static void exportJSON(Proteu proteu, Hili hili, Values rowTritaoTable) throws IOException, ParserConfigurationException, TransformerException {
    	proteu.setResponseHeaderDownloadFile(rowTritaoTable.getString("name") +"_"+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) +".json");
    	proteu.setResponseHeaderNoCache();
        boolean exportId = rowTritaoTable.getBoolean("export_id");
        boolean exportUid = rowTritaoTable.getBoolean("export_uid");
        boolean exportLastChange = rowTritaoTable.getBoolean("export_lastchange");
    	String tableName = rowTritaoTable.getString("name");
    	Values root = new Values();
    	root.set("name", tableName);
    	List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTritaoTable.getString("id"));
    	org.netuno.tritao.db.DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch(0, 0, "");
    	List<Values> rsSearch = dataSelected.getResults();
    	Values list = new Values();
    	for (int i = 0; i < rsSearch.size(); i++) {
        	Values rowSearch = rsSearch.get(i);
        	Values item = new Values();
            if (exportId) {
            	item.set("id", rowSearch.getString(tableName.concat("_id")));
            }
            if (exportUid) {
            	item.set("uid", rowSearch.getString(tableName.concat("_uid")));
            }
        	for (int c = 0; c < rsDesignXY.size(); c++) {
                Values rowTritaoDesignXY = rsDesignXY.get(c);
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                	continue;
                }
                if (!rowTritaoDesignXY.getBoolean("whenexport")) {
                    continue;
                }
                Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowTritaoDesignXY);
                com.setTableData(rowTritaoTable);
                com.setMode(Component.Mode.SearchResult);
                com.setValues(tableName.concat("_"), rowSearch);
                item.set(rowTritaoDesignXY.getString("name"), com.getTextValue());
    	    }
        	if (exportLastChange) {
            	item.set("lastchange_time", DateTime.getDateTimeString(rowSearch.getString(tableName.concat("_lastchange_time"))));
            	item.set("lastchange_user_id", rowSearch.getString(tableName.concat("_lastchange_user_id")));
            	item.set("lastchange_user", rowSearch.getString(tableName.concat("_lastchange_user")));
        	}
        	list.add(item);
    	}
    	root.set("rows", list);
    	proteu.getOutput().print(root.toJSON(2));
    }
}
