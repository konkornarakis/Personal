#Author: Konstantinos Ioannis Kornarakis
#Date: 6/11/2018
#Description: To programma diavazei dio times tis opoies dinei o xristis ws eisodo kai ypologizei
#ton megisto koino diaireti. Sti sinexeia o xristis kaleitai na perasei ws eisodo mia timi i opoia antistoixei ston 
#megisto koino diaireti twn dio ari8mwn pou edwse proigoumenos kai se periptwsi opou i apantisi einai lan8asmeni, 
#kaleitai na apantisi 3ana mexri na dwsei tin swsti apantisi.


#Using register t0 for a, t1 for b and y, t3 for user's answer, a0 for storing strings and v0 for function results

.data
	#Declaring strings to output messages
	str1: .asciiz "Enter first integer n1: "
	str2: .asciiz "Enter second integer n2: "
	str3: .asciiz "What is the greatest common divisor of "
	str4: .asciiz " and "
	str5: .asciiz "?"
	str6: .asciiz "Wrong! Try again."
	str7: .asciiz "Which is the greatest common divisor?"
	str8: .asciiz "Congratulations!"
	
.globl main #global main
.text		#code

main: 
	
	#print(" Give an integer: "); a = Read_Integer(); print(" Give an integer: "); b = Read_Integer();
	
	la $a0, str1 #print str1
	li $v0, 4
	syscall

	la $v0, 5	#read input
	syscall

	move $t0, $v0	#store input in t0 

	la $a0, str2	#print str2
	li $v0, 4
	syscall

	la $v0, 5	#read second input
	syscall

	move $t1, $v0	#store second input in t1
		
	#print("Which is the greatest common divisor of " + a + " and " + b + "?"); y = a % b; while (y != 0 ) { a = b; b = y; y = a%b}
	
	la $a0, str3	#print str3
	li $v0, 4
	syscall
	
	li $v0, 1		#print content of t0(variable a)
	move $a0, $t0
	syscall
	
	la $a0, str4		#print str4
	li $v0, 4
	syscall
	
	li $v0, 1		#print content of t1(variable b)
	move $a0, $t1
	syscall
	
	la $a0, str5		#print str5
	li $v0, 4
	syscall
	
	rem $t2, $t0, $t1		#t2 = t0 % t1
	
	while:
		beq $t2, $zero, end		#beq y, 0 
		move $t0, $t1			#t0 = t1
		move $t1, $t2			#t1 = t2
		rem $t2, $t0, $t1		#t2 = t0% t1
	
		j while
	end:

	#s = Read_Integer(); while (s!=b) { print("Wrong! Try again."); print("Which is the greatest common divisor?"); s = Read_Integer();} print("Congratulations!");
	
	la $v0, 5		#reading user's input
	syscall

	move $t3, $v0		#storing user's input
	
	while2:
		beq $t3, $t1, end2	#beq s, b 
		
		la $a0, str6		#print str6
		li $v0, 4
		syscall
		
		la $a0, str7		#print str7
		li $v0, 4
		syscall
		
		la $v0, 5		#reading user's input
		syscall

		move $t3, $v0		#storing user's input 
		
		j while2
	end2:
	
	la $a0, str8		#print str8
	li $v0, 4
	syscall
	
	li $v0, 10	#Exiting program
	syscall