
package ve.ula.edukt_mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import ve.ula.edukt_mobile.adapter.FeedListAdapter;
import ve.ula.edukt_mobile.app.AppController;
import ve.ula.edukt_mobile.data.FeedItem;
import ve.ula.edukt_mobile.data.Universidad;
import ve.ula.edukt_mobile.library.DatabaseHandler;
import ve.ula.edukt_mobile.library.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {
	TextView btnRegister;
	TextView btnLinkToLogin;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;
	EditText inputCedula;
	Spinner inputSpinner;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";


	private static final String TAG = MainActivity.class.getSimpleName();
	private ListView listView;
	private FeedListAdapter listAdapter;
	private List<FeedItem> feedItems;
	private String URL_FEED;
	private Universidad universidad_selected;
    //For handle the circle progress bar showing
    private CircleProgressBar circle_progress_bar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

        //Add to prevent the emulator localhost main threat error
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

		//Set the title toolbar
		TextView titletoolbar = (TextView) findViewById(R.id.titletoolbar);
		titletoolbar.setText(getString(R.string.login_crear));

		// Set a toolbar which will replace the action bar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//Hide actionbar title (substituted by toolbar)
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		inputCedula = (EditText) findViewById(R.id.registerCedula);
		btnRegister = (TextView) findViewById(R.id.btnRegister);
		//btnLinkToLogin = (TextView) findViewById(R.id.btnLinkToLoginScreen);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		inputSpinner = (Spinner) findViewById(R.id.registerUniversidad);
		//Obtengo las universidades de la BD
        //Define the progress bar
        circle_progress_bar = (CircleProgressBar) findViewById(R.id.circle_progress_bar);
        circle_progress_bar.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        circle_progress_bar.setVisibility(View.VISIBLE);
		//Url to get json response
		URL_FEED =   getString(R.string.url_json_getuniversidades);
		// making fresh volley request and getting json
		JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
				URL_FEED, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				VolleyLog.d(TAG, "Response: " + response.toString());
				if (response != null) {
                    circle_progress_bar.setVisibility(View.GONE);
                    ((LinearLayout) findViewById(R.id.linear_base)).setVisibility(View.VISIBLE);
					parseJsonFeed(response, inputSpinner);

				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
                circle_progress_bar.setVisibility(View.GONE);
			}
		});

		// Adding request to volley request queue
		AppController.getInstance().addToRequestQueue(jsonReq);

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String cedula = inputCedula.getText().toString();
				//String spinner = Long.toString(inputSpinner.getSelectedItemId());
				String spinner = Integer.toString(universidad_selected.getId());
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(name, email, password, cedula, spinner);
				
				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						registerErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully registred
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Toast.makeText(getApplicationContext(), getString(R.string.register_user_success), Toast.LENGTH_LONG).show();
							startActivity(dashboard);
							// Close Registration Screen
							finish();
						}else{
							// Error in registration
                            Toast.makeText(getApplicationContext(), getString(R.string.register_user_error), Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		// Link to Login Screen
		/*btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global, menu);
		return true;
	}



	/**
	 * Parsing json reponse and passing the data to feed view list adapter
	 * */
	private void parseJsonFeed(JSONObject response, Spinner inputSpinner) {
		try {
			JSONArray feedArray = response.getJSONArray("feed");
			List<Universidad> list = new ArrayList<Universidad>();
			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject feedObj = (JSONObject) feedArray.get(i);
				Universidad universidad = new Universidad(feedObj.getInt("id"), feedObj.getString("nombre"));
				list.add(universidad);
			}

			ArrayAdapter<Universidad> dataAdapter = new ArrayAdapter<Universidad>(this,
					R.layout.spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//on item select listener
			inputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					universidad_selected = (Universidad) parent.getItemAtPosition(pos);

				}

				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			inputSpinner.setPrompt(getString(R.string.spinner_prompt));
			inputSpinner.setAdapter(dataAdapter);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
