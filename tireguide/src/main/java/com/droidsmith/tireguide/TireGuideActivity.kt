package com.droidsmith.tireguide

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.droidsmith.tireguide.calcengine.Calculator
import com.droidsmith.tireguide.databinding.ActivityTireGuideBinding
import com.droidsmith.tireguide.extensions.openExternalUrl
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat

class TireGuideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // This property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityTireGuideBinding

    private var totalWeight: Double = 0.0
    private var frontLoadWeight: Double = 0.0
    private var frontLoadPercent: Double = 0.0
    private var rearLoadWeight: Double = 0.0
    private var rearLoadPercent: Double = 0.0
    private var bodyWeightAmount: Double = 0.0
    private var bikeWeightAmount: Double = 0.0
    private var itemSelectedFromProfile: Boolean = false
    private lateinit var tirePressureDataBase: TirePressureDataBase

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
            hideKeyboard(this@TireGuideActivity)
            onAddProfile(button)
        }

        binding.appBarTireGuide.contentTireGuide.profileText.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.requestFocus()
                handled = true
            }

            handled
        }

        binding.appBarTireGuide.contentTireGuide.bodyWeightEdit.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.appBarTireGuide.contentTireGuide.bikeWeightEdit.requestFocus()
                handled = true
            }

            handled
        }

        binding.appBarTireGuide.contentTireGuide.bikeWeightEdit.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.requestFocus()
                handled = true
            }

            handled
        }

        binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!itemSelectedFromProfile) {
                    binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.requestFocus()
                }

                itemSelectedFromProfile = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!itemSelectedFromProfile) {
                    binding.appBarTireGuide.contentTireGuide.frontLoadEdit.requestFocus()
                }

                itemSelectedFromProfile = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.appBarTireGuide.contentTireGuide.frontLoadEdit.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.appBarTireGuide.contentTireGuide.rearLoadEdit.requestFocus()
                handled = true
            }

            handled
        }

        binding.appBarTireGuide.contentTireGuide.rearLoadEdit.setOnEditorActionListener { load, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(load.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
        binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.adapter = riderTypeAdapter

        val frontWidthAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.width_array,
                R.layout.spinner_item
        )
        frontWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.adapter = frontWidthAdapter

        val rearWidthAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.width_array,
                R.layout.spinner_item
        )
        rearWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.adapter = rearWidthAdapter

        val frontLoadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.load_array,
                R.layout.spinner_item
        )
        frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.appBarTireGuide.contentTireGuide.frontLoadUnitsSpinner.adapter = frontLoadAdapter

        val rearLoadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.load_array,
                R.layout.spinner_item
        )
        rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.appBarTireGuide.contentTireGuide.rearLoadUnits.adapter = rearLoadAdapter

        binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!itemSelectedFromProfile) {
                    when (parent.selectedItem) {
                        RACER -> {
                            binding.appBarTireGuide.contentTireGuide.frontLoadEdit.setText(RACER_FRONT)
                            binding.appBarTireGuide.contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            binding.appBarTireGuide.contentTireGuide.rearLoadEdit.setText(RACER_REAR)
                            binding.appBarTireGuide.contentTireGuide.rearLoadUnits.setSelection(0, true)
                        }
                        SPORT -> {
                            binding.appBarTireGuide.contentTireGuide.frontLoadEdit.setText(SPORT_FRONT)
                            binding.appBarTireGuide.contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            binding.appBarTireGuide.contentTireGuide.rearLoadEdit.setText(SPORT_REAR)
                            binding.appBarTireGuide.contentTireGuide.rearLoadUnits.setSelection(0, true)
                        }
                        else -> {
                            binding.appBarTireGuide.contentTireGuide.frontLoadEdit.setText(CASUAL_FRONT)
                            binding.appBarTireGuide.contentTireGuide.frontLoadUnitsSpinner.setSelection(0, true)
                            binding.appBarTireGuide.contentTireGuide.rearLoadEdit.setText(CASUAL_REAR)
                            binding.appBarTireGuide.contentTireGuide.rearLoadUnits.setSelection(0, true)
                        }
                    }
                    binding.appBarTireGuide.contentTireGuide.bodyWeightEdit.requestFocus()
                }

                itemSelectedFromProfile = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.appBarTireGuide.contentTireGuide.calculateButton.setOnClickListener {
            onCalculateTirePressure()
        }

        getProfile()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (frontLoadPercent > 0) {
            binding.appBarTireGuide.contentTireGuide.frontLoadEdit.setText(fmt(frontLoadPercent))
        }

        if (rearLoadPercent > 0) {
            binding.appBarTireGuide.contentTireGuide.rearLoadEdit.setText(fmt(rearLoadPercent))
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

    /**
     * Retrieves the profile from the database and displays the values.
     */
    private fun getProfile() {
        val profile = tirePressureDataBase.profiles
        profile.moveToFirst()
        while (!profile.isAfterLast) {
            itemSelectedFromProfile = true
            binding.appBarTireGuide.contentTireGuide.profileEdit.setText(profile.getString(Profiles.PROFILE_NAME))
            bodyWeightAmount = profile.getDouble(Profiles.BODY_WEIGHT)
            binding.appBarTireGuide.contentTireGuide.bodyWeightEdit.setText(fmt(bodyWeightAmount))

            bikeWeightAmount = profile.getDouble(Profiles.BIKE_WEIGHT)
            binding.appBarTireGuide.contentTireGuide.bikeWeightEdit.setText(fmt(bikeWeightAmount))

            when (profile.getString(Profiles.RIDER_TYPE)) {
                RACER -> binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.setSelection(0)
                SPORT -> binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.setSelection(1)
                CASUAL -> binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.setSelection(2)
                else -> binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.setSelection(2) // Default to Casual
            }

            // There has to be a better way than checking every value
            when (profile.getString(Profiles.FRONT_TIRE_WIDTH)) {
                "20" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(0)
                "21" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(1)
                "22" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(2)
                "23" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(3)
                "24" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(4)
                "25" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(5)
                "26" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(6)
                "27" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(7)
                "28" -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(8)
                else -> binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.setSelection(5) //Default to 25mm
            }

            when (profile.getString(Profiles.REAR_TIRE_WIDTH)) {
                "20" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(0)
                "21" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(1)
                "22" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(2)
                "23" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(3)
                "24" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(4)
                "25" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(5)
                "26" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(6)
                "27" -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(7)
                else -> binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.setSelection(8)
            }

            frontLoadPercent = profile.getDouble(Profiles.FRONT_LOAD_PERCENT)
            rearLoadPercent = profile.getDouble(Profiles.REAR_LOAD_PERCENT)
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
                openExternalUrl(TIRE_INFLATION_PDF)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.closeDrawer(GravityCompat.START)

        return true
    }

    private fun onAddProfile(view: View) {
        val profileNameText = if (binding.appBarTireGuide.contentTireGuide.profileEdit.text.isNullOrEmpty()) {
            DEFAULT
        } else {
            binding.appBarTireGuide.contentTireGuide.profileEdit.text.toString()
        }
        val riderTypeText = binding.appBarTireGuide.contentTireGuide.riderTypeSpinner.selectedItem.toString()
        val frontTireWidth = binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.selectedItem.toString()
        val rearTireWidth = binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.selectedItem.toString()
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
        hideKeyboard(this)
        bodyWeightAmount = if (binding.appBarTireGuide.contentTireGuide.bodyWeightEdit.text.isNullOrEmpty()) {
            0.0
        } else {
            binding.appBarTireGuide.contentTireGuide.bodyWeightEdit.text.toString().toDouble()
        }
        bikeWeightAmount = if (binding.appBarTireGuide.contentTireGuide.bikeWeightEdit.text.isNullOrEmpty()) {
            0.0
        } else {
            binding.appBarTireGuide.contentTireGuide.bikeWeightEdit.text.toString().toDouble()
        }
        val frontLoadText = if (binding.appBarTireGuide.contentTireGuide.frontLoadEdit.text.isNullOrEmpty()) {
            "0.0"
        } else {
            binding.appBarTireGuide.contentTireGuide.frontLoadEdit.text.toString()
        }
        val rearLoadText = if (binding.appBarTireGuide.contentTireGuide.rearLoadEdit.text.isNullOrEmpty()) {
            "0.0"
        } else {
            binding.appBarTireGuide.contentTireGuide.rearLoadEdit.text.toString()
        }

        totalWeight = bodyWeightAmount + bikeWeightAmount
        val frontLoadItem = binding.appBarTireGuide.contentTireGuide.frontLoadUnitsSpinner.selectedItem.toString()
        if ("%" == frontLoadItem) {
            frontLoadPercent = frontLoadText.toDouble()
            frontLoadWeight = totalWeight * frontLoadPercent / 100
        } else {
            frontLoadPercent = frontLoadText.toDouble() * 100 / totalWeight
            frontLoadWeight = frontLoadText.toDouble()
        }

        val rearLoadItem = binding.appBarTireGuide.contentTireGuide.rearLoadUnits.selectedItem.toString()
        if ("%" == rearLoadItem) {
            rearLoadPercent = rearLoadText.toDouble()
            rearLoadWeight = totalWeight * rearLoadPercent / 100
        } else {
            rearLoadPercent = rearLoadText.toDouble() * 100 / totalWeight
            rearLoadWeight = rearLoadText.toDouble()
        }

        val frontTireCalculator = Calculator()
        binding.appBarTireGuide.contentTireGuide.frontTirePressure.text = fmt(
                frontTireCalculator.psi(
                        frontLoadWeight,
                        binding.appBarTireGuide.contentTireGuide.frontWidthSpinner.selectedItem.toString()
                )
        )
        val rearTireCalculator = Calculator()
        binding.appBarTireGuide.contentTireGuide.rearTirePressure.text = fmt(
                rearTireCalculator.psi(
                        rearLoadWeight,
                        binding.appBarTireGuide.contentTireGuide.rearWidthSpinner.selectedItem.toString()
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

