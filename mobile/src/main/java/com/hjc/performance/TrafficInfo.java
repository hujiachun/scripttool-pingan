/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.hjc.performance;

import android.net.TrafficStats;
import android.util.Log;
import com.hjc.util.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;


public class TrafficInfo {

	private boolean TAG = true;
	private static final int UNSUPPORTED = -1;
	private static final int UNSUPPORTED_0 = 0;
	private int uid;

	public TrafficInfo(int uid) {
		this.uid = uid;
	}

	/**
	 * get total network traffic, which is the sum of upload and download
	 * traffic.
	 * 
	 * @return total traffic include received and send traffic
	 */
	public long getTrafficInfo() throws IOException {

		long rcvTraffic = UNSUPPORTED;
		long sndTraffic = UNSUPPORTED;

		// Use getUidRxBytes and getUidTxBytes to get network traffic,these API
		// return both tcp and udp usage
		rcvTraffic = TrafficStats.getUidRxBytes(uid);
		sndTraffic = TrafficStats.getUidTxBytes(uid);

		if (rcvTraffic == UNSUPPORTED || sndTraffic == UNSUPPORTED ||
				rcvTraffic == UNSUPPORTED_0 || sndTraffic == UNSUPPORTED_0) {
			if (TAG) {
				Log.e(Constants.TAG, "该型号不支持API获取");
				TAG = false;
			}



			return getNet();
		}
		return rcvTraffic + sndTraffic;

	}


	private long getNet(){
		long rcvTraffic = UNSUPPORTED;
		long sndTraffic = UNSUPPORTED;
		RandomAccessFile rafRcv = null, rafSnd = null;
		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";

		try {
			rafRcv = new RandomAccessFile(rcvPath, "r");
			rafSnd = new RandomAccessFile(sndPath, "r");
			rcvTraffic = Long.parseLong(rafRcv.readLine());
			sndTraffic = Long.parseLong(rafSnd.readLine());
		} catch (FileNotFoundException e) {
			rcvTraffic = UNSUPPORTED;
			sndTraffic = UNSUPPORTED;
		} catch (NumberFormatException e) {
			Log.e(Constants.TAG, "NumberFormatException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(Constants.TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rafRcv != null) {
					rafRcv.close();
				}
				if (rafSnd != null)
					rafSnd.close();
			} catch (IOException e) {
				Log.e(Constants.TAG, "Close randomAccessFile exception: " + e.getMessage());
			}
		}

		if (rcvTraffic == UNSUPPORTED || sndTraffic == UNSUPPORTED) {

			return UNSUPPORTED;
		} else

			return rcvTraffic + sndTraffic / 1024;

	}


	private long readNet() throws IOException {

		//小米 不支持读取
		String netPath = "/proc/net/xt_qtaguid/stats";

		RandomAccessFile rafRcv = new RandomAccessFile(netPath, "r");
		String line = "";
		long traffic = 0;
		while ((line = rafRcv.readLine()) != null){
			Log.e(Constants.TAG, line);
			if(line.split("\\s+")[3].equals(String.valueOf(this.uid))){
				traffic += Integer.parseInt(line.split("\\s+")[5]) + Integer.parseInt(line.split("\\s+")[7]);

			}
		}
		return traffic;
	}
}
