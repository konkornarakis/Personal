import random
#Phase 1
print("===== Knowledge test game about Rubik's cube =====")
print ("Instructions: Each player is asked a question. 4 answers are available for each question, with one of those to be the right answer. Each player has one use of '50-50' option and one use of 'Skip this question' option. Each correct answer counts as 10 pts. Not using '50-50' or 'Skip this question' option will add 5 points for each in final score of the player. Game ends when all players have answered 10 questions each.")
player1 = input("What's the name of the first player? ")
player2 = input("What's the name of the second player? ")
player3 = input("What's the name of the third player? ")
playerList=[player1, player2, player3]
options=["A","B","C","D"]
#Phase 2
#All 20 questions
questionList=["1. What kind of puzzle is the Rubik's Magic?","2. Who was the first speedcuber to solve the Rubik's Cube under 5 seconds on an official competition?","3. How fast does this robot solve the cube?","4. Which of the following puzzles became an official competition event in 2014?","5. In total, how many little blocks make up the Rubik's Cube?","6. Which of the following organizations governs competitions for all puzzles labeled as Rubik puzzles?","7. What happens 'Just as my SD card runs out'?","8. What is the Domino Cube?","9. Who is Oskar van Deventer?","10. How many stickers go on a Teraminx?","11. Which one is a holey twisty puzzle?","12. What is the title of the movie in which the character played by Will Smith solves a cube? ","13. What is the Rubik's Cube mathematically?","14. Which one of the following puzzles is NOT a 3x3x3 shape mod?","15. Which of the following scrambles applied on a solved Rubik's Cube results a checkerboard pattern?","16. What is the Rubikubism?","17. Which one is a Square-1 algorithm?","18. What is the nationality of Ernő Rubik, the inventor of the Rubik's Cube?","19. What timing device is used on WCA competitions?","20. When was the Rubik's Cube invented?"]
#All 20*4 answers for the 20 questions
answerList=["Twisty puzzle","Folding puzzle","Sliding puzzle","Jigsaw puzzle","Feliks Zemdegs", "Collin Burns", "Oskar van Deventer", "Lucas Etter ","More than 10 seconds","3-10 seconds","1-4 seconds","Less than 1 second ","Hungarian Rings","Void Cube","Skewb","Babylon Tower","27","26","54","9","WCA","RCA","CubeOrg","FIDESZ","The GoPro falls off the desk","The Square-1 explodes during an official solve","Feliks Zemdegs breaks the World Record","WALL-E hands over a cube to EVE","A version of the classic Domino game","2x3x3 twisty puzzle ","4x3x3 twisty puzzle","Rubik's Cube with domino pattern stickers","Custom twisty puzzle designer and inventor ","Speedcuber who broke the 2x2 and 3x3 records in 2008","Magician who performs Rubik's Cube tricks ","Founder of the World Cube Association","732","389","613","555","Hollow Twisterrr","Empty Hole 3D","Void Cube","Drilled IQ","Despicable Me 3","The Pursuit of Happyness ","The Amazing Spider-Man","Dude, Where’s My Car?","Third-degree polynomial function","Permutation group ","Linear equation in three variables","Inverse trigonometric factorial Gamma logarithm","Ghost Cube","Mastermorphix","Mirror Cube/Mirror Blocks","Futuro Cube","M2 E2 S2","F2 R2 U2 M E S","L R U D F B","L' R U' D F' B","Movement in the '90s to save the Cube","An organization to protect copyrights","Rubik’s Cube mosaic pixel art ","A fashion trend inspired by the six colors of the cube","L' R D U2 L'","3' 4 5'' 2' 4'","U d R / U d / R++","(1,0) / (-2,-1) / (0,2) ","Indian","Romanian","Hungarian","Polish","Eggtimer Pro 3.0","Stackmat Timer","Google Atom Stopwatch","One of the 12 WCA certified Android applications","1949","1974","1848","1985",]
#All 20 correct answers for the 20 questions
correctAnswerList=["B","D","D","C","B","A","C","B","A","A","C","B","B","D","A","C","D","C","B","B"]
#List that keeps the number of correct answers of each player
answeredList=[0,0,0]
#Lists that keep the number of available 50-50 and "Skip this question" uses for each player
used50_50=[1,1,1]
usedSkip=[1,1,1]
#A list that keeps the numbers of the next question for each player
questionCounter=[0,0,0]
randQList=[0,0]
#Displaying questions
#Phase 3
i=0
while (answeredList[0]<10 and answeredList[1]<10 and answeredList[2]<10):
    print(playerList[i]+" is now playing.")
    print(questionList[questionCounter[i]])
    a=questionCounter[i]*4
    print(" A. "+answerList[a])
    print(" B. "+answerList[a+1])
    print(" C. "+answerList[a+2])
    print(" D. "+answerList[a+3])

    if (used50_50[i]==1):
        print(" E. 50-50")
    if (usedSkip[i]==1):
        print(" F. Skip this question")

    answer=input("Choose an answer or choose help: ")

    if (answer==correctAnswerList[questionCounter[i]]): #############################
        print("Correct!")
        questionCounter[i]+=1
        answeredList[i]+=1
    elif (answer!=correctAnswerList[questionCounter[i]] and answer!="E" and answer!="F"): ############################
        print("Wrong answer.")
        questionCounter[i]+=1
        answeredList[i]+=1
    elif (answer=="E"): ####################
        used50_50[i]-=1
        random_q = random.randint(0,3)
        while (options[random_q]==correctAnswerList[questionCounter[i]]) :
            random_q = random.randint(0, 3)
        
        randOrder = random.randint(1,2)
        print(questionList[questionCounter[i]])
        ans2=0
        if (options[random_q]=="A") :
            ans2=0
        elif (options[random_q]=="B") :
            ans2=1
        elif (options[random_q]=="C") :
            ans2=2
        elif (options[random_q]=="D") :
            ans2=3
        ans=0
        if (correctAnswerList[questionCounter[i]]=="A") :
            ans=0
        elif (correctAnswerList[questionCounter[i]]=="B") :
            ans=1
        elif (correctAnswerList[questionCounter[i]]=="C") :
            ans=2
        elif (correctAnswerList[questionCounter[i]]=="D") :
            ans=3
        if (randOrder==1) :
            print(" A. "+answerList[a+ans2])
            print(" B. "+answerList[a+ans])
        else :
            print(" A. "+answerList[a+ans])
            print(" B. "+answerList[a+ans2])

        answer=input("Choose an answer or choose help: ")
        if ((randOrder==1 and answer=="B") or (randOrder==2 and answer=="A")):
            print("Correct!")
            questionCounter[i]+=1
            answeredList[i]+=1
        else :
            print("Wrong answer.")
            questionCounter[i]+=1
            answeredList[i]+=1
    elif (answer=="F"): #########################
        usedSkip[i]-=1
        questionCounter[i]+=1
        print(questionList[questionCounter[i]])
        a=questionCounter[i]*4
        print(" A. "+answerList[a])
        print(" B. "+answerList[a+1])
        print(" C. "+answerList[a+2])
        print(" D. "+answerList[a+3])
        answer=input("Choose an answer or choose help: ")
        if (answer==correctAnswerList[questionCounter[i]]):
            print("Correct!")
            questionCounter[i]+=1
            answeredList[i]+=1
        else :
            print("Wrong answer.")
            questionCounter[i]+=1
            answeredList[i]+=1 
    i+=1
    if (i==3):
        i=0

#Phase 4
#Calculating score
k=0
scoreList=[0,0,0]
while (k<2):
    scoreList[k] = answeredList[k]*10
    if (used50_50[k]==1):
        scoreList[k]+=5
    if (usedSkip==1):
        scoreList[k]+=5
    k+=1
#Sorting scoreList array with bubble sort(decreasing)
flag = True
while flag:
    flag = False
    for i in range(len(scoreList) - 1):
        if scoreList[i] < scoreList[i+1]:
            scoreList[i], scoreList[i+1] = scoreList[i+1], scoreList[i]
            playerList[i], playerList[i+1] = playerList[i+1], playerList[i]
            flag = True
#Phase 5
#Displaying ranking
print("***************")
print("The winner is: "+ playerList[0])
print("Ranking: \n   1. "+playerList[0]+" \n   2. "+playerList[1]+" \n   3. "+playerList[2])
print("***************")
#pausing the cmd to prevent closing
PAUSE=input()
