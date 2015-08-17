package java_learning;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Stack;

public class DirStatistic {
    public static void main (String[] args) {
        if (args.length > 1) {
            String usage = "Usage: DirStatistic Dir";
            System.out.println(usage);
            return;    
        }
        // ��ȡ��ǰ·��
        File dir = new File(System.getProperty("user.dir")); 
        if (args.length == 1) {
            dir = new File(args[0]);
        }
        if (!dir.isDirectory()) {
            System.out.println(dir.getAbsolutePath() + " is not a directory!");
            return;
        }
        long count_files = 0, count_lines = 0;
        // ��ջ����Ŀ¼����������ȵ�˳����ʸ���Ŀ¼
        Stack<File> dir_stack = new Stack<File>();
        dir_stack.push(dir);
        while (!dir_stack.empty()) {
            File[] filelist = dir_stack.pop().listFiles();
            for (File f : filelist) {
                if (f.isDirectory()) {
                    dir_stack.push(f);
                }
                else if (f.isFile()){
                    try {
                        // �� FileReader ȥ���ַ�����LineNumberReaderȥ���棬���Ҽ���
                        LineNumberReader lnr = new LineNumberReader(new FileReader(f));
                        ++count_files;
                        while (lnr.readLine() != null);
                        count_lines += lnr.getLineNumber();
                        lnr.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Dir: " + dir.getAbsolutePath() + ", " + 
                count_files + " files, " + count_lines + " lines");
    }
}
