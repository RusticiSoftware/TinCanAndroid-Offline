/*
    Copyright 2013 Rustici Software

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.rs;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * TCLocalStorageDatabase - Provides access to the local db storage for statement and state storage
 * @author Derek Clark
 * @author Brian Rogers
 * Date: 5/8/13
 */

/**
 * Definitions for the database structures
 */
public final class TCLocalStorageDatabase {

    public static final String AUTHORITY = "com.rs.TCLocalStorageDatabase";

    private TCLocalStorageDatabase() {}

    /**
     * LocalState table
     */
    public static final class LocalState implements BaseColumns {

        public LocalState() {}

        public static final String CONTENT_TYPE = "com.rs.TCLocalStorageDatabase/LocalState";

        public static final String CONTENT_ITEM_TYPE = "com.rs.TCLocalStorageDatabase.item/LocalState";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/localState");

        public static final String DEFAULT_SORT_ORDER = "createDate DESC";

        public static final String STATE_ID = "stateId";

        public static final String STATE_CONTENTS = "stateContents";

        public static final String CREATE_DATE = "createDate";

        public static final String POST_DATE = "postDate";

        public static final String QUERY_STRING = "querystring";

        public static final String ACTIVITY_ID = "activityId";

        public static final String AGENT_JSON = "agentJson";

    }

    /**
     * LocalStatements table
     */
    public static final class LocalStatements implements BaseColumns {


        public LocalStatements() {}

        public static final String CONTENT_TYPE = "com.rs.TCLocalStorageDatabase/LocalStatements";

        public static final String CONTENT_ITEM_TYPE = "com.rs.TCLocalStorageDatabase.item/LocalStatements";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/localStatements");

        public static final String DEFAULT_SORT_ORDER = "createDate DESC";

        public static final String STATEMENT_ID = "statementId";

        public static final String STATEMENT_JSON = "statementJson";

        public static final String CREATE_DATE = "createDate";

        public static final String POSTED_DATE = "postedDate";

        public static final String QUERY_STRING = "querystring";



    }


}
