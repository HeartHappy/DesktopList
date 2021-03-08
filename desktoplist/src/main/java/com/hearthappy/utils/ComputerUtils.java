package com.hearthappy.utils;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created Date 2019-11-28.
 *
 * @author ChenRui
 * ClassDescription：
 */
public class ComputerUtils {


    private static final String TAG = "ComputerUtil";
    public static int DEFAULT_SHOW_DATA = 15;

    /**
     * 计算总也数
     *
     * @param allCount  总数量
     * @param showCount 每页显示多少数量
     * @return
     */
    public static int getAllPage(int allCount, int showCount) {
        return (allCount % showCount) != 0 ? (allCount / showCount) + 1 : (allCount / showCount);
    }


    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     *
     * @param source 源集合
     * @param n      拆分后的集合的个数
     * @param <T>
     * @return 拆分的集合集
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        if (source == null || n < 2) {
            return null;
        }
        List<List<T>> result = new ArrayList<>();
        //(先计算出余数)
        int remaider = source.size() % n;
        //然后是商
        int number = source.size() / n;
        //偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 将集合拆分指定大小的多个集合
     *
     * @param resList 源集合
     * @param count   每个集合的大小
     * @param <T>     泛型
     * @return 拆分后的集合集
     */
    public static <T> List<List<T>> split(List<T> resList, int count) {

        if (resList == null || count < 1) {
            return null;
        }
        List<List<T>> ret = new ArrayList<List<T>>();
        int size = resList.size();
        //数据量不足count指定的大小
        if (size <= count) {
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            //前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            //last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;

    }


    /**
     * 判断网格适配器的item是否为最左侧首个item
     *
     * @param position
     * @return
     */
    public static boolean alculateLeftFirstIndex(int position, int span) {
        if (position % span == 0) {
            Log.e(TAG, "是左侧第一个下标");
            return true;
        }
        return false;
    }


    /**
     * 计算最后一行(翻页使用)
     *
     * @param itemCount item总数量
     * @param span      列数
     * @param position  按下键的position
     * @return true 是最后一行，否则
     */
    public static boolean calculateLastRow(int itemCount, int span, int position) {
        //计算总行数
        int allRows = (int) Math.ceil((itemCount - 1) / span);
        //计算当前所在行
        int currentRow = (int) Math.ceil(position / span);
        Log.i("ComputerUtil", "calculateLastRow: itemCount: " + itemCount + ",allRows:" + allRows + ",currentRow:" + currentRow);
        if (allRows == currentRow) {
            return true;
        }
        return false;
    }

    /**
     * 对入参保留最多两位小数(舍弃末尾的0)，如:
     * 3.345->3.34
     * 3.40->3.4
     * 3.0->3
     */
    public static String getFloatLimit(float number) {
        DecimalFormat format = new DecimalFormat("0.#");
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(number);
    }


}
