# Lab 1: “Super-fast” Sudoku Solving

Enter in the folder you have cloned from our lab git repo, and pull the latest commit. 

`git pull`

You can find this lab1's instruction in `Lab1/README.md` 

All materials of lab1 are in folder `Lab1/`

## 1. Overview

使用多线程或多个进程在单个计算机上运行，实现Sudoku解决程序。 尝试**利用所有CPU内核**，并使程序**尽可能快地运行**！

### Goals

*练习基本的并行编程技能，例如使用多个线程/进程；
*熟悉Unix OS环境开发（例如，文件I / O，获取时间戳）；
*熟悉源代码版本控制工具（git），并学习使用github与他人进行协作；
*练习如何进行性能测试并编写高质量的性能测试报告。

## 2. Background

### 2.1 Introduction to Sudoku

数独（最初称为数字放置）是基于逻辑的组合数字放置拼图。

您将获得一个9×9的木板，由81个正方形组成，逻辑上分为9个列，9行和9个3×3子正方形。 
我们的目标是，给一个带有一些初始数字的棋盘（我们称其为“数独谜题”），然后填充棋盘的其余部分，
以使每一列，每一行和所有子方块的数字都从1到9 并且每个数字仅出现一次（我们称其为“数独解决方案”）。


 <u>An example Sudoku puzzle:</u>

<img src="src/Sudoku_puzzle.png" alt="Sudoku" title="Sudoku puzzle" style="zoom:67%;" />

 <u>An example Sudoku solution to above puzzle:</u>

<img src="src/Sudoku_answer.png" alt="Sudoku" title="Sudoku answer" style="zoom:67%;" />

### 2.2 Some useful resources

如果您不知道可以使用什么算法来解决数独难题，建议您阅读 [this](https://rafal.io/posts/solving-sudoku-with-dancing-links.html). To simplify your work, we have provided a simple [implementation](src/Sudoku/) `(Lab1/src/Sudoku`) of 4 Sudoku solving algorithms (some are slow, some are fast), but without using multiple threads/processes. The two files *test1* and *test1000* contain many puzzles for you to test. 
当然，总是鼓励您（不是强制性的）自己甚至是自己的算法（如果有时间的话）实施那些算法。


## 3. Your Lab Task

### 3.1 Write a program 

实施满足以下要求的程序：

#### 3.1.1 Program input and output

##### **3.1.1.1 Input** 

1. Your program **<u>must</u>** have no arguments during start. Attention, your program must be called *sudoku_solve*,  just typing `./sudoku_solve` and your program will run correctly.
2. But after start, your program should be able to read multiple strings from ***stdin***, where each string is separated by a line-break. Each string is a **name of a file**, which contains one or more Sudoku puzzles that your program is going to solve. 
3. In the input file, **each line** is a Sudoku puzzle that needs to be solved. Each line contains 81 decimal digits. The 1st-9th digits are the 1st row of the 9×9 grid, and the 10th-18th digits are the 2nd row of the 9×9 grid, and so on.
 Digit 0 means this digit is unknown and your program needs to figure it out according to the Sudoku rules described above.
 数字0表示该数字未知，您的程序需要根据上述Sudoku规则找出该数字。
 
**Example contents**

<img src="src/Input_file.png" alt="Input file" title="Input file" style="zoom:67%;" />

**Example input**

```
./test1 
./test2
./test3
```

##### 3.1.1.2 Output

For each test case, you just only output the Sudoku solutions. And don't forget, the output order should correspond with the input order of Sudoku puzzles.
对于每个测试用例，您只需输出Sudoku解决方案。 并且不要忘记，输出顺序应与Sudoku拼图的输入顺序相对应。

**Example output**

```
312647985786953241945128367854379126273461859691285473437592618569814732128736594
693784512487512936125963874932651487568247391741398625319475268856129743274836159 
869725413512934687374168529798246135231857946456319872683571294925483761147692358
693784512487512936125963874932651487568247391741398625319475268856129743274836159
364978512152436978879125634738651429691247385245389167923764851486512793517893246
378694512564218397291753684643125978712869453859437261435971826186542739927386145
```

**Output order requirement**
在上面的输入示例中，输入文件名的顺序为“ ./test1”、“./test2”和“ ./test3”。 
因此，您应该首先在文件“ test1”中输出数独谜题的解，然后在文件“ test2”中输出，最后在文件“ test3”中输出。 
不用说，解决方案的输出顺序应与输入拼图的顺序相同。

在上面的输出示例中，第一行是数独难题在文件“ test1”中的解决方案。 
之后，第二行和第三行是文件“ test2”中第一和第二个数独谜题的解。 
最后，第四行，第五行和第六行是文件“ test3”中第一，第二和第三数独难题的解决方案。


#### 3.1.3 Implementation requirements 

##### 3.1.3.1 Basic version

Your program should be able to: 

1. Accept **one** input file name, and the size of the input file is smaller than 100MB. 
2. Successfully solve the puzzles in the input file, and output the results in the format described before.
3. Use multiple threads/processes to make use of most of your machine's CPU cores.

\[Tips\]: 1) Use event queue to dispatch tasks and merge results to/from worker threads. 2) Dynamically detect how many CPU cores are there on your machine, in order to decide how many threads/processes your program uses. 3) Be careful about the contention among multiple threads/processes

