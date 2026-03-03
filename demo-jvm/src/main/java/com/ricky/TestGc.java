package com.ricky;

public class TestGc {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("1111111111111111111111111");
        Thread.sleep(20000);

        byte[] array = new byte[1024 * 1024 * 10]; // 10M内存
        System.out.println("2222222222222222222222222");
        Thread.sleep(20000);

        System.gc(); // 垃圾回收
        System.out.println("3333333333333333333333333");
        Thread.sleep(10000);
    }

}

/*
PS F:\Develop\java\demo\demo-jvm> jps
18128 Jps
27216 Main
3328 Launcher
16564 RemoteMavenServer36
34084 TestGc
PS F:\Develop\java\demo\demo-jvm> jhsdb jmap --heap --pid 34084
Attaching to process ID 34084, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 17.0.16+12-LTS-247

using thread-local object allocation.
Garbage-First (G1) GC with 13 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4223664128 (4028.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 2533359616 (2416.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 22020096 (21.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 2097152 (2.0MB)

Heap Usage:
G1 Heap:
   regions  = 2014
   capacity = 4223664128 (4028.0MB)
   used     = 2097152 (2.0MB)
   free     = 4221566976 (4026.0MB)
   0.04965243296921549% used
G1 Young Generation:
Eden Space:
   regions  = 1
   capacity = 25165824 (24.0MB)
   used     = 2097152 (2.0MB)
   free     = 23068672 (22.0MB)
   8.333333333333334% used
Survivor Space:
   regions  = 0
   capacity = 0 (0.0MB)
   used     = 0 (0.0MB)
   free     = 0 (0.0MB)
   0.0% used
G1 Old Generation:
   regions  = 0
   capacity = 239075328 (228.0MB)
   used     = 0 (0.0MB)
   free     = 239075328 (228.0MB)
   0.0% used

PS F:\Develop\java\demo\demo-jvm> jhsdb jmap --heap --pid 34084
Attaching to process ID 34084, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 17.0.16+12-LTS-247

using thread-local object allocation.
Garbage-First (G1) GC with 13 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4223664128 (4028.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 2533359616 (2416.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 22020096 (21.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 2097152 (2.0MB)

Heap Usage:
G1 Heap:
   regions  = 2014
   capacity = 4223664128 (4028.0MB)
   used     = 14680064 (14.0MB)
   free     = 4208984064 (4014.0MB)
   0.34756703078450846% used
G1 Young Generation:
Eden Space:
   regions  = 1
   capacity = 25165824 (24.0MB)
   used     = 2097152 (2.0MB)
   free     = 23068672 (22.0MB)
   8.333333333333334% used
Survivor Space:
   regions  = 0
   capacity = 0 (0.0MB)
   used     = 0 (0.0MB)
   free     = 0 (0.0MB)
   0.0% used
G1 Old Generation:
   regions  = 6
   capacity = 239075328 (228.0MB)
   used     = 12582912 (12.0MB)
   free     = 226492416 (216.0MB)
   5.2631578947368425% used

PS F:\Develop\java\demo\demo-jvm> jhsdb jmap --heap --pid 34084
Attaching to process ID 34084, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 17.0.16+12-LTS-247

using thread-local object allocation.
Garbage-First (G1) GC with 13 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 4223664128 (4028.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 2533359616 (2416.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 22020096 (21.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 2097152 (2.0MB)

Heap Usage:
G1 Heap:
   regions  = 2014
   capacity = 4223664128 (4028.0MB)
   used     = 13508480 (12.8826904296875MB)
   free     = 4210155648 (4015.1173095703125MB)
   0.31982846151160627% used
G1 Young Generation:
Eden Space:
   regions  = 0
   capacity = 18874368 (18.0MB)
   used     = 0 (0.0MB)
   free     = 18874368 (18.0MB)
   0.0% used
Survivor Space:
   regions  = 0
   capacity = 0 (0.0MB)
   used     = 0 (0.0MB)
   free     = 0 (0.0MB)
   0.0% used
G1 Old Generation:
   regions  = 8
   capacity = 37748736 (36.0MB)
   used     = 13508480 (12.8826904296875MB)
   free     = 24240256 (23.1173095703125MB)
   35.785251193576386% used
*/