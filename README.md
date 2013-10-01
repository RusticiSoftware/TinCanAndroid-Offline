TinCanAndroid-Offline
=====================

Offline wrapper for TinCanJava with Android

##Sample Usage

###Setting up the connector
	Map<String,String> lrs = new HashMap<String, String>();

    lrs.put("endpoint", "https://cloud.scorm.com/ScormEngineInterface/TCAPI/APPID/sandbox/");
    lrs.put("auth", "Basic SzVRTlJBNUo1Sjp2UFhLejBkd3pZM0gxQnEzZFIzVTNJc01DejBUN2ZHgsyfhhGDhGGJsd3472364");
    lrs.put("version", "1.0.0");

    Map<String, Object> options = new HashMap<String, Object>();

    List<Object> list = new ArrayList<Object>();
    list.add(lrs);

    options.put("recordStore", list);

    tincan = new RSTinCanOfflineConnector(options, this);

###Sending a Statement
	Map<String, Object> statementOptions = new HashMap<String, Object>();
    statementOptions.put("activityId","http://tincanapi.com/test");

    Verb verb = new Verb();

    try
    {
        verb.setId("http://adlnet.gov/expapi/verbs/experienced");
    }
    catch (URISyntaxException e)
    {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        return;
    }
    verb.setDisplay(new LanguageMap());
    verb.getDisplay().put("en-US", "attempted");

    statementOptions.put("verb",verb);
    statementOptions.put("activityType","http://adlnet.gov/expapi/activities/course");

    Statement statementToSend = createTestStatementWithOptions(statementOptions);

    //add the statement to local db
    tincan.enqueueStatement(statementToSend, new TCOfflineStatementCollection.addStatementInterface() {
        @Override
        public void completionBlock() {
            Log.d("enqueueStatement", "completionBlock");
        }

        @Override
        public void errorBlock(String s) {
            Log.d("enqueueStatement errorBlock :", s);
        }
    });

    List<TCOfflineStructures.LocalStatementsItem> statementArray =  tincan.getCachedStatements();

    Log.d("[statementArray count] :", Integer.toString(statementArray.size()));

    Assert.assertNotNull(statementArray);


    //send the statement to the lrs
    tincan.sendOldestStatementFromQueueWithCompletionBlock(new RSTinCanOfflineConnector.sendOldestInterface() {
        @Override
        public void completionBlock() {
            Log.d("sendOldestStatementFromQueueWithCompletionBlock ", "statements flushed");
        }

        @Override
        public void errorBlock(String s) {
            Log.d("sendOldestStatementFromQueueWithCompletionBlock error :", s);
        }
    });