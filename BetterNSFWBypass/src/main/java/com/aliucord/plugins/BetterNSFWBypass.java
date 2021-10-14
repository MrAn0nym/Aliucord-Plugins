/*
 * The methods I needed to patch are copied from https://github.com/swishs-client-mod-plugins/aliucord-plugins/blob/main/NSFWGateBypass/src/main/java/com/aliucord/plugins/NSFWGateBypass.java
 * Those parts are released under GPL3 (https://github.com/swishs-client-mod-plugins/aliucord-plugins/blob/main/LICENSE)
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 *
 * Everything else is created by me and falls under the repos license
 */

package com.aliucord.plugins;

import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.discord.api.user.NsfwAllowance;
import com.discord.models.user.MeUser;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterNSFWBypass extends Plugin {

  @Override
  public void start(Context context) throws NoSuchMethodException {
    patcher.patch(MeUser.class.getDeclaredMethod("getNsfwAllowance"),
        new PreHook(callFrame -> callFrame.setResult(
            NsfwAllowance.ALLOWED)));
    patcher.patch(MeUser.class.getDeclaredMethod("getHasBirthday"),
        new PreHook(callFrame -> callFrame.setResult(true)));
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
  }
}