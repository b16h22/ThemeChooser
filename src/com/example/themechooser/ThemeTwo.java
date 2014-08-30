package com.example.themechooser;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class ThemeTwo extends Activity {
	
	Button theme2;
	private ProgressDialog pd;
	String appliedinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme_two);
appliedinfo = getSharedPreferences("PrefsFile",MODE_PRIVATE).getString("appliedinfo", "not applied");
		
		theme2 = (Button) findViewById(R.id.theme2);		
		theme2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (appliedinfo.equals("applied")) {
            		
                	showDialog();
        		} 
        		else if (appliedinfo.equals("not applied")) {
        			
                	InstallThemeTask task = new InstallThemeTask();
                    task.execute(new String[] { "theme2.apk" });
                    progressDialog();
                    
        		}
            }
        });
	}
	
	protected void showDialog(){
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ThemeTwo.this);		 
        alertDialog.setTitle("Apply");
        alertDialog.setMessage("You are going to apply Theme 2. Your device will reboot after applying the theme");
 
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	InstallThemeTask task = new InstallThemeTask();
                task.execute(new String[] { "theme2.apk" });
                progressDialog();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            dialog.cancel();
            }
        });

        alertDialog.show();
	}
	
	protected void storeSharedPrefs() {
		SharedPreferences sharedPreferences = getSharedPreferences("PrefsFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //opens the editor
        editor.putString("appliedinfo", "applied");
        editor.commit();
    } 
	
	protected void goToThemeChooser(){
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ThemeTwo.this);		 
        alertDialog.setTitle("Select theme");
        alertDialog.setMessage("You are going to open Theme chooser. Please select Theme 2 from there and reboot");
 
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	storeSharedPrefs();
            	
            	Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(new ComponentName("org.cyanogenmod.theme.chooser", "org.cyanogenmod.theme.chooser.ChooserActivity"));
				startActivity(intent);
            }
        });

        alertDialog.show();
	}
	
	protected void progressDialog(){
		pd = new ProgressDialog(ThemeTwo.this);
		pd.setTitle("Applying...");
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}
	
	protected void dismissDialog(){
		pd.dismiss();
	}
	
	  private class InstallThemeTask extends AsyncTask<String, Void, String> {

		  		  
		    @Override
		    protected String doInBackground(String... theme) {
		      String response = "";
		      for (String themechoice : theme){
		      try {   
                  String command;
                  command = "pm install -r " +Environment.getExternalStorageDirectory() + "/Example/Data/" + themechoice;
                  Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                  proc.waitFor();
              } catch (Exception e) {
              e.printStackTrace();
              }

		      }
		      return response;
		    }
		  

		    @Override
		    protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	            
	            	if (appliedinfo.equals("applied")) {
	            		
	            		try {
						Runtime.getRuntime().exec(new String[]{"su","-c","reboot now"});
	            		} catch (IOException e) {

	    					e.printStackTrace();
	    				}
	        		} 
	        		else if (appliedinfo.equals("not applied")) {
	        			dismissDialog();
	        			goToThemeChooser();	
	        		}

	            
		    }
		  }
	}

