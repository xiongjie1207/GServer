package com.wegame.util;

import java.util.ArrayList;

/**
 * @Author xiongjie
 * @Date 2023/07/08 22:58
 **/
public class ArrayListUtils {
    private static <V> boolean isEmpty(ArrayList<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

}
