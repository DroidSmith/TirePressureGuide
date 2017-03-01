package com.droidsmith.tireguide.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.droidsmith.tireguide.TireGuideActivity;

/**
 * The initial splash screen.
 */
public class SplashActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, TireGuideActivity.class);
		startActivity(intent);
		finish();
	}
}
