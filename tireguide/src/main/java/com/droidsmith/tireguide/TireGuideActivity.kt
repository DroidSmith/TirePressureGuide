package com.droidsmith.tireguide

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
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
import com.droidsmith.tireguide.databinding.ActivityTireGuideBinding
import com.droidsmith.tireguide.databinding.AppBarTireGuideBinding
import com.droidsmith.tireguide.databinding.ContentTireGuideBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import kotlin.math.roundToLong

class TireGuideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // This property is only valid between onCreateView and onDestroyView
    private lateinit var binding: ActivityTireGuideBinding
    private lateinit var appBarBinding: AppBarTireGuideBinding
    private lateinit var contentBinding: ContentTireGuideBinding

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
        appBarBinding = AppBarTireGuideBinding.bind(view)
        contentBinding = ContentTireGuideBinding.bind(view)
        setContentView(view)
        setSupportActionBar(appBarBinding.toolbar)
        tirePressureDataBase = TirePressureDataBase(this)

        val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                appBarBinding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
        appBarBinding.fab.setOnClickListener { button ->
            hideKeyboard(this@TireGuideActivity)
            onAddProfile(button)
        }

        contentBinding.profileName.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                contentBinding.bodyWeight.requestFocus()
                handled = true
            }

            handled
        }

        contentBinding.bodyWeight.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                contentBinding.bikeWeight.requestFocus()
                handled = true
            }

            handled
        }

        contentBinding.bikeWeight.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                contentBinding.frontLoad.requestFocus()
                handled = true
            }

            handled
        }

        contentBinding.frontLoad.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                contentBinding.rearLoad.requestFocus()
                handled = true
            }

            handled
        }

        contentBinding.rearLoad.setOnEditorActionListener { load, actionId, _ ->
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
        contentBinding.riderType.adapter = riderTypeAdapter

        val frontWidthAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.width_array,
                R.layout.spinner_item
        )
        frontWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentBinding.frontWidth.adapter = frontWidthAdapter

        val rearWidthAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.width_array,
                R.layout.spinner_item
        )
        rearWidthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentBinding.rearWidth.adapter = rearWidthAdapter

        val frontLoadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.load_array,
                R.layout.spinner_item
        )
        frontLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentBinding.frontLoadUnits.adapter = frontLoadAdapter

        val rearLoadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.load_array,
                R.layout.spinner_item
        )
        rearLoadAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        contentBinding.rearLoadUnits.adapter = rearLoadAdapter

        contentBinding.riderType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                if (!itemSelectedFromProfile) {
                    when (parent.selectedItem) {
                        RACER -> {
                            contentBinding.frontLoad.setText(RACER_FRONT)
                            contentBinding.frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                            contentBinding.rearLoad.setText(RACER_REAR)
                            contentBinding.rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                        }
                        SPORT -> {
                            contentBinding.frontLoad.setText(SPORT_FRONT)
                            contentBinding.frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                            contentBinding.rearLoad.setText(SPORT_REAR)
                            contentBinding.rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                        }
                        else -> {
                            contentBinding.frontLoad.setText(CASUAL_FRONT)
                            contentBinding.frontLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                            contentBinding.rearLoad.setText(CASUAL_REAR)
                            contentBinding.rearLoadUnits.setSelection(0, java.lang.Boolean.TRUE)
                        }
                    }
                }

                itemSelectedFromProfile = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        contentBinding.calculateTirePressure.setOnClickListener {
            onCalculateTirePressure()
        }

        getProfile()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (frontLoadPercent > 0) {
            contentBinding.frontLoad.setText(fmt(frontLoadPercent))
        }

        if (rearLoadPercent > 0) {
            contentBinding.rearLoad.setText(fmt(rearLoadPercent))
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
            contentBinding.profileName.setText(profile.getString(Profiles.PROFILE_NAME))
            bodyWeightAmount = profile.getDouble(Profiles.BODY_WEIGHT)
            contentBinding.bodyWeight.setText(fmt(bodyWeightAmount))

            bikeWeightAmount = profile.getDouble(Profiles.BIKE_WEIGHT)
            contentBinding.bikeWeight.setText(fmt(bikeWeightAmount))

            when (profile.getString(Profiles.RIDER_TYPE)) {
                RACER -> contentBinding.riderType.setSelection(0)
                SPORT -> contentBinding.riderType.setSelection(1)
                CASUAL -> contentBinding.riderType.setSelection(2)
                else -> contentBinding.riderType.setSelection(2) // Default to Casual
            }

            // There has to be a better way than checking every value
            when (profile.getString(Profiles.FRONT_TIRE_WIDTH)) {
                "20" -> contentBinding.frontWidth.setSelection(0)
                "21" -> contentBinding.frontWidth.setSelection(1)
                "22" -> contentBinding.frontWidth.setSelection(2)
                "23" -> contentBinding.frontWidth.setSelection(3)
                "24" -> contentBinding.frontWidth.setSelection(4)
                "25" -> contentBinding.frontWidth.setSelection(5)
                "26" -> contentBinding.frontWidth.setSelection(6)
                "27" -> contentBinding.frontWidth.setSelection(7)
                "28" -> contentBinding.frontWidth.setSelection(8)
                else -> contentBinding.frontWidth.setSelection(5) //Default to 25mm
            }

            when (profile.getString(Profiles.REAR_TIRE_WIDTH)) {
                "20" -> contentBinding.rearWidth.setSelection(0)
                "21" -> contentBinding.rearWidth.setSelection(1)
                "22" -> contentBinding.rearWidth.setSelection(2)
                "23" -> contentBinding.rearWidth.setSelection(3)
                "24" -> contentBinding.rearWidth.setSelection(4)
                "25" -> contentBinding.rearWidth.setSelection(5)
                "26" -> contentBinding.rearWidth.setSelection(6)
                "27" -> contentBinding.rearWidth.setSelection(7)
                else -> contentBinding.rearWidth.setSelection(8)
            }

            frontLoadPercent = profile.getDouble(Profiles.FRONT_LOAD_PERCENT)
            contentBinding.frontLoad.setText(fmt(frontLoadPercent))

            rearLoadPercent = profile.getDouble(Profiles.REAR_LOAD_PERCENT)
            contentBinding.rearLoad.setText(fmt(rearLoadPercent))
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

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.closeDrawer(GravityCompat.START)

        return true
    }

    private fun onAddProfile(view: View) {
        val profileNameText = if (contentBinding.profileName.text.isNullOrEmpty()) {
            DEFAULT
        } else {
            contentBinding.profileName.text.toString()
        }
        val riderTypeText = contentBinding.riderType.selectedItem.toString()
        val frontTireWidth = contentBinding.frontWidth.selectedItem.toString()
        val rearTireWidth = contentBinding.rearWidth.selectedItem.toString()
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
        bodyWeightAmount = if (contentBinding.bodyWeight.text.isNullOrEmpty()) {
            0.0
        } else {
            contentBinding.bodyWeight.text.toString().toDouble()
        }
        bikeWeightAmount = if (contentBinding.bikeWeight.text.isNullOrEmpty()) {
            0.0
        } else {
            contentBinding.bikeWeight.text.toString().toDouble()
        }
        val frontLoadText = if (contentBinding.frontLoad.text.isNullOrEmpty()) {
            "0.0"
        } else {
            contentBinding.frontLoad.text.toString()
        }
        val rearLoadText = if (contentBinding.rearLoad.text.isNullOrEmpty()) {
            "0.0"
        } else {
            contentBinding.rearLoad.text.toString()
        }

        totalWeight = bodyWeightAmount + bikeWeightAmount
        contentBinding.totalWeightAmount.text = fmt(totalWeight)
        val frontLoadItem = contentBinding.frontLoadUnits.selectedItem.toString()
        if ("%" == frontLoadItem) {
            frontLoadPercent = frontLoadText.toDouble()
            frontLoadWeight = totalWeight * frontLoadPercent / 100
            contentBinding.frontLoadPercentAmount.text = String.format(
                    " " + getString(R.string.loadPercentLabel),
                    frontLoadText
            )
        } else {
            frontLoadPercent = frontLoadText.toDouble() * 100 / totalWeight
            frontLoadWeight = frontLoadText.toDouble()
            contentBinding.frontLoadPercentAmount.text = String.format(
                    " " + getString(R.string.loadPercentLabel),
                    fmt(frontLoadPercent)
            )
        }

        val rearLoadItem = contentBinding.rearLoadUnits.selectedItem.toString()
        if ("%" == rearLoadItem) {
            rearLoadPercent = rearLoadText.toDouble()
            rearLoadWeight = totalWeight * rearLoadPercent / 100
            contentBinding.rearLoadPercentAmount.text = String.format(
                    " " + getString(R.string.loadPercentLabel),
                    rearLoadText
            )
        } else {
            rearLoadPercent = rearLoadText.toDouble() * 100 / totalWeight
            rearLoadWeight = rearLoadText.toDouble()
            contentBinding.rearLoadPercentAmount.text = String.format(
                    " " + getString(R.string.loadPercentLabel),
                    fmt(rearLoadPercent)
            )
        }

        contentBinding.frontLoadWeightAmount.text = fmt(frontLoadWeight.roundToLong().toDouble())
        contentBinding.rearLoadWeightAmount.text = fmt(rearLoadWeight.roundToLong().toDouble())
        val frontTireCalculator = Calculator()
        contentBinding.frontTire.text = fmt(
                frontTireCalculator.psi(
                        frontLoadWeight,
                        contentBinding.frontWidth.selectedItem.toString()
                )
        )
        val rearTireCalculator = Calculator()
        contentBinding.rearTire.text = fmt(
                rearTireCalculator.psi(
                        rearLoadWeight,
                        contentBinding.rearWidth.selectedItem.toString()
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

