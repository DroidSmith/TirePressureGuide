package com.droidsmith.tireguide

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.droidsmith.tireguide.calcengine.Calculator
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_tire_guide.*
import kotlinx.android.synthetic.main.app_bar_tire_guide.*
import kotlinx.android.synthetic.main.content_tire_guide.*
import java.text.DecimalFormat

class TireGuideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	private var totalWeight: Double = 0.toDouble()
	private var frontLoadWeight: Double = 0.toDouble()
	private var frontLoadPercent: Double = 0.toDouble()
	private var rearLoadWeight: Double = 0.toDouble()
	private var rearLoadPercent: Double = 0.toDouble()
	private var bodyWeightAmount: Double = 0.toDouble()
	private var bikeWeightAmount: Double = 0.toDouble()
	private var itemSelectedFromProfile: Boolean = false
	private lateinit var tirePressureDataBase: TirePressureDataBase

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_tire_guide)
		setSupportActionBar(toolbar)
		tirePressureDataBase = TirePressureDataBase(this)

		val toggle = ActionBarDrawerToggle(
			this,
			drawer_layout,
			toolbar,
			R.string.navigation_drawer_open,
			R.string.navigation_drawer_close
		)

		if (drawer_layout != null) {
			drawer_layout.addDrawerListener(toggle)
			toggle.syncState()
		}

		navigationView?.setNavigationItemSelectedListener(this)
		fab?.setOnClickListener { view ->
			hideKeyboard(this@TireGuideActivity)
			onAddProfile(view)
		}

		profileName.setOnEditorActionListener { _, actionId, _ ->
			var handled = false
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				bodyWeight.requestFocus()
				handled = true
			}

			handled
		}

		bodyWeight.setOnEditorActionListener { _, actionId, _ ->
			var handled = false
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				bikeWeight.requestFocus()
				handled = true
			}

			handled
		}

		bikeWeight.setOnEditorActionListener { _, actionId, _ ->
			var handled = false
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				frontLoad.requestFocus()
				handled = true
			}

			handled
		}

		frontLoad.setOnEditorActionListener { _, actionId, _ ->
			var handled = false
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				rearLoad.requestFocus()
				handled = true
			}

			handled
		}

		rearLoad.setOnEditorActionListener { view, actionId, _ ->
			var handled = false
			if (actionId == EditorInfo.IME_ACTION_GO) {
				val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

				onCalculateTirePressure()
				handled = true
			}

			handled
		}

		val riderTypeAdapter = ArrayAdapter.createFromResource(
			this,
			R.array.rider_type_array,
			R.layout.spinner_item
		)
		riderTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
		riderType.adapter = riderTypeAdapter

		val frontWidthAdapter = ArrayAdapter.createFromResource(
			this,
			R.array.width_array,
			R.layout.spinner_item
		)
		frontWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
		frontWidth.adapter = frontWidthAdapter

		val rearWidthAdapter = ArrayAdapter.createFromResource(
			this,
			R.array.width_array,
			R.layout.spinner_item
		)
		rearWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
		rearWidth.adapter = rearWidthAdapter

		val frontLoadAdapter = ArrayAdapter.createFromResource(
			this,
			R.array.load_array,
			R.layout.spinner_item
		)
		frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
		frontLoadUnits.adapter = frontLoadAdapter

		val rearLoadAdapter = ArrayAdapter.createFromResource(
			this,
			R.array.load_array,
			R.layout.spinner_item
		)
		rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
		rearLoadUnits.adapter = rearLoadAdapter

		riderType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(
				parent: AdapterView<*>,
				view: View?,
				position: Int,
				id: Long
			) {
				if (!itemSelectedFromProfile) {
					when (parent.selectedItem) {
						RACER -> {
							frontLoad.setText(RACER_FRONT)
							frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
							rearLoad.setText(RACER_REAR)
							rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
						}
						SPORT -> {
							frontLoad.setText(SPORT_FRONT)
							frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
							rearLoad.setText(SPORT_REAR)
							rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
						}
						else -> {
							frontLoad.setText(CASUAL_FRONT)
							frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
							rearLoad.setText(CASUAL_REAR)
							rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
						}
					}
				}

				itemSelectedFromProfile = false
			}

			override fun onNothingSelected(parent: AdapterView<*>) {}
		}

		calculateTirePressure.setOnClickListener {
			onCalculateTirePressure()
		}

		getProfile()
	}

	override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		if (frontLoadPercent > 0) {
			frontLoad.setText(fmt(frontLoadPercent))
		}

		if (rearLoadPercent > 0) {
			rearLoad.setText(fmt(rearLoadPercent))
		}
	}

	private fun fmt(d: Double): String {
		return if (d == d.toLong().toDouble()) {
			d.toString()
		} else {
			DecimalFormat("#.#").format(d)
		}
	}

	override fun onBackPressed() {
		if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START)) {
			drawer_layout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.tire_guide, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		val id = item.itemId

		return if (id == R.id.action_settings) true
		else super.onOptionsItemSelected(item)

	}

	/**
	 * Retrieves the profile from the database and displays the values.
	 */
	private fun getProfile() {
		val profile = tirePressureDataBase.profiles
		profile.moveToFirst()
		while (!profile.isAfterLast) {
			itemSelectedFromProfile = true
			profileName.setText(profile.getString(Profiles.PROFILE_NAME))
			bodyWeightAmount = profile.getDouble(Profiles.BODY_WEIGHT)
			bodyWeight.setText(fmt(bodyWeightAmount))

			bikeWeightAmount = profile.getDouble(Profiles.BIKE_WEIGHT)
			bikeWeight.setText(fmt(bikeWeightAmount))

			when (profile.getString(Profiles.RIDER_TYPE)) {
				RACER -> riderType.setSelection(0)
				SPORT -> riderType.setSelection(1)
				CASUAL -> riderType.setSelection(2)
				else -> riderType.setSelection(2) // Default to Casual
			}

			// There has to be a better way than checking every value
			when (profile.getString(Profiles.FRONT_TIRE_WIDTH)) {
				"20" -> frontWidth.setSelection(0)
				"21" -> frontWidth.setSelection(1)
				"22" -> frontWidth.setSelection(2)
				"23" -> frontWidth.setSelection(3)
				"24" -> frontWidth.setSelection(4)
				"25" -> frontWidth.setSelection(5)
				"26" -> frontWidth.setSelection(6)
				"27" -> frontWidth.setSelection(7)
				"28" -> frontWidth.setSelection(8)
				else -> frontWidth.setSelection(5) //Default to 25mm
			}

			when (profile.getString(Profiles.REAR_TIRE_WIDTH)) {
				"20" -> rearWidth.setSelection(0)
				"21" -> rearWidth.setSelection(1)
				"22" -> rearWidth.setSelection(2)
				"23" -> rearWidth.setSelection(3)
				"24" -> rearWidth.setSelection(4)
				"25" -> rearWidth.setSelection(5)
				"26" -> rearWidth.setSelection(6)
				"27" -> rearWidth.setSelection(7)
				else -> rearWidth.setSelection(8)
			}

			frontLoadPercent = profile.getDouble(Profiles.FRONT_LOAD_PERCENT)
			frontLoad.setText(fmt(frontLoadPercent))

			rearLoadPercent = profile.getDouble(Profiles.REAR_LOAD_PERCENT)
			rearLoad.setText(fmt(rearLoadPercent))
			profile.moveToNext()
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		// Handle navigation view item clicks here.

		when (item.itemId) {
			R.id.navigationRecents -> {
				//Todo pull the latest profile, I think
			}
			R.id.navigationAdd -> {
				val group = this.findViewById<ViewGroup>(android.R.id.content)
				if (group != null) {
					val viewGroup = group.getChildAt(0) as ViewGroup
					onAddProfile(viewGroup)
				}
			}
			R.id.navigationManage -> {
				// TODO I'm going to remember what this is some day
			}
			R.id.navigationHelp -> {
				val myMime = MimeTypeMap.getSingleton()
				val mimeType = myMime.getMimeTypeFromExtension(PDF)
				val uri = Uri.parse(TIRE_INFLATION_PDF)

				val intent = Intent(Intent.ACTION_VIEW)
				intent.setDataAndType(uri, mimeType)
				val packageManager = packageManager
				val activities = packageManager.queryIntentActivities(intent, 0)
				if (activities.isNotEmpty()) {
					val chooser = Intent.createChooser(intent, "Choose a PDF Viewer")
					startActivity(chooser)
				} else {
					// If no internal viewer is present, then allow Google Docs Viewer to view the PDF.
					intent.data = Uri.parse(GOOGLE_DOC_URL + TIRE_INFLATION_PDF)
					try {
						startActivity(intent)
					} catch (e: ActivityNotFoundException) {
						Toast.makeText(
							this,
							"No Application Available to View PDF files",
							Toast.LENGTH_SHORT
						).show()
					}

				}
			}
		}

		val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
		drawer?.closeDrawer(GravityCompat.START)

		return true
	}

	private fun onAddProfile(view: View) {
		val profileNameText = if (profileName.text.isNullOrEmpty()) DEFAULT else profileName.text.toString()
		val riderTypeText = riderType.selectedItem.toString()
		val frontTireWidth = frontWidth.selectedItem.toString()
		val rearTireWidth = rearWidth.selectedItem.toString()
		onCalculateTirePressure()
		val profile = tirePressureDataBase.addProfile(
			profileNameText,
			riderTypeText,
			bodyWeightAmount,
			bikeWeightAmount,
			frontTireWidth,
			rearTireWidth,
			frontLoadPercent,
			rearLoadPercent
		)
		if (profile.toFloat() == 0f) {
			Snackbar.make(view, "Updated existing record", Snackbar.LENGTH_SHORT).show()
		} else {
			Snackbar.make(
				view,
				"Created a new record with id: $profile",
				Snackbar.LENGTH_SHORT
			).show()
		}
	}

	private fun onCalculateTirePressure() {
		hideKeyboard(this)
		bodyWeightAmount = if (bodyWeight.text.isNullOrEmpty()) 0.0 else bodyWeight.text.toString().toDouble()
		bikeWeightAmount = if (bikeWeight.text.isNullOrEmpty()) 0.0 else bikeWeight.text.toString().toDouble()
		val frontLoadText = if (frontLoad.text.isNullOrEmpty()) "0.0" else frontLoad.text.toString()
		val rearLoadText = if (rearLoad.text.isNullOrEmpty()) "0.0" else rearLoad.text.toString()

		totalWeight = bodyWeightAmount + bikeWeightAmount
		totalWeightAmount.text = fmt(totalWeight)
		val frontLoadItem = frontLoadUnits.selectedItem.toString()
		if ("%" == frontLoadItem) {
			frontLoadPercent = frontLoadText.toDouble()
			frontLoadWeight = totalWeight * frontLoadPercent / 100
			frontLoadPercentAmount.text = String.format(
				" " + getString(R.string.loadPercentLabel),
				frontLoadText
			)
		} else {
			frontLoadPercent = frontLoadText.toDouble() * 100 / totalWeight
			frontLoadWeight = frontLoadText.toDouble()
			frontLoadPercentAmount.text = String.format(
				" " + getString(R.string.loadPercentLabel),
				fmt(frontLoadPercent)
			)
		}

		val rearLoadItem = rearLoadUnits.selectedItem.toString()
		if ("%" == rearLoadItem) {
			rearLoadPercent = rearLoadText.toDouble()
			rearLoadWeight = totalWeight * rearLoadPercent / 100
			rearLoadPercentAmount.text = String.format(
				" " + getString(R.string.loadPercentLabel),
				rearLoadText
			)
		} else {
			rearLoadPercent = rearLoadText.toDouble() * 100 / totalWeight
			rearLoadWeight = rearLoadText.toDouble()
			rearLoadPercentAmount.text = String.format(
				" " + getString(R.string.loadPercentLabel),
				fmt(rearLoadPercent)
			)
		}

		frontLoadWeightAmount.text = fmt(Math.round(frontLoadWeight).toDouble())
		rearLoadWeightAmount.text = fmt(Math.round(rearLoadWeight).toDouble())
		val frontTireCalculator = Calculator()
		frontTire.text = fmt(
			frontTireCalculator.psi(
				frontLoadWeight,
				frontWidth.selectedItem.toString()
			)
		)
		val rearTireCalculator = Calculator()
		rearTire.text = fmt(
			rearTireCalculator.psi(
				rearLoadWeight,
				rearWidth.selectedItem.toString()
			)
		)
	}

	companion object {

		fun hideKeyboard(activity: Activity) {
			val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
			//Find the currently focused view, so we can grab the correct window token from it.
			var view = activity.currentFocus
			//If no view currently has focus, create a new one, just so we can grab a window token from it
			if (view == null) {
				view = View(activity)
			}

			imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
		}
	}
}

