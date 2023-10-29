# Script experiment 2
base = "Experiment2Data/20map50rep" # Posar el nom base especificat a l'script
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

library(RColorBrewer)

expData = rbind(RNF, MNF, EF, BKR, MD)
b <- boxplot(expData$ExecutionTime ~ expData$InitStrat, ylim=c(0,60),
             outpch=4, col= brewer.pal(n=5, name="Pastel1"),
             xlab="Estrategia de inicialización", ylab="Tiempo de ejecución (ms)",
             main = "Tiempo de ejecución en función de la estrategia de inicialización")
b <- boxplot(expData$NodesExpanded ~ expData$InitStrat,
             outpch=4, col= brewer.pal(n=5, name="Pastel1"),
             xlab="Estrategia de inicialización", ylab="Número de nodos expandidos",
             main = "Número de nodos expandidos en función de la estrategia de inicialización")
b <- boxplot(expData$TotalProfit ~ expData$InitStrat,
             outpch=4, col= brewer.pal(n=5, name="Pastel1"),
             xlab="Estrategia de inicialización", ylab="Beneficio total (€)",
             main = "Beneficio obtenido en función de la estrategia de inicialización")

# Gràfics per mapes individuals
# for (map in unique(expData$MapSeed)) {
#   singleMapData <- subset(expData, MapSeed==map)
#   b <- boxplot(singleMapData$TotalProfit ~ singleMapData$InitStrat, ylim=c(-100,100))
# }

# Extract best profit for each map and strategy
maxData = data.frame(matrix(NA, nrow=0, ncol=ncol(expData)))
colnames(maxData) = colnames(expData)
for (map in unique(expData$MapSeed)) {
  singleMapData <- subset(expData, MapSeed==map)
  for (strat in unique(singleMapData$InitStrat)) {
        stratData <- subset(singleMapData, InitStrat==strat)
        maxData = rbind(maxData, stratData[which.max(stratData$TotalProfit),])
  }
}

b <- boxplot(maxData$TotalProfit ~ maxData$InitStrat,
             outpch=4, col= brewer.pal(n=5, name="Pastel1"),
             xlab="Estrategia de inicialización", ylab="Beneficio total máximo por mapa (€)",
             main = "Beneficio máximo obtenido por mapa en función de la estrategia de inicialización")

# t.tests comprovant si són iguals els profits entre estocàstics
maxMD = maxData[maxData$InitStrat=="MD",]
maxMNF = maxData[maxData$InitStrat=="MNF",]
maxRNF = maxData[maxData$InitStrat=="RNF",]
t.test(maxMD$TotalProfit, maxMNF$TotalProfit, paired = T)
t.test(maxMNF$TotalProfit, maxRNF$TotalProfit, paired = T)
t.test(maxMD$TotalProfit, maxRNF$TotalProfit, paired = T)

# t.test para el tiempo de ejecución MD vs RNF
t.test(MD$ExecutionTime, RNF$ExecutionTime, paired = T)
meanExecMD <- mean(MD$ExecutionTime)
meanExecRNF <- mean(RNF$ExecutionTime)
speedup <- meanExecRNF/meanExecMD
speedup
