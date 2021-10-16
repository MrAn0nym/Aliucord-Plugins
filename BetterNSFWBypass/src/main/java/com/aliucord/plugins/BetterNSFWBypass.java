package com.aliucord.plugins;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.patcher.PreHook;
import com.aliucord.widgets.BottomSheet;
import com.discord.api.user.NsfwAllowance;
import com.discord.views.CheckedSetting;
import com.discord.views.CheckedSetting.ViewType;
import com.discord.widgets.home.WidgetHomeModel;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterNSFWBypass extends Plugin {

  public BetterNSFWBypass() {
    settingsTab = new SettingsTab(BetterNSFWBypassSettings.class, SettingsTab.Type.BOTTOM_SHEET)
        .withArgs(settings);
  }

  @Override
  public void start(Context context) throws NoSuchMethodException, NoSuchFieldException {
    patcher.patch(WidgetHomeModel.class.getDeclaredMethod("getNsfwAllowed"),
        new PreHook(callFrame -> callFrame
            .setResult(NsfwAllowance.ALLOWED)));
    patcher.patch(WidgetHomeModel.class.getDeclaredMethod("isNsfwUnConsented"),
        (settings.getBool("Alwaysconfirm", true)) ? new PreHook(
            callFrame -> callFrame.setResult(true)) : InsteadHook.DO_NOTHING);
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

    public void onViewCreated(View view, Bundle bundle) {

      super.onViewCreated(view, bundle);
      CheckedSetting always_confirm = Utils
          .createCheckedSetting(requireContext(), ViewType.SWITCH, "NSFW Warning",
              "Displays a warning before showing the NSFW channel.");
      always_confirm.setChecked(true);
      always_confirm.setChecked(settings.getBool("Alwaysconfirm", true));
      always_confirm.setOnCheckedListener(checked -> {
        settings.setBool("Alwaysconfirm", checked);
        Utils.showToast("Please restart Aliucord to apply");
      });
      addView(always_confirm);
    }
  }
}