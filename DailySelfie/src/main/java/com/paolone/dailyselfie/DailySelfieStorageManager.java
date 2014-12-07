package com.paolone.dailyselfie;

import android.content.Context;
    import android.os.Environment;
    import android.util.Log;

    import com.google.gson.Gson;
    import com.google.gson.GsonBuilder;
    import com.google.gson.JsonArray;
    import com.google.gson.reflect.TypeToken;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.OutputStreamWriter;
    import java.io.PrintWriter;
    import java.util.ArrayList;
    import java.util.List;


/*********************************************
 *    Storage Management for Daily Selfie    *
 *********************************************/

public class DailySelfieStorageManager {

    /*****************************************
     *              CONSTANTS                *
     *****************************************/
    // TAG for logging
    private static final String TAG = "Dailiy_Selfie";

    // Default values for storage paths
    private static final String STORAGE_DIR = "DailySelfie";
    private static final String LIST_FILE = "DailiySelfiesJSONList.txt";

    // Constants to define internal or external storage usage
    public static final boolean EXTERNAL_MEMORY = true;
    public static final boolean INTERNAL_MEMORY = false;


    /*****************************************
     *                FIELDS                 *
     *****************************************/

    private final File mStorageDIR;
    private final String mListFileName;
    private final File mListFile;
    private final Context mContext;

    /*****************************************
     *              CONSTRUCTOR              *
     *****************************************/

    public DailySelfieStorageManager(Context context){

        this(STORAGE_DIR, LIST_FILE, EXTERNAL_MEMORY, context);

    }

    public DailySelfieStorageManager(String storageDIR, String listFile, boolean memoryToUse, Context context){

        if (memoryToUse == EXTERNAL_MEMORY  && isExternalStorageWritable()){
            this.mStorageDIR =  new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + storageDIR);
        } else {
            this.mStorageDIR = context.getDir(storageDIR, Context.MODE_PRIVATE);
        }
        this.mListFileName = listFile;
        this.mListFile = new File(mStorageDIR, mListFileName);
        this.mContext = context;

    }

    /*****************************************
     *           EXPOSED METHODS             *
     *****************************************/

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        Log.i(TAG, "DailySelfieStorageManager.isExternalStorageWritable entered");
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            return true;
        }
        return false;
    }

    // Persistance management
    public void saveSelfieList (ArrayList<SelfieItem> list){

        Log.i(TAG, "DailySelfieStorageManager.saveSelfieList entered");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jsArray = new JsonArray();

        if (list != null){
            jsArray = gson.toJsonTree(list).getAsJsonArray();
        }

        JSONObject obj = new JSONObject();

        try {
            obj.put("Name", "paolone");
            obj.put("Author", "paolone");
            obj.put("SelfieList", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            writeFile(obj, mStorageDIR, mListFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<SelfieItem> loadSelfieList () {

        Log.i(TAG, "DailySelfieStorageManager.loadSelfieList entered");

        ArrayList<SelfieItem> list = new ArrayList<SelfieItem>();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = readFile(mListFile);
        } catch (IOException e) {
            Log.i(TAG, "DailySelfieStorageManager.loadSelfieList: readFile trows file not found error");
        }


        String jsonArray = null;
        if (jsonObject != null) {
            jsonArray = null;
            try {
                String name = (String) jsonObject.get("Name");
                String author = (String) jsonObject.get("Author");
                Log.i(TAG, "DailySelfieStorageManager.loadSelfieList decodes object: Author = " + author + ", name = " + name);
                jsonArray = (String) jsonObject.get("SelfieList") ;
                Log.i(TAG, "DailySelfieStorageManager.loadSelfieList decodes object: SelfieList = " + jsonArray.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonArray != null) {

                list = new Gson().fromJson(jsonArray, new TypeToken<List<SelfieItem>>(){}.getType());

            }

        }

        return list;

    }

    /*****************************************
     *           SUPPORT METHODS             *
     *****************************************/


    private void writeFile(JSONObject listToSave, File dir, File file) throws FileNotFoundException{

        Log.i(TAG, "DailySelfieStorageManager.writeFile entered");

        boolean success = true;
        if (!dir.exists()) {
            success = dir.mkdir();
            Log.i(TAG, "DailySelfieStorageManager.getOutputStream dir creation result = " + success);

        }

        if (success) {

            Log.i(TAG, "DailySelfieStorageManager.getOutputStream try to create output stream");

            FileOutputStream fos = new FileOutputStream(file);

            PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(fos)));

            pw.write(listToSave.toString());

            pw.close();

        }
    }

    private JSONObject readFile(File file) throws IOException {

        Log.i(TAG, "DailySelfieStorageManager.readFile entered");

        JSONObject listToFill = new JSONObject();

        if (!file.exists()) return null;

        FileInputStream fis = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        StringBuilder responseStrBuilder = new StringBuilder();

        String mInputStr = br.readLine();

        while (mInputStr != null){
            responseStrBuilder.append(mInputStr.toString());
            mInputStr = br.readLine();
        }

        try {
            listToFill = new JSONObject(responseStrBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        br.close();

        return listToFill;

    }
}
