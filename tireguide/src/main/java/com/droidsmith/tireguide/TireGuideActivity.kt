package com.droidsmith.tireguide

import android.icu.text.NumberFormat
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.droidsmith.tireguide.calcengine.Calculator
import com.droidsmith.tireguide.data.RiderType
import com.droidsmith.tireguide.data.TireWidth
import com.droidsmith.tireguide.databinding.ActivityTireGuideBinding
import com.droidsmith.tireguide.databinding.ContentTireGuideBinding
import com.droidsmith.tireguide.extensions.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class TireGuideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // This property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityTireGuideBinding
    private lateinit var contentTireGuide: ContentTireGuideBinding
    private lateinit var tirePressureDataBase: TirePressureDataBase
    private var totalWeight: Double = 0.0
    private var frontLoadWeight: Double = 0.0
    private var frontLoadPercent: Double = 0.0
    private var rearLoadWeight: Double = 0.0
    private var rearLoadPercent: Double = 0.0
    private var bodyWeight: Double = 0.0
    private var bikeWeight: Double = 0.0
    private var itemSelectedFromProfile = false
    private var initialLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTireGuideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.appBarTireGuide.toolbar)
        tirePressureDataBase = TirePressureDataBase(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarTireGuide.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.appBarTireGuide.fab.setOnClickListener { button ->
            view.hideKeyboard()
            onAddProfile(button)
        }

        contentTireGuide = binding.appBarTireGuide.contentTireGuide
        contentTireGuide.profileEdit.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (wasReturnPressed(actionId, event)) {
                contentTireGuide.bodyWeightEdit.requestFocus()
                handled = true
            }

            handled
        }

        contentTireGuide.bodyWeightEdit.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (wasReturnPressed(actionId, event)) {
                contentTireGuide.bikeWeightEdit.requestFocus()
                handled = true
            }

            handled
        }

        contentTireGuide.bikeWeightEdit.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (wasReturnPressed(actionId, event)) {
                view.hideKeyboard()
                contentTireGuide.frontWidthSpinner.showDropDown()
                handled = true
            }

            handled
        }

        val tireWidths: Array<String> = TireWidth.getWidthsAsStrings()
        val frontWidthAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, tireWidths)
        contentTireGuide.frontWidthSpinner.apply {
            setAdapter(frontWidthAdapter)
            inputType = InputType.TYPE_NULL
            setText(TireWidth.TWENTY_FIVE.displayName, false)
            addOnTextChangedBehavior {
                if (!itemSelectedFromProfile && !initialLoad) {
                    view.hideKeyboard()
                    contentTireGuide.rearWidthSpinner.showDropDown()
                }
            }
        }

        val rearWidthAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, tireWidths)
        contentTireGuide.rearWidthSpinner.apply {
            setAdapter(rearWidthAdapter)
            inputType = InputType.TYPE_NULL
            setText(TireWidth.TWENTY_FIVE.displayName, false)
            addOnTextChangedBehavior {
                if (!itemSelectedFromProfile && !initialLoad) {
                    hideKeyboard()
                    contentTireGuide.riderTypeSpinner.showDropDown()
                }
            }
        }

        val riderTypes: Array<String> = RiderType.getRiderTypesAsStrings(requireActivity())
        val riderTypeAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, riderTypes)
        contentTireGuide.riderTypeSpinner.apply {
            setAdapter(riderTypeAdapter)
            inputType = InputType.TYPE_NULL
            setText(getString(RiderType.CASUAL.displayName), false)
            addOnTextChangedBehavior { riderDisplayType ->
                if (!itemSelectedFromProfile && !initialLoad) {
                    when (RiderType.getTypeFromDisplayName(requireActivity(), riderDisplayType)) {
                        RiderType.RACER -> {
                            contentTireGuide.frontLoadEdit.setText(RACER_FRONT)
                            contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            contentTireGuide.rearLoadEdit.setText(RACER_REAR)
                            contentTireGuide.rearLoadUnitsSpinner.setSelection(0, true)
                        }
                        RiderType.SPORT -> {
                            contentTireGuide.frontLoadEdit.setText(SPORT_FRONT)
                            contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            contentTireGuide.rearLoadEdit.setText(SPORT_REAR)
                            contentTireGuide.rearLoadUnitsSpinner.setSelection(0, true)
                        }
                        else -> {
                            contentTireGuide.frontLoadEdit.setText(CASUAL_FRONT)
                            contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            contentTireGuide.rearLoadEdit.setText(CASUAL_REAR)
                            contentTireGuide.rearLoadUnitsSpinner.setSelection(0, true)
                        }
                    }
                    contentTireGuide.frontLoadEdit.requestFocus()
                    contentTireGuide.frontLoadEdit.showKeyboard()
                }
            }
        }

        contentTireGuide.frontLoadEdit.setText(CASUAL_FRONT)
        contentTireGuide.frontLoadEdit.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (wasReturnPressed(actionId, event)) {
                contentTireGuide.rearLoadEdit.requestFocus()
                handled = true
            }

            handled
        }

        val frontLoadAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.load_array,
            R.layout.spinner_item
        )
        frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentTireGuide.frontLoadUnitsSpinner.adapter = frontLoadAdapter
        contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)

        contentTireGuide.rearLoadEdit.setText(CASUAL_REAR)
        contentTireGuide.rearLoadEdit.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (wasReturnPressed(actionId, event)) {
                view.hideKeyboard()
                onCalculateTirePressure()
                handled = true
            }

            handled
        }

        val rearLoadAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.load_array,
            R.layout.spinner_item
        )
        rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentTireGuide.rearLoadUnitsSpinner.adapter = rearLoadAdapter
        contentTireGuide.rearLoadUnitsSpinner.setSelection(0, true)

        contentTireGuide.calculateButton.setOnClickListener { button ->
            button.hideKeyboard()
            onCalculateTirePressure()
        }

        contentTireGuide.profileEdit.requestFocus()
        getProfile()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (frontLoadPercent > 0) {
            contentTireGuide.frontLoadEdit.setText(fmt(frontLoadPercent))
        }

        if (rearLoadPercent > 0) {
            contentTireGuide.rearLoadEdit.setText(fmt(rearLoadPercent))
        }
        initialLoad = false
    }

    private fun fmt(d: Double): String = NumberFormat.getNumberInstance(Locale.US).format(d)

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        totalWeight = savedInstanceState.getDouble(BUNDLE_TOTAL_WEIGHT)
        frontLoadWeight = savedInstanceState.getDouble(BUNDLE_FRONT_LOAD_WEIGHT)
        frontLoadPercent = savedInstanceState.getDouble(BUNDLE_FRONT_LOAD_PERCENT)
        rearLoadWeight = savedInstanceState.getDouble(BUNDLE_REAR_LOAD_WEIGHT)
        rearLoadPercent = savedInstanceState.getDouble(BUNDLE_REAR_LOAD_PERCENT)
        bodyWeight = savedInstanceState.getDouble(BUNDLE_BODY_WEIGHT)
        bikeWeight = savedInstanceState.getDouble(BUNDLE_BIKE_WEIGHT)
        itemSelectedFromProfile = savedInstanceState.getBoolean(BUNDLE_ITEM_SELECTED_FROM_PROFILE)
        val tireWidths: Array<String> = TireWidth.getWidthsAsStrings()
        val frontWidthAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, tireWidths)
        contentTireGuide.frontWidthSpinner.setAdapter(frontWidthAdapter)
        val rearWidthAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, tireWidths)
        contentTireGuide.rearWidthSpinner.setAdapter(rearWidthAdapter)
        val riderTypes: Array<String> = RiderType.getRiderTypesAsStrings(requireActivity())
        val riderTypeAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, riderTypes)
        contentTireGuide.riderTypeSpinner.setAdapter(riderTypeAdapter)
        initialLoad = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(BUNDLE_TOTAL_WEIGHT, totalWeight)
        outState.putDouble(BUNDLE_FRONT_LOAD_WEIGHT, frontLoadWeight)
        outState.putDouble(BUNDLE_FRONT_LOAD_PERCENT, frontLoadPercent)
        outState.putDouble(BUNDLE_REAR_LOAD_WEIGHT, rearLoadWeight)
        outState.putDouble(BUNDLE_REAR_LOAD_PERCENT, rearLoadPercent)
        outState.putDouble(BUNDLE_BODY_WEIGHT, bodyWeight)
        outState.putDouble(BUNDLE_BIKE_WEIGHT, bikeWeight)
        outState.putBoolean(BUNDLE_ITEM_SELECTED_FROM_PROFILE, itemSelectedFromProfile)
        tirePressureDataBase.close()
    }

    /**
     * Retrieves the profile from the database and displays the values.
     */
    private fun getProfile() {
        val profile = tirePressureDataBase.profiles
        profile.moveToFirst()
        while (!profile.isAfterLast) {
            itemSelectedFromProfile = true
            contentTireGuide.profileEdit.setText(profile.getString(Profiles.PROFILE_NAME))
            bodyWeight = profile.getDouble(Profiles.BODY_WEIGHT)
            contentTireGuide.bodyWeightEdit.setText(fmt(bodyWeight))

            bikeWeight = profile.getDouble(Profiles.BIKE_WEIGHT)
            contentTireGuide.bikeWeightEdit.setText(fmt(bikeWeight))

            val frontTireWidth = TireWidth.getWidthFromDisplayName(profile.getString(Profiles.FRONT_TIRE_WIDTH))
            contentTireGuide.frontWidthSpinner.setText(frontTireWidth?.displayName, false)

            val rearTireWidth = TireWidth.getWidthFromDisplayName(profile.getString(Profiles.REAR_TIRE_WIDTH))
            contentTireGuide.rearWidthSpinner.setText(rearTireWidth?.displayName, false)

            val riderType = RiderType.getTypeFromServiceName(profile.getString(Profiles.RIDER_TYPE))
            contentTireGuide.riderTypeSpinner.setText(riderType?.displayName?.let { getString(it) }, false)

            frontLoadPercent = profile.getDouble(Profiles.FRONT_LOAD_PERCENT)
            rearLoadPercent = profile.getDouble(Profiles.REAR_LOAD_PERCENT)
            profile.moveToNext()
        }
        itemSelectedFromProfile = false
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
                openExternalUrl(TIRE_INFLATION_PDF)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.closeDrawer(GravityCompat.START)

        return true
    }

    private fun onAddProfile(view: View) {
        val profileNameText = if (contentTireGuide.profileEdit.text.isNullOrEmpty()) {
            DEFAULT
        } else {
            contentTireGuide.profileEdit.text.toString()
        }
        val selectedRiderType =
            RiderType.getTypeFromDisplayName(requireActivity(), contentTireGuide.riderTypeSpinner.text.toString())
        val frontTireWidth = TireWidth.getWidthFromDisplayName(contentTireGuide.frontWidthSpinner.text.toString())
        val rearTireWidth = TireWidth.getWidthFromDisplayName(contentTireGuide.rearWidthSpinner.text.toString())
        view.hideKeyboard()
        onCalculateTirePressure()
        val profile = tirePressureDataBase.addProfile(
            profileNameText,
            selectedRiderType?.serviceName.orEmpty(),
            bodyWeight,
            bikeWeight,
            frontTireWidth?.displayName.orEmpty(),
            rearTireWidth?.displayName.orEmpty(),
            frontLoadPercent,
            rearLoadPercent
        )
        if (profile.toFloat() == 0f) {
            Snackbar.make(view, R.string.updated_existing_profile, Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(
                view,
                getString(R.string.created_new_profile, profile),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun onCalculateTirePressure() {
        bodyWeight = if (contentTireGuide.bodyWeightEdit.text.isNullOrEmpty()) {
            0.0
        } else {
            contentTireGuide.bodyWeightEdit.text.toString().toDouble()
        }
        bikeWeight = if (contentTireGuide.bikeWeightEdit.text.isNullOrEmpty()) {
            0.0
        } else {
            contentTireGuide.bikeWeightEdit.text.toString().toDouble()
        }
        val frontLoadText = if (contentTireGuide.frontLoadEdit.text.isNullOrEmpty()) {
            "0.0"
        } else {
            contentTireGuide.frontLoadEdit.text.toString()
        }
        val rearLoadText = if (contentTireGuide.rearLoadEdit.text.isNullOrEmpty()) {
            "0.0"
        } else {
            contentTireGuide.rearLoadEdit.text.toString()
        }

        totalWeight = bodyWeight + bikeWeight
        val frontLoadItem = contentTireGuide.frontLoadUnitsSpinner.selectedItem.toString()
        if ("%" == frontLoadItem) {
            frontLoadPercent = frontLoadText.toDouble()
            frontLoadWeight = totalWeight * frontLoadPercent / 100
        } else {
            frontLoadPercent = frontLoadText.toDouble() * 100 / totalWeight
            frontLoadWeight = frontLoadText.toDouble()
        }

        val rearLoadItem = contentTireGuide.rearLoadUnitsSpinner.selectedItem.toString()
        if ("%" == rearLoadItem) {
            rearLoadPercent = rearLoadText.toDouble()
            rearLoadWeight = totalWeight * rearLoadPercent / 100
        } else {
            rearLoadPercent = rearLoadText.toDouble() * 100 / totalWeight
            rearLoadWeight = rearLoadText.toDouble()
        }

        val frontTireCalculator = Calculator()
        contentTireGuide.frontTirePressure.text = fmt(
            frontTireCalculator.psi(
                frontLoadWeight,
                contentTireGuide.frontWidthSpinner.text.toString()
            )
        )
        val rearTireCalculator = Calculator()
        contentTireGuide.rearTirePressure.text = fmt(
            rearTireCalculator.psi(
                rearLoadWeight,
                contentTireGuide.rearWidthSpinner.text.toString()
            )
        )
    }

    private fun wasReturnPressed(actionId: Int, event: KeyEvent?): Boolean {
        val action = actionId and EditorInfo.IME_MASK_ACTION
        return event?.keyCode == KeyEvent.FLAG_EDITOR_ACTION ||
                action == EditorInfo.IME_ACTION_DONE ||
                action == EditorInfo.IME_ACTION_NEXT ||
                action == EditorInfo.IME_ACTION_GO ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER
    }

    companion object {
        private const val BUNDLE_TOTAL_WEIGHT = "BUNDLE_TOTAL_WEIGHT"
        private const val BUNDLE_FRONT_LOAD_WEIGHT = "BUNDLE_FRONT_LOAD_WEIGHT"
        private const val BUNDLE_FRONT_LOAD_PERCENT = "BUNDLE_FRONT_LOAD_PERCENT"
        private const val BUNDLE_REAR_LOAD_WEIGHT = "BUNDLE_REAR_LOAD_WEIGHT"
        private const val BUNDLE_REAR_LOAD_PERCENT = "BUNDLE_REAR_LOAD_PERCENT"
        private const val BUNDLE_BODY_WEIGHT = "BUNDLE_BODY_WEIGHT"
        private const val BUNDLE_BIKE_WEIGHT = "BUNDLE_BIKE_WEIGHT"
        private const val BUNDLE_ITEM_SELECTED_FROM_PROFILE = "BUNDLE_IS_SELECTED_FROM_PROFILE"
    }
}

