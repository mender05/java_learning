// 从命令行参数获得文件名和文件大小
// 创建一个空洞文件，并设置大小（RandomAccessFile）
// 获取线程池（ExecutorService, Executors.newFixedThreadPool）
// 开多个线程并行写文件各个部分（Thread）
// 关闭线程池，等待所有线程结束

// 每个线程都要：
// 1. 重新打开一次文件，获得文件读写通道，然后指定好偏移量（FileChannel）
// 2. 创建指定大小的字节数组，并把它包装为字节缓存（ByteBuffer）
// 3. 生成指定大小的随机字节，写入字节缓存中（Random）
// 4. 将字节缓存通过文件通道写入磁盘，并关闭通道

package java_learning;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreateFile {
    // 写文件的线程数目
    private static int nThread = 4;
    // 每个线程处理的文件块大小
    private static int fileBlock = 32 * 1024 * 1024;
    public static void main (String[] args) throws Exception {
        if (args.length != 2) {
            String usage = "CreateFile.jar fileName fileSize (M)";
            System.out.println(usage);
            return;
        }    
        System.out.println("Main thread starts");
        try {
            RandomAccessFile raf = new RandomAccessFile(args[0], "rw");
            long length = Long.parseLong(args[1]) * 1024 * 1024;
            raf.setLength(length);
            raf.close();
            // 用Executors工厂方法，获得一个线程池
            ExecutorService pool = Executors.newFixedThreadPool(nThread); 
            long beg = 0;
            while (length >= beg + fileBlock) {
                // 开多个线程，每个线程写fileBlock大小的文件
                pool.execute(new FileWriteThread(args[0], beg, fileBlock));
                beg += fileBlock;
            }
            if (length > beg) {
                // 写剩余部分
                pool.execute(new FileWriteThread(args[0], beg, length - beg));
            }
            // 关闭线程池，不再接受新的任务
            pool.shutdown();
            // 等待线程池里的线程全部结束
            while (!pool.awaitTermination(5, TimeUnit.SECONDS));
        } catch (FileNotFoundException e) {
            System.out.println("File: " + args[0] + " is not found (or created).");
        }
        System.out.println("Main thread exits");
    }
}

class FileWriteThread extends Thread{
    // 往raf所代表的文件中第[beg, beg+length)的区间里写入随机字节
    private long beg = 0, length = 0;
    private String pathname;
    
    public FileWriteThread (String pathname, long beg, long length) throws Exception {
        if (pathname.isEmpty() || beg < 0 || length < 0) {
            throw new Exception("invalid arguments");
        }
        this.pathname = pathname;
        this.beg = beg;
        this.length = length;
    }
    
    public void run () {
        RandomAccessFile raf = null;
        System.out.println(this.getName() + " :: writing " + (beg/(1024*1024)) + 
                    " to " + (((beg+length))/(1024*1024)) + "Mb");
        try {
            raf = new RandomAccessFile(pathname, "rw");
            // 获得RandomAccessFile的Channle
            FileChannel fc = raf.getChannel();
            fc.position(beg);
            byte[] barray = new byte[(int)length];
            ByteBuffer bb = ByteBuffer.wrap(barray);
            // 产生随机数，直接写到ByteBuffer的backing array中
            Random rdm = new Random();
            rdm.nextBytes(bb.array());
            fc.write(bb);
            fc.close();
            System.out.println(this.getName() + " :: over ");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("run fileWriteThread failed");
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
