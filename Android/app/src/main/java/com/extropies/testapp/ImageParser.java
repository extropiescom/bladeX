package com.extropies.testapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageParser {
    public static class DevCompatImage {
        int m_iWidth;
        int m_iHeight;
        byte[] m_imgData;

        DevCompatImage(int iWidth, int iHeight, byte[] imgData) {
            m_iWidth = iWidth;
            m_iHeight = iHeight;
            m_imgData = imgData;
        }

        public byte[] getM_imgData() {
            return m_imgData;
        }

        public int getM_iWidth() {
            return m_iWidth;
        }

        public int getM_iHeight() {
            return m_iHeight;
        }
    }

    static private byte getBit(byte[] pbData, int nWidth, int nHeight, int rowIndex, int colIndex)
    {
        return (byte)((((int)pbData[rowIndex * nWidth / 8 + colIndex / 8]) >> (7 - colIndex % 8)) & 0x01);
    }
    static private void setBit(byte[] pbData, int nWidth, int nHeight, int rowIndex, int colIndex, byte nData)
    {
        byte nByte = (byte)0x01;

        nByte <<= (7 - rowIndex % 8);
        if (nData != 0)
        {
            pbData[colIndex * nHeight / 8 + rowIndex / 8] |= nByte;
        }
        else
        {
            pbData[colIndex * nHeight / 8 + rowIndex / 8] &= ~nByte;
        }
    }

    private static long getLong(byte[] bb, int index, int cont , boolean big) {
        long lg = 0;
        if (big) {
            //big-endian
            switch (cont) {
                case 1:
                    lg = bb[0+index];
                    break;
                case 2:
                    lg = ((((long) bb[index + 0] & 0xff) << 8)
                            | (((long) bb[index + 1] & 0xff) << 0));
                    break;
                case 3:
                    lg = ((((long) bb[index + 0] & 0xff) << 16)
                            | (((long) bb[index + 1] & 0xff) << 8)
                            | (((long) bb[index + 2] & 0xff) << 0));
                    break;
                case 4:
                    lg = ((((long) bb[index + 0] & 0xff) << 24)
                            | (((long) bb[index + 1] & 0xff) << 16)
                            | (((long) bb[index + 2] & 0xff) << 8)
                            | (((long) bb[index + 3] & 0xff) << 0));
                    break;

                case 8:
                    lg = ((((long) bb[index + 0] & 0xff) << 56)
                            | (((long) bb[index + 1] & 0xff) << 48)
                            | (((long) bb[index + 2] & 0xff) << 40)
                            | (((long) bb[index + 3] & 0xff) << 32)
                            | (((long) bb[index + 4] & 0xff) << 24)
                            | (((long) bb[index + 5] & 0xff) << 16)
                            | (((long) bb[index + 6] & 0xff) << 8)
                            | (((long) bb[index + 7] & 0xff) << 0));
                    break;
                default:
            }
            return lg;
        } else {
            //little-endian
            switch (cont) {
                case 1:
                    lg = bb[0+index];
                    break;
                case 2:
                    lg = ((((long) bb[index + 1] & 0xff) << 8)
                            | (((long) bb[index + 0] & 0xff) << 0));
                    break;
                case 3:
                    lg = ((((long) bb[index + 2] & 0xff) << 16)
                            | (((long) bb[index + 1] & 0xff) << 8)
                            | (((long) bb[index + 0] & 0xff) << 0));
                    break;
                case 4:
                    lg = ((((long) bb[index + 3] & 0xff) << 24)
                            | (((long) bb[index + 2] & 0xff) << 16)
                            | (((long) bb[index + 1] & 0xff) << 8)
                            | (((long) bb[index + 0] & 0xff) << 0));
                    break;

                case 8:
                    lg = ((((long) bb[index + 7] & 0xff) << 56)
                            | (((long) bb[index + 6] & 0xff) << 48)
                            | (((long) bb[index + 5] & 0xff) << 40)
                            | (((long) bb[index + 4] & 0xff) << 32)
                            | (((long) bb[index + 3] & 0xff) << 24)
                            | (((long) bb[index + 2] & 0xff) << 16)
                            | (((long) bb[index + 1] & 0xff) << 8)
                            | (((long) bb[index + 0] & 0xff) << 0));
                    break;
                default:

            }
            return lg;
        }
    }

    private static byte[] convert2DeviceBin(byte[] origData, int iWidth, int iHeight) {
        byte nByteData = 0;
        int iPadWidth = (((iWidth / 8) - 1) / 4 + 1) * 32;
        byte[] destData = new byte[origData.length];

        for (int i = 0; i < iHeight; i++)
        {
            for (int j = 0; j < iWidth; j++)
            {
                nByteData = getBit(origData, iPadWidth, iHeight, i, j);
                setBit(destData, iWidth, iHeight, i, j, nByteData);
            }
        }

        return destData;
    }

    private static byte[] convert2Binary(byte[] origData) {
        //https://www.cnblogs.com/wainiwann/p/7086844.html
        if (origData == null) {
            return null;
        }

        byte[] destData = null;
        final int bfTypeIndex = 0x00;
        final int bfOffbitsIndex = 0x0A;

        long biWidth = 0;
        long biHeight = 0;
        long biBitCount = 0;
        long zeroColor = 0;
        long oneColor = 0;

        //check bfType == 'BM'
        if (!((origData[bfTypeIndex] == (byte)'B') && (origData[bfTypeIndex + 1] == (byte)'M'))) {
            return null;
        }
        //check bfOffbits, 0x3E means Windows, and 0x20 means MAC
        long bfOffbits = getLong(origData, bfOffbitsIndex, 4, false);
        if (bfOffbits == (long)0x3E) {
            //Windows
            final int biWidthIndex = 0x12;
            final int biHeightIndex = 0x16;
            final int biBitCountIndex = 0x1C;

            biWidth = getLong(origData, biWidthIndex, 4, false);
            biHeight = getLong(origData, biHeightIndex, 4, false);
            biBitCount = getLong(origData, biBitCountIndex, 2, false);

            int zeroColorIndex = (int)(bfOffbits - 8); //bit 0 color
            int oneColorIndex = (int)(bfOffbits - 4); //bit 1 color
            zeroColor = getLong(origData, zeroColorIndex, 4, false);
            oneColor = getLong(origData, oneColorIndex, 4, false);

        } else if (bfOffbits == (long)0x20) {
            //MAC
            final int biWidthIndex = 0x12;
            final int biHeightIndex = 0x14;
            final int biBitCountIndex = 0x18;

            biWidth = getLong(origData, biWidthIndex, 2, false);
            biHeight = getLong(origData, biHeightIndex, 2, false);
            biBitCount = getLong(origData, biBitCountIndex, 2, false);

            int zeroColorIndex = (int)(bfOffbits - 6); //bit 0 color
            int oneColorIndex = (int)(bfOffbits - 3); //bit 1 color
            zeroColor = getLong(origData, zeroColorIndex, 3, false);
            oneColor = getLong(origData, oneColorIndex, 3, false);
        } else {
            return null;
        }

        if (biBitCount != (long)0x01) {
            return null;
        }

        if (zeroColor < oneColor) {
            //zeroColor is darker than oneColor
            //in device, bit 0 means black, so we don't need to do anything here
        } else {
            //zeroColor is brighter than oneColor
            //in device, bit 0 means black, we should revert colors here
            for (int i = 0; i < origData.length; i++) {
                origData[i] = (byte)(~origData[i]);
            }
        }

        destData = new byte[origData.length - (int)bfOffbits];
        System.arraycopy(origData, (int)bfOffbits, destData, (int)0, origData.length - (int)bfOffbits);

        return destData;
    }

    private static int[] getBMPSize(byte[] origData) {
        if (origData == null) {
            return null;
        }

        final int bfTypeIndex = 0x00;
        final int bfOffbitsIndex = 0x0A;

        long biWidth = 0;
        long biHeight = 0;

        //check bfType == 'BM'
        if (!((origData[bfTypeIndex] == (byte)'B') && (origData[bfTypeIndex + 1] == (byte)'M'))) {
            return null;
        }
        //check bfOffbits, 0x3E means Windows, and 0x20 means MAC
        long bfOffbits = getLong(origData, bfOffbitsIndex, 4, false);
        if (bfOffbits == (long)0x3E) {
            //Windows
            final int biWidthIndex = 0x12;
            final int biHeightIndex = 0x16;

            biWidth = getLong(origData, biWidthIndex, 4, false);
            biHeight = getLong(origData, biHeightIndex, 4, false);
        } else if (bfOffbits == (long)0x20) {
            //MAC
            final int biWidthIndex = 0x12;
            final int biHeightIndex = 0x14;

            biWidth = getLong(origData, biWidthIndex, 2, false);
            biHeight = getLong(origData, biHeightIndex, 2, false);
        } else {
            return null;
        }

        int[] retData = new int[2];
        retData[0] = (int)biWidth;
        retData[1] = (int)biHeight;
        return retData;
    }

    public static DevCompatImage convertFromBMP(String filePath) {
        if (filePath == null) {
            return null;
        }

        File filterFile = new File(filePath);
        byte[] fileData = null;

        try{
            byte[] bufferData = new byte[1024];
            int dataLen = 0;// 一次读取1024字节大小，没有数据后返回-1.

            FileInputStream fis = new FileInputStream(filterFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((dataLen = fis.read(bufferData)) != -1) {
                baos.write(bufferData, 0, dataLen);
            }
            fileData = baos.toByteArray();
            baos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] rawData = null;
        byte[] devBin = null;
        int[] bmpSize = null;
        if (fileData != null) {
            rawData = convert2Binary(fileData);
            if (rawData == null) {
                return null;
            }
            bmpSize = getBMPSize(fileData);
            if (bmpSize == null) {
                return null;
            }
            //devBin = convert2DeviceBin(rawData, iWidth, iHeight);
            devBin = rawData;
        }

        return new DevCompatImage(bmpSize[0], bmpSize[1], devBin);
    }
}
