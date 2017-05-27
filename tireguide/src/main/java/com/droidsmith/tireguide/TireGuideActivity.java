package com.droidsmith.tireguide;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.droidsmith.tireguide.calcengine.Calculator;

import java.text.DecimalFormat;
import java.util.List;

public class TireGuideActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	double totalWeight;
	double frontLoadWeight;
	double frontLoadPercent;
	double rearLoadWeight;
	double rearLoadPercent;
	TirePressureDataBase tirePressureDataBase;
	private EditText profileName;
	private EditText bodyWeight;
	private EditText bikeWeight;
	private EditText frontLoad;
	private EditText rearLoad;
	private Spinner riderType;
	private Spinner frontWidth;
	private Spinner rearWidth;
	private Spinner frontLoadUnits;
	private Spinner rearLoadUnits;
	private TextView totalWeightLabel;
	private TextView frontLoadWeightLabel;
	private TextView rearLoadWeightLabel;
	private TextView frontLoadPercentLabel;
	private TextView rearLoadPercentLabel;
	private TextView frontTireLabel;
	private TextView rearTireLabel;
	private double bodyWeightAmount;
	private double bikeWeightAmount;
	private boolean itemSelectedFromProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tire_guide);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		tirePressureDataBase = new TirePressureDataBase(this);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
																 drawer,
																 toolbar,
																 R.string.navigation_drawer_open,
																 R.string.navigation_drawer_close);
		if (drawer != null) {
			drawer.addDrawerListener(toggle);
			toggle.syncState();
		}

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			navigationView.setNavigationItemSelectedListener(this);
		}
		profileName = (EditText) findViewById(R.id.profileName);
		bodyWeight = (EditText) findViewById(R.id.bodyWeight);
		bikeWeight = (EditText) findViewById(R.id.bikeWeight);
		frontLoad = (EditText) findViewById(R.id.frontLoad);
		rearLoad = (EditText) findViewById(R.id.rearLoad);
		riderType = (Spinner) findViewById(R.id.riderType);
		frontWidth = (Spinner) findViewById(R.id.frontWidth);
		rearWidth = (Spinner) findViewById(R.id.rearWidth);
		frontLoadUnits = (Spinner) findViewById(R.id.frontLoadUnits);
		rearLoadUnits = (Spinner) findViewById(R.id.rearLoadUnits);
		totalWeightLabel = (TextView) findViewById(R.id.totalWeight);
		frontLoadWeightLabel = (TextView) findViewById(R.id.frontLoadWeight);
		rearLoadWeightLabel = (TextView) findViewById(R.id.rearLoadWeight);
		frontLoadPercentLabel = (TextView) findViewById(R.id.frontLoadPercent);
		rearLoadPercentLabel = (TextView) findViewById(R.id.rearLoadPercent);
		frontTireLabel = (TextView) findViewById(R.id.frontTire);
		rearTireLabel = (TextView) findViewById(R.id.rearTire);
		FloatingActionButton addAction = (FloatingActionButton) findViewById(R.id.fab);
		if (addAction != null) {
			addAction.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					hideKeyboard(TireGuideActivity.this);
					onAddProfile(view);
				}
			});
		}

		profileName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					bodyWeight.requestFocus();
					handled = true;
				}

				return handled;
			}
		});

		bodyWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					bikeWeight.requestFocus();
					handled = true;
				}

				return handled;
			}
		});

		bikeWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					frontLoad.requestFocus();
					handled = true;
				}

				return handled;
			}
		});

		frontLoad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					rearLoad.requestFocus();
					handled = true;
				}

				return handled;
			}
		});

		rearLoad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_GO) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(),
																												  InputMethodManager.HIDE_NOT_ALWAYS);
					onCalculateTirePressure(view);
					handled = true;
				}

				return handled;
			}
		});

		ArrayAdapter<CharSequence> riderTypeAdapter =
				ArrayAdapter.createFromResource(this, R.array.rider_type_array, R.layout.spinner_item);
		riderTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		riderType.setAdapter(riderTypeAdapter);

		ArrayAdapter<CharSequence> frontWidthAdapter =
				ArrayAdapter.createFromResource(this, R.array.width_array, R.layout.spinner_item);
		frontWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		frontWidth.setAdapter(frontWidthAdapter);

		ArrayAdapter<CharSequence> rearWidthAdapter =
				ArrayAdapter.createFromResource(this, R.array.width_array, R.layout.spinner_item);
		rearWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		rearWidth.setAdapter(rearWidthAdapter);

		ArrayAdapter<CharSequence> frontLoadAdapter =
				ArrayAdapter.createFromResource(this, R.array.load_array, R.layout.spinner_item);
		frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		frontLoadUnits.setAdapter(frontLoadAdapter);

		ArrayAdapter<CharSequence> rearLoadAdapter =
				ArrayAdapter.createFromResource(this, R.array.load_array, R.layout.spinner_item);
		rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		rearLoadUnits.setAdapter(rearLoadAdapter);

		riderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (!itemSelectedFromProfile) {
					final String selectedItem = (String) parent.getSelectedItem();
					if (TextUtils.equals(Constants.RACER, selectedItem)) {
						frontLoad.setText(Constants.RACER_FRONT);
						frontLoadUnits.setSelection(0, Boolean.TRUE);
						rearLoad.setText(Constants.RACER_REAR);
						rearLoadUnits.setSelection(0, Boolean.TRUE);
					} else if (TextUtils.equals(Constants.SPORT, selectedItem)) {
						frontLoad.setText(Constants.SPORT_FRONT);
						frontLoadUnits.setSelection(0, Boolean.TRUE);
						rearLoad.setText(Constants.SPORT_REAR);
						rearLoadUnits.setSelection(0, Boolean.TRUE);
					} else {
						frontLoad.setText(Constants.CASUAL_FRONT);
						frontLoadUnits.setSelection(0, Boolean.TRUE);
						rearLoad.setText(Constants.CASUAL_REAR);
						rearLoadUnits.setSelection(0, Boolean.TRUE);
					}
				}

				itemSelectedFromProfile = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		getProfile();
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (frontLoadPercent > 0) {
			frontLoad.setText(fmt(frontLoadPercent));
		}

		if (rearLoadPercent > 0) {
			rearLoad.setText(fmt(rearLoadPercent));
		}
	}

	private String fmt(double d) {
		if (d == (long) d) {
			return Long.toString((long) d);
		} else {
			return new DecimalFormat("#.#").format(d);
		}
	}

	/**
	 * Retrieves the profile from the database and displays the values.
	 */
	void getProfile() {
		final Cursor profile = tirePressureDataBase.getProfiles();
		if (profile != null) {
			profile.moveToFirst();
			while (!profile.isAfterLast()) {
				itemSelectedFromProfile = true;
				profileName.setText(profile.getString(ProfileColumns.Profiles.PROFILE_NAME));
				bodyWeightAmount = profile.getDouble(ProfileColumns.Profiles.BODY_WEIGHT);
				bodyWeight.setText(fmt(bodyWeightAmount));

				bikeWeightAmount = profile.getDouble(ProfileColumns.Profiles.BIKE_WEIGHT);
				bikeWeight.setText(fmt(bikeWeightAmount));

				String riderTypeString = profile.getString(ProfileColumns.Profiles.RIDER_TYPE);
				if (TextUtils.equals(Constants.RACER, riderTypeString)) {
					riderType.setSelection(0);
				} else if (TextUtils.equals(Constants.SPORT, riderTypeString)) {
					riderType.setSelection(1);
				} else if (TextUtils.equals(Constants.CASUAL, riderTypeString)) {
					riderType.setSelection(2);
				} else {
					riderType.setSelection(2); // Default to Casual
				}

				String frontTireWidthString = profile.getString(ProfileColumns.Profiles.FRONT_TIRE_WIDTH);
				// There has to be a better way than checking every value
				if (TextUtils.equals("20", frontTireWidthString)) {
					frontWidth.setSelection(0);
				} else if (TextUtils.equals("21", frontTireWidthString)) {
					frontWidth.setSelection(1);
				} else if (TextUtils.equals("22", frontTireWidthString)) {
					frontWidth.setSelection(2);
				} else if (TextUtils.equals("23", frontTireWidthString)) {
					frontWidth.setSelection(3);
				} else if (TextUtils.equals("24", frontTireWidthString)) {
					frontWidth.setSelection(4);
				} else if (TextUtils.equals("25", frontTireWidthString)) {
					frontWidth.setSelection(5);
				} else if (TextUtils.equals("26", frontTireWidthString)) {
					frontWidth.setSelection(6);
				} else if (TextUtils.equals("27", frontTireWidthString)) {
					frontWidth.setSelection(7);
				} else if (TextUtils.equals("28", frontTireWidthString)) {
					frontWidth.setSelection(8);
				} else {
					frontWidth.setSelection(5); //Default to 25mm
				}

				String rearTireWidthString = profile.getString(ProfileColumns.Profiles.REAR_TIRE_WIDTH);
				if (TextUtils.equals("20", rearTireWidthString)) {
					rearWidth.setSelection(0);
				} else if (TextUtils.equals("21", frontTireWidthString)) {
					rearWidth.setSelection(1);
				} else if (TextUtils.equals("22", frontTireWidthString)) {
					rearWidth.setSelection(2);
				} else if (TextUtils.equals("23", frontTireWidthString)) {
					rearWidth.setSelection(3);
				} else if (TextUtils.equals("24", frontTireWidthString)) {
					rearWidth.setSelection(4);
				} else if (TextUtils.equals("25", frontTireWidthString)) {
					rearWidth.setSelection(5);
				} else if (TextUtils.equals("26", frontTireWidthString)) {
					rearWidth.setSelection(6);
				} else if (TextUtils.equals("27", frontTireWidthString)) {
					rearWidth.setSelection(7);
				} else {
					rearWidth.setSelection(8);
				}

				frontLoadPercent = profile.getDouble(ProfileColumns.Profiles.FRONT_LOAD_PERCENT);
				frontLoad.setText(fmt(frontLoadPercent));

				rearLoadPercent = profile.getDouble(ProfileColumns.Profiles.REAR_LOAD_PERCENT);
				rearLoad.setText(fmt(rearLoadPercent));
				profile.moveToNext();
			}
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tire_guide, menu);
		return true;
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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_recents) {

		} else if (id == R.id.nav_add) {
			final ViewGroup group = (ViewGroup) this.findViewById(android.R.id.content);
			if (group != null) {
				final ViewGroup viewGroup = (ViewGroup) group.getChildAt(0);
				onAddProfile(viewGroup);
			}
		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_help) {
			MimeTypeMap myMime = MimeTypeMap.getSingleton();
			String mimeType = myMime.getMimeTypeFromExtension(Constants.PDF);
			Uri uri = Uri.parse(Constants.TIRE_INFLATION_PDF);

			final Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, mimeType);
			PackageManager packageManager = getPackageManager();
			List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
			if (!(activities.isEmpty())) {
				Intent chooser = Intent.createChooser(intent, "Choose a PDF Viewer");
				startActivity(chooser);
			} else {
				//If no internal viewer is present, then allow Google Docs Viewer to view the PDF.
				intent.setData(Uri.parse(
						"http://docs.google.com/gview?embedded=true&url=" + Constants.TIRE_INFLATION_PDF));
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this, "No Application Available to View PDF files", Toast.LENGTH_SHORT).show();
				}
			}
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer != null) {
			drawer.closeDrawer(GravityCompat.START);
		}

		return true;
	}

	void onAddProfile(View view) {
		final String profileNameText =
				(TextUtils.isEmpty(profileName.getText())) ? Constants.DEFAULT : profileName.getText().toString();

		final String riderTypeText = (String) riderType.getSelectedItem();
		final String frontTireWidth = (String) frontWidth.getSelectedItem();
		final String rearTireWidth = (String) rearWidth.getSelectedItem();
		onCalculateTirePressure(view);
		final long profile = tirePressureDataBase.addProfile(profileNameText,
															 riderTypeText,
															 bodyWeightAmount,
															 bikeWeightAmount,
															 frontTireWidth,
															 rearTireWidth,
															 frontLoadPercent,
															 rearLoadPercent);
		if (profile == 0f) {
			Snackbar.make(view, "Updated existing record", Snackbar.LENGTH_SHORT).show();
		} else {
			Snackbar.make(view, "Created a new record with id: " + profile, Snackbar.LENGTH_SHORT).show();
		}
	}

	public void onCalculateTirePressure(View view) {
		hideKeyboard(this);
		final String bodyWeightText =
				(TextUtils.isEmpty(bodyWeight.getText())) ? "0.0" : bodyWeight.getText().toString();
		final String bikeWeightText =
				(TextUtils.isEmpty(bikeWeight.getText())) ? "0.0" : bikeWeight.getText().toString();
		final String frontLoadText = (TextUtils.isEmpty(frontLoad.getText())) ? "0.0" : frontLoad.getText().toString();
		final String rearLoadText = (TextUtils.isEmpty(rearLoad.getText())) ? "0.0" : rearLoad.getText().toString();

		bodyWeightAmount = Double.parseDouble(bodyWeightText);
		bikeWeightAmount = Double.parseDouble(bikeWeightText);
		totalWeight = bodyWeightAmount + bikeWeightAmount;
		totalWeightLabel.setText(fmt(totalWeight));
		final String frontLoadItem = (String) frontLoadUnits.getSelectedItem();
		if (TextUtils.equals("%", frontLoadItem)) {
			frontLoadPercent = Double.parseDouble(frontLoadText);
			frontLoadWeight = totalWeight * frontLoadPercent / 100;
			frontLoadPercentLabel.setText(String.format(getString(R.string.loadPercentLabel), frontLoadText));
		} else {
			frontLoadPercent = Double.parseDouble(frontLoadText) * 100 / totalWeight;
			frontLoadWeight = Double.parseDouble(frontLoadText);
			frontLoadPercentLabel.setText(String.format(getString(R.string.loadPercentLabel), fmt(frontLoadPercent)));
		}

		final String rearLoadItem = (String) rearLoadUnits.getSelectedItem();
		if (TextUtils.equals("%", rearLoadItem)) {
			rearLoadPercent = Double.parseDouble(rearLoadText);
			rearLoadWeight = totalWeight * rearLoadPercent / 100;
			rearLoadPercentLabel.setText(String.format(getString(R.string.loadPercentLabel), rearLoadText));
		} else {
			rearLoadPercent = Double.parseDouble(rearLoadText) * 100 / totalWeight;
			rearLoadWeight = Double.parseDouble(rearLoadText);
			rearLoadPercentLabel.setText(String.format(getString(R.string.loadPercentLabel), fmt(rearLoadPercent)));
		}

		frontLoadWeightLabel.setText(fmt(Math.round(frontLoadWeight)));
		rearLoadWeightLabel.setText(fmt(Math.round(rearLoadWeight)));
		Calculator frontTireCalculator = new Calculator(frontLoadWeight, (String) frontWidth.getSelectedItem());
		frontTireLabel.setText(fmt(Math.round(frontTireCalculator.psi())));
		Calculator rearTireCalculator = new Calculator(rearLoadWeight, (String) rearWidth.getSelectedItem());
		rearTireLabel.setText(fmt(Math.round(rearTireCalculator.psi())));
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}

		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
