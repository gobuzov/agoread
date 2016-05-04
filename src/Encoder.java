/**
 * Created with IntelliJ IDEA.
 * User: Ago
 * Date: 05.11.14
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public class Encoder {
    public final static int ENC_UNKNOWN = -1;
    public final static int ENC_ISO8859_1 = 0;
    public final static int ENC_ISO8859_2 = 1;
    public final static int ENC_KOI8 = 2;
    public final static int ENC_1250 = 3;
    public final static int ENC_1251 = 4;
    public final static int ENC_1257 = 5;
    public final static int ENC_UTF8 = 6;

    static char[][] encodings = {
/*ENC_ISO8859_1*/ "\u0402\u0403\u201a\u201e\u201e\u2026\u2020\u2021\u20ac\u2030\u0409\u2039\u040a\u040c\u040b\u040f\u0452\u2018\u2019\u201c\u201d\u2022\u2013\u2014\u2122\u0459\u203a\u045a\u045c\u045b\u045f \u040e\u045e\u0408\u00a4\u0490\u00a6\u00a7\u0401\u00a9\u0404\u00ab\u00ac\u00ad\u00ae\u0407\u00b0Z\u00b1\u0406\u0456\u0491\u00b5\u00b6\u00b7\u0451\u2116\u0454\u00bb\u0458\u0405\u0455\u0457\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042c\u042b\u042a\u042d\u042e\u042f\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f".toCharArray(),
/*ENC_ISO8859_2*/ "\200\201\202\203\204\205\206\207\210\211\212\213\214\215\216\217\220\221\222\223\224\225\226\227\230\231\232\233\234\235\236\237\240\u0104\u02D8\u0141\244\u013D\u015A\247\250\u0160\u015E\u0164\u0179\255\u017D\u017B\260\u0105\u02DB\u0142\264\u013E\u015B\u02C7\270\u0161\u015F\u0165\u017A\u02DD\u017E\u017C\u0154\301\302\u0102\304\u0139\u0106\307\u010C\311\u0118\313\u011A\315\316\u010E\u0110\u0143\u0147\323\324\u0150\326\327\u0158\u016E\332\u0170\334\335\u0162\337\u0155\341\342\u0103\344\u013A\u0107\347\u010D\351\u0119\353\u011B\355\356\u010F\u0111\u0144\u0148\363\364\u0151\366\367\u0159\u016F\372\u0171\374\375\u0163\u02D9".toCharArray(),
/*ENC_KOI8*/ "\u2500\u2502\u250C\u2510\u2514\u2518\u251C\u2524\u252C\u2534\u253C\u2580\u2584\u2588\u258C\u2590\u2591\u2592\u2593\u2320\u25A0\u2219\u221A\u2248\u2264\u2265\u00A0\u2321\u00B0\u00B2\u00B7\u00F7\u2550\u2551\u2552\u0451\u2553\u2554\u2555\u2556\u2557\u2558\u2559\u255A\u255B\u255C\u255D\u255E\u255F\u2560\u2561\u0401\u2562\u2563\u2564\u2565\u2566\u2567\u2568\u2569\u256A\u256B\u256C\u00A9\u044E\u0430\u0431\u0446\u0434\u0435\u0444\u0433\u0445\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F\u044F\u0440\u0441\u0442\u0443\u0436\u0432\u044C\u044B\u0437\u0448\u044D\u0449\u0447\u044A\u042E\u0410\u0411\u0426\u0414\u0415\u0424\u0413\u0425\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u042F\u0420\u0421\u0422\u0423\u0416\u0412\u042C\u042B\u0417\u0428\u042D\u0429\u0427\u042A".toCharArray(),
/*ENC_1250*/ "\u20AC?\u201A?\u201E\u2026\u2020\u2021?\u2030\u0160\u2039\u015A\u0164\u017D\u0179?\u2018\u2019\u201C\u201D\u2022\u2013\u2014?\u2122\u0161\u203A\u015B\u0165\u017E\u017A\u00A0\u02C7\u02D8\u0141\u00A4\u0104\u00A6\u00A7\u00A8\u00A9\u015E\u00AB\u00AC\u00AD\u00AE\u017B\u00B0\u00B1\u02DB\u0142\u00B4\u00B5\u00B6\u00B7\u00B8\u0105\u015F\u00BB\u013D\u02DD\u013E\u017C\u0154\u00C1\u00C2\u0102\u00C4\u0139\u0106\u00C7\u010C\u00C9\u0118\u00CB\u011A\u00CD\u00CE\u010E\u0110\u0143\u0147\u00D3\u00D4\u0150\u00D6\u00D7\u0158\u016E\u00DA\u0170\u00DC\u00DD\u0162\u00DF\u0155\u00E1\u00E2\u0103\u00E4\u013A\u0107\u00E7\u010D\u00E9\u0119\u00EB\u011B\u00ED\u00EE\u010F\u0111\u0144\u0148\u00F3\u00F4\u0151\u00F6\u00F7\u0159\u016F\u00FA\u0171\u00FC\u00FD\u0163\u02D9".toCharArray(),
/*ENC_1251*/ "ÄÅÇÉÑÖÜáàâäãåçéèêëíìîï-ó\u0098ôöõúùûü†°¢£§•¶ß®©™´¨≠ÆØ∞±≤≥¥µ∂∑∏π∫ªºΩæø¿¡¬√ƒ≈∆«»… ÀÃÕŒœ–—“”‘’÷◊ÿŸ⁄€‹›ﬁﬂ‡·‚„‰ÂÊÁËÈÍÎÏÌÓÔÒÚÛÙıˆ˜¯˘˙˚¸˝˛ˇ".toCharArray(),
/*ENC_1257*/ "\u20AC\0\u201A\0\u201E\u2026\u2020\u2021\0\u2030\0\u2039\0\250\u02C7\270\0\u2018\u2019\u201C\u201D\u2022\u2013\u2014\0\u2122\0\u203A\0\257\u02DB\0\240\0\242\243\244\0\246\247\330\251\u0156\253\254\255\256\306\260\261\262\263\264\265\266\267\370\271\u0157\273\274\275\276\346\u0104\u012E\u0100\u0106\304\305\u0118\u0112\u010C\311\u0179\u0116\u0122\u0136\u012A\u013B\u0160\u0143\u0145\323\u014C\325\326\327\u0172\u0141\u015A\u016A\334\u017B\u017D\337\u0105\u012F\u0101\u0107\344\345\u0119\u0113\u010D\351\u017A\u0117\u0123\u0137\u012B\u013C\u0161\u0144\u0146\363\u014D\365\366\367\u0173\u0142\u015B\u016B\374\u017C\u017E\u02D9".toCharArray(),
    };
    public Encoder(ByteVector bv, CharVector cv, FileLink fl) {
        int enc = fl.encoding;
        if (ENC_UNKNOWN==enc || ENC_UTF8==enc){
            bv.setFreemem(fl.encoding_defined);
            if (decodeUTF8(bv, cv)){
                fl.encoding = ENC_UTF8;
                fl.encoding_defined = true;
                return;
            }
            enc = ENC_1251;
        }
        bv.setFreemem(true);
        int len = bv.getLen();

        char[] map = encodings[enc];
        for (int i = 0; i < len; i++) {
            byte b = bv.getByte(i);
            cv.putChar(i, (b >= 0) ? (char) b : map[b + 128]);
/*            if (i>value){
                value+=DVALUE;
                OptimizingSplash.getInstance().setValue(i*8/sz);
            }*/
        }
        if (false==fl.encoding_defined){
            fl.encoding = enc;
            fl.encoding_defined = true;
        }
        cv.setLen(len);
    }
    private boolean decodeUTF8(ByteVector bv, CharVector cv){
        byte a, b, c;
        int j=0,sz = bv.getLen();
        System.out.println("free:"+Runtime.getRuntime().freeMemory());
        for (int i = 0; i < sz; i++) {
            try {
                a = bv.getByte(i);
                if ((a & 0x80) == 0) {
                    cv.putChar(j++, (char) a);
                } else if ((a & 0xe0) == 0xc0) {
                    b = bv.getByte(i + 1);
                    if ((b & 0xc0) == 0x80) {
                        //char d = (char) (((a & 0x1F) << 6) | (b & 0x3F));
                        //System.out.print(d);
                        cv.putChar(j++, (char) (((a & 0x1F) << 6) | (b & 0x3F)));
                        i++;
                    } else {
                        Debug.log("UTFDataFormatException(Illegal 2-byte group); readed:"+i);
                        return false;
                    }
                } else if ((a & 0xf0) == 0xe0) {
                    b = bv.getByte(i + 1);
                    c = bv.getByte(i + 2);
                    if (((b & 0xc0) == 0x80) && ((c & 0xc0) == 0x80)) {
                        cv.putChar(j++,  (char) (((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F)));
                        i += 2;
                    } else {
                        Debug.log("Illegal 3-byte group");
                        return false;
                    }
                } else if (((a & 0xf0) == 0xf0) || ((a & 0xc0) == 0x80)) {
                    Debug.log("Illegal first byte of a group");
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                Debug.log("Unexpected EOF; readed:" +i);
                return false;
            }
            /*if (i>value){
                value+=DVALUE;
                OptimizingSplash.getInstance().setValue(i*8/sz);
            } //*/
        }
        cv.setLen(j);
        return true;
    }

}
