package com.github.MrAn0nym;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

@SuppressWarnings("unused")
@AliucordPlugin
public class Timestamps extends Plugin {
	
	@RequiresApi(api = VERSION_CODES.O)
	@Override
	public void start(Context context) {
		var modes = Arrays.asList(Utils.createCommandChoice("Short Time", "t"),
				Utils.createCommandChoice("Long Time", "T"), Utils.createCommandChoice("Short Date", "d"),
				Utils.createCommandChoice("Long Date", "D"),
				Utils.createCommandChoice("Short Date/Time", "f"),
				Utils.createCommandChoice("Long Date/Time", "F"),
				Utils.createCommandChoice("Relative Time", "R"));
		
		ZoneId.getAvailableZoneIds();
		var timezones = new ArrayList<CommandChoice>();
		for (String s : ZoneId.getAvailableZoneIds()) {
			timezones.add(Utils.createCommandChoice(s, s));
		}
		
		var options = Arrays
				.asList(Utils.createCommandOption(ApplicationCommandType.INTEGER, "yyyy", "The year"),
						Utils.createCommandOption(ApplicationCommandType.INTEGER, "MM", "The month"),
						Utils.createCommandOption(ApplicationCommandType.INTEGER, "dd", "The day"),
						Utils.createCommandOption(ApplicationCommandType.INTEGER, "HH", "The hour"),
						Utils.createCommandOption(ApplicationCommandType.INTEGER, "mm", "The minute"),
						Utils.createCommandOption(ApplicationCommandType.INTEGER, "ss", "The second"),
						Utils.createCommandOption(ApplicationCommandType.STRING, "AM/PM", "If the timestamp should be AM or PM", null, false, "AM". {0, 1, 3}, {"AM", "PM"}), Utils
								.createCommandOption(ApplicationCommandType.STRING, "z", "The timezone", null,
										false, false, null, timezones, null, false), Utils
								.createCommandOption(ApplicationCommandType.STRING, "mode",
										"The mode in which discord will display the date", null, false, false, null,
										modes, null, false), Utils
								.createCommandOption(ApplicationCommandType.STRING, "message",
										"Append the timestamp to your message"), Utils
								.createCommandOption(ApplicationCommandType.BOOLEAN, "send",
										"Send the timestamp instead of copying it"));
		
		commands
				.registerCommand("timestamp", "Generates a unix timestamp and copies it to the clipboard",
						options, ctx -> {
							String result;
							ZoneId zone;
							ClipboardManager clipboard = (ClipboardManager) context
									.getSystemService(Context.CLIPBOARD_SERVICE);
							
							var year = ctx.getLongOrDefault("yyyy", Calendar.getInstance().get(Calendar.YEAR));
							var month = ctx
									.getLongOrDefault("MM", Calendar.getInstance().get(Calendar.MONTH) + 1);
							var day = ctx
									.getLongOrDefault("dd", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
							var hour = ctx
									.getLongOrDefault("HH", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
							var minute = ctx.getLongOrDefault("mm", Calendar.getInstance().get(Calendar.MINUTE));
							var second = ctx.getLongOrDefault("ss", Calendar.getInstance().get(Calendar.SECOND));
							var doPm = ctx.getStringOrDefault("AM/PM", "AM").equals("PM");
							var zoneString = ctx.getStringOrDefault("z", ZoneId.systemDefault().toString());
							var mode = ctx.getStringOrDefault("mode", "f");
							var message = ctx.getStringOrDefault("message", "") + " ";
							message = message.equals(" ") ? "" : message;
							var send = ctx.getBoolOrDefault("send", false);
							
							if (doPm && hour <= 12) hour += 12;
							
							try {
								zone = ZoneId.of(zoneString);
							} catch (DateTimeException e) {
								return new CommandsAPI.CommandResult("Invalid timezone identifier", null, false);
							}
							
							try {
								// Create ZonedDateTime object
								ZonedDateTime time = ZonedDateTime
										.of((int) year, (int) month, (int) day, (int) hour, (int) minute, (int) second,
												0, zone);
								
								// Create timestamp
								String timestamp =
										"<t:" + time.toEpochSecond() + ":" + ctx.getStringOrDefault("mode", "f") + ">";
								
								// Send or copy the message + timestamp
								if (send) {
									result = message + timestamp;
								} else {
									Utils.setClipboard("Discord timestamp", message + timestamp);
									result = "Copied \"" + message + timestamp + "\" to your clipboard";
								}
								
							} catch (DateTimeException e) {
								
								// Catch invalid dates/times and send the exception message to the user
								return new CommandsAPI.CommandResult(
										"Invalid date\n```java\n" + e.getMessage() + "\n```", null, false);
							}
							
							return new CommandsAPI.CommandResult(result, null, send);
						});
	}
	
	@Override
	public void stop(Context context) {
		commands.unregisterAll();
	}
}
