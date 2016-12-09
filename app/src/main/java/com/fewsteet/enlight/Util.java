package com.fewsteet.enlight;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

/**
 * Created by peter on 12/8/16.
 */

public class Util {
    public static InetAddress getBroadcastAddress(Context ctx) throws IOException {
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        InetAddress inetAddress = InetAddress.getByAddress(extractBytes(dhcp.ipAddress));
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        InetAddress broadcast;
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            if(address.getAddress().equals(inetAddress)) {
                return address.getBroadcast();
            }
        }

        return InetAddress.getByName("255.255.255.255");
    }
    private static byte[] extractBytes(int addr) {
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (addr >> (k * 8));
        return quads;
    }
    public static <T> T readFromFile(Context context, String filename, Type t) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (IOException e) {
            return null;
        }
        Gson gson = new Gson();
        Log.d("Util", ret);
        return gson.fromJson(ret, t);
    }
    public static void writeToFile(Context context, String filename, Object contents) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(new Gson().toJson(contents));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Utils", "File write failed: " + e.toString());
        }
    }
}
