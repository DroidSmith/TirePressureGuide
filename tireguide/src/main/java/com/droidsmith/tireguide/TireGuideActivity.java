package com.droidsmith.tireguide;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.droidsmith.tireguide.calcengine.Calculator;

import java.text.DecimalFormat;
import java.util.List;

public class TireGuideActivity extends AppCompatActivity implements NavigationView
		.OnNavigationItemSelectedListener {


	double totalWeight;
	double frontLoadWeight;
	double rearLoadWeight;
	private EditText bodyWeight;
	private EditText bikeWeight;
	private EditText frontLoad;
	private EditText rearLoad;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tire_guide);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction(
						"Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		bodyWeight = (EditText) findViewById(R.id.bodyWeight);
		bikeWeight = (EditText) findViewById(R.id.bikeWeight);
		frontLoad = (EditText) findViewById(R.id.frontLoad);
		rearLoad = (EditText) findViewById(R.id.rearLoad);
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
					onCalculateTirePressure(view);
					handled = true;
				}

				return handled;
			}
		});

		ArrayAdapter<CharSequence> frontWidthAdapter = ArrayAdapter.createFromResource(this,
				R.array.width_array, R.layout.spinner_item);
		frontWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		frontWidth.setAdapter(frontWidthAdapter);

		ArrayAdapter<CharSequence> rearWidthAdapter = ArrayAdapter.createFromResource(this,
				R.array.width_array, R.layout.spinner_item);
		rearWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		rearWidth.setAdapter(rearWidthAdapter);

		ArrayAdapter<CharSequence> frontLoadAdapter = ArrayAdapter.createFromResource(this,
				R.array.load_array, R.layout.spinner_item);
		frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		frontLoadUnits.setAdapter(frontLoadAdapter);

		ArrayAdapter<CharSequence> rearLoadAdapter = ArrayAdapter.createFromResource(this,
				R.array.load_array, R.layout.spinner_item);
		rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		rearLoadUnits.setAdapter(rearLoadAdapter);
	}

	public void onCalculateTirePressure(View view) {
		((InputMethodManager) this.getSystemService(
				Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
		final String bodyWeightText = (bodyWeight.getText() != null && !"".equals(
				bodyWeight.getText().toString())) ? bodyWeight.getText().toString() : "0.0";
		final String bikeWeightText = (bikeWeight.getText() != null && !"".equals(
				bikeWeight.getText().toString())) ? bikeWeight.getText().toString() : "0.0";
		final String frontLoadText = (frontLoad.getText() != null && !"".equals(
				frontLoad.getText().toString())) ? frontLoad.getText().toString() : "0.0";
		final String rearLoadText = (rearLoad.getText() != null && !"".equals(
				rearLoad.getText().toString())) ? rearLoad.getText().toString() : "0.0";

		totalWeight = Double.parseDouble(bodyWeightText) + Double.parseDouble(bikeWeightText);
		totalWeightLabel.setText(fmt(totalWeight));
		final String frontLoadItem = (String) frontLoadUnits.getSelectedItem();
		if ("%".equals(frontLoadItem)) {
			final double frontLoadPercent = Double.parseDouble(frontLoadText) / 100;
			frontLoadWeight = totalWeight * frontLoadPercent;
			frontLoadPercentLabel.setText(
					String.format(getString(R.string.loadPercentLabel), frontLoadText));
		} else {
			final double frontLoadPercent = Double.parseDouble(frontLoadText) * 100 / totalWeight;
			frontLoadWeight = Double.parseDouble(frontLoadText);
			frontLoadPercentLabel.setText(
					String.format(getString(R.string.loadPercentLabel), fmt(frontLoadPercent)));
		}

		final String rearLoadItem = (String) rearLoadUnits.getSelectedItem();
		if ("%".equals(rearLoadItem)) {
			final double rearLoadPercent = Double.parseDouble(rearLoadText) / 100;
			rearLoadWeight = totalWeight * rearLoadPercent;
			rearLoadPercentLabel.setText(
					String.format(getString(R.string.loadPercentLabel), rearLoadText));
		} else {
			final double rearLoadPercent = Double.parseDouble(rearLoadText) * 100 / totalWeight;
			rearLoadWeight = Double.parseDouble(rearLoadText);
			rearLoadPercentLabel.setText(
					String.format(getString(R.string.loadPercentLabel), fmt(rearLoadPercent)));
		}

		frontLoadWeightLabel.setText(fmt(frontLoadWeight));
		rearLoadWeightLabel.setText(fmt(rearLoadWeight));
		Calculator frontTireCalculator = new Calculator(frontLoadWeight,
				(String) frontWidth.getSelectedItem());
		frontTireLabel.setText(fmt(Math.round(frontTireCalculator.psi())));
		Calculator rearTireCalculator = new Calculator(rearLoadWeight,
				(String) rearWidth.getSelectedItem());
		rearTireLabel.setText(fmt(Math.round(rearTireCalculator.psi())));
	}

	private String fmt(double d) {
		if (d == (long) d) {
			return Long.toString((long) d);
		} else {
			return new DecimalFormat("#.#").format(d);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_recents) {

		} else if (id == R.id.nav_add) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_help) {
			MimeTypeMap myMime = MimeTypeMap.getSingleton();
			String mimeType = myMime.getMimeTypeFromExtension(Constants.PDF);
			Uri uri = Uri.parse(Constants.TIRE_INFLATION_PDF);


			final Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, mimeType);
			PackageManager packageManager = getPackageManager();
			List<ResolveInfo>activities = packageManager.queryIntentActivities(intent, 0);
			if (!(activities.isEmpty())) {
				Intent chooser=Intent.createChooser(intent, "Choose a PDF Viewer");
				startActivity(chooser);
			} else {
				//If no internal viewer is present, then allow Google Docs Viewer to view the PDF.
				intent.setData(Uri.parse("http://docs.google.com/gview?embedded=true&url=" +
						Constants.TIRE_INFLATION_PDF));
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this, "No Application Available to View PDF files", Toast
							.LENGTH_SHORT).show();
				}
			}
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
