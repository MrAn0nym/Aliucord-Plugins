package com.aliucord.plugins;

import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.discord.api.user.NsfwAllowance;
import com.discord.widgets.home.WidgetHomeModel;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterNSFWBypass extends Plugin {

  @Override
  public void start(Context context) throws NoSuchMethodException, NoSuchFieldException {
    patcher.patch(WidgetHomeModel.class.getDeclaredMethod("getNsfwAllowed"),
        new PreHook(callFrame -> callFrame
            .setResult(NsfwAllowance.ALLOWED)));
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
  }
}