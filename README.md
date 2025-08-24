# ScriptPost-CTF
This is a CTF about a made-up language called ScriptPost (definitely nothing to do with PostScript).

## Introduction
### Prerequisites
Before you learn about this language, make sure you understand queues and stacks first. 
You will need to understand these both conceptually and syntactically in Java. It's not too hard, just search up something online.

### Basics
The language ScriptPost is a language based on postfix notation. 
For example, consider the expression ```1 + 2```. In postfix, this expression is ```1 2 +```. 
I will not spoil the specifics of the language, since you have to figure that out. Instead, I will provide some broad guidance.
ScriptPost uses an operand stack to keep track of numbers and a queue to handle input.
When you run the language, you will be prompted for input. You can enter numbers and other operators. When you press enter, it will print the top of the operand stack.
Try to play around with the language and carefully inspect the code to understand how everything works. 
Consider this simple example.

```aiignore
ScriptPost>> 8 5 3 4 + - *
-16
```

Try to figure out why this results in -16.

This language also supports more complicated operations in addition to basic arithmetic.
Make sure you understand each operation this language offers. If it helps, make a table that shows what each operation does to the operand stack.

### Functions
The language also supports functions. Functions are defined by a starting colon (```:```) and an ending semicolon (```;```).
Consider a simple example.

```aiignore
: dup 1 pick ;
: square dup * ;
```

In this example, the function ```dup``` is defined as ```1 pick```, and ```square``` is defined as ```dup *```.
Try to figure out why calling something like ```5 square``` squares the number 5.

Functions are actually not as straightforward as you might think. We use a State object, which keeps track of two things: a dictionary and a return stack.
The dictionary defines functions. The return stack will deal with the logic when we actually call a function.
I recommend you carefully trace the code for the sequence ```3 square 4 square +```. It might help to draw a table with three columns for the operand stack, the queue, and the return stack.

## Your Task
Your task is to write a ScriptPost program in ```answer.sp```. Your program should define a function ```sumDigits``` that calculates the sum of the digits of the top number on the stack.
It should look like the following.

```aiignore
ScriptPost>> 1234 sumDigits
10
```

Note that I will test your program on 200 non-negative integers. I also expect your program to run in less than 5ms for each number. If you think your program is valid but exceeds the time limit, consult me in person.

I have provided some examples of other ScriptPost programs. Don't spend too much time studying them. They could be helpful for inspiration.

```fib``` computes the n-th fibonacci number where n is the integer at the top of the stack.

```sum``` computes the arithmetic sum of n numbers on the stack, where n is the integer at the top of the stack. Consider the following example.

```aiignore
ScriptPost>> 9 6 3 4 1 5 sum
23
```

Note that this computes 23 because 23 = 9+6+3+4+1. Note that 5 is the number of integers we want to sum.

I recommend you add comments and some print statements to help you understand all the commands. Do not modify ```Grader.java```.
You can run ```ScriptPost.java``` to test or mess around in ScriptPost. Run ```Grader.java``` to see if your program works. Good luck.
