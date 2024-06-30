package com.mxtech.os;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import com.mxtech.app.MXApplication;

@SuppressLint({"NewApi"})
/* loaded from: classes.dex */
public final class Cpu {
    public static final boolean classLoaded;
    public static final int coreCount;
    public static final int family;
    public static final long features;

    /* loaded from: classes2.dex */
    public static class Family {
        public static final int ARM = 1;
        public static final int ARM64 = 4;
        public static final int MAX = 7;
        public static final int MIPS = 3;
        public static final int MIPS64 = 6;
        public static final int UNKNOWN = 0;
        public static final int X86 = 2;
        public static final int X86_64 = 5;
    }

    /* loaded from: classes.dex */
    public static class Features {

        /* loaded from: classes.dex */
        public static class Arm {
            public static final int AES = 4096;
            public static final int ARMv6 = 8;
            public static final int ARMv7 = 1;
            public static final int CRC32 = 65536;
            public static final int IDIV_ARM = 512;
            public static final int IDIV_THUMB2 = 1024;
            public static final int LDREX_STREX = 8;
            public static final int NEON = 4;
            public static final int NEON_FMA = 256;
            public static final int PMULL = 8192;
            public static final int SHA1 = 16384;
            public static final int SHA2 = 32768;
            public static final int VFP = 242;
            public static final int VFP_D32 = 32;
            public static final int VFP_FMA = 128;
            public static final int VFP_FP16 = 64;
            public static final int VFPv2 = 16;
            public static final int VFPv3 = 2;
            public static final int iWMMXt = 2048;
        }

        /* loaded from: classes2.dex */
        public static class Arm64 {
            public static final int AES = 4;
            public static final int ASIMD = 2;
            public static final int CRC32 = 64;
            public static final int FP = 1;
            public static final int PMULL = 8;
            public static final int SHA1 = 16;
            public static final int SHA2 = 32;
        }

        /* loaded from: classes2.dex */
        public static class Mips {
            public static final int MSA = 2;
            public static final int R6 = 1;
        }

        /* loaded from: classes.dex */
        public static class X86 {
            public static final int AES_NI = 32;
            public static final int AVX = 64;
            public static final int AVX2 = 256;
            public static final int MOVBE = 4;
            public static final int POPCNT = 2;
            public static final int RDRAND = 128;
            public static final int SHA_NI = 512;
            public static final int SSE4_1 = 8;
            public static final int SSE4_2 = 16;
            public static final int SSSE3 = 1;
        }
    }

    private static native int getCoreCount();

    private static native int getFamily();

    private static native long getFeatures();

    static {
        String[] strArr;
        try {
            System.loadLibrary("loader.mx");
            int nativeFamily = getFamily();
            long nativeFeatures = getFeatures();
            coreCount = getCoreCount();
            if (nativeFamily == 1 && "x86".equalsIgnoreCase(Build.CPU_ABI)) {
                family = 2;
                features = 1L;
            } else {
                family = nativeFamily;
                features = nativeFeatures;
            }
            classLoaded = true;
            StringBuilder sb = new StringBuilder();
            sb.append("CpuFamily=[").append(family).append("] CpuFeatures=[").append(features).append("] CpuCount=[").append(coreCount).append("] os.arch=[").append(System.getProperty("os.arch")).append("] ABIs=[");
            if (Build.VERSION.SDK_INT >= 21) {
                for (String abi : Build.SUPPORTED_ABIS) {
                    sb.append(abi).append(';');
                }
            } else {
                sb.append(Build.CPU_ABI).append(';');
                if (Build.VERSION.SDK_INT >= 8) {
                    sb.append(Build.CPU_ABI2).append(';');
                }
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            Log.i(MXApplication.TAG, sb.toString());
        } catch (UnsatisfiedLinkError e) {
            Log.e(MXApplication.TAG, "", e);
            classLoaded = false;
            family = 0;
            features = 0L;
            coreCount = 1;
        }
    }
}
