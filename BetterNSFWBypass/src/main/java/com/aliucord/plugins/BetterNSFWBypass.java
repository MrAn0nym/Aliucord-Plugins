package com.aliucord.plugins;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.user.NsfwAllowance;
import com.discord.models.user.MeUser;
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
		patcher.patch(MeUser.class.getDeclaredMethod("getHasBirthday"),
				new InsteadHook(param -> true));
		patcher.patch(WidgetHomeModel.class.getDeclaredMethod("getNsfwAllowed"),
				new InsteadHook(param -> NsfwAllowance.ALLOWED));

		if (settings.getBool(ALWAYSCONFIRM, true)) {
			patcher.patch(WidgetHomeModel.class.getDeclaredMethod("isNsfwUnConsented"),
					new InsteadHook(param -> true));
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
			alwaysConfirm.setChecked(settings.getBool(ALWAYSCONFIRM, true));
			alwaysConfirm.setOnCheckedListener(checked -> {
				settings.setBool(ALWAYSCONFIRM, checked);
				Utils.showToast("Please restart Aliucord to apply");
			});
			addView(alwaysConfirm);
		}
	}
}
