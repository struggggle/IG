package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;

/**
 * 这里是保存GC结果，三类
 *
 * https://programmersought.com/article/1973993875/;jsessionid=D248276FF430825ECDCF1D0F21FF20E3
 * GC log并不会输出所有的GC event。
 * ART does not output all GC results to Logcat. Only those GCs that are considered slow to execute will be exported to Logcat.
 *
 * To be exact, only the GC pause time exceeds 5ms or the entire GC takes more than 100ms to be output to Logcat
 *
 * (Note: Garbage collection is optimized after 3.0, and only a small amount of time during the entire GC process will cause the application to pause). The actively initiated GC will be output to Logcat.
 *
 * * Concurrent mark sweep(CMS) Garbage the entire heap, except for the image space.
 * * Concurrent partial mark sweep Recycle almost the entire heap, except for image space and zynote space.
 * * Concurrent sticky mark sweep A normal garbage collection, it is only responsible for recycling the objects after the last garbage collection. It performs much more often than the Concurrent partial mark sweep because it performs faster and has less pause time.
 *
 * I/art : Explicit concurrent mark sweep GC freed 104710(7MB) AllocSpace objects, 21(416KB) LOS objects, 33% free, 25MB/38MB, paused 1.230ms total 67.216ms
 *
 * Similarly, in the case of ART, if a large number of GC records are seen in Logcat. And the value of the number of objects (the number of objects/heap space) in the Heap stats information is increasing, and there is no tendency to become smaller. Then the application is likely to have a memory leak.
 * If you see the information corresponding to the GC Reason becomes "Alloc", it means that the heap of the application is almost full, and then the memory will overflow soon
 *
 *
 * 这里是一个比较好的oracle，如果每次GC过后，已经使用/总共的比值很大，表明cache设置的过大。
 * 当看到大量的GC log信息在logcat，可查看堆统计(如样例中 5MB/38MB)。如果这个值持续增长，并且一直不见它变小，那可能发生了内存泄露。
 * */
public class GcResult {
    //2020-06-08 12:17:31.196 1742-1751/? I/art: Background partial concurrent mark sweep GC freed 54216(4MB) AllocSpace objects, 92(1840KB) LOS objects, 25% free, 47MB/63MB, paused 1.585ms total 103.904ms
    ArrayList<GC> backgroundPartialGc=new ArrayList<>();

    //Background sticky concurrent mark sweep GC freed 166749(7MB) AllocSpace objects, 4(80KB) LOS objects, 8% free, 74MB/81MB, paused 5.401ms total 140.635ms
    ArrayList<GC> backgroundStickyGc=new ArrayList<>();

    //06-12 15:04:34.881 24874 25093 I art     : Alloc partial concurrent mark sweep GC freed 1154(71KB) AllocSpace objects, 29(170MB) LOS objects, 34% free, 31MB/47MB, paused 355us total 34.547ms
    ArrayList<GC> allocPartialGc=new ArrayList<>();

    /**注意，我们记录对三类都属于 Concurrent mark sweep */

    ArrayList<GC> otherGc=new ArrayList<>();

}
