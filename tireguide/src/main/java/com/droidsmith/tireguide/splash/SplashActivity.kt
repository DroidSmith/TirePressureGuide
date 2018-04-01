package com.droidsmith.tireguide.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.droidsmith.tireguide.TireGuideActivity

/**
 * The initial splash screen.
 */
class SplashActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val intent = Intent(this, TireGuideActivity::class.java)
		startActivity(intent)
		finish()
	}
}
