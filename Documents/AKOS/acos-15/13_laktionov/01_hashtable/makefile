all: prog

prog: main.o hash.o tests.o
  gcc main.o hash.o tests.o prog 

main.o: main.c
  gcc -c main.c -o main.o

tests.o: tests.c
  gcc -c tests.c -o tests.o

hash.o: hash.c
  gcc -c hash.c -o hash.o

main.c:

tests.c:

hash.c: 

clean:
  rm main.o tests.o hash.h
