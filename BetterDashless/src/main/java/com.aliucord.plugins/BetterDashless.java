/*
 * The methods I needed to patch and way to get the channel name are copied from https://github.com/Sepruko/aliucord-plugins/blob/main/plugins/Dashless/src/main/java/com/aliucord/plugins/Dashless.kt
 * Those parts are released under GPL3 (https://github.com/Sepruko/aliucord-plugins/blob/main/LICENSE)
 *
 * Everything else is created by me and falls under the repos license
 */

package com.aliucord.plugins;

import android.content.Context;
import android.widget.TextView;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.utils.ReflectUtils;
import com.discord.databinding.WidgetChannelsListItemChannelBinding;
import com.discord.databinding.WidgetHomeBinding;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter.ItemChannelText;
import com.discord.widgets.channels.list.items.ChannelListItem;
import com.discord.widgets.home.WidgetHome;
import com.discord.widgets.home.WidgetHomeHeaderManager;
import com.discord.widgets.home.WidgetHomeModel;

@AliucordPlugin
@SuppressWarnings("unused")
public class BetterDashless extends Plugin {
	
	@Override
	public void start(Context context) {
		
		patcher.patch(ItemChannelText.class, "onConfigure",
				new Class<?>[]{int.class, ChannelListItem.class}, new Hook(callFrame -> {
					ItemChannelText _this = (ItemChannelText) callFrame.thisObject;
					
					WidgetChannelsListItemChannelBinding binding = null;
					try {
						binding = (WidgetChannelsListItemChannelBinding) ReflectUtils
								.getField(_this, "binding");
					} catch (NoSuchFieldException | IllegalAccessException e) {
						e.printStackTrace();
					}
					
					assert binding != null;
					TextView channelName = binding.getRoot()
							.findViewById(Utils.getResId("channels_item_channel_name", "id"));
					
					channelName.setText(channelName.getText().toString().replace("-", " "));
				}));
		
		patcher.patch(WidgetHomeHeaderManager.class, "configure",
				new Class<?>[]{WidgetHome.class, WidgetHomeModel.class, WidgetHomeBinding.class},
				new Hook(callFrame -> {
					WidgetHomeHeaderManager _this = (WidgetHomeHeaderManager) callFrame.thisObject;
					WidgetHome widgetHome = (WidgetHome) callFrame.args[0];
					
					widgetHome.setActionBarTitle(
							((WidgetHomeModel) callFrame.args[1]).getChannel().m().replace("-", " "));
				}));
	}
	
	@Override
	public void stop(Context context) {
		patcher.unpatchAll();
	}
}