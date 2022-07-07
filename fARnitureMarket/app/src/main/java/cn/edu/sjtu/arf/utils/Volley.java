//package cn.edu.sjtu.arf.utils;
//
//import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Build;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.BaseHttpStack;
//import com.android.volley.toolbox.BasicNetwork;
//import com.android.volley.toolbox.DiskBasedCache;
//import com.android.volley.toolbox.HttpClientStack;
//import com.android.volley.toolbox.HttpStack;
//import com.android.volley.toolbox.HurlStack;
//
//import org.apache.http.params.HttpParams;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateFactory;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManagerFactory;
//
//import cn.edu.sjtu.arf.R;
//
//public class Volley {
//
//    /**
//     * Default on-disk cache directory.
//     */
//    private static final String DEFAULT_CACHE_DIR = "volley";
//    private static BasicNetwork network;
//    private static RequestQueue queue;
//
//    private Context mContext;
//
//    /**
//     * Creates a default instance of the worker pool and calls
//     * {@link RequestQueue#start()} on it.
//     *
//     * @param context A {@link Context} to use for creating the cache dir.
//     * @param stack   An {@link HttpStack} to use for the network, or null for
//     *                default.
//     * @return A started {@link RequestQueue} instance.
//     */
//    public static RequestQueue newRequestQueue(Context context,
//                                               BaseHttpStack stack, boolean selfSignedCertificate, int rawId) {
//        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
//
//        String userAgent = "volley/0";
//        try {
//            String packageName = context.getPackageName();
//            PackageInfo info = context.getPackageManager().getPackageInfo(
//                    packageName, 0);
//            userAgent = packageName + "/" + info.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//        }
//
//        if (stack == null) {
//            if (Build.VERSION.SDK_INT >= 9) {
//                if (selfSignedCertificate) {
//                    stack = new HurlStack(null, buildSSLSocketFactory(context,
//                            rawId));
//                } else {
//                    stack = new HurlStack();
//                }
//            } else {
//                // Prior to Gingerbread, HttpUrlConnection was unreliable.
//                // See:
//                // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                if (selfSignedCertificate)
//                    stack = new HttpClientStack(getHttpClient(context, rawId));
//                else {
//                    stack = new HttpClientStack(
//                            AndroidHttpClient.newInstance(userAgent));
//                }
//            }
//        }
//
//        if (network == null) {
//            network = new BasicNetwork(stack);
//        }
//        if (queue == null) {
//            queue = new RequestQueue(new DiskBasedCache(cacheDir),network);
//        }
//        queue.start();
//
//        return queue;
//    }
//
//    /**
//     * Creates a default instance of the worker pool and calls
//     * {@link RequestQueue#start()} on it.
//     *
//     * @param context A {@link Context} to use for creating the cache dir.
//     * @return A started {@link RequestQueue} instance.
//     */
//    public static RequestQueue newRequestQueue(Context context) {
//        // 如果你目前还没有证书,那么先用下面的这行代码,http可以照常使用.
//        //       return newRequestQueue(context, null, false, 0);
//        // 此处R.raw.certificateName 表示你的证书文件,替换为自己证书文件名字就好
//        return newRequestQueue(context, null, true, R.raw.certificateName);
//    }
//
//    private static SSLSocketFactory buildSSLSocketFactory(Context context,
//                                                          int certRawResId) {
//        KeyStore keyStore = null;
//        try {
//            keyStore = buildKeyStore(context, certRawResId);
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = null;
//        try {
//            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        }
//
//        SSLContext sslContext = null;
//        try {
//            sslContext = SSLContext.getInstance("TLS");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            sslContext.init(null, tmf.getTrustManagers(), null);
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//
//        return sslContext.getSocketFactory();
//
//    }
//
//    private static HttpClient getHttpClient(Context context, int certRawResId) {
//        KeyStore keyStore = null;
//        try {
//            keyStore = buildKeyStore(context, certRawResId);
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (keyStore != null) {
//        }
//        org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = null;
//        try {
//            sslSocketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(
//                    keyStore);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        }
//
//        HttpParams params = new BasicHttpParams();
//
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(new Scheme("http", PlainSocketFactory
//                .getSocketFactory(), 80));
//        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
//
//        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
//                params, schemeRegistry);
//
//        return new DefaultHttpClient(cm, params);
//    }
//
//    private static KeyStore buildKeyStore(Context context, int certRawResId)
//            throws KeyStoreException, CertificateException,
//            NoSuchAlgorithmException, IOException {
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//
//        Certificate cert = readCert(context, certRawResId);
//        keyStore.setCertificateEntry("ca", cert);
//
//        return keyStore;
//    }
//
//    private static Certificate readCert(Context context, int certResourceID) {
//        InputStream inputStream = context.getResources().openRawResource(
//                certResourceID);
//        Certificate ca = null;
//
//        CertificateFactory cf = null;
//        try {
//            cf = CertificateFactory.getInstance("X.509");
//            ca = cf.generateCertificate(inputStream);
//
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        return ca;
//    }
//}