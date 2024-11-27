package com.sublayer.api.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();

    private static final DecimalFormat fourdf = new DecimalFormat("0000");

    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    public static String getFourBitRandom() {
        return fourdf.format(random.nextInt(10000));
    }

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }

    /**
     * @param list
     * @param n
     * @return
     */
    public static ArrayList getRandom(List list, int n) {

        Random random = new Random();

        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

        for (int i = 0; i < list.size(); i++) {

            int number = random.nextInt(100) + 1;

            hashMap.put(number, i);
        }

        Object[] robjs = hashMap.values().toArray();

        ArrayList r = new ArrayList();

        for (int i = 0; i < n; i++) {
            r.add(list.get((int) robjs[i]));
            System.out.print(list.get((int) robjs[i]) + "\t");
        }
        System.out.print("\n");
        return r;
    }

    /**
     * @return
     */
    public static String getRandomString(int length) {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&";

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        char randomChar = getRandomChar(random, "abcdefghijklmnopqrstuvwxyz");
        sb.append(randomChar);
        randomChar = getRandomChar(random, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        sb.append(randomChar);
        randomChar = getRandomChar(random, "0123456789");
        sb.append(randomChar);
        randomChar = getRandomChar(random, "@#$%&");
        sb.append(randomChar);

        for (int i = 4; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private static char getRandomChar(Random random, String characterSet) {
        int randomIndex = random.nextInt(characterSet.length());
        return characterSet.charAt(randomIndex);
    }
}
