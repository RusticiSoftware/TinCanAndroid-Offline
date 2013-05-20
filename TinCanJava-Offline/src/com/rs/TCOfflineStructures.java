package com.rs;

import java.io.Serializable;

//convenience structures for the database objects
public class TCOfflineStructures {

    public static class LocalStateItem implements Serializable {
        int  id;
        String stateId;
        String stateContents;
        long createDate;
        long postDate;
        String querystring;
        String activityId;
        String agentJson;
    }

    public static class LocalStatementsItem implements Serializable {
        int  id;
        String statementId;
        String statementJson;
        long createDate;
        long postedDate;
        String querystring;
    }

}
