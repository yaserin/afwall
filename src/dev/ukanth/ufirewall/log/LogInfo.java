/**
 * Capture Logs from dmesg and return the formatted string
 * 
 * 
 * Copyright (C) 2014  Umakanthan Chandran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Umakanthan Chandran
 * @version 1.0
 */

package dev.ukanth.ufirewall.log;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import dev.ukanth.ufirewall.Api;
import dev.ukanth.ufirewall.Api.PackageInfoData;
import dev.ukanth.ufirewall.R;

public class LogInfo {
	String uidString;
	String in;
	String out;
	String proto;
	String spt;
	String dst;
	String len;
	String src;
	String dpt;
	String timestamp;
	int totalBlocked; 
	

	public static void parseLog(Context ctx, String dmesg, TextView textView) {
		final BufferedReader r = new BufferedReader(new StringReader(dmesg.toString()));
		final Integer unknownUID = -99;
		StringBuilder address = new StringBuilder();
		String line;
		int start, end;
		Integer uid;
		String out, src, dst, proto, spt, dpt, len;
		HashMap<Integer,String> appNameMap = new HashMap<Integer, String>();
		LogInfo logInfo = null;
		
		/*SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		destFormat.setTimeZone(TimeZone.getDefault());*/
		
		final List<PackageInfoData> apps = Api.getApps(ctx,null);
		try {
			while ((line = r.readLine()) != null) {
				if (line.indexOf("{AFL}") == -1) continue;
				uid = unknownUID;
				
				if (((start=line.indexOf("UID=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					uid = Integer.parseInt(line.substring(start+4, end));
				}
				
				logInfo = new LogInfo();
				
				if (((start=line.indexOf("DST=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					dst = line.substring(start+4, end);
					logInfo.dst = dst;
				}
				
			/*	String date = line.substring(0,19);
				Date parsed = sourceFormat.parse(date); 
				logInfo.timestamp = destFormat.format(parsed);*/
				
				
				if (((start=line.indexOf("DPT=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					dpt = line.substring(start+4, end);
					logInfo.dpt = dpt;
				}
				
				if (((start=line.indexOf("SPT=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					spt = line.substring(start+4, end);
					logInfo.spt = spt;
				}
				
				if (((start=line.indexOf("PROTO=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					proto = line.substring(start+6, end);
					logInfo.proto = proto;
				}
				
				if (((start=line.indexOf("LEN=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					len = line.substring(start+4, end);
					logInfo.len = len;
				}
				
				if (((start=line.indexOf("SRC=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					src = line.substring(start+4, end);
					logInfo.src = src;
				}
				
				if (((start=line.indexOf("OUT=")) != -1) && ((end=line.indexOf(" ", start)) != -1)) {
					out = line.substring(start+4, end);
					logInfo.out = out;
				}
				String appName = "";
				if(uid != -99) {
					if(!appNameMap.containsKey(uid)) {
						appName = ctx.getPackageManager().getNameForUid(uid);
						for (PackageInfoData app : apps) {
							if (app.uid == uid) {
								appName = app.names.get(0);
								break;
							}
						}
						appNameMap.put(uid, appName);
					} else {
						appName = appNameMap.get(uid);
					}
				} else {
					appName = ctx.getString(R.string.kernel_item);
				}
				address = new StringBuilder();
				address.append(" " + appName + "(" + uid  + ")" + " ");
				address.append(logInfo.dst + ":" +  logInfo.dpt + "\n" );
				textView.append(address.toString());
			}
			
			
		/*	String appName = "";
			int appId = -99;
			for (Map.Entry<Integer, ArrayList<LogInfo>> entry : logEntityMap.entrySet())
			{
				appId = entry.getKey();
				if(appId != -99) {
					for (PackageInfoData app : apps) {
						if (app.uid == appId) {
							appName = app.names.get(0);
							break;
						}
					}
				} else {
					appName = ctx.getString(R.string.kernel_item);
				}
				
				StringBuilder address = new StringBuilder();
				res.append("Application = " +  appName + "(" + appId  + ")" + "\n");
				address.append("\n");
				Set<String> addedEntry = new HashSet<String>();
				for(LogInfo info : entry.getValue()) {
					String uniqueKey = appId + ":" + info.dst + ":" +  info.dpt;
					if(totalCount.containsKey(uniqueKey)) {
						if(!addedEntry.contains(uniqueKey)) {
							address.append( "[" + info.proto + "]" + info.dst + ":" +  info.dpt + "(" +  totalCount.get(uniqueKey) + ")" + "\n" );
							addedEntry.add(uniqueKey);
							info.totalBlocked = totalCount.get(uniqueKey); 
						}
					} else {
						if(!addedEntry.contains(uniqueKey)) {
							address.append( "[" + info.proto + "]" + info.dst + ":" +  info.dpt + "(" +  1 + ")" + "\n" );
							addedEntry.add(uniqueKey);
						}
					}
				}
				res.append(address.toString());
				res.append("\n\t-------------------------\n");
			}*/
		} catch (Exception e) {
			Log.e(Api.TAG, e.getMessage());
		}
		
	}
	

	public static String parseLogs(String result,final Context ctx) {

		final Integer unknownUID = -99;
		StringBuilder address = new StringBuilder();
		int start, end;
		Integer uid;
		String out, src, dst, proto, spt, dpt, len;
		LogInfo logInfo = null;

		SimpleDateFormat sourceFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat destFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		destFormat.setTimeZone(TimeZone.getDefault());
		HashMap<Integer,String> appNameMap = new HashMap<Integer, String>();
		final List<PackageInfoData> apps = Api.getApps(ctx,null);
		int pos = 0;
		try {
			while ((pos = result.indexOf("{AFL}", pos)) > -1) {
				if (result.indexOf("{AFL}") == -1)
					continue;
				uid = unknownUID;

				if (((start = result.indexOf("UID=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					uid = Integer.parseInt(result.substring(start + 4, end));
				}

				logInfo = new LogInfo();

				if (((start = result.indexOf("DST=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					dst = result.substring(start + 4, end);
					logInfo.dst = dst;
				}

				if (((start = result.indexOf("DPT=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					dpt = result.substring(start + 4, end);
					logInfo.dpt = dpt;
				}

				if (((start = result.indexOf("SPT=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					spt = result.substring(start + 4, end);
					logInfo.spt = spt;
				}

				if (((start = result.indexOf("PROTO=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					proto = result.substring(start + 6, end);
					logInfo.proto = proto;
				}

				if (((start = result.indexOf("LEN=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					len = result.substring(start + 4, end);
					logInfo.len = len;
				}

				if (((start = result.indexOf("SRC=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					src = result.substring(start + 4, end);
					logInfo.src = src;
				}

				if (((start = result.indexOf("OUT=")) != -1)
						&& ((end = result.indexOf(" ", start)) != -1)) {
					out = result.substring(start + 4, end);
					logInfo.out = out;
				}
				String appName = "";
				if(uid != -99) {
					if(!appNameMap.containsKey(uid)) {
						appName = ctx.getPackageManager().getNameForUid(uid);
						for (PackageInfoData app : apps) {
							if (app.uid == uid) {
								appName = app.names.get(0);
								break;
							}
						}
						appNameMap.put(uid, appName);
					} else {
						appName = appNameMap.get(uid);
					}
				} else {
					appName = ctx.getString(R.string.kernel_item);
				}
				address = new StringBuilder();
				address.append(" " + appName + "(" + uid  + ")" + " ");
				address.append(logInfo.dst + ":" +  logInfo.dpt + "\n" );
				return address.toString();
				
			}
		} catch (Exception e) {
			Log.e(Api.TAG, e.getMessage());
		}
		return address.toString();
	}
}