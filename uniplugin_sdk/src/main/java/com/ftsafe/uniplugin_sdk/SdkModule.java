package com.ftsafe.uniplugin_sdk;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.ftsafe.audioKey.FtBankLoader;
import com.ftsafe.keyinterface.FTCallback;
import com.ftsafe.keyinterface.FTUserCertInfo;
import com.ftsafe.keyinterface.FTUserDeviceType;
import com.ftsafe.keyinterface.FTUserErrCode;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.util.List;

import static com.ftsafe.uniplugin_sdk.SdkModuleHookProxy.iKeyInterface;
import static com.ftsafe.uniplugin_sdk.SdkModuleHookProxy.mAppContext;
import static com.ftsafe.uniplugin_sdk.SdkModuleHookProxy.mJarPath;

public class SdkModule extends WXSDKEngine.DestroyableModule {
    public SdkModule(){
        Log.e("wzw","SdkModule================SdkModule(constructor) ===========");
        try{
            iKeyInterface = FtBankLoader.getInstance(mAppContext, mJarPath);
        }catch (Exception e){
            Log.e("wzw","SdkModule=== SdkModule(constructor)================"+e.toString());
        }
    }
//    @Override
//    public void onActivityCreate() {
//        super.onActivityCreate();
//        Log.e("wzw","SdkModule================onActivityCreate() ===========");
//        try{
//            Context aCtx1 = mWXSDKInstance.getContext();
//            Context aCtx2 = mWXSDKInstance.getUIContext();
//            iKeyInterface = FtBankLoader.getInstance(aCtx1, mJarPath);
//            if(iKeyInterface == null){
//                Log.e("wzw","iKeyInterface is null!!!!");
//            }else{
//                Log.e("wzw","iKeyInterface is not null!!!!");
//            }
//        }catch (Exception e){
//            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
//        }
//    }

    @Override
    public void destroy() {
        Log.e("wzw","SdkModule================destroy() ===========");
    }