##### 3.1.3.2 Advanced version

Your program should be able to: 

1. Complete all the requirements in the basic version.
2. Accept **any number of** input file names, and the size of input file can be **any large** (as long as it can be stored on your disk)
3. When the program is solving puzzles in the previously input file(s), the program can **meanwhile accept more input file names from *stdin***.

\[Tips\]: 1) Use a dedicated thread to accept input; 2) To avoid consuming all the memory, read different parts of the file into memory and solve them one by one; 3) You are encouraged to try more optimizations such as cache coherency processing.

### 3.2. Finish a performance test report

Please test your code first, and commit a test report along with your lab code into your group’s course github repo. 

The test report should describe your test inputs, and the performance result under various testing conditions. Specifically, in your test report, you should at least contain the following two things:

1. Compare the performance of your “super-fast” Sudoku solving program with a simple single-thread version, using the same input and under the same environment.
2. Change the input (e.g., change file size) and environment (e.g., using machines with different CPUs and hard drives), and draw several curves of your program’s performance under various conditions.
请先测试您的代码，然后将测试报告和实验室代码一起提交到小组的课程github存储库中。

测试报告应描述您的测试输入以及各种测试条件下的性能结果。 具体来说，在测试报告中，您至少应包含以下两件事：

1.使用相同的输入，在相同的环境下，将“超快速” Sudoku解决程序的性能与简单的单线程版本进行比较。
2.更改输入（例如，更改文件大小）和环境（例如，使用具有不同CPU和硬盘驱动器的计算机），并在各种条件下绘制程序性能的多条曲线。
## 4. Lab submission

Please put all your code in folder `Lab1` and write a `Makefile` so that we **can compile your code in one single command** `make`. The compiled runnable executable binary should be named `sudoku_solve` and located in folder `Lab1`. If you do not know how to write `Makefile`, you can find a simple example in `Lab1/src/Sudoku`. Please carefully following above rules so that TAs can automatically test your code!!!

Please submit your lab program and performance test report following the guidance in the [Overall Lab Instructions](../README.md) (`../README.md`)
请将所有代码放在“ Lab1”文件夹中，并编写一个“ Makefile”，以便我们“可以在一个命令中编译您的代码”“ make”。
编译后的可运行可执行二进制文件应命名为“ sudoku_solve”，并位于文件夹“ Lab1”中。 
如果您不知道如何编写“ Makefile”，则可以在“ Lab1 / src / Sudoku”中找到一个简单的示例。 
请仔细遵守以上规则，以便TA可以自动测试您的代码！！！
请按照[总体实验说明]（../ README.md）（`../ README.md`）中的指南提交实验程序和性能测试报告。
## 5. Grading standards

1. You can get 38 points if you can: 1) finish all the requirements of the basic version, and 2) your performance test report has finished the two requirements described before. If you missed some parts, you will get part of the points depending how much you finished
2. You can get 40 points (full score) if you can: 1) finish all the requirements of the advanced version, and 2) your performance test report has finished the two requirements described before. If you missed some parts, you will get part of the points depending how much you finished.
1.如果可以，您可以得到38分：1）完成基本版本的所有要求，以及2）性能测试报告已满足上述两个要求。 如果您错过了一些部分，您将获得部分积分，具体取决于您完成了多少
2.如果可以，您将获得40分（满分）：1）完成高级版本的所有要求，以及2）性能测试报告已满足上述两个要求。 如果您错过了一些部分，您将获得部分积分，具体取决于您完成了多少。
