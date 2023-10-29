# Script experimento 5
base = "Experiment5_Data_50reps/test" #Posar el nom base especificat a l'script

HCh1 <- read.table(paste(base, "Experimento5_HC_bikes_profit.txt", sep="_"), header=TRUE, sep="\t")
HCh3 <- read.table(paste(base, "Experimento5_HC_dynamic.txt", sep="_"), header=TRUE, sep="\t")
SAh1 <- read.table(paste(base, "Experimento5_SA_bikes_profit.txt", sep="_"), header=TRUE, sep="\t")
SAh3 <- read.table(paste(base, "Experimento5_SA_dynamic.txt", sep="_"), header=TRUE, sep="\t")
HCh1["comb"] <- rep("HC+Heur1", nrow(HCh1))
HCh3["comb"] <- rep("HC+Heur3", nrow(HCh3))
SAh1["comb"] <- rep("SA+Heur1", nrow(SAh1))
SAh3["comb"] <- rep("SA+Heur3", nrow(SAh3))


library(RColorBrewer)

expData = rbind(HCh1, HCh3, SAh1, SAh3)
boxplot(expData$ExecutionTime ~ expData$comb, ylim=c(0,9000),
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Tiempo de ejecución en función del algoritmo y heurística",
        xlab="Combinación de algoritmo y heurística", ylab="Tiempo de ejecución (ms)")

boxplot(expData$TotalProfit ~ expData$comb,
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Beneficio total en función del algoritmo y heurística",
        xlab="Conjunto de operadores", ylab="Beneficio Obtenido (€)")

boxplot(expData$TotalDistance ~ expData$comb,
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Distancia total recorrida en función del algoritmo y heurística",
        xlab="Conjunto de operadores", ylab="Distancia total recorrida (km)")

# t.tests comprovant si són iguals els profits
t.test(HCh3$TotalProfit, SAh3$TotalProfit, paired = T) # p molt petita i IC lluny de 0 -> set2 millor que set1