    @JSMethod(uiThread = true)
    public  void getLibVerion(com.alibaba.fastjson.JSONObject options, final JSCallback jsCallback) {
        Log.e("wzw", "SdkModule=========getLibVerion==================");
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("sdkVersion", iKeyInterface.getLibVersion()+"");
        jsCallback.invoke(result);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @JSMethod(uiThread = true)
   public void initDevice(com.alibaba.fastjson.JSONObject opt,JSCallback jsCallback){
       Log.e("wzw", "SdkModule=========initDevice==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                int deviceType = (int)opt.get("deviceType");
                if(1 == (deviceType)){
                    if(ctx instanceof  Activity){
                        Activity activity = (Activity) ctx;
                        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE},0);
                    }
                    iKeyInterface.initialize(ctx, FTUserDeviceType.FT_COMMTYPE_BT4);
                }else{
                     if(ctx instanceof  Activity){
                          Activity activity = (Activity) ctx;
                          activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},0);
                     }
                    iKeyInterface.initialize(ctx, FTUserDeviceType.FT_COMMTYPE_AUDIO);
                }
            }
        }catch (Exception e){
            Log.e("wzw","initDevice()  iKeyInterface cause exception:::::::"+e.toString());
        }
   }

    @JSMethod(uiThread = true)
    public void getSn(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========getSn==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                 iKeyInterface.getSN(ctx, new FTCallback<String, Void>() {
                     @Override
                     public void onResult(FTUserErrCode ftUserErrCode, String s, Void aVoid) {
                         Log.e("wzw","RET=============="+String.format("%x",ftUserErrCode.getValue()));
                         Log.e("wzw","s=============="+s);
                            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
                            if(ftUserErrCode == FTUserErrCode.FT_SUCCESS){
                                result.put("ret", "0");
                                result.put("sn", s+"");
                                jsCallback.invoke(result);
                            }else{
                                int ret = ftUserErrCode.getValue();
                                result.put("ret", String.format("%x",ret));
                                result.put("err", s+"");
                                jsCallback.invoke(result);
                            }
                     }
                     @Override
                     public void onShowUI(UICallbackType uiCallbackType, String s) {
                         if (uiCallbackType != UICallbackType.FT_CLOSE_LOADING) {
                             pgd.setMessage("正在连接,请稍后...");
                             pgd.show();
                         } else {
                             pgd.dismiss();
                         }
                     }
                 });

            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }
    @JSMethod(uiThread = true)
    public void connect(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========connect==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                Log.e("wzw","opt =============="+opt.toString());
                iKeyInterface.connectKey(ctx,  new FTCallback<String, Void>() {
                    @Override
                    public void onResult(FTUserErrCode ftUserErrCode, String s, Void aVoid) {
                        Log.e("wzw","RET=============="+String.format("%x",ftUserErrCode.getValue()));
                        Log.e("wzw","s=============="+s);
                        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
                        if(ftUserErrCode == FTUserErrCode.FT_SUCCESS){
                            result.put("ret", "0");
                            result.put("result", s+"");
                            jsCallback.invoke(result);
                        }else{
                            int ret = ftUserErrCode.getValue();
                            result.put("ret", String.format("%x",ret));
                            result.put("result", s+"");
                            jsCallback.invoke(result);
                        }
                    }
                    @Override
                    public void onShowUI(UICallbackType uiCallbackType, String s) {
                        if (uiCallbackType != UICallbackType.FT_CLOSE_LOADING) {
                            pgd.setMessage("正在连接,请稍后...");
                            pgd.show();
                        } else {
                            pgd.dismiss();
                        }
                    }
                });

            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }
    @JSMethod(uiThread = true)
    public void connectWithSn(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========connectWithSn==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                Log.e("wzw","opt =============="+opt.toString());
                String sn = (String)opt.get("sn");
                int timeout = (int)opt.get("timeout");
                iKeyInterface.connectKey(ctx, sn,timeout,new FTCallback<String, Void>() {
                    @Override
                    public void onResult(FTUserErrCode ftUserErrCode, String s, Void aVoid) {
                        Log.e("wzw","RET=============="+String.format("%x",ftUserErrCode.getValue()));
                        Log.e("wzw","s=============="+s);
                        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
                        if(ftUserErrCode == FTUserErrCode.FT_SUCCESS){
                            result.put("ret", "0");
                            result.put("result", s+"");
                            jsCallback.invoke(result);
                        }else{
                            int ret = ftUserErrCode.getValue();
                            result.put("ret", String.format("%x",ret));
                            result.put("result", s+"");
                            jsCallback.invoke(result);
                        }
                    }
                    @Override
                    public void onShowUI(UICallbackType uiCallbackType, String s) {
                        if (uiCallbackType != UICallbackType.FT_CLOSE_LOADING) {
                            pgd.setMessage("正在连接,请稍后...");
                            pgd.show();
                        } else {
                            pgd.dismiss();
                        }
                    }
                });

            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }


    @JSMethod(uiThread = true)
    public void disconnect(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========disconnect==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                Log.e("wzw","opt =============="+opt.toString());
                iKeyInterface.disConnectKey();
            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }

    @JSMethod(uiThread = true)
    public void emulateCerts(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========connectWithSn==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                Log.e("wzw","opt =============="+opt.toString());
                iKeyInterface.enumCert(ctx, new FTCallback<String, List<FTUserCertInfo>>() {
                    @Override
                    public void onResult(FTUserErrCode ftUserErrCode, String s, List<FTUserCertInfo> ftUserCertInfos) {
                        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
                        if(ftUserErrCode == FTUserErrCode.FT_SUCCESS){
                            result.put("certs", ftUserCertInfos);
                        }
                        result.put("ret", String.format("%x",ftUserErrCode.getValue()));
                        jsCallback.invoke(result);
                    }

                    @Override
                    public void onShowUI(UICallbackType uiCallbackType, String s) {
                        if (uiCallbackType != UICallbackType.FT_CLOSE_LOADING) {
                            pgd.setMessage("正在获取证书,请稍后...");
                            pgd.show();
                        } else {
                            pgd.dismiss();
                        }
                    }
                });
            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }


    @JSMethod(uiThread = true)
    public void changePin(com.alibaba.fastjson.JSONObject opt, final JSCallback jsCallback){
        Log.e("wzw", "SdkModule=========connectWithSn==================");
        try{
            Context ctx = mWXSDKInstance.getContext();
            final ProgressDialog pgd = new ProgressDialog(ctx);
            if(iKeyInterface == null){
                Log.e("wzw","iKeyInterface is null!!!!");
            }else{
                Log.e("wzw","opt =============="+opt.toString());
                String oldPin = (String)opt.get("oldPin");
                String newPin = (String)opt.get("newPin");
                iKeyInterface.changePin(ctx,oldPin,newPin, new FTCallback<String, Integer>() {
                    @Override
                    public void onResult(FTUserErrCode ftUserErrCode, String s, Integer integer) {
                        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
                        Log.e("wzw","opt =====ftUserErrCode========="+String.format("%x",ftUserErrCode.getValue()));
                        Log.e("wzw","opt =====s========="+s);
                        Log.e("wzw","opt =====integer========="+integer);

                        result.put("ret", String.format("%x",ftUserErrCode.getValue()));
                        jsCallback.invoke(result);
                    }



                    @Override
                    public void onShowUI(UICallbackType uiCallbackType, String s) {
                        if (uiCallbackType != UICallbackType.FT_CLOSE_LOADING) {
                            pgd.setMessage("正在修改密码,请稍后...");
                            pgd.show();
                        } else {
                            pgd.dismiss();
                        }
                    }
                });
            }
        }catch (Exception e){
            Log.e("wzw","iKeyInterface cause exception:::::::"+e.toString());
        }
    }
}
