package com.android.ftsafe.audioKey;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import com.ftsafe.keyinterface.IKeyInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FtBankLoader {
    private Context mCtx = null;
    private static IKeyInterface iUkey = null;
//    private static Object mLock = null;

    private  FtBankLoader() {
    }

    private static boolean isInstalled(Context context, String mJarPath) {
        File mFile = new File(mJarPath);
        return mFile != null && mFile.exists();
    }

    public static IKeyInterface getInstance(Context context, String aJarPath) throws NullPointerException {
        if (!isInstalled(context, aJarPath)) {
            throw new NullPointerException("The plugin can not be found!");
        } else {
            if (iUkey == null) {
                try {
                    iUkey = getIUKeyInterface(context, aJarPath);
                    return iUkey;
                } catch (SecurityException var3) {
                    var3.printStackTrace();
                } catch (IllegalArgumentException var4) {
                    var4.printStackTrace();
                } catch (ClassNotFoundException var5) {
                    var5.printStackTrace();
                } catch (NoSuchMethodException var6) {
                    var6.printStackTrace();
                } catch (Exception var7) {
                    var7.printStackTrace();
                }
            }

            return iUkey;
        }
    }

    private static IKeyInterface getIUKeyInterface(final Context context, final String mJarPath) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, Exception {
      /*  mLock = new Object();
        (new Thread(new Runnable() {
            public void run() {
                Looper.prepare();

                try {
                    FtBankLoader.iUkey = FtBankLoader.loadPlugin(context, mJarPath);
                } catch (SecurityException var2) {
                    var2.printStackTrace();
                } catch (IllegalArgumentException var3) {
                    var3.printStackTrace();
                } catch (ClassNotFoundException var4) {
                    var4.printStackTrace();
                } catch (NoSuchMethodException var5) {
                    var5.printStackTrace();
                } catch (IllegalAccessException var6) {
                    var6.printStackTrace();
                } catch (InvocationTargetException var7) {
                    var7.printStackTrace();
                } catch (IOException var8) {
                    var8.printStackTrace();
                }

            }
        })).start();
        synchronized(mLock) {
            try {
                mLock.wait();
            } catch (Exception var4) {
            }
        }
*/
        try {
            FtBankLoader.iUkey = FtBankLoader.loadPlugin(context, mJarPath);
        } catch (SecurityException var2) {
            var2.printStackTrace();
        } catch (IllegalArgumentException var3) {
            var3.printStackTrace();
        } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
        } catch (NoSuchMethodException var5) {
            var5.printStackTrace();
        } catch (IllegalAccessException var6) {
            var6.printStackTrace();
        } catch (InvocationTargetException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }
        if (iUkey == null) {
            throw new Exception("File couldn't  be found");
        } else {
            return iUkey;
        }
    }

    private static IKeyInterface loadPlugin(Context context, String jarPath) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        String dexPath = context.getDir("ft_odex", 0).getAbsolutePath();
        Class<?> cls = null;
        File libs = context.getDir("libs", 0);
        String CPU_API = Build.CPU_ABI;
        String out = libs + File.separator + "libFT_ClassLoader.so";
        getAssetsFile(context, "ft_" + CPU_API + ".ini", out);
        System.load(libs + File.separator + "libFT_ClassLoader.so");
        if (cls == null) {
            String[] p = jarPath.split("/");
            String oldFileName = p[p.length - 1];
            cls = L1(jarPath, dexPath, oldFileName, context);
            if (cls == null) {
                throw new NullPointerException("ft class not found!");
            }
        }

        Method methodCopySo = cls.getDeclaredMethod("copySo", Context.class);
        methodCopySo.setAccessible(true);
        methodCopySo.invoke((Object)null, context);
        Method methodonCreate = cls.getDeclaredMethod("getInstance", Context.class);
        methodonCreate.setAccessible(true);
        iUkey = (IKeyInterface)methodonCreate.invoke((Object)null, context);
        if (iUkey == null) {
            Log.e("ftsafe_log", "ftsafe driver load failed!");
            throw new NullPointerException();
        }

        int end = jarPath.lastIndexOf(47);
        String path = jarPath.substring(0, end + 1);
        deleteFile(path, "test.jar");
        deleteFile(path, "test.dex");
        return iUkey;
    }

    public static void getAssetsFile(Context c, String assets_file, String out) throws IOException {
        InputStream is = c.getAssets().open(assets_file);
        OutputStream os = new FileOutputStream(out);
        byte[] buffer = new byte[1024];
        int length = 0;
        while((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        os.flush();
        os.close();
        is.close();
    }

    private static void deleteFile(String path, String name) {
        File f = new File(path + File.separator + name);
        if (f.exists() && f.isFile()) {
            f.delete();
        }

    }

    private static native Class<?> L1(String var0, String var1, String var2, Context var3);
}
