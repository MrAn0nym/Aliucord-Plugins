/*
 * The methods I needed to patch and way to get the channel name are copied from https://github.com/Sepruko/aliucord-plugins/blob/main/plugins/Dashless/src/main/java/com/aliucord/plugins/Dashless.kt
 * Those parts are released under GPL3 (https://github.com/Sepruko/aliucord-plugins/blob/main/LICENSE)
 *
 * Everything else is created by me and falls under the repos license
 */

package com.aliucord.plugins;

import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.discord.api.channel.Channel;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterDashless extends Plugin {

  @Override
  public void start(Context context) throws NoSuchMethodException {
    patcher.patch(Channel.class.getDeclaredMethod("m"), new Hook(
        callFrame -> callFrame.setResult(callFrame.getResult().toString().replace("-", " "))));
  }

  @Override
  public void stop(Context context) {
    patcher.unpatchAll();
  }
}