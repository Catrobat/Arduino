Some notes for Developing with Arduino: 

* Be careful with RAM - Arduino Uno only has 2KB 

When you enable verbose compiling (Arduino IDE --> File -->
Prefrences), the compilation files are stored in a temporary folder
(i.e. /tmp/build*).  That folder contains the elf, which can be read
out with "avr-size", when you sum up BSS and data you will get the
size currently used.
