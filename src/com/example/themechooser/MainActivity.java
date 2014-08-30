package com.example.themechooser;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	SharedPreferences sharedPreferences;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button1;
		Button button2;
		
		// Checking if app is being launched for the first time
		String launchinfo = getSharedPreferences("PrefsFile",MODE_PRIVATE).getString("launch", "not launched");
		
		if (launchinfo.equals("launched")) {			
		
		} 
		else if (launchinfo.equals("not launched")) {
			
			//show copying dialog
			progressDialog();
			
			CopyThemeTask task = new CopyThemeTask();
            task.execute(new String[] { "theme1.apk" });	
            CopyThemeTask task2 = new CopyThemeTask();
            task2.execute(new String[] { "theme2.apk" });	
		}
		
		storeSharedPrefs();
		
		button1 = (Button) findViewById(R.id.theme1);
		button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),ThemeOne.class);
                startActivity(i);
            }
        });
		
		button2 = (Button) findViewById(R.id.theme2);
		button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),ThemeTwo.class);
                startActivity(i);
            }
        });
	}
	
	protected void progressDialog(){
		pd = new ProgressDialog(MainActivity.this);
		pd.setTitle("Copying files...");
		pd.setMessage("Copying necessary files. Dont exit now...");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	protected void dismissDialog(){
		pd.dismiss();
	}
	
	// storing launch info in shared preference
	protected void storeSharedPrefs() {
		SharedPreferences sharedPreferences = getSharedPreferences("PrefsFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //opens the editor
        editor.putString("launch", "launched");
        editor.commit();
    } 
	
	//copying themes from assets to sd card in background
	private class CopyThemeTask extends AsyncTask<String, Void, String> {

		  
	    @Override
	    protected String doInBackground(String... filename) {
	      String response = "";
	      for (String themechoice : filename){
	    	  
	    	  AssetManager assetManager = getApplicationContext().getAssets();

	    	    InputStream in = null;
	    	    OutputStream out = null;
	      try {   
            
	    	 // Creates new directory in sd 
	    	File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/Example/Data");

	    	wallpaperDirectory.mkdirs();
	    	
	    	//copies themes 
	    	in = assetManager.open(themechoice);
	        String newFileName = Environment.getExternalStorageDirectory() + "/Example/Data/" + themechoice;
	        out = new FileOutputStream(newFileName);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;
	        out.flush();
	        out.close();
	        out = null;
	        

        } catch (Exception e) {
        e.printStackTrace();
        }

	      }
	      return response;
	    }
	  

	    @Override
	    protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //dismiss progress dialog after copying finished
            dismissDialog();
            
	    }
	  }
}
