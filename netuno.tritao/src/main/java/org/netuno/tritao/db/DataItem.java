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

package org.netuno.tritao.db;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;

/**
 * Data Item loaded in the database operations and useful with actions.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language=LanguageDoc.PT,
                title = "DataItem",
                introduction = "Transição de dados e de controlo de status das operações das actions.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_dataItem.isUpdate()) {\n" +
                                        "    _log.info('ID que está sendo alterado'+ _dataItem.id);\n" +
                                        "}"
                        )
                }
        )
})
public class DataItem {
    public enum Status {
        None,
        Insert,
        Inserted,
        Update,
        Updated,
        Delete,
        Deleted,
        Exists,
        NotFound,
        Relations,
        Error
    };

    public enum StatusType {
        None,
        Error,
        Ok
    };

    public String id = "";
    public String uid = "";
    public Status status = Status.None;
    public StatusType statusType = StatusType.None;
    public String table = "";
    public String field = "";
    public String errorTitle = "";
    public String errorMessage = "";
    public Values record = new Values();
    public Values oldRecord = new Values();
    public Values values = new Values();
    public Values relationTable = new Values();
    public Values relationItem = new Values();
    public int counter = 0;
    public boolean programmatically = false;

    public boolean firebase = false;
    public Values firebaseValues = new Values();

    public DataItem(Proteu proteu, String id, String uid) {
        this.id = id;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public DataItem setId(String id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public DataItem setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getTable() {
        return table;
    }

    public DataItem setTable(String table) {
        this.table = table;
        return this;
    }

    public String getField() {
        return field;
    }

    public DataItem setField(String field) {
        this.field = field;
        return this;
    }
    
    public boolean isProgrammatically() {
        return programmatically;
    }

    public DataItem setProgrammatically(boolean programmatically) {
        this.programmatically = programmatically;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public DataItem setStatus(Status status) {
        switch (status) {
        case None:
            statusType = StatusType.None;
            break;
        case Inserted:
            statusType = StatusType.Ok;
            break;
        case Insert:
            statusType = StatusType.Ok;
            break;
        case Update:
            statusType = StatusType.Ok;
            break;
        case Updated:
            statusType = StatusType.Ok;
            break;
        case Delete:
            statusType = StatusType.Ok;
            break;
        case Deleted:
            statusType = StatusType.Ok;
            break;
        case Exists:
            statusType = StatusType.Error;
            break;
        case NotFound:
            statusType = StatusType.Error;
            break;
        case Relations:
            statusType = StatusType.Error;
            break;
        case Error:
            statusType = StatusType.Error;
            break;
        default:
            break;
        }
        this.status = status;
        return this;
    }

    public boolean isStatusAsNone() {
        return getStatus() == Status.None;
    }

    public boolean isStatusAsInsert() {
        return getStatus() == Status.Insert;
    }

    public boolean isStatusAsInserted() {
        return getStatus() == Status.Inserted;
    }

    public boolean isStatusAsUpdate() {
        return getStatus() == Status.Update;
    }

    public boolean isStatusAsUpdated() {
        return getStatus() == Status.Updated;
    }

    public boolean isStatusAsDelete() {
        return getStatus() == Status.Delete;
    }

    public boolean isStatusAsDeleted() {
        return getStatus() == Status.Deleted;
    }

    public boolean isStatusAsExists() {
        return getStatus() == Status.Exists;
    }

    public boolean isStatusAsNotFound() {
        return getStatus() == Status.NotFound;
    }

    public boolean isStatusAsRelations() {
        return getStatus() == Status.Relations;
    }

    public boolean isStatusAsError() {
        return getStatus() == Status.Error;
    }

    public DataItem setStatusWithError() {
        this.setStatus(Status.Error);
        return this;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public boolean isStatusTypeAsNone() {
        return getStatusType() == StatusType.None;
    }

    public boolean isStatusTypeAsOk() {
        return getStatusType() == StatusType.Ok;
    }

    public boolean isStatusTypeAsError() {
        return getStatusType() == StatusType.Error;
    }

    public boolean isInsert() {
        return getStatus() == Status.Insert || getStatus() == Status.Inserted;
    }

    public boolean isUpdate() {
        return getStatus() == Status.Update || getStatus() == Status.Updated;
    }

    public boolean isDelete() {
        return getStatus() == Status.Delete || getStatus() == Status.Deleted;
    }

    public boolean isOk() {
        return getStatusType() == StatusType.Ok;
    }

    public boolean isError() {
        return getStatusType() == StatusType.Error;
    }

    public boolean isNone() {
        return getStatus() == Status.None || getStatusType() == StatusType.None;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public DataItem setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
        setStatusWithError();
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DataItem setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        setStatusWithError();
        return this;
    }

    public Values getRecord() {
        return record;
    }

    public void setRecord(Values record) {
        if (this.record != null && !this.record.isEmpty()) {
            this.oldRecord = this.record;
        }
        this.record = record;
    }

    public Values getOldRecord() {
        return this.oldRecord;
    }

    public Values getValues() {
        return values;
    }

    public DataItem setValues(Values values) {
        this.values = values;
        return this;
    }

    public Values getRelationTable() {
        return relationTable;
    }

    public DataItem setRelationTable(Values relationTable) {
        this.relationTable = relationTable;
        return this;
    }

    public Values getRelationItem() {
        return relationItem;
    }

    public DataItem setRelationItem(Values relationItem) {
        this.relationItem = relationItem;
        return this;
    }

    public int getCounter() {
        return counter;
    }

    public DataItem setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    public boolean isFirebase() {
        return firebase;
    }

    public DataItem setFirebase(boolean firebase) {
        this.firebase = firebase;
        return this;
    }

    public Values getFirebaseValues() {
        return firebaseValues;
    }

    public DataItem setFirebaseValues(Values firebaseValues) {
        this.firebaseValues = firebaseValues;
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        this.record = null;
        this.oldRecord = null;
        this.values = null;
        this.relationTable = null;
        this.relationItem = null;
        this.firebaseValues = null;
    }
}
