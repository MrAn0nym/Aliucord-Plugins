package com.aliucord.plugins;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.user.NsfwAllowance;
import com.discord.views.CheckedSetting;
import com.discord.views.CheckedSetting.ViewType;
import com.discord.widgets.home.WidgetHomeModel;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterNSFWBypass extends Plugin {
	
	public static final String ALWAYSCONFIRM = "Alwaysconfirm";
	
	public BetterNSFWBypass() {
		settingsTab = new SettingsTab(BetterNSFWBypassSettings.class, SettingsTab.Type.BOTTOM_SHEET)
				.withArgs(settings);
	}
	
	@Override
	public void start(Context context) throws NoSuchMethodException {
		patcher.patch(WidgetHomeModel.class.getDeclaredMethod("getNsfwAllowed"),
				new PreHook(callFrame -> callFrame.setResult(NsfwAllowance.ALLOWED)));
		var isNsfwUnConsented = patcher
				.patch(WidgetHomeModel.class.getDeclaredMethod("isNsfwUnConsented"),
						new PreHook(callFrame -> callFrame.setResult(true)));
		if (!settings.getBool(ALWAYSCONFIRM, true)) {
			isNsfwUnConsented.run();
		}
		
	}
	
	@Override
	public void stop(Context context) {
		patcher.unpatchAll();
	}
	
	public static class BetterNSFWBypassSettings extends BottomSheet {
		
		private final SettingsAPI settings;
		
		public BetterNSFWBypassSettings(SettingsAPI settings) {
			this.settings = settings;
		}
		
		@Override
		public void onViewCreated(View view, Bundle bundle) {
			
			super.onViewCreated(view, bundle);
			CheckedSetting alwaysConfirm = Utils
					.createCheckedSetting(requireContext(), ViewType.SWITCH, "NSFW Warning",
							"Displays a warning before showing the NSFW channel.");
			alwaysConfirm.setChecked(true);
			alwaysConfirm.setChecked(settings.getBool(ALWAYSCONFIRM, true));
			alwaysConfirm.setOnCheckedListener(checked -> {
				settings.setBool(ALWAYSCONFIRM, checked);
				Utils.showToast("Please restart Aliucord to apply");
			});
			addView(alwaysConfirm);
		}
	}
}