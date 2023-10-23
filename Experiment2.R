base = "10map50rep" #Posar el nom base especificat a l'script
noms<-c("RANDOM_NUM_FURGOS", "MAX_NUM_FURGOS", "EMPTY_FURGOS", "BEST_K_ROUTES", "MIN_DIST")

RNF <- read.table(paste(base, "_", noms[1], ".txt", sep=""), header=TRUE, sep="\t")
MNF <- read.table(paste(base, "_", noms[2], ".txt", sep=""), header=TRUE, sep="\t")
EF <- read.table(paste(base, "_", noms[3], ".txt", sep=""), header=TRUE, sep="\t")
BKR <- read.table(paste(base, "_", noms[4], ".txt", sep=""), header=TRUE, sep="\t")
MD <- read.table(paste(base, "_", noms[5], ".txt", sep=""), header=TRUE, sep="\t")
RNF["InitStrat"] <- rep("RNF", nrow(RNF))
MNF["InitStrat"] <- rep("MNF", nrow(MNF))
EF["InitStrat"] <- rep("EF", nrow(EF))
BKR["InitStrat"] <- rep("BKR", nrow(BKR))
MD["InitStrat"] <- rep("MD", nrow(MD))


data = rbind(RNF, MNF, EF, BKR, MD)
b <- boxplot(data$ExecutionTime ~ data$InitStrat, ylim=c(0,100))
b <- boxplot(data$TotalProfit ~ data$InitStrat)

# t.tests comprovant si són iguals els profits
t.test(RNF$TotalProfit, MD$TotalProfit, paired = T)
t.test(MD$TotalProfit, EF$TotalProfit, paired = T)
...

