package com.rs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.rusticisoftware.tincan.*;
import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;



//Test class for Android TinCanJava-Offline
public class TinCanJava_OfflineTests extends Activity {

    private static final int MAX_AVAILABLE = 1;
    RSTinCanOfflineConnector tincan;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button button = (Button) findViewById(R.id.starttests);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                runStatementTests();
            }
        });

        Button button2 = (Button) findViewById(R.id.startstatetests);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                runStateTests();
            }
        });

    }

    void runStatementTests()
    {
        setUp();
        testOfflineStatements();
    }

    void runStateTests()
    {
        setUp();
        testOfflineState();
        testSendLocalState();
    }

    //set up the lrs for adding statements and states
    public void setUp()
    {
        Map<String,String> lrs = new HashMap<String, String>();

        lrs.put("endpoint", "https://cloud.scorm.com/ScormEngineInterface/TCAPI/K5QNRA5J5J/sandbox/");
        lrs.put("auth", "Basic SzVRTlJBNUo1Sjp2UFhLejBkd3pZM0gxQnEzZFIzVTNJc01DejBUN2Z5T0tVdE5TR3lm");
        lrs.put("version", "1.0.0");

        Map<String, Object> options = new HashMap<String, Object>();

        List<Object> list = new ArrayList<Object>();
        list.add(lrs);

        options.put("recordStore", list);

        tincan = new RSTinCanOfflineConnector(options, this);

    }

    //enqueue a statement and then send it to the lrs
    void testOfflineStatements()
    {
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

    }


    //create a test statement for sending
    Statement createTestStatementWithOptions(Map<String,Object> options)
    {
        Agent actor = new Agent();
        actor.setName("Brian Rogers");
        actor.setMbox("mailto:brian@tincanapi.com");

        ActivityDefinition def = new ActivityDefinition();

        def.setName(new LanguageMap());
        def.getName().put("en-US", "http://tincanapi.com/test");

        def.setDescription(new LanguageMap());
        def.getDescription().put("en-US", "Description for test statement");

        try {
            def.setType((String)options.get("activityType"));
        }
        catch (Exception e)
        {
            Log.d("createTestStatementWithOptions setType :", e.toString());
            return null;
        }

        com.rusticisoftware.tincan.Activity activity = new com.rusticisoftware.tincan.Activity();

        try {
            activity.setId((String) options.get("activityId"));
        }
        catch (Exception e)
        {
            Log.d("createTestStatementWithOptions setId :", e.toString());
            return null;
        }

        activity.setDefinition(def);

        Verb verb = (Verb)options.get("verb");

        Statement statementToSend = new Statement(actor, verb, activity);

        statementToSend.setId(UUID.randomUUID());

        return statementToSend;

    }


    //save a test state to the local db
    void testOfflineState()
    {

        Agent actor = new Agent();
        actor.setName("Brian Rogers");
        actor.setMbox("mailto:brian@tincanapi.com");

        Map<String,String> stateContents = new HashMap<String, String>();
        stateContents.put("bookmark", "page 1");

        String stateId = UUID.randomUUID().toString();

        tincan.setStateWithValue(stateContents, stateId, stringByAddingPercentEscapesUsingEncoding("http://tincanapi.com/test"),actor,null,null,new RSTinCanOfflineConnector.setStateInterface() {
            @Override
            public void completionBlock() {
                Log.d("setStateWithValue", "completionBlock");
            }

            @Override
            public void errorBlock(String s) {
                Log.d("setStateWithValue errorBlock :", s);
            }
        });



    }

    //send the local state to the lrs
    void testSendLocalState()
    {

        tincan.sendLocalStateToServerWithCompletionBlock(new RSTinCanOfflineConnector.sendLocalStateInterface() {
            @Override
            public void completionBlock() {
                Log.d("sendLocalStateToServerWithCompletionBlock", "completionBlock");
            }

            @Override
            public void errorBlock(String s) {
                Log.d("sendLocalStateToServerWithCompletionBlock errorBlock :", s);
            }
        });

    }

    //helper encoding function
    public static String stringByAddingPercentEscapesUsingEncoding( String input, String charset ) throws UnsupportedEncodingException {
        byte[] bytes = input.getBytes(charset);
        StringBuilder sb = new StringBuilder(bytes.length);
        for( int i = 0; i < bytes.length; ++i ) {
            int cp = bytes[i] < 0 ? bytes[i] + 256 : bytes[i];
            if( cp <= 0x20 || cp >= 0x7F || (
                    cp == 0x22 || cp == 0x25 || cp == 0x3C ||
                            cp == 0x3E || cp == 0x20 || cp == 0x5B ||
                            cp == 0x5C || cp == 0x5D || cp == 0x5E ||
                            cp == 0x60 || cp == 0x7b || cp == 0x7c ||
                            cp == 0x7d
            )) {
                sb.append( String.format( "%%%02X", cp ) );
            }
            else {
                sb.append( (char)cp );
            }
        }
        return sb.toString();
    }

    public static String stringByAddingPercentEscapesUsingEncoding( String input ) {
        try {
            return stringByAddingPercentEscapesUsingEncoding(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Java platforms are required to support UTF-8");
            // will never happen
        }
    }


}

