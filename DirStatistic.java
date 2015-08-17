// 从命令行参数获得要统计的目录名（File）
// 以深度优先的顺序遍历整个目录，包括子目录（Stack）
// 统计每个目录下的文件个数，统计每个文件的行数（FileReader, LineNumberedReader）

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
        // 获取当前路径
        File dir = new File(System.getProperty("user.dir")); 
        if (args.length == 1) {
            dir = new File(args[0]);
        }
        if (!dir.isDirectory()) {
            System.out.println(dir.getAbsolutePath() + " is not a directory!");
            return;
        }
        long count_files = 0, count_lines = 0;
        // 用栈来存目录，以深度优先的顺序访问各个目录
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
                        // 用 FileReader 去读字符，用LineNumberReader去缓存，并且计数
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
