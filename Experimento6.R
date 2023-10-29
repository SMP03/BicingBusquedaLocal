base = "Experiment6Data/test" #Posar el nom base especificat a l'script

equi <- read.table(paste(base, "Experimento6_equilibrium.txt", sep="_"), header=TRUE, sep="\t")
rush <- read.table(paste(base, "Experimento6_rush_hour.txt", sep="_"), header=TRUE, sep="\t")
equi["scenery"] <- rep("Equilibrio", nrow(equi))
rush["scenery"] <- rep("Hora Punta", nrow(rush))



library(RColorBrewer)

expData = rbind(equi, rush)
boxplot(expData$ExecutionTime ~ expData$scenery,
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Tiempo de ejecución en función del tipo de escenario",
        xlab="Tipo de escenario", ylab="Tiempo de ejecución (ms)")

boxplot(expData$NodesExpanded ~ expData$scenery,
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Tiempo de ejecución en función del tipo de escenario",
        xlab="Tipo de escenario", ylab="Tiempo de ejecución (ms)")

boxplot(expData$TotalProfit ~ expData$scenery,
        outpch=4, col= brewer.pal(n=3, name="Blues"),
        main = "Beneficio total en función del tipo de escenario",
        xlab="Tipo de escenario", ylab="Beneficio Obtenido (€)")

# # t.tests comprovant si són iguals els profits
# t.test(HCh3$TotalProfit, SAh3$TotalProfit, paired = T) # p molt petita i IC lluny de 0 -> set2 millor que set1
