# Papaya
### Second Semester APCS Coding Project: Aditya Prerepa

Payaya is an *interpreted* language and its interpreter is written in Java. 

## Hello World

To run a hello world program, put the following code in any `.txt` file:

```
def helloWorld = "Hello World!"
print $helloWorld
```
Then, compile the interpreter with:
```shell
javac Compiler.java
```
After this, you can finally run the program. If you put your code in `papaya.txt`, run with:
```shell
java Compiler papaya.txt
```

## Language Features

Papaya supports a variety of features. These include:
- Writing comments
- Defining & Accessing variables
- Reading from `stdin`
- Writing to `stdout`
- Comparing integers with `>`, `<`, `==` and `!=` & order of operations
- If statements & Conditionals
- While loops

### Writing Comments
Comments start with the character `~`, and are completely ignored by the interpreter.
```
~ This is a comment
~ another comment
~ We don't support multiline comments
```

### Defining & Accessing Variables

To define an integer and string, one would write:
```
~ Defining an integer
def myInt = 5
~ Defining a string
def myString = "Hey Mr. Jan"
```
To access these variables, one would then write:
```
~ printing myInt
print $myInt
~ printing myString
print $myString
```
This method of referencing variables with the `$` is heavily inspired by bash and PHP.
Note that `print` does not require parenthesis - it only prints whatever is directly next to it.

### Reading from `stdin`
You can read either strings or integers from the console, much like `Scanner.next()` and `Scanner.nextint()`. Here is how:
```
~ reading an integer into myInt
def myInt = readint()
~ reading a string into myString
def myString = readstring()
```
You can then access these variables like you would any other variable.

### Writing to `stdout`
You can write to stdout just with the `print` function. This is covered in the `Hello World` section.

You can also quickly write strings (as opposed to creating a variable and writing) with `printqstr` (print quick string):
```
printqstr "foo bar baz mr jan is the goat"
```
This will print:
```
foo bar baz mr jan is the goat
```
Its main use is debugging.
### Comparing integers & Order of Operations
You can compare integers in an expression for a while loop or if statement. Conditionals are always wrapped in `[]` For example:
```
def z = 10
def y = 5
~ this would evaluate to false
[$z == $y]
~ this will evaluate to true
[$z != $y]
~ this would evaluate to false
[$z < $y]
~ this would evaluate to true
[$z > $y]
```
You can also expect expressions to be **simplified, then evaluated**. For example:
```
~ this would evaluate to true, using the values from above
[$z - 6 < $y]
~ z was previously 10, but 10 - 6 is 4, which is less than 5.
```
The order of operations is also supported. `5*5+2` will evaluate to `27`.

### If Statements and conditions
If statements are supported in papaya. There are three components:
- The condition
- The code that runs if the condition evaluates
- The `endif`

For example:
```
~ define my two variables
def a = 10
def b = 7
~ The if statement, evaluates to true
if [$a != $b]
~ Anything between the if and endif will run.
print $a
endif
~ The endif marks the end of the if statement (duh), just like a closing } would in java.
```
Note how there is no indent in the code between the `if` and `endif`. If the `if` statement does not evaluate to true, the code in between will not run.

### While Loops
Last, but not least, while loops are also supported. Here is how to write one:
```
def a = 5
def b = 1
~ While loop that will run 4 times
while [$a > $b]
~ print b, and increment it
print $b
$b = $b + 1
endwhile
```
The output of this program is:
```
1
2
3
4
```
The code inside the while loop runs until the condition is satisfied. Papaya does **not** support nested loops, so you can only do one level of while loops.

# See `papaya.txt` for a full sample program.
