package com.wegame.util;

import java.util.ArrayList;
import java.util.Random;

/**
 * @Author xiongjie
 * @Date 2023/07/08 22:58
 **/
public class ArrayListUtils {
    private static <V> boolean isEmpty(ArrayList<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

    /**
     * 打乱ArrayList
     */
    public static <V> ArrayList<V> randomList(ArrayList<V> sourceList) {
        if (isEmpty(sourceList)) {
            return sourceList;
        }
        Random random = new Random(System.currentTimeMillis());
        ArrayList<V> randomList = new ArrayList<V>(sourceList.size());
        do {
            int randomIndex = Math.abs(random.nextInt(sourceList.size()));
            randomList.add(sourceList.remove(randomIndex));
        } while (sourceList.size() > 0);
        return randomList;
    }
}
