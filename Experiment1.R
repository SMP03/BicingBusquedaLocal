base = "Experiment1Data/10map50rep" #Posar el nom base especificat a l'script

set1 <- read.table(paste(base, "set1.txt", sep="_"), header=TRUE, sep="\t")
set2 <- read.table(paste(base, "set2.txt", sep="_"), header=TRUE, sep="\t")
set3 <- read.table(paste(base, "set3.txt", sep="_"), header=TRUE, sep="\t")
set1["OpSet"] <- rep(1, nrow(set1))
set2["OpSet"] <- rep(2, nrow(set2))
set3["OpSet"] <- rep(3, nrow(set3))

library(RColorBrewer)

sets = rbind(set1,set2,set3)
boxplot(sets$ExecutionTime ~ sets$OpSet, ylim=c(0,50), outpch=4, col= brewer.pal(n=3, name="Blues"), xlab="Conjunto de operadores", ylab="Tiempo de ejecución (ms)", main = "Tiempo de ejecución en función del conjunto de operadores")

boxplot(sets$TotalProfit ~ sets$OpSet, outpch=4, col= brewer.pal(n=3, name="Blues"), main = "Beneficio total en función del conjunto de operadores", xlab="Conjunto de operadores", ylab="Beneficio Obtenido (€)")

# t.tests comprovant si són iguals els profits
t.test(set1$TotalProfit, set2$TotalProfit, paired = T) # p molt petita i IC lluny de 0 -> set2 millor que set1
t.test(set2$TotalProfit, set3$TotalProfit, paired = T) # p no tan petita i IC conté 0 -> set2 igual que set3

# t.tests comprovant si són iguals els profits
t.test(set1$ExecutionTime, set2$ExecutionTime, paired = T) # p molt petita i IC lluny de 0 -> set2 millor que set1
t.test(set2$ExecutionTime, set3$ExecutionTime, paired = T) # p gran i IC centrat a 0 -> set2 igual que set3

