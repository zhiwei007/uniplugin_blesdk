package com.ftsafe.uniplugin_sdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.ftsafe.audioKey.FtBankLoader;
import com.ftsafe.keyinterface.IKeyInterface;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;

import java.io.File;
import java.io.IOException;

import io.dcloud.weex.AppHookProxy;

/*
* 1.监听应用声明周期
* 2.要想起作用，必须在宿主程序 app/src/main/assets/data/dcloud_uniplugins.json 中注册该类
* 如下：
*     {
      "hooksClass": "com.ftsafe.uniplugin_sdk.SdkModuleHookProxy",
      "plugins": [
        {
          "type": "module",
          "name": "SdkModule",
          "class": "com.ftsafe.uniplugin_sdk.SdkModule"
        }
      ]
    }
* */
public final class SdkModuleHookProxy implements AppHookProxy {
    public static String mJarPath = "";
    public static IKeyInterface iKeyInterface;
    public static Context mAppContext;
    @Override
    public void onCreate(Application application) {
        try {
            WXSDKEngine.registerModule("SdkModule",SdkModule.class,true);
        } catch (WXException e) {
            e.printStackTrace();
            Log.e("wzw","SdkModuleHookProxy========= =onCreate()   "+e.toString());
        }

        Log.e("wzw","SdkModuleHookProxy===============================onCreate()");
        mAppContext = application.getApplicationContext();
        mJarPath = "/data/data/" + mAppContext.getPackageName() + "/ftKey_Api.jar";
        Log.e("wzw", "mJarPath======>" + mJarPath);

        File file = new File(mJarPath);
        if (file != null && file.exists()) {
            Log.e("wzw", "11 ftKey_Api.jar  存在!!!!!!!!!!");
        } else {
            Log.e("wzw", "11  ftKey_Api.jar  不存在!!!!!!!!!!");
        }

        try {
            FileTool.makeFilePath(application, "ftKey_Api.jar");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            File file2 = new File(mJarPath);
            if (file2 != null && file2.exists()) {
                Log.e("wzw", "22  ftKey_Api.jar  存在!!!!!");
                Log.e("wzw", "22  ftKey_Api.jar 路径::::" + file2.getAbsolutePath());
            } else {
                Log.e("wzw", "22  ftKey_Api.jar  不存在!!!!!!!!!!");
            }

        }
    }
}
