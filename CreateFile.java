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
    // д�ļ����߳���Ŀ
    private static int nThread = 4;
    // ÿ���̴߳�����ļ����С
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
            // ��Executors�������������һ���̳߳�
            ExecutorService pool = Executors.newFixedThreadPool(nThread); 
            long beg = 0;
            while (length >= beg + fileBlock) {
                // ������̣߳�ÿ���߳�дfileBlock��С���ļ�
                pool.execute(new FileWriteThread(args[0], beg, fileBlock));
                beg += fileBlock;
            }
            if (length > beg) {
                // дʣ�ಿ��
                pool.execute(new FileWriteThread(args[0], beg, length - beg));
            }
            // �ر��̳߳أ����ٽ����µ�����
            pool.shutdown();
            // �ȴ��̳߳�����߳�ȫ������
            while (!pool.awaitTermination(5, TimeUnit.SECONDS));
        } catch (FileNotFoundException e) {
            System.out.println("File: " + args[0] + " is not found (or created).");
        }
        System.out.println("Main thread exits");
    }
}

class FileWriteThread extends Thread{
    // ��raf��������ļ��е�[beg, beg+length)��������д������ֽ�
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
        System.out.println(this.getName() + " :: writing " + (beg/(1024*1024)) + " to " + (((beg+length))/(1024*1024)) + "Mb");
        try {
            raf = new RandomAccessFile(pathname, "rw");
            // ���RandomAccessFile��Channle
            FileChannel fc = raf.getChannel();
            fc.position(beg);
            byte[] barray = new byte[(int)length];
            ByteBuffer bb = ByteBuffer.wrap(barray);
            // �����������ֱ��д��ByteBuffer��backing array��
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
