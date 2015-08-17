# java_learning
##Simple projects to practice java. All projects come from [langyu](http://langyu.iteye.com/blog/1474290).

## [统计本地磁盘中某个目录下的所有文件数和总行数](https://github.com/mender05/java_learning/blob/master/DirStatistic.java)
**题目要求**：传入如"D:\workspace" 这样的一个目录，返回这个目录下有多少个文件，且计算这些文件的总行数。 

**结果示例**："Dir: D:\workspace, 1342 files, 87822 lines" 

**扩展训练**：
  - 如果传入的参数是一个文件名（不是目录名），那么你该怎么办？
  - 有在网上看过那些统计代码行数的工具软件么？没了解过的话去下载个并观摩，看能不能实现跟它一样的功能


## [创建指定大小的文件](https://github.com/mender05/java_learning/blob/master/CreateFile.java)
**题目要求**：在本地磁盘上创建一个大小578MB的文件，文件内容你自己生成，可以是任何数据 

**扩展训练**：
  - 文件内容必须是一行一行的文本，内容随机生成
  - 创建十万行包含随机文本的文件
  - 要求文件中每行必须包含20列，每列一个随机字符串
